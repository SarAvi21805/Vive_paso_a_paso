package com.example.vivepasoapaso.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object ImageManager {

    private const val PROFILE_IMAGE_DIR = "profile_images"
    private const val PROFILE_IMAGE_NAME = "profile_picture.jpg"

    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = File(context.filesDir, PROFILE_IMAGE_DIR)

        //Crear directorio si no existe
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    fun getImageUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "com.example.vivepasoapaso.fileprovider",
            file
        )
    }

    fun saveProfileImage(context: Context, bitmap: Bitmap): Boolean {
        return try {
            val file = File(context.filesDir, PROFILE_IMAGE_NAME)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun saveProfileImageFromUri(context: Context, uri: Uri): Boolean {
        return try {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            saveProfileImage(context, bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun loadProfileImage(context: Context): Bitmap? {
        return try {
            val file = File(context.filesDir, PROFILE_IMAGE_NAME)
            if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteProfileImage(context: Context): Boolean {
        return try {
            val file = File(context.filesDir, PROFILE_IMAGE_NAME)
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //Función para verificar si el directorio de imágenes existe
    fun ensureProfileImageDir(context: Context): Boolean {
        val storageDir = File(context.filesDir, PROFILE_IMAGE_DIR)
        return if (!storageDir.exists()) {
            storageDir.mkdirs()
        } else {
            true
        }
    }
}