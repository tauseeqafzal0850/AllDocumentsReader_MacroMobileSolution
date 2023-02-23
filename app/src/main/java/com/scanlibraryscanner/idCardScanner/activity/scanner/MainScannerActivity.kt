package com.scanlibraryscanner.idCardScanner.activity.scanner

import android.app.ActivityOptions
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.example.alldocumentsreaderimagescanner.HomeActivity
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.ads.BannerActivity
import com.example.alldocumentsreaderimagescanner.converter.util.DialogUtils
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_DOCUMENTS_SCANNER_BANNER
import com.scanlibraryscanner.ScanConstants
import com.scanlibraryscanner.idCardScanner.activity.CameraViewerActivity
import com.scanlibraryscanner.idCardScanner.activity.MainScannerPolygonActivity
import com.scanlibraryscanner.idCardScanner.helpers.DialogUtil
import com.scanlibraryscanner.idCardScanner.helpers.DialogUtilCallback
import com.scanlibraryscanner.idCardScanner.helpers.FileWritingCallback
import com.scanlibraryscanner.idCardScanner.helpers.PDFWriterUtil
import kotlinx.android.synthetic.main.fragment_scanner.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainScannerActivity : BannerActivity() {

    private val scannedBitmaps: ArrayList<Uri> = ArrayList()
    private var mMaterialDialog: MaterialDialog? = null
    private fun mkdir(dirPath: String?) {
        val sd = this.getExternalFilesDir("$dirPath").toString()
        val storageDirectory = File(sd)
        if (!storageDirectory.exists()) {
            storageDirectory.mkdir()
        }
    }

    fun mkdir11(dirPath: String?): Boolean {
        val sd = "${Environment.getExternalStorageDirectory()}"
//        val sd = this.getExternalFilesDir("$dirPath").toString()
        val storageDirectory = File(sd, dirPath)
        if (!storageDirectory.exists()) {
            val a = storageDirectory.mkdirs()
            return a
        }
        return true
    }

    var doScanMore: Boolean? = false
    override fun onCreate(savedInstanceState: Bundle?) {

        setContentView(R.layout.fragment_scanner)
        super.onCreate(savedInstanceState)
        showBanner(CHECK_DOCUMENTS_SCANNER_BANNER)
        val baseStorageDirectory: String = ScanConstants.PDFScanner_tmp
        mkdir(baseStorageDirectory)

        val baseStagingDirectory: String = ScanConstants.PDFScanner_stage
        mkdir(baseStagingDirectory)

        val scanningTmpDirectory: String = ScanConstants.PDFScanner_scantmp
        mkdir(scanningTmpDirectory)

        val uriTemp: String = ScanConstants.URI_Tmp
        mkdir(uriTemp)

        val camera_img_temp: String = ScanConstants.PDF_Converter_tmp
        mkdir(camera_img_temp)

        btnCamera.setOnClickListener {
            openCamera()
        }
        btnGallery.setOnClickListener {
            openGallery()
        }

        backBtn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
            finish()
        }
        doScanMore = intent?.getBooleanExtra("doScanMore", false)

    }

    private fun openCamera() {
        scannedBitmaps.clear()
        val stagingDirPath = ScanConstants.PDFScanner_stage
        val scanningTmpDirectory = ScanConstants.PDFScanner_scantmp
        if (doScanMore == false) {
            clearDirectory(stagingDirPath)
            clearDirectory(scanningTmpDirectory)
        }
//        val intent = Intent(this, ScanActivity::class.java)
        val intent = Intent(this, CameraViewerActivity::class.java)
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA)

        val options = ActivityOptions.makeSceneTransitionAnimation(this)
        startActivityForResult(intent, ScanConstants.START_CAMERA_REQUEST_CODE, options.toBundle())
    }

    private fun openGallery() {
        scannedBitmaps.clear()
        val stagingDirPath = ScanConstants.PDFScanner_stage
        if (doScanMore == false) {
            clearDirectory(stagingDirPath)
        }
//        val intent = Intent(this, ScanActivity::class.java)
        val intent = Intent(this, CameraViewerActivity::class.java)
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_MEDIA)
        startActivityForResult(intent, ScanConstants.PICKFILE_REQUEST_CODE)

    }

    fun clearDirectory(dirPath: String?) {
        val sd = getExternalFilesDir("$dirPath").toString()
        val targetDirectory = File(sd)
        if (targetDirectory.listFiles() != null) {
            for (tempFile in targetDirectory.listFiles()) {
                tempFile.delete()
            }
        }
    }

    suspend fun writeBitmap(
        bitmap: Bitmap,
        baseDirectory: String,
        filename: String
    ) {
        withContext(Dispatchers.IO) {
            mkdir(baseDirectory)
            val sd = getExternalFilesDir(baseDirectory).toString()
            val dest = File(sd)

            val filePath = File.createTempFile(
                filename,  /* prefix */
                ".png",  /* suffix */
                dest /* directory */
            )
            try {
                val out = FileOutputStream(filePath)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }
        }
    }


    suspend fun writeFile(
        context: Context,
        baseDirectory: String,
        filename: String,
        callback: FileWritingCallback
    ) {
        withContext(Dispatchers.IO) {
            val sd = "${Environment.getExternalStorageDirectory()}/$baseDirectory"

            val dest = File(sd, filename)

            val out = FileOutputStream(dest)

            callback.write(out)
            out.flush()
            out.close()
            MediaScannerConnection.scanFile(
                this@MainScannerActivity, arrayOf(dest.path), arrayOf("application/pdf"),
                null
            )

        }

    }


    private suspend fun saveBitmap(bitmap: Bitmap, addMore: Boolean) {
        withContext(Dispatchers.IO) {
            val baseDirectory =
                if (addMore) ScanConstants.PDFScanner_stage else ScanConstants.PDFScanner_tmp

            val simpleDateFormat = SimpleDateFormat("ddMMyyyyhhmm", Locale.getDefault())
            val timestamp = simpleDateFormat.format(Date())
            if (addMore) {
                try {
                    val filename = "SCANNED_STG_$timestamp.png"
                    writeBitmap(bitmap, baseDirectory, filename)
                } catch (ioe: Exception) {
                    ioe.printStackTrace()
                }
            } else {
                DialogUtil.askUserFilaname(
                    this@MainScannerActivity,
                    null,
                    object : DialogUtilCallback {
                        override fun onSave(textValue: String?) {
                            try {
                                val itemName = textValue?.replace("[^a-zA-Z0-9\\s]".toRegex(), "")
                                val filename = "$itemName$timestamp.png"
                                lifecycleScope.launch {
                                    writeBitmap(bitmap, baseDirectory, filename)
                                }
                            } catch (ioe: Exception) {
                                ioe.printStackTrace()
                            }

                        }

                    })

            }
        }

    }

    private val TAG = "MainScannerFragment"


    fun getAllFiles(dirPath: String?): List<File> {
        val fileList: MutableList<File> = ArrayList()
        val sd = getExternalFilesDir("$dirPath").toString()
        val targetDirectory = File(sd)
        if (targetDirectory.listFiles() != null) {
            for (eachFile in targetDirectory.listFiles()) {
                fileList.add(eachFile)
            }
        }
        fileList.sortWith { f1, f2 -> f1.lastModified().compareTo(f2.lastModified()) }
        return fileList
    }

    private fun savePdf() {
//        Log.d(TAG, "savePdf: khan 11")
        val baseDirectory = ScanConstants.PDFScanner_tmp
        val sd = Environment.getExternalStorageDirectory()
        val simpleDateFormat = SimpleDateFormat("dd-MM-yy , hh-mm-ss", Locale.getDefault())
        val timestamp = simpleDateFormat.format(Date())
//        Log.d(TAG, "savePdf: khan 222")

        DialogUtil.askUserFilaname(
            this, null, object : DialogUtilCallback {

                override fun onSave(textValue: String?) {
                    lifecycleScope.launch {
                        try {

                            val pdfWriter = PDFWriterUtil()
                            val stagingDirPath = ScanConstants.PDFScanner_stage
                            val stagingFiles = getAllFiles(stagingDirPath)


                            for (stagedFile in stagingFiles) {
                                pdfWriter.addFile(stagedFile)
                            }


                            val itemName = textValue?.replace("[^a-zA-Z0-9\\s]".toRegex(), "")
                            val filename = "$itemName-$timestamp.pdf"
                            mkdir11(baseDirectory)


                            writeFile(
                                this@MainScannerActivity,
                                baseDirectory,
                                filename
                            ) { out ->
                                try {
                                    pdfWriter.write(out)
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }

//                            Log.d(TAG, "savePdf: khan 11")

                            clearDirectory(stagingDirPath)
                            clearDirectory(
                                ScanConstants.URI_Tmp
                            )
//                            Log.d(TAG, "savePdf: khan 12")

                            pdfWriter.close()
                            System.gc()
//                            Log.d(TAG, "savePdf: khan 13")

                        } catch (ioe: java.lang.Exception) {
                            ioe.printStackTrace()
                        }
                    }

                }
            })
    }


    private suspend fun getBitmap(selectedimg: Uri): Bitmap? = withContext(Dispatchers.IO) {
        val bitmap: Bitmap?
        val contentResolver: ContentResolver = contentResolver
        try {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(contentResolver, selectedimg)
            } else {
                val source = ImageDecoder.createSource(contentResolver, selectedimg)
                ImageDecoder.decodeBitmap(source)
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == ScanConstants.PICKFILE_REQUEST_CODE || requestCode == ScanConstants.START_CAMERA_REQUEST_CODE) &&
            resultCode == RESULT_OK
        ) {

            lifecycleScope.launch {
                val saveMode = if (data!!.extras!!.containsKey(ScanConstants.SAVE_PDF)) {
                    data.extras!!.getBoolean(ScanConstants.SAVE_PDF)
                } else {
                    false
                }
                if (saveMode) {
                    savePdf()
                } else {
                    mMaterialDialog =
                        DialogUtils.getInstance().createAnimationDialog(this@MainScannerActivity)
                    mMaterialDialog?.show()
                    val uri = data.extras!!.getParcelable<Uri>(ScanConstants.SCANNED_RESULT)
                    val doScanMore = data.extras!!.getBoolean(ScanConstants.SCAN_MORE)
                    val bitmap = getBitmap(uri!!)
                    saveBitmap(bitmap!!, doScanMore)
                    if (doScanMore) {
                        scannedBitmaps.add(uri)
                        val intent = Intent(this@MainScannerActivity, MultiPageActivity::class.java)
                        intent.putExtra(
                            ScanConstants.OPEN_INTENT_PREFERENCE,
                            ScanConstants.OPEN_CAMERA
                        )
                        try {
                            if (mMaterialDialog != null && mMaterialDialog!!.isShowing) mMaterialDialog!!.dismiss()
                            startActivityForResult(intent, ScanConstants.START_CAMERA_REQUEST_CODE)
                        } catch (e: Exception) {
                        }

                    }

                }
            }

        }

    }

    override fun onBackPressed() {
//        val i = Intent(this, HomeActivity::class.java)
//        finish()
//        startActivity(i)
        startActivity(Intent(this, HomeActivity::class.java).apply {
            flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
        finish()
    }


}