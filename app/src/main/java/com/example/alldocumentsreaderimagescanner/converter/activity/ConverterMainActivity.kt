package com.example.alldocumentsreaderimagescanner.converter.activity

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.ads.BannerActivity
import com.example.alldocumentsreaderimagescanner.converter.fragments.ExcelToPdfFragment
import com.example.alldocumentsreaderimagescanner.converter.fragments.ImageToPdfFragment
import com.example.alldocumentsreaderimagescanner.converter.fragments.MergerFragment
import com.example.alldocumentsreaderimagescanner.converter.fragments.TextToPdfFragment
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.WRITE_PERMISSIONS
import com.example.alldocumentsreaderimagescanner.converter.util.PermissionsUtils
import com.example.alldocumentsreaderimagescanner.databinding.ActivityConverterMainBinding
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_EXCEL_TO_PDF_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_IMAGE_TO_PDF_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_PDF_MERGE_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_TEXT_TO_PDF_BANNER



class ConverterMainActivity : BannerActivity() {

    lateinit var binding: ActivityConverterMainBinding
    private var mSettingsActivityOpenedForManageStoragePermission = false
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityConverterMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        val whichFragment = intent.getIntExtra("fragment", 1)
        checkAndAskForStoragePermission()



        binding.run {

            when (whichFragment) {
                1 -> {
                    showBanner(CHECK_IMAGE_TO_PDF_BANNER)
                    supportFragmentManager.beginTransaction()
                        .add(frameForFragment.id, ImageToPdfFragment())
                        .commit()
                }
                2 -> {
                    showBanner(CHECK_TEXT_TO_PDF_BANNER)
                    supportFragmentManager.beginTransaction()
                        .add(frameForFragment.id, TextToPdfFragment())
                        .commit()
                }
                3 -> {
                    showBanner(CHECK_EXCEL_TO_PDF_BANNER)
                    supportFragmentManager.beginTransaction()
                        .add(frameForFragment.id, ExcelToPdfFragment())
                        .commit()
                }
                4 -> {
                    showBanner(CHECK_PDF_MERGE_BANNER)
                    supportFragmentManager.beginTransaction()
                        .add(frameForFragment.id, MergerFragment())
                        .commit()
                }
                else -> {
                    super.onBackPressed()
                }
            }

        }


    }

    private fun checkAndAskForStoragePermission() {
        if (!PermissionsUtils.getInstance().checkRuntimePermissions(this, WRITE_PERMISSIONS)) {
            getRuntimePermissions()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    askStorageManagerPermission()
                }
            }
        }
    }

    private fun getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(
            this,
            WRITE_PERMISSIONS,
            REQUEST_CODE_FOR_WRITE_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionsUtils.getInstance().handleRequestPermissionsResult(
            this, grantResults,
            requestCode, REQUEST_CODE_FOR_WRITE_PERMISSION
        ) { askStorageManagerPermission() }
    }

    private fun askStorageManagerPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                AlertDialog.Builder(this)
                    .setTitle(R.string.one_more_thing_text)
                    .setMessage(R.string.storage_manager_permission_rationale)
                    .setCancelable(false)
                    .setPositiveButton(R.string.allow_text) { dialog, which ->
                        val intent =
                            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        val uri =
                            Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        mSettingsActivityOpenedForManageStoragePermission = true
                        startActivity(intent)
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.close_app_text) { dialog, which -> finishAndRemoveTask() }
                    .show()
            }
        }
    }


}