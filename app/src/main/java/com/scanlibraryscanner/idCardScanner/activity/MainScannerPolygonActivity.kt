package com.scanlibraryscanner.idCardScanner.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.databinding.ActivityMainScannerBinding
import com.scanlibrary.ScanActivity
import com.scanlibrary.ScanConstants
import com.scanlibraryscanner.GetImageUriFromBitmap
import com.scanlibraryscanner.idCardScanner.BitmapUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


public class MainScannerPolygonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainScannerBinding
    private var original: Bitmap? = null
    private var progressDialog: ProgressDialog? = null
    private var tempBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            original = getBitmap(getUri())
            tempBitmap = original
            binding.ivPreviewCrop.setImageToCrop(original)
            binding.ivPreviewCrop.setFullImgCrop()
        } catch (e: Exception) {
            finish()
        }

        binding.scanButton.setOnClickListener {
            isGraySelected = false
            cropImages()
        }
        binding.retake.setOnClickListener {
            isGraySelected = false
            super.onBackPressed()
        }
        binding.rotateLeft.setOnClickListener {
            isGraySelected = false
            rotateDoc()
        }
        binding.llGrayShader.setOnClickListener {
            sharpBlackEffect()
        }
    }

    fun rotateDoc() {
        val bitmap: Bitmap? = original
        val matrix = Matrix()
        matrix.postRotate(90.0f)
        val createBitmap =
            Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)
        original?.recycle()
        System.gc()
        original = createBitmap
        binding.ivPreviewCrop.setImageToCrop(original)
        binding.ivPreviewCrop.setFullImgCrop()
    }

    var isGraySelected: Boolean = false
    fun sharpBlackEffect() {
        if (!isGraySelected) {
            isGraySelected=true
            showProgressDialog()
            AsyncTask.execute(object : Runnable {
                override fun run() {
                    try {
                        tempBitmap = ScanActivity.getGrayBitmap(original)
                    } catch (e: OutOfMemoryError) {
                        runOnUiThread(object : Runnable {
                            override fun run() {
                                tempBitmap = original
                                binding.ivPreviewCrop.setImageBitmap(original)
                                e.printStackTrace()
                                dismissProgressDialog()
                                binding.llGrayShader.setBackgroundColor(
                                    ContextCompat.getColor(
                                        this@MainScannerPolygonActivity,
                                        R.color.black
                                    )
                                )
                            }
                        })
                    }
                    runOnUiThread(object : Runnable {
                        override fun run() {
                            binding.ivPreviewCrop.setImageBitmap(tempBitmap)
//                            binding.llGrayShader.setBackgroundColor(
//                                ContextCompat.getColor(
//                                    this@MainScannerPolygonActivity,
//                                    R.color.gray
//                                )
//                            )
                            dismissProgressDialog()
                        }
                    })
                }
            })
        } else {
            isGraySelected=false
            tempBitmap = original
            binding.ivPreviewCrop.setImageBitmap(tempBitmap)
            binding.llGrayShader.setBackgroundColor(
                ContextCompat.getColor(
                    this@MainScannerPolygonActivity,
                    R.color.black
                )
            )
        }
    }

    private fun getBitmap(selectedimg: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        val contentResolver: ContentResolver = contentResolver
        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedimg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap

    }

    private fun getUri(): Uri {
        return intent.getParcelableExtra(ScanConstants.SELECTED_BITMAP)!!
    }

    private fun getIntentPreference(): Int {
        return intent.getIntExtra(ScanConstants.OPEN_INTENT_PREFERENCE, 0)
    }

    private fun showProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setIndeterminate(true)
        progressDialog!!.setMessage("Applying Filter...")
        progressDialog!!.setCancelable(true)
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.show()
    }


    fun dismissProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing()) {
            progressDialog!!.dismiss()
        }
    }

    var mCroppedFile: File? = null
    fun cropImages() {
        if (binding.ivPreviewCrop.canRightCrop()) {
            tempBitmap = binding.ivPreviewCrop.crop()
            if (original != null) {
                sendCropImage()
            } else {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        } else {
            Toast.makeText(
                this@MainScannerPolygonActivity,
                "cannot crop correctly",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    fun sendCropImage() {
        try {
            val data = Intent()
            var bitmap = tempBitmap
            if (bitmap == null) {
                bitmap = original
            }
//                    val uri = getImageUriFromBitmap(activity!!, bitmap!!)
            val uri = bitmapToUri(bitmap!!)
            data.putExtra(ScanConstants.SCANNED_RESULT, uri)
            data.putExtra(ScanConstants.SCAN_MORE, true)
            data.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, getIntentPreference())
            setResult(RESULT_OK, data)
            original!!.recycle()
            System.gc()
            runOnUiThread {
//                val intent = Intent(this@MainScannerActivity, IdCardScannerActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                startActivity(intent)
                finish()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun bitmapToUri(bitmap: Bitmap): Uri {
        // Get the context wrapper

        val dirPath = getExternalFilesDir(GetImageUriFromBitmap.URI_Tmp)

        // Initialize a new file instance to save bitmap object
        val file = File(dirPath, "${UUID.randomUUID()}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val u = getUriFromFile(file)
        return u!!
    }

    fun getUriFromFile(file: File): Uri? {
        return FileProvider.getUriForFile(
            this,
            getPackageName().toString() + ".provider",
            file
        )
    }

    private fun saveImage(bitmap: Bitmap, saveFile: File) {
        try {
            val fos = FileOutputStream(saveFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun calculateSampleSize(options: BitmapFactory.Options): Int {
        val outHeight: Int = options.outHeight
        val outWidth: Int = options.outWidth
        var sampleSize = 1
        val destHeight = 1000
        val destWidth = 1000
        if (outHeight > destHeight || outWidth > destHeight) {
            sampleSize = if (outHeight > outWidth) {
                outHeight / destHeight
            } else {
                outWidth / destWidth
            }
        }
        if (sampleSize < 1) {
            sampleSize = 1
        }
        return sampleSize
    }


}