package com.example.zucker.easycrypto

interface AuthenticationService {

    fun register(password: CharArray)

    fun authenticate(password: CharArray): Boolean
}