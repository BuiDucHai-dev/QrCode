package com.ecomobile.retrofit.decode

import android.util.Base64
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class Encryption {

    private val key = "ecoMobileCompany"
    private val salt = "roomTeam"
    private val iterations = 10000
    private val keyLength = 256

    /*fun encrypt(apikey: String): String {
        val saltBytes = SALT.toByteArray(StandardCharsets.UTF_8)

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec = PBEKeySpec(SECRET_KEY.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH)
        val secretKey: SecretKey = SecretKeySpec(factory.generateSecret(spec).encoded, "AES")

        val ivBytes = ByteArray(16)
        val ivSpec = IvParameterSpec(ivBytes)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)

        val encrypted = cipher.doFinal(apikey.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }*/

    fun decrypt(encryptedApikey: String): String {
        val saltBytes = salt.toByteArray(StandardCharsets.UTF_8)

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec = PBEKeySpec(key.toCharArray(), saltBytes, iterations, keyLength)
        val secretKey: SecretKey = SecretKeySpec(factory.generateSecret(spec).encoded, "AES")

        val ivBytes = ByteArray(16)
        val ivSpec = IvParameterSpec(ivBytes)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)

        val original = cipher.doFinal(Base64.decode(encryptedApikey, Base64.DEFAULT))
        return String(original, StandardCharsets.UTF_8)
    }
}