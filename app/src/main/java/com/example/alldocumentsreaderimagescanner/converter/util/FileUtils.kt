package com.example.alldocumentsreaderimagescanner.converter.util


import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.converter.interfaces.IConsumer
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.AUTHORITY_APP
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.PATH_SEPARATOR
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.STORAGE_LOCATION
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.pdfExtension
import com.example.alldocumentsreaderimagescanner.reader.pdfui.PdfViewerActivity
import com.example.alldocumentsreaderimagescanner.utils.MyPreference
import java.io.File
import java.util.*

class FileUtils(private val mContext: Activity) {

    enum class FileType {
        e_PDF
    }

    /**
     * Emails the desired PDF using application of choice by user
     *
     * @param file - the file to be shared
     */
    fun shareFile(file: File?) {
        val uri = FileProvider.getUriForFile(mContext, AUTHORITY_APP, file!!)
        val uris = ArrayList<Uri>()
        uris.add(uri)
        shareFile(uris)
    }

    /**
     * Share the desired PDFs using application of choice by user
     *
     * @param files - the list of files to be shared
     */
    fun shareMultipleFiles(files: List<File?>) {
        val uris = ArrayList<Uri>()
        for (file in files) {
            val uri = FileProvider.getUriForFile(mContext, AUTHORITY_APP, file!!)
            uris.add(uri)
        }
        shareFile(uris)
    }

