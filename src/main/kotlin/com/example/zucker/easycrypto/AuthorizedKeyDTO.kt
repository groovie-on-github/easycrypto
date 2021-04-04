package com.example.zucker.easycrypto

import java.io.Serializable
import java.security.PublicKey

class AuthorizedKeyDTO(val name: String, val publicKey: PublicKey): Serializable