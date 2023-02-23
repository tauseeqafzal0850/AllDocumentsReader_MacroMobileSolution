package com.scanlibraryscanner.idCardScanner.activity.scanner

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alldocumentsreaderimagescanner.HomeActivity
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.ads.InterstitialAdManager
import com.example.alldocumentsreaderimagescanner.utils.Constant
import com.scanlibraryscanner.ScanConstants
import kotlinx.android.synthetic.main.activity_multi_page.*
import kotlinx.coroutines.launch
import java.io.File

class MultiPageActivity : AppCompatActivity(), OnMoreClick, InterstitialAdManager.AdCallback {
    lateinit var stagingFiles: List<File>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_page)

//        Log.d(TAG, "onCreate: MultiPageActivity")

        val stagingDirPath = ScanConstants.PDFScanner_stage

//        InterstitialAdManager.with(applicationContext).setCallbackListener(this)
        stagingFiles = getAllFiles(stagingDirPath)

        multi_page_grid.apply {
            adapter =
                ImageAdapterGridView(
                    this@MultiPageActivity,
                    stagingFiles,
                    this@MultiPageActivity
                )
            layoutManager = GridLayoutManager(this@MultiPageActivity, 3)
            setHasFixedSize(true)
        }

        backBtn.setOnClickListener {
            super.onBackPressed()
        }
        convertPdfBtn.setOnClickListener {
            showInterstitial(Constant.CHECK_DOCUMENTS_SCANNER_INTERSTITIALS)
        }

    }

    private fun getAllFiles(dirPath: String?): List<File> {
        val fileList: MutableList<File> = ArrayList()
        val sd = getExternalFilesDir("").toString()
        val targetDirectory = File(sd, dirPath)
        if (targetDirectory.listFiles() != null) {
            for (eachFile in targetDirectory.listFiles()) {
                fileList.add(eachFile)
            }
        }
        fileList.sortWith { f1, f2 -> f1.lastModified().compareTo(f2.lastModified()) }
        return fileList
    }

    private val TAG = "MultiPageActivity"

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data?.extras != null) {
            val out = Intent()
            out.putExtra(
                ScanConstants.SCANNED_RESULT,
                data.extras!!.getParcelable<Parcelable>(ScanConstants.SCANNED_RESULT) as Uri?
            )
            out.putExtra(ScanConstants.SCAN_MORE, data.extras!!.getBoolean(ScanConstants.SCAN_MORE))
            setResult(RESULT_OK, out)
            finish()
            System.gc()
        }
    }


    fun scanMore() {
//        val intent = Intent(this, ScanActivity::class.java)
        val intent = Intent(this, MainScannerActivity::class.java)
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA)
        val options = ActivityOptions.makeSceneTransitionAnimation(this)
        startActivityForResult(intent, ScanConstants.START_CAMERA_REQUEST_CODE, options.toBundle())
    }

    fun doScanMore() {
        val intent = Intent(this, MainScannerActivity::class.java)
        intent.putExtra("doScanMore", true)
        startActivity(intent)
    }

    private fun showImage(stagingFiles: File, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val newFileName = stagingFiles.path
        val toOpen = File(newFileName)


        val sharedFileUri = FileProvider.getUriForFile(
            context,
            context.packageName.toString() + ".provider",
            toOpen
        )
        intent.setDataAndType(sharedFileUri, "image/png")
        val pm = context.packageManager
        if (intent.resolveActivity(pm) != null) {
            context.startActivity(intent)
        }
    }

    inner class ImageAdapterGridView(
        private val c: Context,
        private val list: List<File>,
        private val onMoreClick: OnMoreClick
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val AddMore = 0
        val EachFile = 1
        var letValue = 1

        inner class AddMoreView(view: View) : RecyclerView.ViewHolder(view) {
            val addMoreBtn: LinearLayout = view.findViewById(R.id.add_page)
        }

        inner class EachFileView(view: View) : RecyclerView.ViewHolder(view) {
            val eachScreenshot: ImageView = view.findViewById(R.id.each_file_screenshot)
            val pageCount: TextView = view.findViewById(R.id.each_pageno)
            val deleteIcon: ImageView = view.findViewById(R.id.each_file_delete)
        }

        override fun getItemCount(): Int {
            return list.size + letValue
        }

        override fun getItemViewType(position: Int): Int {
            return when (position) {
                0 -> AddMore
                1 -> EachFile
                else -> -1
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                0 -> {
                    AddMoreView(
                        LayoutInflater.from(c).inflate(R.layout.add_more_img, parent, false)
                    )
                }
                else -> {
                    EachFileView(
                        LayoutInflater.from(c).inflate(R.layout.each_file_img, parent, false)
                    )
                }
            }

        }

        private val TAG = "MultiPageActivity"

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//            Log.d(TAG, "onBindViewHolder: ${list.size}")

            when (position) {
                0 -> {
                    (holder as AddMoreView).addMoreBtn.setOnClickListener {
                        //scanMore()
                        doScanMore()
                    }
                }
                else -> {
                    Glide.with(c).load(list[position - 1])
                        .into((holder as EachFileView).eachScreenshot)
                    holder.eachScreenshot.setOnClickListener {
                        onMoreClick.onItemClick(position, c, list[position - 1])
                    }
                    holder.pageCount.text = "Page $position"
                    holder.deleteIcon.setOnClickListener {
                        val delete = list[position - 1].delete()
                        if (delete) {
                            Toast.makeText(c, "Image Deleted", Toast.LENGTH_SHORT).show()
                            letValue = 0
                            notifyDataSetChanged()
                        } else {
                            Toast.makeText(c, "Cannot Delete", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }


        }

    }

    override fun onItemClick(position: Int, context: Context, stagingFiles: File) {
        if (position != 0) showImage(stagingFiles, context)
    }


    private fun showInterstitial(ad: String) {
        when (ad) {
            Constant.AD_ADMOB -> showInterstitial(isAdmob = true, isMax = false)
            Constant.AD_MAX -> showInterstitial(isAdmob = false, isMax = true)
            else -> {
                showInterstitial(isAdmob = false, isMax = false)
            }
        }
    }

    private fun showInterstitial(isAdmob: Boolean, isMax: Boolean) {
        lifecycleScope.launch {
            if (isMax || isAdmob) {
                if (InterstitialAdManager.isLoaded) {
                    blankView.visibility = View.VISIBLE
//                    InterstitialAdManager.with(applicationContext)
//                        .showInterstitial(this@MultiPageActivity, isAdmob, isMax)
                    InterstitialAdManager.with()
                        .showInterstitial(this@MultiPageActivity, isAdmob, isMax)
                } else {
                    saveNow()
                }
            } else {
                saveNow()
            }

        }
    }


    private fun saveNow() {
        if (getAllFiles(ScanConstants.PDFScanner_stage)
                .isNotEmpty()
        ) {
            val out = Intent()
            out.putExtra(ScanConstants.SAVE_PDF, true)
            setResult(RESULT_OK, out)
            finish()
        } else {
            Toast.makeText(this, "No Image found", Toast.LENGTH_SHORT).show()
        }

    }

   /* override fun onAdShow() {

    }*/

    override fun onAdClosed() {
        saveNow()
    }

   /* override fun onAdFailed() {

    }

    override fun onAdFailedToShow() {

    }*/

    private fun showSettingsDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("Are you sure you want to discard the edits?")
        dialog.setTitle("Not saved yet")
        dialog.setPositiveButton("Discard & Leave") { dialogInterface, _ ->
            dialogInterface.cancel()
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
            finish()
        }
        dialog.setNegativeButton("Save") { dialogInterface, _ ->
            dialogInterface.cancel()
            showInterstitial(Constant.CHECK_DOCUMENTS_SCANNER_INTERSTITIALS)
        }
        dialog.show()
    }

    override fun onBackPressed() {
        if (getAllFiles(ScanConstants.PDFScanner_stage)
                .isNotEmpty()
        ) {
            showSettingsDialog()
        } else {
            super.onBackPressed()
        }

    }

    override fun onResume() {
        super.onResume()
        InterstitialAdManager.with().setCallbackListener(this)

    }

}


interface OnMoreClick {
    fun onItemClick(position: Int, context: Context, stagingFiles: File)
}