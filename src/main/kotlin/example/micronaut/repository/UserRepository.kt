package example.micronaut.repository

import example.micronaut.model.User
import javax.inject.Singleton

@Singleton
class UserRepository {
    private var users = ArrayList<User>()

    init{
        users.add(User("Aurelio", "abmf", "vaca"))
        users.add(User("Flavio", "ffpsj", "boi"))
        users.add(User("Malta", "malta", "mm"))
        users.add(User("Pedro", "ps", "1234"))
    }

    fun getAllUsers() = users

    fun removeUserByLogin(user: User) = users.removeAll { u -> u.login ==  user.login}

    fun addUser(user: User)= users.add(user)

    fun findUserByLoginAndPassword(user: User) = users.find { u ->
        u.login == user.login && u.password == user.password
    }
}