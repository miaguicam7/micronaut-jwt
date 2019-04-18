package example.micronaut.controller

import example.micronaut.model.User
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.*

object UserControllerTest : Spek({

    describe("UserController Suite") {
        val embeddedServer : EmbeddedServer = ApplicationContext.run(EmbeddedServer::class.java)
        val client : HttpClient = HttpClient.create(embeddedServer.url)

        val users = "/users"
        var accessToken : String? = null

        beforeGroup {
            val creds = UsernamePasswordCredentials("sherlock", "password")
            val request = HttpRequest.POST("/login", creds)

            val response: HttpResponse<BearerAccessRefreshToken> = client.toBlocking().exchange(request,
                    BearerAccessRefreshToken::class.java)

            assertEquals(response.status()!!, HttpStatus.OK)

            accessToken = response.body()!!.accessToken
        }

        it("test /users responds list of Users") {
            val request = HttpRequest.GET<String>("$users").header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            val response : HttpResponse<Any> = client.toBlocking().exchange(request)
            assertEquals(response.status, HttpStatus.OK)
        }

        it("test /users signIn user ok") {
            val user = User("Malta","malta","mm")
            val request = HttpRequest.POST("$users", user)
            val response : HttpResponse<Any> = client.toBlocking().exchange(request)
            assertEquals(response.status, HttpStatus.OK)
        }

        it("test /users signIn user no content") {
            val user = User("Maltade","maltdedea","mmdede")
            val request = HttpRequest.POST("$users", user)
            val response : HttpResponse<Any> = client.toBlocking().exchange(request)
            assertEquals(response.status, HttpStatus.NO_CONTENT)
        }

        afterGroup {
            client.close()
            embeddedServer.close()
        }
    }
})
