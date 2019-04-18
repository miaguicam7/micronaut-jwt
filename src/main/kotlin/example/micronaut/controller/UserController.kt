package example.micronaut.controller

import example.micronaut.model.User
import example.micronaut.repository.UserRepository
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import javax.validation.Valid


@Controller("/users")
class UserController(val userRepository: UserRepository) {

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get(produces = [MediaType.APPLICATION_JSON])
    fun list() = HttpResponse.ok<Any>(userRepository.getAllUsers())

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post(produces = [MediaType.APPLICATION_JSON])
    fun signIn(@Body @Valid user: User): HttpResponse<*>  {
        val rsp = userRepository.findUserByLoginAndPassword(user)
        return if (rsp !=null) HttpResponse.ok<Any>(rsp) else HttpResponse.noContent<Any>()
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post("/signup", produces = [MediaType.APPLICATION_JSON])
    fun singUp(@Body @Valid user: User): HttpResponse<*> {
        val rsp = userRepository.findUserByLoginAndPassword(user)?.takeIf { it == null }.let { userRepository.addUser(user) }
        return if (rsp) HttpResponse.created<Any>(user) else HttpResponse.badRequest<Any>()
    }
}