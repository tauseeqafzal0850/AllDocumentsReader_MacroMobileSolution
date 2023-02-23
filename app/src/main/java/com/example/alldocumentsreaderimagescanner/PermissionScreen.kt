package com.example.alldocumentsreaderimagescanner

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.alldocumentsreaderimagescanner.databinding.ActivityPermissionScreenBinding
import com.example.alldocumentsreaderimagescanner.utils.Constant
import com.example.alldocumentsreaderimagescanner.utils.PreDataStoreUtils.write
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PermissionScreen : AppCompatActivity() {

    private val STORAGE_PERMISSION_REQUEST_CODE = 11
    lateinit var permissionViewsBindings:ActivityPermissionScreenBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        permissionViewsBindings=ActivityPermissionScreenBinding.inflate(layoutInflater)
        setContentView(permissionViewsBindings.root)
        Constant.SHOW_AD=true

        permissionViewsBindings.permiBtn.setOnClickListener {
            OkPermmissionGoToMain()
        }

        permissionViewsBindings.cancelScreen.setOnClickListener {
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestStoragePermission() {

        if (!checkPermissionForReadExtertalStorage()) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                try {
                    requestPermissionForReadExtertalStorage()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                openSettingDialog()
            }


        } else {
            skipOkPermmissionGoToMain()

        }

    }

    private fun checkPermissionForReadExtertalStorage(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            return result == PackageManager.PERMISSION_GRANTED
        }
        return false
    }

    private fun requestStoragePermissionForEleven() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                skipOkPermmissionGoToMain()
            } else { //request for the permission

                showNotiPermiDialog()
            }
        }
    }


    private fun openSettingDialog() {

        val dialog: Dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cus_open_internet_dialog)

        val DoYouWantToDeleteTv = dialog.findViewById<TextView>(R.id.DoYouWantToDeleteTv)
        DoYouWantToDeleteTv.text =
            resources.getString(R.string.manage_permission)
        val buttonConnect = dialog.findViewById<TextView>(R.id.buttonConnect)
        buttonConnect.text = "Go to setting"
        val buttonCancel = dialog.findViewById<TextView>(R.id.buttonCancel)
        buttonCancel.setOnClickListener { dialog.dismiss() }

        buttonConnect.setOnClickListener {
            dialog.dismiss()

            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)

        }

        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun showNotiPermiDialog() {
        val progressDialogBack: Dialog = Dialog(this)
        progressDialogBack.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialogBack.setCancelable(false)

        try {
            progressDialogBack.setContentView(R.layout.manage_permission)
            // ... rest of body of onCreateView() ...
        } catch (e: Exception) {
//            Log.e("onCreateViewNot", "onCreateView", e)
        }

        val buttonOkay = progressDialogBack.findViewById<TextView>(R.id.buttonOkay)

        buttonOkay.setOnClickListener {

            progressDialogBack.dismiss()
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        progressDialogBack.show()
    }

    @Throws(java.lang.Exception::class)
    fun requestPermissionForReadExtertalStorage() {
        try {
            ActivityCompat.requestPermissions(
                (this as Activity),
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.size >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // do Operation
                skipOkPermmissionGoToMain()

            } else {

                Toast.makeText(
                    this,
                    resources.getString(R.string.manage_permission),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    override fun onBackPressed() {
        finish()
    }

    private fun skipOkPermmissionGoToMain() {
        lifecycleScope.launch {
            write(
                Constant.PERMISSION_SHOW_KEY,
                false
            )
            withContext(Dispatchers.Main){
                startActivity(Intent(this@PermissionScreen, HomeActivity::class.java))
                finish()
            }
        }


    }
    private fun OkPermmissionGoToMain() {
        lifecycleScope.launch {
            write(
                Constant.PERMISSION_SHOW_KEY,
                true
            )
            withContext(Dispatchers.Main){
                startActivity(Intent(this@PermissionScreen, HomeActivity::class.java))
                finish()
            }
        }


    }
}