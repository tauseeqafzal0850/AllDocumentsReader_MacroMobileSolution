package com.example.alldocumentsreaderimagescanner.reader.models

import android.annotation.SuppressLint
import android.app.Application
import android.app.RecoverableSecurityException
import android.content.Context
import android.content.IntentSender
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.alldocumentsreaderimagescanner.utils.Constant
import com.example.alldocumentsreaderimagescanner.utils.Constant.isCollumNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.concurrent.TimeUnit


class ListViewModel(application: Application) : AndroidViewModel(application) {

    private var allFileList: ArrayList<AllFileListModel>? = null
    private var pdfList: ArrayList<AllFileListModel>? = null
    private var wordList: ArrayList<AllFileListModel>? = null
    private var excelList: ArrayList<AllFileListModel>? = null
    private var pptList: ArrayList<AllFileListModel>? = null
    private var textList: ArrayList<AllFileListModel>? = null
    var deleteItemResult: MutableLiveData<Int> = MutableLiveData()
    private var pendingDeleteImage: AllFileListModel? = null
    private val _permissionNeededForDelete = MutableLiveData<IntentSender?>()
    val permissionNeededForDelete: LiveData<IntentSender?> = _permissionNeededForDelete
    private var contentObserver: ContentObserver? = null

    init {

        allFileList = ArrayList()
        pdfList = ArrayList()
        wordList = ArrayList()
        excelList = ArrayList()
        pptList = ArrayList()
        textList = ArrayList()
    }


