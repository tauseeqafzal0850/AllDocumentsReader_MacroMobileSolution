package com.example.alldocumentsreaderimagescanner.reader.models

import android.annotation.SuppressLint
import android.app.Application
import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.IntentSender
import android.database.ContentObserver
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class DeleteViewModel(application: Application) : AndroidViewModel(application) {
    private val _images = MutableLiveData<List<AllFileListModel>>()
    val images: LiveData<List<AllFileListModel>> get() = _images

    private var contentObserver: ContentObserver? = null

    private var pendingDeleteImage: AllFileListModel? = null
    private val _permissionNeededForDelete = MutableLiveData<IntentSender?>()
    val permissionNeededForDelete: LiveData<IntentSender?> = _permissionNeededForDelete

    /**
     * Performs a one shot load of images from [MediaStore.Video.Media.EXTERNAL_CONTENT_URI] into
     * the [_images] [LiveData] above.
     */
    fun loadImages() {
        viewModelScope.launch {
//            val imageList = queryImages()
//            _images.postValue(imageList)
            if (contentObserver == null) {
                contentObserver = getApplication<Application>().contentResolver.registerObserver(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                ) {
                    loadImages()
                }
            }
        }
    }

    fun deleteImage(image: AllFileListModel) {
        viewModelScope.launch {
            performDeleteImage(image)
        }
    }

    fun deletePendingImage() {
        pendingDeleteImage?.let { image ->
            pendingDeleteImage = null
            deleteImage(image)
        }
    }


//    private suspend fun queryImages(): List<AllFileListModel> {
//        val videos = mutableListOf<AllFileListModel>()
//        withContext(Dispatchers.IO) {
//            if (Build.VERSION.SDK_INT>Build.VERSION_CODES.Q) {
//                var projection = arrayOf(
//                    MediaStore.Video.Media._ID,
//                    MediaStore.Video.Media.RELATIVE_PATH,
//                    MediaStore.Video.Media.DISPLAY_NAME,
//                    MediaStore.Video.Media.DATA,
//                    MediaStore.Video.Media.SIZE,
//                    MediaStore.Video.Media.TITLE,
//                    MediaStore.Video.Media.DURATION,
//                    MediaStore.Video.Media.HEIGHT,
//                    MediaStore.Video.Media.WIDTH,
//                    MediaStore.Video.Media.ORIENTATION,
//                    MediaStore.Video.Media.MIME_TYPE,
//                    MediaStore.Video.Media.DATE_MODIFIED
//                )
//                projection += arrayOf(MediaStore.Images.Media.IS_FAVORITE)
//                val selection = "${MediaStore.Video.Media.DATE_ADDED} >= ?"
//                //val sortOrder = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"
//                val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"
//                val selectionArgs = arrayOf(
//                    // Release day of the G1. :)
//                    dateToTimestamp(day = 22, month = 10, year = 2008).toString()
//                )
//                getApplication<Application>().contentResolver.query(
//                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
//                    projection,
//                    selection,
//                    selectionArgs,
//                    sortOrder
//                )?.use { cursor ->
//
//                    while (cursor.moveToNext()) {
//                        try {
//                            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID))
//                            val size =
//                                cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
//                            val title =
//                                cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
//                            val data =
//                                cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
//                            val dateAdded =
//                                cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED))
//                            val videoSize =
//                                cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
//                            val duration =
//                                try {
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
//                                            .toLong()
//                                    } else {
//                                        0L
//                                    }
//                                } catch (e: Exception) {
//                                    0L
//                                }
//                            var d: Long=0L
//                            if (File(data).exists() && File(data).isFile) {
//                                val retriveer = MediaMetadataRetriever()
//                                retriveer.setDataSource(data)
//                                d = retriveer.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong()
//                            }else{
//                                d=0L
//                            }
////                        val thumbnail: Bitmap = ApplicationProvider.getApplicationContext<Context>()
////                            .getContentResolver().loadThumbnail(
////                                content - uri, Size(640, 480), null
////                            )
//
//                            val uri = ContentUris.withAppendedId(
//                                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
//                                id
//                            )
//
//                            // Discard invalid images that might exist on the device
//                            if (size == null) {
//                                continue
//                            }
//
//                            videos += AllFileListModel(
//                                id,
//                                title,
//                                duration,
//                                uri,
//                                data,
//                                data,
//                                dateAdded,
//                                id,
//                                videoSize
//                            )
//                        } catch (e: Exception) {
//                            //   Toast.makeText(context,"Error"+e,Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    cursor.close()
//                }
//            } else {
//                val projection = arrayOf(
//                    MediaStore.Video.Media._ID,
//                    MediaStore.Video.Media.DATA,
//                    MediaStore.Video.Media.DATE_ADDED,
//                    MediaStore.Video.Media.DISPLAY_NAME,
//                    MediaStore.Video.Media.SIZE,
//                    MediaStore.Video.Media.TITLE,
//                    MediaStore.Video.Media.DURATION,
//                    MediaStore.Video.Media.MIME_TYPE,
//                    MediaStore.Video.Media.DATE_MODIFIED
//                )
//                val selection = "${MediaStore.Video.Media.DATE_ADDED} >= ?"
//
//                val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"
//                val selectionArgs = arrayOf(
//                    // Release day of the G1. :)
//                    dateToTimestamp(day = 22, month = 10, year = 2008).toString()
//                )
//
//                getApplication<Application>().contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                    projection,
//                    selection,
//                    selectionArgs,
//                    sortOrder
//                )?.use { cursor ->
//
//                    while (cursor.moveToNext()) {
//
//                        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID))
//                        val size =
//                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
//                        val title =
//                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
//                        val data =
//                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
//                        val dateAdded =
//                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))
//                        val videoSize =
//                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
//                        var d: Long=0L
//                        /*if (File(data).exists() && File(data).isFile) {
//                            val retriveer = MediaMetadataRetriever()
//                            retriveer.setDataSource(data)
//                            d = retriveer.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong()
//                        }else{
//                            d=0L
//                        }*/
//                        val uri = ContentUris.withAppendedId(
//                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                            id
//                        )
//                        // Discard invalid images that might exist on the device
//                        if (size == null) {
//                            continue
//                        }
//
//                        videos += AllFileListModel(
//                            id,
//                            title,
//                            d,
//                            uri,
//                            data,
//                            data,
//                            dateAdded,
//                            id,
//                            videoSize
//                        )
//                    }
//
//                    cursor.close()
//                }
//
//
//            }
//        }
//        return videos
//    }

    private suspend fun performDeleteImage(image: AllFileListModel) {
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
                getApplication<Application>().contentResolver.delete(
                    image.fileUri,
                    "${MediaStore.Files.FileColumns._ID} = ?",
                    arrayOf(image.id.toString())
                )
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

    /**
     * Since we register a [ContentObserver], we want to unregister this when the `ViewModel`
     * is being released.
     */
    override fun onCleared() {
        contentObserver?.let {
            getApplication<Application>().contentResolver.unregisterContentObserver(it)
        }
    }
}

/**
 * Convenience extension method to register a [ContentObserver] given a lambda.
 */
fun ContentResolver.registerObserver(
    uri: Uri,
    observer: (selfChange: Boolean) -> Unit): ContentObserver {
    val contentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            observer(selfChange)
        }
    }
    registerContentObserver(uri, true, contentObserver)
    return contentObserver
}


private const val TAG = "MainActivityVM"