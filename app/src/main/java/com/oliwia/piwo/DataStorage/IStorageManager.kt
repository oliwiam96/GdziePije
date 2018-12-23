package com.oliwia.piwo.DataStorage

import java.io.*

interface IStorageManager<T>{
    fun save(objectToSave: T, filename: String)
    fun load(filename: String): T?
}