    private val projection = arrayOf(
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.DATE_ADDED,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.MIME_TYPE
    )
    private val sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
    private val selection = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"
    private val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Files.getContentUri("external")
    }
    private val collectionString:String= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.VOLUME_EXTERNAL
    } else {
        "external"
    }
    private val selectionArgsPdf =
        arrayOf(MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf"))
    private val selectionArgsXls =
        arrayOf(MimeTypeMap.getSingleton().getMimeTypeFromExtension("xls"))
    private val selectionArgsXlsX =
        arrayOf(MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx"))
    private val selectionArgsWord =
        arrayOf(MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc"))
    private val selectionArgsWordx =
        arrayOf(MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx"))
    private val selectionArgsPpt =
        arrayOf(MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt"))
    private val selectionArgsPptx =
        arrayOf(MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx"))
    private val selectionArgsTxt =
        arrayOf(MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt"))


    public fun deleteImage(image: AllFileListModel,context: Context) {
//        if (contentObserver == null) {
//            contentObserver = context.contentResolver.registerObserver(
//                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//                    ) {
//            }
//        }
        viewModelScope.launch {
            performDeleteImage(image,context)
        }
    }
    fun deletePendingImage(context: Context) {
        pendingDeleteImage?.let { image ->
            pendingDeleteImage = null
            deleteImage(image,context)
        }
    }
    private suspend fun performDeleteImage(image: AllFileListModel, context: Context) {
        withContext(Dispatchers.IO) {
            try {
                /**
                 * In [Build.VERSION_CODES.Q] and above, it isn't possible to modify
                 * or delete items in MediaStore directly, and explicit permission
                 * must usually be obtained to do this.
                 *
                 * The way it works is the OS will throw a [RecoverableSecurityException],
                 * which we can catch here. Inside there's an [IntentSender] which the
                 * activity can use to prompt the user to grant permission to the item
                 * so it can be either updated or deleted.
                 */
//               val id=getApplication<Application>().contentResolver.delete(
//                    image.fileUri,
//                    null,
//                    null)
//                val boolean=try {
//                    DocumentsContract.deleteDocument(getApplication<Application>().contentResolver, image.fileUri)
//                } catch (e: Exception) {
//                    Log.e(TAG, "performDeleteImage: ${e.message}", )
//                    false
//                }
                val id=getApplication<Application>().contentResolver.delete(
                    image.fileUri,
                    "${MediaStore.Files.FileColumns._ID} = ?",
                    arrayOf(image.id.toString()))

                deleteItemResult.postValue(id)
            } catch (securityException: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableSecurityException =
                        securityException as? RecoverableSecurityException
                            ?: throw securityException

                    // Signal to the Activity that it needs to request permission and
                    // try the delete again if it succeeds.
                    pendingDeleteImage = image
                    _permissionNeededForDelete.postValue(
                        recoverableSecurityException.userAction.actionIntent.intentSender
                    )
                } else {
                    throw securityException
                }
            }
        }
    }

    /**
     * Convenience method to convert a day/month/year date into a UNIX timestamp.
     *
     * We're suppressing the lint warning because we're not actually using the date formatter
     * to format the date to display, just to specify a format to use to parse it, and so the
     * locale warning doesn't apply.
     */
    @Suppress("SameParameterValue")
    @SuppressLint("SimpleDateFormat")
    private fun dateToTimestamp(day: Int, month: Int, year: Int): Long =
        SimpleDateFormat("dd.MM.yyyy").let { formatter ->
            TimeUnit.MICROSECONDS.toSeconds(formatter.parse("$day.$month.$year")?.time ?: 0)
        }

    @SuppressLint("Range")
    suspend fun getPdfList(context: Context): ArrayList<AllFileListModel>? = withContext(IO) {
        async {
            try {
                context.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    selectionArgsPdf,
                    sortOrder
                ).use { cursor ->
                    cursor?.let {
                        if (it.moveToFirst()) {
                            val columnData = it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                            var columnName =
                                it.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                            val cursorrange=it.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)
                            val id = it.getColumnIndex(MediaStore.Video.Media._ID)
                            do {
                                if (it.getString(columnName) == null) {
                                    isCollumNull = true
                                    columnName = 1
                                }
                                val uri= MediaStore.Files.getContentUri(collectionString,it.getLong(id))
                                val columnDate =if (cursorrange==-1){
                                    convertLongToDate(1000)
                                }else{
                                    convertLongToDate(it.getLong(cursorrange))
                                }
                                if (File(it.getString(columnData)).length() > 1024)
                                    pdfList?.add(
                                        AllFileListModel(cursor.getLong(id),
                                            it.getString(columnName),
                                            it.getString(columnData),
                                             columnDate,uri
                                        )
                                    )

                            } while (it.moveToNext())
                        }
                        if (pdfList!!.isNotEmpty()) {
                            Constant.pdfSizeCount = pdfList!!.size
                        }else
                            Constant.pdfSizeCount=0
                        cursor.close()
                    }
                }

                return@async pdfList
            } catch (e: Exception) {
                Log.e(TAG, "getPdfList: ${e.message}")
                null
            }
        }
    }.await()
    fun convertLongToDate(time: Long): String =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("dd MM yyyy").format(
                Instant.ofEpochMilli(time*1000)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate())
        } else {
            SimpleDateFormat("dd MM yyyy").format(
                Date(time * 1000)
            )
        }
    suspend fun getExcelList(context: Context): ArrayList<AllFileListModel>? = withContext(IO) {

        async {
            try {
                context.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    selectionArgsXls,
                    sortOrder
                )
                    .use { cursor ->
                        cursor?.let {
                            if (it.moveToFirst()) {
                                val columnData =
                                    it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                                val columnName =
                                    it.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                                val cursorrange=it.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)

                                val id = it.getColumnIndex(MediaStore.Video.Media._ID)
                                do {
                                    val uri= MediaStore.Files.getContentUri(collectionString,it.getLong(id))
                                    val columnDate =if (cursorrange==-1){
                                        convertLongToDate(1000)
                                    }else{
                                        convertLongToDate(it.getLong(cursorrange))
                                    }
                                    if (File(it.getString(columnData)).length() > 1024)
                                        try {
                                            excelList?.add(
                                                AllFileListModel(cursor.getLong(id),
                                                    it.getString(columnName),
                                                    it.getString(columnData),
                                                    columnDate,uri
                                                )
                                            )
                                        } catch (e: Exception) {
                                            Log.e(TAG, "getXlsList: ${e.message}")
                                        }

                                } while (it.moveToNext())
                            }
                            cursor.close()
                        }
                    }
                context.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    selectionArgsXlsX,
                    sortOrder
                )
                    .use { cursor ->
                        cursor?.let {
                            if (it.moveToFirst()) {
                                val columnData =
                                    it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                                val columnName =
                                    it.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                                val cursorrange=it.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)

                                val id = it.getColumnIndex(MediaStore.Video.Media._ID)
                                do {
                                    val uri= MediaStore.Files.getContentUri(collectionString,it.getLong(id))
                                    val columnDate =if (cursorrange==-1){
                                        convertLongToDate(1000)
                                    }else{
                                        convertLongToDate(it.getLong(cursorrange))
                                    }
                                    if (File(it.getString(columnData)).length() > 1024)
                                        try {
                                            excelList?.add(
                                                AllFileListModel(cursor.getLong(id),
                                                    it.getString(columnName),
                                                    it.getString(columnData),
                                                    columnDate,uri
                                                )
                                            )
                                        } catch (e: Exception) {
                                            Log.e(TAG, "excelxList: ${e.message}")

                                        }

                                } while (it.moveToNext())
                            }
                            if (excelList!!.isNotEmpty()) {
                                Constant.excelSizeCount = excelList!!.size
                            }else
                                Constant.excelSizeCount=0

                            cursor.close()
                        }
                    }
                return@async excelList
            } catch (e: Exception) {
                Log.e("getExcelList", "${e.message}")
                null
            }

        }
    }.await()

    suspend fun getWordList(context: Context): ArrayList<AllFileListModel>? = withContext(IO) {

        async {
            try {
                context.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    selectionArgsWord,
                    sortOrder
                )
                    .use { cursor ->
                        cursor?.let {
                            if (it.moveToFirst()) {
                                val columnData =
                                    it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                                val columnName =
                                    it.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                                val cursorrange=it.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)

                                val id = it.getColumnIndex(MediaStore.Video.Media._ID)
                                do {
                                    val uri= MediaStore.Files.getContentUri(collectionString,it.getLong(id))
                                    val columnDate =if (cursorrange==-1){
                                        convertLongToDate(1000)
                                    }else{
                                        convertLongToDate(it.getLong(cursorrange))
                                    }
                                    if (File(it.getString(columnData)).length() > 1024)
                                        try {
                                            wordList?.add(
                                                AllFileListModel(cursor.getLong(id),
                                                    it.getString(columnName),
                                                    it.getString(columnData),
                                                    columnDate,uri
                                                )
                                            )
                                        } catch (e: Exception) {
                                            Log.e(TAG, "getWordList: ${e.message}")

                                        }

                                } while (it.moveToNext())
                            }

                            cursor.close()
                        }
                    }
                context.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    selectionArgsWordx,
                    sortOrder
                )
                    .use { cursor ->
                        cursor?.let {
                            if (it.moveToFirst()) {
                                val columnData =
                                    it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                                val columnName =
                                    it.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                                val cursorrange=it.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)

                                val id = it.getColumnIndex(MediaStore.Video.Media._ID)
                                do {
                                    val uri= MediaStore.Files.getContentUri(collectionString,it.getLong(id))
                                    val columnDate =if (cursorrange==-1){
                                        convertLongToDate(1000)
                                    }else{
                                        convertLongToDate(it.getLong(cursorrange))
                                    }
                                    if (File(it.getString(columnData)).length() > 1024)
                                        try {
                                            wordList?.add(
                                                AllFileListModel(cursor.getLong(id),
                                                    it.getString(columnName),
                                                    it.getString(columnData),
                                                    columnDate,uri
                                                )
                                            )
                                        } catch (e: Exception) {
                                            Log.e(TAG, "getwordxList: ${e.message}")
                                        }

                                } while (it.moveToNext())
                            }
                            cursor.close()
                        }
                    }
                if (wordList!!.isNotEmpty()) {
                    Constant.wordSizeCount = wordList!!.size
                }else
                    Constant.wordSizeCount=0
                return@async wordList
            } catch (e: Exception) {
                Log.e(TAG, "getWordList: ${e.message}")
                null
            }

        }
    }.await()

    suspend fun getPptList(context: Context): ArrayList<AllFileListModel>? = withContext(IO) {

        async {
            try {
                context.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    selectionArgsPpt,
                    sortOrder
                )
                    .use { cursor ->
                        cursor?.let {
                            if (it.moveToFirst()) {
                                val columnData =
                                    it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                                val columnName =
                                    it.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                                val cursorrange=it.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)

                                val id = it.getColumnIndex(MediaStore.Video.Media._ID)
                                do {
                                    val uri= MediaStore.Files.getContentUri(collectionString,it.getLong(id))
                                    val columnDate =if (cursorrange==-1){
                                        convertLongToDate(1000)
                                    }else{
                                        convertLongToDate(it.getLong(cursorrange))
                                    }
                                    if (File(it.getString(columnData)).length() > 1024)
                                        try {
                                            pptList?.add(
                                                AllFileListModel(cursor.getLong(id),
                                                    it.getString(columnName),
                                                    it.getString(columnData),
                                                    columnDate,uri
                                                )
                                            )
                                        } catch (e: Exception) {
                                        }

                                } while (it.moveToNext())
                            }
                            cursor.close()
                        }
                    }
                context.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    selectionArgsPptx,
                    sortOrder
                )
                    .use { cursor ->
                        cursor?.let {
                            if (it.moveToFirst()) {
                                val columnData =
                                    it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                                val columnName =
                                    it.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                                val cursorrange=it.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)

                                val id = it.getColumnIndex(MediaStore.Video.Media._ID)
                                do {
                                    val uri= MediaStore.Files.getContentUri(collectionString,it.getLong(id))
                                    val columnDate =if (cursorrange==-1){
                                        convertLongToDate(1000)
                                    }else{
                                        convertLongToDate(it.getLong(cursorrange))
                                    }
                                    if (File(it.getString(columnData)).length() > 1024)
                                        try {
                                            pptList?.add(
                                                AllFileListModel(cursor.getLong(id),
                                                    it.getString(columnName),
                                                    it.getString(columnData),
                                                    columnDate,uri
                                                )
                                            )
                                        } catch (e: Exception) {
                                            Log.e(TAG, "TxtList: ${e.message}")
                                        }

                                } while (it.moveToNext())
                            }
                            if (pptList!!.isNotEmpty()) {
                                Constant.pptSizeCount = pptList!!.size
                            }else
                                Constant.pptSizeCount=0
                            cursor.close()
                        }
                    }
                return@async pptList
            } catch (e: Exception) {
                Log.e(TAG, "getPPTList: ${e.message}")
                null
            }

        }
    }.await()

    suspend fun getTxtList(context: Context): ArrayList<AllFileListModel>? = withContext(IO) {
        async {
            try {
                context.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    selectionArgsTxt,
                    sortOrder
                )
                    .use { cursor ->
                        cursor?.let {
                            if (it.moveToFirst()) {
                                val columnData =
                                    it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                                val columnName =
                                    it.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                                val cursorrange=it.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)

                                val id = it.getColumnIndex(MediaStore.Video.Media._ID)
                                do {

                                    try {
                                        val uri= MediaStore.Files.getContentUri(collectionString,it.getLong(id))
                                        val columnDate =if (cursorrange==-1){
                                            convertLongToDate(1000)
                                        }else{
                                            convertLongToDate(it.getLong(cursorrange))
                                        }
                                        if (File(it.getString(columnData)).length() > 100)
                                            textList?.add(
                                                AllFileListModel(cursor.getLong(id),
                                                    it.getString(columnName),
                                                    it.getString(columnData),
                                                    columnDate,uri
                                                )
                                            )
                                    } catch (e: Exception) {
                                    }

                                } while (it.moveToNext())
                            }
                            if (textList!!.isNotEmpty()) {
                                Constant.txtSizeCount = textList!!.size
                            }else
                                Constant.txtSizeCount=0
                            cursor.close()
                        }
                    }
                return@async textList
            } catch (e: Exception) {
                Log.e(TAG, "getTxtList: ${e.message}")

                null
            }

        }
    }.await()

    suspend fun getAllList(context: Context): ArrayList<AllFileListModel>? = withContext(IO) {
        async {
            try {
                getPdfList(context)?.let {
                    allFileList?.addAll(it)
                }
                getExcelList(context)?.let {
                    allFileList?.addAll(it)
                }
                getWordList(context)?.let {
                    allFileList?.addAll(it)
                }
                getPptList(context)?.let {
                    allFileList?.addAll(it)
                }
                getTxtList(context)?.let {
                    allFileList?.addAll(it)
                }
                return@async allFileList
            } catch (e: Exception) {
                Log.e(TAG, "getAllList: ${e.message}")

                null
            }

        }
    }.await()

    fun onClear() {
        allFileList?.clear()
        pdfList?.clear()
        wordList?.clear()
        excelList?.clear()
        pptList?.clear()
        textList?.clear()
    }

    override fun onCleared() {
        Log.e(TAG, "onCleared: destory app")
        super.onCleared()
    }

}
private const val TAG = "ListViewModel"
