package com.example.alldocumentsreaderimagescanner.converter.util

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import com.example.alldocumentsreaderimagescanner.converter.util.FileUtilsForFd
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder

object FileUtilsForFd {
    private const val EOF = -1
    private const val DEFAULT_BUFFER_SIZE = 1024 * 4
    @Throws(IOException::class)
    fun createTempFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = getFileName(context, uri)
        val splitName = splitFileName(fileName)
        var tempFile = File.createTempFile(splitName[0], splitName[1])
        tempFile = rename(tempFile, fileName)
        tempFile.deleteOnExit()
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(tempFile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        if (inputStream != null) {
            copy(inputStream, out)
            inputStream.close()
        }
        out?.close()
        return tempFile
    }

    @Throws(IOException::class)
    fun pathCreateFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = getFileName(context, uri)
        val splitName = splitFileName(fileName)
        val builder = StringBuilder()
        builder.append(context.getExternalFilesDir(null))
        builder.append("/")
        builder.append("PDF Converter")
        val directory = File(builder.toString())
        if (!directory.exists()) {
            directory.mkdirs()
        }
        //        StringBuilder builderCreateFile =new java.lang.StringBuilder();
//        builder.append(context.getExternalFilesDir(null));
//        builder.append("/");
//        builder.append("VideoEditor");
        var tempFile = File(directory, fileName)
        //        File tempFile = new File(context.getExternalFilesDir(splitName[0])).createTempFile(splitName[0], splitName[1]);
        tempFile = rename(tempFile, fileName)
        tempFile.deleteOnExit()
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(tempFile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        if (inputStream != null) {
            copy(inputStream, out)
            inputStream.close()
        }
        out?.close()
        return tempFile
    }

    private fun splitFileName(fileName: String?): Array<String?> {
        var name = fileName
        var extension: String? = ""
        val i = fileName!!.lastIndexOf(".")
        if (i != -1) {
            name = fileName.substring(0, i)
            extension = fileName.substring(i)
        }
        return arrayOf(name, extension)
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf(File.separator)
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun rename(file: File, newName: String?): File {
        val newFile = File(file.parent, newName)
        if (newFile != file) {
            if (newFile.exists() && newFile.delete()) {
                Log.d("FileUtil", "Delete old $newName file")
            }
            if (file.renameTo(newFile)) {
                Log.d("FileUtil", "Rename file to $newName")
            }
        }
        return newFile
    }

    @Throws(IOException::class)
    private fun copy(input: InputStream, output: OutputStream?): Long {
        var count: Long = 0
        var n: Int
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (EOF != input.read(buffer).also { n = it }) {
            output!!.write(buffer, 0, n)
            count += n.toLong()
        }
        return count
    }

    fun deleteFromUri(file: File, context: Context): Boolean {
        var checking = false
        val projection = arrayOf(MediaStore.Images.Media._ID)

// Match on the file path
        val selection = MediaStore.Images.Media.DATA + " = ?"
        val selectionArgs = arrayOf(file.absolutePath)

// Query for the ID of the media matching the file path
        val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val contentResolver = context.contentResolver
        val c = contentResolver.query(queryUri, projection, selection, selectionArgs, null)
        checking = if (c!!.moveToFirst()) {
            // We found the ID. Deleting the item via the content provider will also remove the file
            val id =
                c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
            val deleteUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )
            contentResolver.delete(deleteUri, null, null)
            true
        } else {
            false
            // File not found in media store DB
        }
        c.close()
        return checking
    }
}