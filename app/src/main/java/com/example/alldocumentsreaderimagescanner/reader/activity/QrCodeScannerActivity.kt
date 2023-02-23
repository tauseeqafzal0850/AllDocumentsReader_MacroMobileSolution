package com.example.alldocumentsreaderimagescanner.reader.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.*
import com.example.alldocumentsreaderimagescanner.R
import kotlinx.android.synthetic.main.activity_qr_code_scanner.*

class QrCodeScannerActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scanner)

        codeScanner = CodeScanner(this, scanner_view)

        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                val intent=Intent(this@QrCodeScannerActivity,QrScannerSaveActivity::class.java)
                intent.putExtra("saveQr",it.text)
                startActivity(intent)

//                println("Scan result  : ${it.text}")
//                botom_card.visibility = View.VISIBLE
//                scanned_text.text = it.text
//
//                if (it.text.contains("http://")) {
//                    open_.visibility = View.VISIBLE
//                }

            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(
                    this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        codeScanner.startPreview()


        scanner_view.setOnClickListener {
            codeScanner.startPreview()
        }

        copy_.setOnClickListener {
            val cbManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Qr Code : ", scanned_text.text)
            cbManager.setPrimaryClip(clipData)
            Toast.makeText(this@QrCodeScannerActivity, "Copied", Toast.LENGTH_SHORT).show()

        }

        share_.setOnClickListener {

            val intent = Intent(Intent.ACTION_SEND)
            val shareBody = scanned_text.text

            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.app_name)
            )
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(intent, getString(R.string.app_name)))

        }

        open_.setOnClickListener {

            val url = scanned_text.text.toString()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

    }

    override fun onStop() {
        super.onStop()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }
}