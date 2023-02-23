package com.example.alldocumentsreaderimagescanner.reader.pdfui

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.alldocumentsreaderimagescanner.HomeActivity
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.converter.util.StringUtils
import com.example.alldocumentsreaderimagescanner.databinding.ActivityPdfViewerBinding
import com.shockwave.pdfium.PdfPasswordException
import java.io.File


class PdfViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewerBinding
    var path: Uri? = null
    var isBack = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.app_blue)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pdf_viewer)
        path = intent.data
        if (path == null) {
            isBack = false
            path = Uri.fromFile(intent?.getStringExtra("filePath")?.let { File(it) })
        }

        binding.pdfView.fromUri(path).onError {
            if (it is PdfPasswordException) {
                passwordProtectPDF()
            }
        }.load()


        binding.backBtn.setOnClickListener {
            backPress()
        }

        path?.let {
            try {
                binding.categoryName.text = getFileName(it)
            } catch (e: Exception) {
            }

        }

    }


    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            cursor.let {
                if (it != null && it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
            cursor?.close()
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }

    private fun passwordProtectPDF() {
        val dialog: MaterialDialog = MaterialDialog.Builder(this)
            .title(R.string.enter_password_custom)
            .customView(R.layout.enter_password_dialog, true)
            .positiveText(android.R.string.ok)
            .negativeText(android.R.string.cancel)
            .build()
        val positiveAction: View = dialog.getActionButton(DialogAction.POSITIVE)
        val passwordInput = dialog.customView!!.findViewById<EditText>(R.id.password)
        passwordInput.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    positiveAction.isEnabled = s.toString().trim { it <= ' ' }.isNotEmpty()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        positiveAction.setOnClickListener {
            if (StringUtils.getInstance().isEmpty(passwordInput.text)) {
                StringUtils.getInstance()
                    .showSnackbar(this, R.string.snackbar_password_cannot_be_blank)
            } else {
                binding.pdfView.fromUri(path).password(passwordInput.text.toString())
                    .onError {
                        if (it is PdfPasswordException) {
                            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .load()
                dialog.dismiss()
            }
        }

        dialog.show()
        positiveAction.isEnabled = false
    }

    override fun onBackPressed() {
        backPress()
    }

    private fun backPress() {
        if (isBack) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            super.onBackPressed()
        }
    }

}