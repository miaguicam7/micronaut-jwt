package example.micronaut.Controller

import example.micronaut.model.User
import example.micronaut.repository.UserRepository
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import javax.validation.Valid

@Controller("/users")
class UserController(val userRepository: UserRepository) {

    @Get(produces = [MediaType.APPLICATION_JSON])
    fun getAllUsers(): HttpResponse<*> {
        return HttpResponse.ok<Any>(userRepository.getAllUsers())
    }

    //curl --request POST -H "Content-Type: application/json" --data '{"name":"Malta","login":"malta","password":"mm"}' http://localhost:8080/users
    @Post(produces = [MediaType.APPLICATION_JSON])
    fun signIn(@Body @Valid user: User): HttpResponse<*> {
        return HttpResponse.ok<Any>(userRepository.findUserByLoginAndPassword(user))
    }


    //curl --request POST -H "Content-Type: application/json" --data '{"name":"Malta","login":"malta","password":"mm"}' http://localhost:8080/users/signup
    @Post("/signup", produces = [MediaType.APPLICATION_JSON])
    fun singUp(@Body @Valid user: User): HttpResponse<*> {
        val savedUser = userRepository.findUserByLoginAndPassword(user)?.takeIf { it == null }.let { userRepository.addUser(user) }
        return if (savedUser) HttpResponse.created<Any>(user) else HttpResponse.badRequest<Any>()
    }
}