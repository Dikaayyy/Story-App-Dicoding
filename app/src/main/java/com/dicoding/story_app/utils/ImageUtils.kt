package com.dicoding.story_app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun uriToFile(context: Context, uri: Uri): File {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val tempFile = File.createTempFile("temp_image", null, context.cacheDir)
    tempFile.deleteOnExit()
    val outputStream = FileOutputStream(tempFile)
    inputStream?.copyTo(outputStream)
    inputStream?.close()
    outputStream.close()
    return tempFile
}

fun compressImageToLimit(file: File, maxSizeInBytes: Long = 1 * 1024 * 1024): File {
    val bitmap = BitmapFactory.decodeFile(file.absolutePath)

    var quality = 100
    val outputStream = ByteArrayOutputStream()

    do {
        outputStream.reset()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        quality -= 5
    } while (outputStream.toByteArray().size > maxSizeInBytes && quality > 5)

    val compressedFile = File(file.parent, "compressed_${file.name}")
    compressedFile.writeBytes(outputStream.toByteArray())

    return compressedFile
}

