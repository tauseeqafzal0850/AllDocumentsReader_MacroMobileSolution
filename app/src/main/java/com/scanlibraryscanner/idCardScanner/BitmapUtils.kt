package com.scanlibraryscanner.idCardScanner

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

public object BitmapUtils {
    public fun getBytes(bitmap: Bitmap): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun getImage(bArr: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(bArr, 0, bArr.size)
    }

    fun getUri(context: Context, bitmap: Bitmap): Uri? {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ByteArrayOutputStream())
        return Uri.parse(
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                bitmap,
                "Title",
                null as String?
            )
        )
    }

    @Throws(IOException::class)
    fun getBitmap(context: Context, uri: Uri?): Bitmap? {
        return MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }

    fun getURIFromFile(str: String?, activity: Activity): Uri? {
        return FileProvider.getUriForFile(activity, activity.packageName + ".provider", File(str))
    }

    fun getBitmapFromAsset(context: Context, str: String?): Bitmap? {
        return try {
            BitmapFactory.decodeStream(context.assets.open(str!!))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun overlay(bitmap: Bitmap, bitmap2: Bitmap?): Bitmap? {
        val createBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(createBitmap)
        canvas.drawBitmap(bitmap, Matrix(), null as Paint?)
        canvas.drawBitmap(bitmap2!!, Matrix(), null as Paint?)
        return createBitmap
    }
}
