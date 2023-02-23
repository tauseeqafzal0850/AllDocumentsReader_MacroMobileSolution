package com.example.alldocumentsreaderimagescanner.reader.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidmads.library.qrgenearator.QRGSaver
import androidx.core.content.FileProvider
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.ads.BannerActivity
import com.example.alldocumentsreaderimagescanner.databinding.ActivityQrCodeGeneratorBinding
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_QR_GENERATOR_BANNER
import com.example.alldocumentsreaderimagescanner.utils.hideKeyboard
import com.google.zxing.WriterException
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class QrCodeGeneratorActivity : BannerActivity() {

    var TAG = "QrCodeGeneratorActivity"

    var inputValue: String? = null

    var qrgEncoder: QRGEncoder? = null
    var qrgSaver: QRGSaver? = null
    var bitmap: Bitmap? = null

    lateinit var savePath: String

    lateinit var binding:ActivityQrCodeGeneratorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityQrCodeGeneratorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        qrgSaver = QRGSaver()
        showBanner(CHECK_QR_GENERATOR_BANNER)
        savePath =
            Environment.getExternalStorageDirectory().path.toString() + "/All Doc QRCode/"

        binding.btngenerateQR.setOnClickListener {
            inputValue = binding.edtwriteGenerate.text.toString().trim()
            if (inputValue!!.isNotEmpty()) {
                val manager = getSystemService(WINDOW_SERVICE) as WindowManager
                val display: Display = manager.defaultDisplay
                val point = Point()
                display.getSize(point)
                val width: Int = point.x
                val height: Int = point.y
                var smallerDimension = if (width < height) width else height
                smallerDimension = smallerDimension * 3 / 4
                qrgEncoder = QRGEncoder(
                    inputValue, null,
                    QRGContents.Type.TEXT,
                    smallerDimension
                )
                try {
                    bitmap = qrgEncoder!!.bitmap
                    binding.ivGeneratedQrImage.visibility=View.VISIBLE
                    binding.ivGeneratedQrImage.setImageBitmap(bitmap)
                    binding.btngenerateQR.visibility = View.GONE
                    binding.layoutResult.visibility = View.VISIBLE
                    binding.edtwriteGenerate.hideKeyboard()
                    contentUri=getBitmapFromView(bitmap)
//                    lifecycleScope.launch{
//                       saveBitmapTemp()
////                        scope.launch {
////                        try {
////                            async(Dispatchers.Default) {
////                                delay(1000)
////                                throw RuntimeException()
////                            }.await()
////                        } catch (e: Exception) {
////                            println("catch error: $e")
////                        }
////                    }.join()
////                    println("parent scope is active: ${scope.isActive}")
//                    }
                } catch (e: WriterException) {
                    Log.v(TAG, e.toString())
                }
            } else {
                binding.edtwriteGenerate.setError("Required")
            }
        }

        binding.ibSaveQR.setOnClickListener {
            openbackdialog()
        }
        binding.ibResetQR.setOnClickListener {
            binding.btngenerateQR.visibility = View.VISIBLE
            binding.layoutResult.visibility = View.GONE
            binding.ivGeneratedQrImage.visibility=View.GONE
        }
        binding.ibShareQR.setOnClickListener {
            if (contentUri==null){
                Toast.makeText(this@QrCodeGeneratorActivity,"image not Shareable",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val shareIntent = Intent()
            shareIntent.setAction(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            shareIntent.setType("image/*")
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            val intent = Intent(Intent.ACTION_SEND)
//            intent.type = "image/*"
//            intent.putExtra(Intent.EXTRA_STREAM, contentUri)
            startActivity(Intent.createChooser(shareIntent, "Share Image"))
        }
        binding.backIcon.setOnClickListener {
            finish()
        }

    }

    var contentUri:Uri?=null

    val job = Job()
    val scope = CoroutineScope(Dispatchers.Default + job)
     fun saveBitmapTemp() {
        val imagePath = File(filesDir, "images")
        val newFile = File(imagePath, "myImage.jpg")
         //Convert bitmap to file
        //Convert bitmap to file
        val bytes = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val fo = FileOutputStream(newFile)
            fo.write(bytes.toByteArray())
            fo.flush()
            fo.close()
            contentUri=
                FileProvider.getUriForFile(this@QrCodeGeneratorActivity, "com.camerascanner.documents.pdf.viewer.fileprovider", newFile)
    }

    private fun openbackdialog() {

        val dialog = Dialog(this,R.style.MyAlertDialogTheme)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_save_layout)
        val btnYes = dialog.findViewById<TextView>(R.id.btnYes)
        val btnNo = dialog.findViewById<TextView>(R.id.btnNo)

        btnNo.setOnClickListener { dialog.dismiss() }

        btnYes.setOnClickListener {
            dialog.dismiss()

            val save: Boolean
            val result: String
            try {
                save = qrgSaver!!.save(
                    savePath,
                    binding.edtwriteGenerate.text.toString(),
                    bitmap,
                    QRGContents.ImageType.IMAGE_JPEG
                )
                result = if (save) "Image Saved" else "Image Not Saved"
                Toast.makeText(applicationContext, result, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
    fun getBitmapFromView(bmp: Bitmap?): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(this.externalCacheDir, System.currentTimeMillis().toString() + ".jpg")
            val out = FileOutputStream(file)
            bmp?.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.close()
            bmpUri =
                FileProvider.getUriForFile(this@QrCodeGeneratorActivity, "com.camerascanner.documents.pdf.viewer.provider", file)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }

}