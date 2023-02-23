package com.scanlibraryscanner.idCardScanner.activity

import android.app.ActivityOptions
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.alldocumentsreaderimagescanner.R
import com.scanlibraryscanner.ScanConstants
import com.scanlibraryscanner.idCardScanner.helpers.DialogUtilCallback
import com.scanlibraryscanner.idCardScanner.helpers.PDFWriterUtil
import com.scanlibraryscanner.idCardScanner.helpers.DialogUtil
import com.scanlibraryscanner.idCardScanner.helpers.FileWritingCallback
import kotlinx.android.synthetic.main.activity_id_card_scanner.*
import kotlinx.android.synthetic.main.activity_id_card_scanner.backBtn
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class IdCardScannerActivity : AppCompatActivity() {

    private val scannedBitmaps: ArrayList<Uri> = ArrayList()


    private val empty = "".toUri()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_id_card_scanner)

        clickOnUi()

        scannedBitmaps.clear()
        scannedBitmaps.add(0, empty)
        scannedBitmaps.add(1, empty)


    }

    private fun clickOnUi() {

        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        frontImage.setOnClickListener { clickOnFrontImage(ScanConstants.frontCamera) }

        backImage.setOnClickListener { clickOnFrontImage(ScanConstants.backCamera) }


        convertIdCardToPdf.setOnClickListener {


//            Log.d("ArraySize3333", "onActivityResult: ${scannedBitmaps.size}")
            if (scannedBitmaps.size > 1) {

                if (scannedBitmaps.get(0) != empty && scannedBitmaps.get(1) != empty) {

                    convertToPdfFromList(1)
                } else {
                    Toast.makeText(
                        this,
                        "Please Select Front and Back Page to Continue.",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            } else {
                Toast.makeText(this, "Please Select Front and Back Page to Continue.", Toast.LENGTH_SHORT)
                    .show()
            }


        }

    }


    private fun clickOnFrontImage(cameraView: Int) {


//        val intent = Intent(this, ScanActivity::class.java)
        val intent = Intent(this, CameraViewerActivity::class.java)

        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, cameraView)
        val options = ActivityOptions.makeSceneTransitionAnimation(this)
        startActivityForResult(intent, ScanConstants.CameraActivityResult, options.toBundle())

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanConstants.CameraActivityResult &&
            resultCode == RESULT_OK
        ) {

            val uri = data?.extras!!.getParcelable<Uri>(ScanConstants.SCANNED_RESULT)

//            Log.d("ArraySize", "onActivityResult: ${scannedBitmaps.size}")

            val camertaType = data.extras!!.getInt(ScanConstants.OPEN_INTENT_PREFERENCE)

            if (camertaType == ScanConstants.frontCamera) {
                scannedBitmaps.removeAt(0)
                scannedBitmaps.add(0, uri!!)

//                Log.d("ArraySize111", "onActivityResult: ${scannedBitmaps.size}")

                front_file_screenshot.setImageURI(uri)

            } else if (camertaType == ScanConstants.backCamera) {
                scannedBitmaps.removeAt(1)
                scannedBitmaps.add(1, uri!!)

//                Log.d("ArraySize2222", "onActivityResult: ${scannedBitmaps.size}")

                back_file_screenshot.setImageURI(uri)
            }

        }
    }

    private fun convertToPdfFromList(i: Int) {
        val baseDirectory = ScanConstants.PDFScanner_tmp

//        Log.d("ArraySize4444444", "onActivityResult: ${scannedBitmaps.size}")
        val simpleDateFormat = SimpleDateFormat("dd-MM-yy , hh-mm-ss", Locale.getDefault())
        val timestamp = simpleDateFormat.format(Date())
        DialogUtil.askUserFilaname(
            this, null, object : DialogUtilCallback {
                override fun onSave(textValue: String?) {
                    try {
                        val pdfWriter = PDFWriterUtil()
//                        for (stagedFile in scannedBitmaps) {
                        val bit1 = getBitmap(scannedBitmaps.get(0))
                        val bit2 = getBitmap(scannedBitmaps.get(1))
                        pdfWriter.addBitmap(bit1!!)
                        pdfWriter.addBitmap(bit2!!)
//                        }
                        val itemName = textValue?.replace("[^a-zA-Z0-9\\s]".toRegex(), "")
                        val filename = "$itemName-$timestamp.pdf"
                        mkdir11("$baseDirectory")
                        writeFile(
                            this@IdCardScannerActivity,
                            "$baseDirectory",
                            filename
                        ) { out ->
                            try {
                                pdfWriter.write(out)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }

                        scannedBitmaps.add(0, empty)
                        scannedBitmaps.add(1, empty)
                        clearDirectory(
                            ScanConstants.URI_Tmp
                        )
                        pdfWriter.close()
                        System.gc()
                    } catch (ioe: IOException) {
                        ioe.printStackTrace()
                    }
                    finish()
                }
            })

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

    @Throws(IOException::class)
    fun writeFile(
        context: Context,
        baseDirectory: String,
        filename: String,
        callback: FileWritingCallback
    ) {
//        mkdir(baseDirectory)

        val sd = "${Environment.getExternalStorageDirectory()}/$baseDirectory"

//        val sd = this.getExternalFilesDir("$baseDirectory").toString()
        val dest = File(sd, filename)


        val out = FileOutputStream(dest)

        callback.write(out)
        out.flush()
        out.close()

        MediaScannerConnection.scanFile(context, arrayOf(dest.toString()), null,
            MediaScannerConnection.OnScanCompletedListener { path, uri ->
                Log.e("ExternalStorage", "Scanned $path:")
                Log.e("ExternalStorage", "-> uri=$uri")
            })
    }

    fun mkdir11(dirPath: String?): Boolean {
        val sd = "${Environment.getExternalStorageDirectory()}"
//        val sd = this.getExternalFilesDir("$dirPath").toString()
        val storageDirectory = File(sd, dirPath)
        if (!storageDirectory.exists()) {
            var a = storageDirectory.mkdirs()
            return a
        }
        return true
    }

    @Throws(IOException::class)
    private fun getBitmap(selectedimg: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        val contentResolver: ContentResolver = contentResolver
        try {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(contentResolver, selectedimg)
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, selectedimg)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bitmap
    }


}