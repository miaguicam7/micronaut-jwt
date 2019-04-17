package example.micronaut.controller

import example.micronaut.model.User
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse

object UserControllerTest : Spek({

    describe("UserController Suite") {
        val embeddedServer : EmbeddedServer = ApplicationContext.run(EmbeddedServer::class.java)
        val client : HttpClient = HttpClient.create(embeddedServer.url)

        it("test /users responds list of Users") {
            val users = client.toBlocking().retrieve("/users")
            assertFalse(users.isEmpty());
        }

        it("test /users signIn user ok") {
            val user = User("Malta","malta","mm")
            val request = HttpRequest.POST("/users", user)
            val rsp : HttpResponse<Any> = client.toBlocking().exchange(request)
            assertEquals(rsp.status, HttpStatus.OK)
        }

        it("test /users signIn user no content") {
            val user = User("Maltade","maltdedea","mmdede")
            val request = HttpRequest.POST("/users", user)
            val rsp : HttpResponse<Any> = client.toBlocking().exchange(request)
            assertEquals(rsp.status, HttpStatus.NO_CONTENT)
        }

        afterGroup {
            client.close()
            embeddedServer.close()
        }
    }
})
