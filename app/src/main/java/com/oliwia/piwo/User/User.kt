package com.oliwia.piwo.User

import com.oliwia.piwo.LocalisationService.Location

data class User(val username: String){
    var name = ""
    var lastname = ""
    val friends = hashSetOf<String>()
    var location = Location.emptyLocation(username)

    companion object {
        val empty = User("")
    }
}