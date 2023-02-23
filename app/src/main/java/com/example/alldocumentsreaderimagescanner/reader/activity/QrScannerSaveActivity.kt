package com.example.alldocumentsreaderimagescanner.reader.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.databinding.ActivityQrScannerSaveBinding

class QrScannerSaveActivity : AppCompatActivity() {
    lateinit var bindingLayout: ActivityQrScannerSaveBinding
    var resultQrText: String? = " "
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingLayout = ActivityQrScannerSaveBinding.inflate(layoutInflater)
        setContentView(bindingLayout.root)

        resultQrText = if (intent.hasExtra("saveQr"))
            intent.getStringExtra("saveQr")
        else
            ""
        if (resultQrText != null && resultQrText!!.isEmpty() && !resultQrText!!.contains("http://"))
            finish()

        bindingLayout.tvResult.text = resultQrText.toString()


        bindingLayout.shareLinkQR.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            val shareBody = resultQrText

            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.app_name)
            )
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(intent, getString(R.string.app_name)))
        }
        bindingLayout.copyLinkQR.setOnClickListener {
            val cbManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Qr Code : ", resultQrText)
            cbManager.setPrimaryClip(clipData)
            Toast.makeText(this@QrScannerSaveActivity, "Copied", Toast.LENGTH_SHORT).show()
        }
        bindingLayout.openWebSite.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resultQrText)))
            } catch (e: Exception) {
                Toast.makeText(this,"No link found",Toast.LENGTH_SHORT).show()
            }
        }
        bindingLayout.baackArrow.setOnClickListener {
        onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}