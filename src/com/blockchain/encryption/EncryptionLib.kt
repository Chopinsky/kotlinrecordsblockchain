package com.blockchain.encryption

/**
 * Created by Ziwei on 5/30/2017.
 */

import java.io.*
import java.security.*
import java.security.KeyPair
import javax.crypto.Cipher
import java.security.PublicKey
import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter
import java.io.FileInputStream

val DEFAULT_PUBLIC_KEY_PATH: String = "./keys/public.key"
val DEFAULT_PRIVATE_KEY_PATH: String = "./keys/private/private.key"

fun makeKeys(keySize: Int = 512, publicKeyPath: String = DEFAULT_PUBLIC_KEY_PATH, privateKeyPath: String = DEFAULT_PRIVATE_KEY_PATH, debug: Boolean = false): KeyPair {
    val generator = KeyPairGenerator.getInstance("RSA")
    generator.initialize(keySize)

    val keyPair = generator.generateKeyPair()

    writeKeyFile(publicKeyPath, keyPair.public)
    writeKeyFile(privateKeyPath, keyPair.private)

    if (debug) {
        println("Raw Public Key: ${keyPair.public}")
        println("Public Key: ${keyPair.public.encoded}")
        println("Raw Private Key: ${keyPair.private}")
        println("Private Key: ${keyPair.private.encoded}")
    }

    return keyPair
}

fun encrypt(message: String, keySize: Int): ByteArray {

    val publicKey: File = File(DEFAULT_PUBLIC_KEY_PATH)
    val privateKey: File = File(DEFAULT_PRIVATE_KEY_PATH)

    if (!publicKey.exists() || !publicKey.isFile || !privateKey.exists() || !privateKey.isFile) {
        makeKeys(if (keySize < 1024) 1024 else keySize)
    }

    val inputStream = ObjectInputStream(FileInputStream(DEFAULT_PUBLIC_KEY_PATH))
    val key: PublicKey = inputStream.readObject() as PublicKey

    return encrypt(message, key)
}

fun encrypt(message: String, keyFilePath: String = DEFAULT_PUBLIC_KEY_PATH): ByteArray {
    val keyFile: File = File(keyFilePath)
    if (!keyFile.exists() || !keyFile.isFile)
        return ByteArray(0)

    val inputStream = ObjectInputStream(FileInputStream(keyFile))
    val key: PublicKey = inputStream.readObject() as PublicKey

    return encrypt(message, key)
}

fun encrypt(message: String, key: PublicKey): ByteArray {
    var cipherText: ByteArray = ByteArray(DEFAULT_BUFFER_SIZE)

    try {
        val cipher = Cipher.getInstance("RSA")

        cipher.init(Cipher.ENCRYPT_MODE, key)

        cipherText = cipher.doFinal(message.toByteArray())

    } catch (e: Exception) {
        e.printStackTrace()
    }

    return cipherText
}

fun decrypt(input: ByteArray, keyFilePath: String = DEFAULT_PRIVATE_KEY_PATH): String {
    val keyFile: File = File(keyFilePath)
    if (!keyFile.exists() || !keyFile.isFile)
        return ""

    val inputStream = ObjectInputStream(FileInputStream(keyFile))
    val key: PrivateKey = inputStream.readObject() as PrivateKey

    return decrypt(input, key)
}

fun decrypt(input: ByteArray, key: PrivateKey): String {
    var decryptedText: ByteArray = ByteArray(DEFAULT_BUFFER_SIZE)
    try {

        val cipher: Cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, key)

        decryptedText = cipher.doFinal(input)

    } catch (e: Exception) {
        e.printStackTrace()
    }

    return String(decryptedText)
}

fun createCheckSum(filePath: String): String {
    if (filePath.isNullOrBlank())
        return ""

    val target = File(filePath)
    if (!target.exists() || !target.isFile)
        return ""

    val md: MessageDigest = MessageDigest.getInstance("MD5")
    val fis: FileInputStream = FileInputStream(target)

    var buffer: ByteArray
    var bufferIsEmpty: Boolean

    try {
        do {
            buffer = fis.readBytes(DEFAULT_BUFFER_SIZE)
            bufferIsEmpty = buffer.isEmpty()
            if (!bufferIsEmpty) { md.update(buffer) }
        } while (!bufferIsEmpty)
    } catch (e:Exception) {
        e.printStackTrace()
    } finally {
        fis.close()
    }

    val digest: ByteArray = md.digest()
    return DatatypeConverter.printHexBinary(digest).toUpperCase()
}

fun writeKeyFile(filePath: String, keyObject: Key) {
    if (filePath.isNullOrBlank() || keyObject.equals(null))
        return

    val keyFile: File = File(filePath)

    if (keyFile.parentFile != null) {
        keyFile.parentFile.mkdirs()
    }
    keyFile.createNewFile()

    val publicKeyOS: ObjectOutputStream = ObjectOutputStream(FileOutputStream(keyFile))
    publicKeyOS.writeObject(keyObject)
    publicKeyOS.close()
}

fun writeKeyFile(filePath: String, keyObject: ByteArray) {
    if (filePath.isNullOrBlank() || keyObject.isEmpty())
        return

    val keyFile: File = File(filePath)

    if (keyFile.parentFile != null) {
        keyFile.parentFile.mkdirs()
    }
    keyFile.createNewFile()

    val fileOutputStream: FileOutputStream = FileOutputStream(keyFile)

    fileOutputStream.write(keyObject)
    fileOutputStream.close()
}

fun writeKeyFileFull(publicKeyPath: String = DEFAULT_PUBLIC_KEY_PATH, privateKeyPath: String = DEFAULT_PRIVATE_KEY_PATH, keyPair: KeyPair) {

    val privateKeyFile: File = File(privateKeyPath)
    val publicKeyFile: File = File(publicKeyPath)

    // Create files to store public and private key
    if (privateKeyFile.parentFile != null) {
        privateKeyFile.parentFile.mkdirs()
    }
    privateKeyFile.createNewFile()

    if (publicKeyFile.parentFile != null) {
        publicKeyFile.parentFile.mkdirs();
    }
    publicKeyFile.createNewFile()

    // Saving the Public key in a file
    val publicKeyOS: ObjectOutputStream = ObjectOutputStream(
            FileOutputStream(publicKeyFile))
    publicKeyOS.writeObject(keyPair.public)
    publicKeyOS.close()

    // Saving the Private key in a file
    val privateKeyOS: ObjectOutputStream = ObjectOutputStream(
            FileOutputStream(privateKeyFile))
    privateKeyOS.writeObject(keyPair.private)
    privateKeyOS.close()
}
