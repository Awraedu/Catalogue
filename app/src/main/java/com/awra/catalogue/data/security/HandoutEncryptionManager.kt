package com.awra.catalogue.data.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object HandoutEncryptionManager {

    private const val KEY_ALIAS = "catalogue_handout_key"
    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128
    private const val IV_LENGTH_BYTES = 12
    private const val DIR_NAME = "encrypted_handouts"

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        if (keyStore.containsAlias(KEY_ALIAS)) {
            val entry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
            if (entry != null) {
                return entry.secretKey
            }
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )
        val parameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(parameterSpec)
        return keyGenerator.generateKey()
    }

    private fun getEncryptedFile(context: Context, relativePath: String): File {
        val dir = File(context.filesDir, DIR_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val safeFileName = relativePath
            .replace("/", "_")
            .replace("\\", "_")
            .replace(" ", "_")
            .lowercase() + ".bin"
        return File(dir, safeFileName)
    }

    fun isHandoutDownloaded(context: Context, relativePath: String): Boolean {
        val file = getEncryptedFile(context, relativePath)
        return file.exists() && file.length() > (4 + IV_LENGTH_BYTES)
    }

    fun encryptAndSave(context: Context, relativePath: String, plainText: String): Boolean {
        return try {
            val secretKey = getSecretKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val iv = cipher.iv
            val plainBytes = plainText.toByteArray(Charsets.UTF_8)
            val encryptedBytes = cipher.doFinal(plainBytes)

            val file = getEncryptedFile(context, relativePath)
            FileOutputStream(file).use { fos ->
                val buffer = ByteBuffer.allocate(4 + iv.size + encryptedBytes.size)
                buffer.putInt(iv.size)
                buffer.put(iv)
                buffer.put(encryptedBytes)
                fos.write(buffer.array())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun decryptAndRead(context: Context, relativePath: String): String? {
        val file = getEncryptedFile(context, relativePath)
        if (!file.exists()) return null

        return try {
            val fileBytes = FileInputStream(file).use { it.readBytes() }
            if (fileBytes.size <= 4) return null

            val buffer = ByteBuffer.wrap(fileBytes)
            val ivSize = buffer.int
            if (ivSize <= 0 || ivSize > 128 || fileBytes.size < 4 + ivSize) return null

            val iv = ByteArray(ivSize)
            buffer.get(iv)

            val encryptedBytes = ByteArray(buffer.remaining())
            buffer.get(encryptedBytes)

            val secretKey = getSecretKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteHandout(context: Context, relativePath: String): Boolean {
        val file = getEncryptedFile(context, relativePath)
        return if (file.exists()) file.delete() else true
    }

    fun clearAllEncryptedHandouts(context: Context): Boolean {
        val dir = File(context.filesDir, DIR_NAME)
        return if (dir.exists()) dir.deleteRecursively() else true
    }
}
