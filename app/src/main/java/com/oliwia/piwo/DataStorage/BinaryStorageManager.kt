package com.oliwia.piwo.DataStorage

import android.content.Context
import java.io.*
import java.lang.Exception

class BinaryStorageManager<T>(private val context: Context) : IStorageManager<T>{
    override fun save(objectToSave: T, filename: String) {
        ObjectOutputStream(File(context.filesDir, filename).outputStream()).use {
            it.writeObject(objectToSave)
        }
    }

    override fun load(filename: String): T? =
        File(context.filesDir, filename).let {
            if(!it.exists()){
                null
            } else {
                ObjectInputStream(it.inputStream()).use { output ->
                    try{
                    output.readObject() as T
                    } catch (e: Exception){
                        null
                    }
                }
            }
        }

}