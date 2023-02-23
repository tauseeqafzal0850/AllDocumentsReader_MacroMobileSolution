package com.scanlibraryscanner.idCardScanner.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.alldocumentsreaderimagescanner.R
import com.scanlibrary.ScanConstants
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraViewerActivity : AppCompatActivity() {
    private  val TAG = "PickImageFragment"
    private var fileUri: Uri? = null
    private val PDF_Converter_tmp = "PDF Converter/tmp"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_viewer)

        if (isIntentPreferenceSet()) {
            handleIntentPreference()
        } else {
            finish()
        }
    }

    private fun clearTempImages() {
        try {
            val tempFolder: File = File(getTempImagePath())
            if (tempFolder.listFiles().size > 0) {

                for (f in tempFolder.listFiles()) f.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleIntentPreference() {
        val preference: Int = getIntentPreference()
        if (preference == ScanConstants.OPEN_CAMERA) {
            openCamera()
        } else if (preference == ScanConstants.OPEN_MEDIA) {
            openMediaContent()
        } else {
            openCamera()
        }
    }

    private fun isIntentPreferenceSet(): Boolean {
        if(!intent.hasExtra(ScanConstants.OPEN_INTENT_PREFERENCE))
            return false
        val preference = intent.getIntExtra(ScanConstants.OPEN_INTENT_PREFERENCE, 0)
        return preference != 0
//        val preference = args.preferenceInt
//        return preference != 0
    }

    private fun getIntentPreference(): Int {
        return intent.getIntExtra(ScanConstants.OPEN_INTENT_PREFERENCE, 0)
//        return args.preferenceInt
    }


    private fun openMediaContent() {
        try {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
//            getResult.launch(intent)
            startActivityForResult(intent, ScanConstants.PICKFILE_REQUEST_CODE)
        } catch (e: Exception) {
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = createImageFile()
        val tempFileUri = FileProvider.getUriForFile(
            applicationContext,
            applicationContext.packageName
                .toString() + ".provider",  // As defined in Manifest
            file
        )
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri)
        try {
            startActivityForResult(cameraIntent, ScanConstants.START_CAMERA_REQUEST_CODE)
        } catch (e: Exception) {
        }

    }


    private fun getTempImagePath(): String {
        return getExternalFilesDir(PDF_Converter_tmp).toString()
    }

    private fun createImageFile(): File {
        clearTempImages()
        val timeStamp = SimpleDateFormat("ddMMyyyyhhmm").format(Date())


        val basePath = File(getTempImagePath())

        if (!basePath.exists()) {
            basePath.mkdir()
        }
        val file = File(getTempImagePath(), "IMG_$timeStamp.jpg")
        if (basePath.exists()) {
            file.createNewFile()
            fileUri = getUriFromFile(file)
        }
        return file
    }

    private fun getUriFromFile(file: File): Uri? {
        return FileProvider.getUriForFile(
            this,
            packageName + ".provider",
            file
        )
    }
    // Caller


    // Receiver
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                when(it.resultCode){

                }
                val value = it.data?.getStringExtra("input")
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var bitmap: Uri? = null
        if (resultCode == Activity.RESULT_OK) {
            try {
                when (requestCode) {
                    ScanConstants.OPEN_CAMERA->{
                        val intent=Intent()
                        intent.putExtra(ScanConstants.SCANNED_RESULT, data?.extras!!.getParcelable<Uri>(
                            com.scanlibraryscanner.ScanConstants.SCANNED_RESULT))
                        intent.putExtra(ScanConstants.SCAN_MORE, true)
                        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, data?.extras!!.getInt(
                            com.scanlibraryscanner.ScanConstants.OPEN_INTENT_PREFERENCE))
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    ScanConstants.START_CAMERA_REQUEST_CODE -> bitmap = fileUri
                    ScanConstants.PICKFILE_REQUEST_CODE -> bitmap = data?.data!!

                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            finish()
        }
        if (bitmap!=null) {
//            val uriTempPath=FileUtilsForFd.createTempFile(requireContext(), it)
                Intent(this, MainScannerPolygonActivity::class.java).apply {
                    putExtra(ScanConstants.SELECTED_BITMAP, bitmap)
                    putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, getIntentPreference())
                    startActivityForResult(this,ScanConstants.OPEN_CAMERA)
                }
        }else{
            finish()
        }

    }

}