package authentication

import com.nimbusds.jwt.JWTParser
import com.nimbusds.jwt.SignedJWT
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.token.jwt.endpoints.TokenRefreshRequest
import io.micronaut.security.token.jwt.render.AccessRefreshToken
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken
import junit.framework.Assert.assertEquals
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import java.lang.Thread.sleep

object AuthSpec: Spek({

    describe("Verify JWT authentication works") {

        var embeddedServer : EmbeddedServer = ApplicationContext.run(EmbeddedServer::class.java)
        var client : RxHttpClient = RxHttpClient.create(embeddedServer.url)

        it("Accessing a secured URL without authenticating") {
            var exceptionThrown = false
            try {
                val request = HttpRequest.GET<Any>("/")
                client.toBlocking().exchange(request, String::class.java)
            } catch(e: HttpClientResponseException) {
                exceptionThrown = true
            }
            assertTrue(exceptionThrown)
        }

        it("you can refresh JWT access token with /oauth/access_token endpoint") {
            val creds = UsernamePasswordCredentials("sherlock", "password")
            val request = HttpRequest.POST("/login", creds)

            val rsp: HttpResponse<BearerAccessRefreshToken> = client.toBlocking().exchange(request,
                    BearerAccessRefreshToken::class.java)

            assertEquals(rsp.status()!!, HttpStatus.OK)

            val refreshToken: String = rsp.body()!!.refreshToken
            val accessToken: String = rsp.body()!!.accessToken
            sleep(1_000) // sleep for one second to give time for the issued at `iat` Claim to change
            val refreshTokenRequest: HttpRequest<TokenRefreshRequest> = HttpRequest.POST("/oauth/access_token",
                    TokenRefreshRequest("refresh_token", refreshToken))
            val response: HttpResponse<AccessRefreshToken> = client.toBlocking().exchange(refreshTokenRequest, AccessRefreshToken::class.java)

            assertEquals(response.status()!!, HttpStatus.OK)
            assertNotNull(response.body()!!.accessToken)
            assertTrue(response.body()!!.accessToken != accessToken)
        }

        it("the endpoint can be access with JWT obtained when Login endpoint is called with valid credentials") {
            val creds = UsernamePasswordCredentials("sherlock", "password")
            val request = HttpRequest.POST("/login", creds)

            val rsp : HttpResponse<BearerAccessRefreshToken> = client.toBlocking().exchange(request,
                    BearerAccessRefreshToken::class.java)

            assertEquals(rsp.status()!!, HttpStatus.OK)
            assertNotNull(rsp.body()!!.accessToken)
            assertTrue(JWTParser.parse(rsp.body()!!.accessToken) is SignedJWT)
            assertNotNull(rsp.body()!!.refreshToken)
            assertNotNull(JWTParser.parse(rsp.body()!!.refreshToken) is SignedJWT)

            val accessToken : String = rsp.body()!!.accessToken
            val requestWithAuthorization = HttpRequest.GET<Any>("/users" ).header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            val response : HttpResponse<String>  = client.toBlocking().exchange(requestWithAuthorization)

            assertEquals(response.status()!!, HttpStatus.OK)
        }
        afterGroup {
            client.close()
            embeddedServer.close()
        }
    }
})