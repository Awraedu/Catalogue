package com.awra.catalogue.data.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.LruCache
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

    // Cache the SecretKey reference in memory so we don't query AndroidKeyStore on every read
    @Volatile
    private var cachedSecretKey: SecretKey? = null

    // Cache up to 15 recently decrypted Markdown handouts in RAM for instant 0ms loading
    private val decryptedMemoryCache = LruCache<String, String>(15)

    private fun getSecretKey(): SecretKey {
        cachedSecretKey?.let { return it }
        synchronized(this) {
            cachedSecretKey?.let { return it }

            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
            if (keyStore.containsAlias(KEY_ALIAS)) {
                val entry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
                if (entry != null) {
                    cachedSecretKey = entry.secretKey
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
            return keyGenerator.generateKey().also { cachedSecretKey = it }
        }
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
        if (decryptedMemoryCache.get(relativePath) != null) return true
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
            // Populate memory cache immediately
            decryptedMemoryCache.put(relativePath, plainText)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun decryptAndRead(context: Context, relativePath: String): String? {
        // 1. Instant RAM hit (0ms, zero crypto overhead)
        decryptedMemoryCache.get(relativePath)?.let { return it }

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
            String(decryptedBytes, Charsets.UTF_8).also {
                decryptedMemoryCache.put(relativePath, it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteHandout(context: Context, relativePath: String): Boolean {
        decryptedMemoryCache.remove(relativePath)
        val file = getEncryptedFile(context, relativePath)
        return if (file.exists()) file.delete() else true
    }

    fun clearAllEncryptedHandouts(context: Context): Boolean {
        decryptedMemoryCache.evictAll()
        val dir = File(context.filesDir, DIR_NAME)
        return if (dir.exists()) dir.deleteRecursively() else true
    }
}