package com.scanlibraryscanner.idCardScanner.helpers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class PDFWriterUtil {
    private val document = PdfDocument()
    suspend fun addFile(bitmapFile: File) {
        withContext(Dispatchers.IO){
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmapSt = BitmapFactory.decodeFile(bitmapFile.absolutePath, options)
            val pageInfo = PageInfo.Builder(bitmapSt.width, bitmapSt.height+100, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()
            paint.color = Color.parseColor("#ffffff")
            canvas.drawPaint(paint)
            canvas.drawBitmap(bitmapSt, 0f, 0f, null)
            document.finishPage(page)
        }
    }

    fun addBitmap(bitmap: Bitmap) {

        val pageInfo = PageInfo.Builder(bitmap.width + 100, bitmap.height + 200, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        paint.color = Color.parseColor("#ffffff")
        canvas.drawPaint(paint)
        canvas.drawBitmap(bitmap, 20f, 20f, null)
        document.finishPage(page)
    }

    @Throws(IOException::class)
    fun write(out: FileOutputStream?) {
        document.writeTo(out)
    }

    val pageCount: Int
        get() = document.pages.size

    @Throws(IOException::class)
    fun close() {
        document.close()
    }
}


/*
 return when (ExifInterface(contentResolver.run { openInputStream(selectedPhotoUri) }).getAttributeInt(
            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
            ExifInterface.ORIENTATION_ROTATE_90 ->
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, Matrix().apply {
                    postRotate(90F) }, true)
            ExifInterface.ORIENTATION_ROTATE_180 ->
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, Matrix().apply {
                    postRotate(180F) }, true)
            ExifInterface.ORIENTATION_ROTATE_270 ->
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, Matrix().apply {
                    postRotate(270F) }, true)
            else -> bitmap
 */
