package example.micronaut.repository

import example.micronaut.model.User
import reactor.core.publisher.Flux
import javax.inject.Singleton

@Singleton
class UserRepository {

    fun getAllUsers(): Flux<Any> = Flux.just(User("Aurelio", "abmf", "vaca"),
            User("Flavio", "ffpsj", "boi"),User("Malta", "malta", "mm"),
            User("Malta", "malta", "mm"),User("Pedro", "ps", "1234"))

/*
    fun getAllUsers() = users

    fun removeUserByLogin(user: User) = users.removeAll { u -> u.login ==  user.login}

    fun addUser(user: User) = users.add(user)

    fun findUserByLoginAndPassword(user: User) = users.find { u ->
        u.login == user.login && u.password == user.password
    }
    */

}