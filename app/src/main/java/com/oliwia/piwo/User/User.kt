package com.oliwia.piwo.User

import com.oliwia.piwo.LocalisationService.FirebaseLocator
import com.oliwia.piwo.LocalisationService.ILocalise
import com.oliwia.piwo.LocalisationService.Location
import java.io.Serializable

open class User(val username: String, val id: Long) : Serializable {
    fun updateLocation(location: Location, updateService: ILocalise,callback: (User) -> Unit){
        this.location = location
        updateService.putLocalisation(this.username, this.location)
        callback.invoke(this)
    }
    val friends = hashSetOf<String>()
    var location = Location.emptyLocation(username)
        private set(value) {
            field = value
        }

    override fun equals(other: Any?): Boolean =
        when(other){
            is User -> {
                other.username == username
            }
            else -> false
        }

    override fun hashCode(): Int = username.hashCode() * 13

    override fun toString(): String = "User $username, location: $location"

    companion object {
        val empty = User("", 0)
        private const val serialVersionUID = 1234567890L
    }
}

//data class UserWithLocation(val location: Location, var fullUsername: String) : User(fullUsername), Serializable