package com.blockchain

import java.io.FileInputStream
import com.blockchain.encryption.*
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Ziwei on 5/30/2017.
 */

fun main(args: Array<String>) {
    testScripts()
}

fun testScripts() {

    val message: String = "This is a test! - Signed by Jacob Zuo"
    val encryptedMessage: ByteArray = encrypt(message, 1024)

    writeToFile("./encrypted/message.txt", encryptedMessage)
    println("Encrypted Message: ${encryptedMessage}")

    val inputStream = FileInputStream("./encrypted/message.txt")
    val savedMessage: ByteArray = inputStream.readBytes(DEFAULT_BUFFER_SIZE)
    val decryptedMessage: String = decrypt(savedMessage)

    println("Decrypted Message: ${decryptedMessage}")
    writeToFile("./decrypted/message.txt", decryptedMessage)

    println("Checksum: " + createCheckSum("D:\\Program Files (x86)\\Launchy\\readme.pdf"))
    println("Checksum: " + createCheckSum("./decrypted/message.txt"))

}

fun writeToFile(path: String = "./message.txt", content: String) {
    writeToFile(path, content.toByteArray())
}

fun writeToFile(path: String = "./message.txt", content: ByteArray) {
    val keyFile: File = File(path)
    if (keyFile.parentFile != null) {
        keyFile.parentFile.mkdirs()
    }
    keyFile.createNewFile()

    val fos: FileOutputStream = FileOutputStream(keyFile)
    fos.write(content)
    fos.close()
}