    /**
     * Emails the desired PDF using application of choice by user
     *
     * @param uris - list of uris to be shared
     */
    private fun shareFile(uris: ArrayList<Uri>) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND_MULTIPLE
        intent.putExtra(
            Intent.EXTRA_TEXT,
            mContext.getString(R.string.i_have_attached_pdfs_to_this_message)
        )
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "application/pdf"
        mContext.startActivity(Intent.createChooser(intent, "Chooser"))
    }

    /**
     * opens a file in appropriate application
     *
     * @param path - path of the file to be opened
     */
    fun openFile(path: String?, fileType: FileType) {
        if (path == null) {
            StringUtils.getInstance().showSnackbar(mContext, R.string.error_path_not_found)
            return
        }
        if (fileType == FileType.e_PDF) openPdf(path)
    }

    /**
     * This function is used to open the created file
     * applications on the device.
     *
     * @param path - file path
     */
    private fun openPdf(path: String) {
        mContext.startActivity(
            Intent(mContext, PdfViewerActivity::class.java).putExtra(
                "filePath",
                path
            )
        )
    }

    private fun openFileInternal(path: String, dataType: String) {
        val file = File(path)
        val target = Intent(Intent.ACTION_VIEW)
        target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        try {
            val uri = FileProvider.getUriForFile(mContext, AUTHORITY_APP, file)
            target.setDataAndType(uri, dataType)
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            openIntent(Intent.createChooser(target, mContext.getString(R.string.open_file)))
        } catch (e: Exception) {
            StringUtils.getInstance().showSnackbar(mContext, R.string.error_open_file)
        }
    }

    private fun openIntent(intent: Intent) {
        try {
            mContext.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            StringUtils.getInstance().showSnackbar(mContext, R.string.snackbar_no_pdf_app)
        }
    }

    /**
     * Checks if the new file already exists.
     *
     * @param finalOutputFile Path of pdf file to check
     * @param mFile           File List of all PDFs
     * @return Number to be added finally in the name to avoid overwrite
     */
    private fun checkRepeat(finalOutputFile: String, mFile: List<File>): Int {
        var flag = true
        var append = 0
        while (flag) {
            append++
            val name = finalOutputFile.replace(
                mContext.getString(R.string.pdf_ext),
                append.toString() + mContext.getString(R.string.pdf_ext)
            )
            flag = mFile.contains(File(name))
        }
        return append
    }

    /**
     * Get real image path from uri.
     *
     * @param uri - uri of the image
     * @return - real path of the image file on device
     */
    fun getUriRealPath(uri: Uri?): String? {
        return if (uri == null || FileUriUtils.getInstance()
                .isWhatsappImage(
                    uri.authority!!
                )
        ) null else FileUriUtils.getInstance()
            .getUriRealPathAboveKitkat(mContext, uri)
    }

    /***
     * Check if file already exists in pdf_dir
     * @param mFileName - Name of the file
     * @return true if file exists else false
     */
    fun isFileExist(mFileName: String): Boolean {

        val path = MyPreference.with(mContext).getString(
            STORAGE_LOCATION,
            StringUtils.getInstance().defaultStorageLocation
        ) + mFileName
        val file = File(path)
        return file.exists()
    }

    /**
     * Extracts file name from the URI
     *
     * @param uri - file uri
     * @return - extracted filename
     */
    fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        val scheme = uri.scheme ?: return null
        if (scheme == "file") {
            return uri.lastPathSegment
        } else if (scheme == "content") {
            val cursor = mContext.contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst() && cursor.count != 0) {
                    val columnIndex =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                    fileName = cursor.getString(columnIndex)
                }
                cursor.close()
            }
        }
        return fileName
    }

    /**
     * Returns name of the last file with "_pdf" suffix.
     *
     * @param filesPath - ArrayList of image paths
     * @return fileName with _pdf suffix
     */
    fun getLastFileName(filesPath: ArrayList<String>): String {
        if (filesPath.size == 0) return ""
        val lastSelectedFilePath = filesPath[filesPath.size - 1]
        val nameWithoutExt = stripExtension(getFileNameWithoutExtension(lastSelectedFilePath))
        return nameWithoutExt + mContext.getString(R.string.pdf_suffix)
    }

    /**
     * Returns the filename without its extension
     *
     * @param fileNameWithExt fileName with extension. Ex: androidDev.jpg
     * @return fileName without extension. Ex: androidDev
     */
    fun stripExtension(fileNameWithExt: String?): String? {
        // Handle null case specially.
        if (fileNameWithExt == null) return null

        // Get position of last '.'.
        val pos = fileNameWithExt.lastIndexOf(".")

        // If there wasn't any '.' just return the string as is.
        return if (pos == -1) fileNameWithExt else fileNameWithExt.substring(0, pos)

        // Otherwise return the string, up to the dot.
    }


    fun getUniqueFileName(fileName: String): String {
        var outputFileName = fileName
        val file = File(outputFileName)
        if (!isFileExist(file.name)) return outputFileName
        val parentFile = file.parentFile
        if (parentFile != null) {
            val listFiles = parentFile.listFiles()
            if (listFiles != null) {
                val append = checkRepeat(outputFileName, Arrays.asList(*listFiles))
                outputFileName = outputFileName.replace(
                    mContext.getString(R.string.pdf_ext),
                    append.toString() + mContext.resources.getString(R.string.pdf_ext)
                )
            }
        }
        return outputFileName
    }

    fun getFileChooser(): Intent? {
        val folderPath = Environment.getExternalStorageDirectory().toString() + "/"
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        val myUri = Uri.parse(folderPath)
        intent.setDataAndType(myUri, mContext.getString(R.string.pdf_type))
        return Intent.createChooser(intent, mContext.getString(R.string.merge_select_file))
    }

    fun openSaveDialog(preFillName: String?, ext: String, saveMethod: IConsumer<String>) {
        val builder = DialogUtils.getInstance().createCustomDialog(
            mContext,
            R.string.creating_pdf, R.string.enter_file_name
        )
        builder.input(
            mContext.getString(R.string.example), preFillName
        ) { _: MaterialDialog?, input: CharSequence ->
            if (StringUtils.getInstance().isEmpty(input)) {
                StringUtils.getInstance().showSnackbar(mContext, R.string.snackbar_name_not_blank)
            } else {
                val filename = input.toString()
                if (!isFileExist(filename + ext)) {
                    saveMethod.accept(filename)
                } else {
                    val builder2 =
                        DialogUtils.getInstance().createOverwriteDialog(mContext)
                    builder2.onPositive { _: MaterialDialog?, _: DialogAction? ->
                        saveMethod.accept(
                            filename
                        )
                    }
                        .onNegative { _: MaterialDialog?, _: DialogAction? ->
                            openSaveDialog(
                                preFillName,
                                ext,
                                saveMethod
                            )
                        }.show()
                }
            }
        }.show()
    }

    companion object {
        /**
         * Extracts file name from the path
         *
         * @param path - file path
         * @return - extracted filename
         */
        fun getFileName(path: String?): String? {
            if (path == null) return null
            val index: Int = path.lastIndexOf(PATH_SEPARATOR)
            return if (index < path.length) path.substring(index + 1) else null
        }

        /**
         * Extracts file name from the URI
         *
         * @param path - file path
         * @return - extracted filename without extension
         */
        fun getFileNameWithoutExtension(path: String?): String? {
            if (path == null || path.lastIndexOf(PATH_SEPARATOR) == -1) return path
            var filename: String = path.substring(path.lastIndexOf(PATH_SEPARATOR) + 1)
            filename = filename.replace(pdfExtension, "")
            return filename
        }

        /**
         * Extracts directory path from full file path
         *
         * @param path absolute path of the file
         * @return absolute path of file directory
         */
        fun getFileDirectoryPath(path: String): String {
            return path.substring(0, path.lastIndexOf(PATH_SEPARATOR) + 1)
        }


    }

}