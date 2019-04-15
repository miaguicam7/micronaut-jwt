package example.micronaut.Controller

import example.micronaut.model.User
import example.micronaut.repository.UserRepository
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import reactor.core.publisher.Flux

@Controller("/users")
class UserController(val userRepository: UserRepository) {//(private val userRepository: UserRepository){

    @Get(produces = [MediaType.TEXT_EVENT_STREAM])
    fun getAllUsers()= Flux.just(User("Aurelio", "abmf", "vaca"),
            User("Flavio", "ffpsj", "boi"), User("Malta", "malta", "mm"),
            User("Malta", "malta", "mm"), User("Pedro", "ps", "1234"))
}