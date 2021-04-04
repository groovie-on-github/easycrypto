package com.example.zucker.easycrypto.file

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.util.zip.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class CryptoFile(private val regularFilePath: Path, private val encryptedFilePath: Path) {

    companion object {
        const val CURRENT_VERSION: Byte = 1
        const val RSA_TRANSFORMATION = "RSA/NONE/OAEPWithSHA-256AndMGF1Padding"
        const val AES_ALGORITHM = "AES"
        const val AES_KEY_SIZE = 256
        const val AES_TRANSFORMATION = "AES/CBC/PKCS5Padding"
        const val BUFFER_SIZE = 1024 * 1024
    }


    fun encrypt(publicKeys: List<PublicKey>) {
        val aesKey = KeyGenerator.getInstance(AES_ALGORITHM)
                        .apply { init(AES_KEY_SIZE, SecureRandom.getInstanceStrong()) }
                        .generateKey()
        val aesParam = IvParameterSpec(ByteArray(16).apply { SecureRandom.getInstanceStrong().nextBytes(this) })

        val rsaCipher = Cipher.getInstance(RSA_TRANSFORMATION)
        val encryptedAESKeyList = mutableListOf<Pair<ByteArray, ByteArray>>()
        for (publicKey in publicKeys) {
            rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey, SecureRandom.getInstanceStrong())
            encryptedAESKeyList.add(Pair(rsaCipher.doFinal(aesKey.encoded), rsaCipher.doFinal(aesParam.iv)))
        }

        val aesCipher = Cipher.getInstance(AES_TRANSFORMATION)
                            .apply { init(Cipher.ENCRYPT_MODE, aesKey, aesParam, SecureRandom.getInstanceStrong()) }

        Files.newOutputStream(encryptedFilePath).buffered(BUFFER_SIZE).use { output ->
            val fileHeader = ByteArray(8).apply {
                FileFormat.ECENCF.name.encodeToByteArray().copyInto(this)
                this[6] = CURRENT_VERSION
            }
            output.write(fileHeader)

        ObjectOutputStream(output).use { objOut ->
            objOut.writeObject(encryptedAESKeyList)


        DeflaterOutputStream(CipherOutputStream(output, aesCipher),
                             Deflater(Deflater.BEST_COMPRESSION), BUFFER_SIZE
        ).use { cipherOut ->
            Files.newInputStream(regularFilePath).buffered(BUFFER_SIZE).use { input -> input.copyTo(cipherOut, BUFFER_SIZE) }
    }   }}}

    fun decrypt(privateKey: PrivateKey) {
        Files.newInputStream(encryptedFilePath).buffered(BUFFER_SIZE).use { input ->
            val fileHeader = input.readNBytes(8)
            val fileFormat = fileHeader.sliceArray(0..5).decodeToString()
            if (FileFormat.valueOf(fileFormat) != FileFormat.ECENCF) { TODO("implement throw exception") }
            // TODO "version check"

        ObjectInputStream(input).use { objIn ->
            val encryptedAESKeyList = objIn.readObject() as List<*>

            val rsaCipher = Cipher.getInstance(RSA_TRANSFORMATION)
                                .apply { init(Cipher.DECRYPT_MODE, privateKey) }

            var aesKeyData: Pair<ByteArray,  ByteArray>? = null
            for (encryptedAESKeyData in encryptedAESKeyList) {
                try {
                    val (encryptedAESKey, encryptedAESIv) = encryptedAESKeyData as Pair<*, *>
                    aesKeyData = Pair(rsaCipher.doFinal(encryptedAESKey as ByteArray),
                                      rsaCipher.doFinal(encryptedAESIv as ByteArray))
                    break
                } catch (e: Exception) {
//                    e.printStackTrace()
            }   }

            if (aesKeyData == null) { throw IllegalStateException("復号化する権限がありません") }

            val aesKey = SecretKeyFactory.getInstance(AES_ALGORITHM)
                .generateSecret(SecretKeySpec(aesKeyData.first, AES_ALGORITHM))
            val aesParam = IvParameterSpec(aesKeyData.second)
            val aesCipher = Cipher.getInstance(AES_TRANSFORMATION).apply { init(Cipher.DECRYPT_MODE, aesKey, aesParam) }

        InflaterInputStream(CipherInputStream(input, aesCipher), Inflater(), BUFFER_SIZE).use { cipherIn ->
            Files.newOutputStream(regularFilePath).buffered(BUFFER_SIZE).use { output -> cipherIn.copyTo(output, BUFFER_SIZE) }
    }   }}}
}