package com.example.zucker.easycrypto.file

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.IllegalStateException
import java.nio.file.Files
import java.nio.file.Path
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Signature

class PublicKeyFile(private val filePath: Path) {

    companion object {
        const val CURRENT_VERSION: Byte = 1
        const val BUFFER_SIZE = 1024 * 1024
        const val SIGN_ALGORITHM = "SHA256withRSA"
    }

    fun save(privateKey: PrivateKey, publicKey: PublicKey) {
        Files.newOutputStream(filePath).buffered(BUFFER_SIZE).use { output ->
            val fileHeader = ByteArray(8).apply {
                FileFormat.ECPUBK.name.encodeToByteArray().copyInto(this)
                this[6] = CURRENT_VERSION
            }
            output.write(fileHeader)

        ObjectOutputStream(output).use { objOut ->
            val sign = Signature.getInstance(SIGN_ALGORITHM)
                .apply {
                    initSign(privateKey, SecureRandom.getInstanceStrong())
                    update(publicKey.encoded)
                }.sign()
            objOut.writeObject(sign)
            objOut.writeObject(publicKey)
    }   }}

    fun load(): PublicKey {
        Files.newInputStream(filePath).buffered(BUFFER_SIZE).use { input ->
            val fileHeader = input.readNBytes(8)
            val fileFormat = fileHeader.sliceArray(0..5).decodeToString()
            if (FileFormat.valueOf(fileFormat) != FileFormat.ECPUBK) {
                TODO("implement throw exception")
            }
            // TODO "version check"
        ObjectInputStream(input).use { objIn ->
            val sign = objIn.readObject() as ByteArray
            val publicKey = objIn.readObject() as PublicKey
            if (!Signature.getInstance(SIGN_ALGORITHM).apply {
                    initVerify(publicKey)
                    update(publicKey.encoded)
            }.verify(sign)) {
                throw IllegalStateException("署名の検証に失敗しました")
            }
            return publicKey
    }   }}
}