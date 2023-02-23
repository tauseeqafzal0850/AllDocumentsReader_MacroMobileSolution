package com.example.alldocumentsreaderimagescanner.reader.activity

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.ads.BannerActivity
import com.example.alldocumentsreaderimagescanner.databinding.FileListLayoutBinding
import com.example.alldocumentsreaderimagescanner.reader.adapters.FileListAdapter
import com.example.alldocumentsreaderimagescanner.reader.interfaces.FileViewInterface
import com.example.alldocumentsreaderimagescanner.reader.models.AllFileListModel
import com.example.alldocumentsreaderimagescanner.reader.models.ListViewModel
import com.example.alldocumentsreaderimagescanner.reader.pdfui.PdfViewerActivity
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_ALL_FILES_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_EXCEL_FILES_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_PDF_FILES_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_PPT_FILES_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_TEXT_FILES_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_WORD_FILES_BANNER
import com.faisaldeveloper.office.officereader.AppActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*
import java.io.File
import java.util.*

private const val DELETE_PERMISSION_REQUEST = 0x1033

class FileListActivity : BannerActivity(), FileViewInterface {
    private var itemClickPosition: Int = -1
    private val TAG = "FileListActivity"
    private var fileType: Int = -1
    private lateinit var binding: FileListLayoutBinding
    private lateinit var fileList: ArrayList<AllFileListModel>
    private var listAdapter: FileListAdapter? = null
    private val listViewModel: ListViewModel by viewModels()
    private var sortType = ListSortType.SORT_NAME

    enum class ListSortType {
        SORT_DATE,
        SORT_NAME,
        SORT_SIZE,
    }

    var loading: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        binding = FileListLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        fileType = intent?.getIntExtra("fileType", -1)!!
        fileList = ArrayList()
        listAdapter = FileListAdapter(this@FileListActivity, this@FileListActivity)

        binding.filesRV.apply {
            layoutManager = LinearLayoutManager(this@FileListActivity)
//            setHasFixedSize(true)
            adapter = listAdapter
        }
//        deleteUtilsR = DeleteUtilsR(this)
        loadFiles()
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                searchFilter(s.toString())
            }
        })

        binding.sortingIcon.setOnClickListener {
            showDialog()
        }

        binding.backBtn.setOnClickListener {
           onBackPressedDispatcher.onBackPressed()
        }

        listViewModel.deleteItemResult.observe(this) {
            if (it == 1) {
                Log.e(TAG, "DeleteSuccefully: ${itemClickPosition}")
                listViewModel.onClear()
                dismisMaterialDialog()
                listAdapter?.submitList(null)
                loadFiles()
            } else {
                Log.e(TAG, "DeleteUnSuccefully: ")
            }
        }
        lifecycleScope.launch {
            listViewModel.permissionNeededForDelete.observe(this@FileListActivity, { intentSender ->
                intentSender?.let {
                    startIntentSenderForResult(
                        intentSender,
                        DELETE_PERMISSION_REQUEST,
                        null,
                        0,
                        0,
                        0,
                        null
                    )
                }
            })
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == DELETE_PERMISSION_REQUEST) {
            listViewModel.deletePendingImage(this@FileListActivity)
        }
    }

    fun dismisMaterialDialog() {
        if (materialDialog != null && materialDialog!!.isShowing) {
            materialDialog?.dismiss()
        }
    }

    var materialDialog: androidx.appcompat.app.AlertDialog? = null
    private fun deleteImageDialog(image: AllFileListModel) {
        val dialog = Dialog(this,R.style.MyAlertDialogTheme)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_delete_layout)
        val btnDelete = dialog.findViewById<TextView>(R.id.btnDelete)
        val btnCancel = dialog.findViewById<TextView>(R.id.btnCancel)

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnDelete.setOnClickListener {
            dialog.dismiss()
            try{
                lifecycleScope.launch {
                    listViewModel.deleteImage(image, this@FileListActivity)
                    Toast.makeText(applicationContext, "File Deleted ", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        dialog.show()
    }

    private fun loadFiles() {
        when (fileType) {
            0 -> {
                lifecycleScope.launch {
                    listViewModel.getAllList(this@FileListActivity)?.let {
                        Log.e(TAG, "allloadFiles: ")
                        changeUI(it.isEmpty())
                        fileList.clear()
                        fileList = it
                        binding.categoryName.text = resources.getString(R.string.all_file)
                        isShowListBanner(fileList.isNotEmpty(), CHECK_ALL_FILES_BANNER)
                        arrangeListToSelectedSortOrder()
                    }
                }
            }
            4 -> {
                lifecycleScope.launch {
                    listViewModel.getPdfList(this@FileListActivity)?.let { it ->
                        changeUI(it.isEmpty())
//                        listAdapter?.submitList(it)
                        fileList.clear()
                        fileList = it
                        binding.categoryName.text = resources.getString(R.string.pdf)
                        isShowListBanner(fileList.isNotEmpty(), CHECK_PDF_FILES_BANNER)
                        arrangeListToSelectedSortOrder()

                    }
                }
            }
            5 -> {
                lifecycleScope.launch {
                    listViewModel.getWordList(this@FileListActivity)?.let {
                        changeUI(it.isEmpty())
//                        listAdapter?.submitList(it)
                        fileList.clear()
                        fileList = it
                        binding.categoryName.text = resources.getString(R.string.word)
                        isShowListBanner(fileList.isNotEmpty(), CHECK_WORD_FILES_BANNER)
                        arrangeListToSelectedSortOrder()
                    }
                }

            }
            6 -> {
                lifecycleScope.launch {
                    listViewModel.getExcelList(this@FileListActivity)?.let {
                        changeUI(it.isEmpty())
//                        listAdapter?.submitList(it)
                        fileList.clear()
                        fileList = it
                        binding.categoryName.text = resources.getString(R.string.excel)
                        isShowListBanner(fileList.isNotEmpty(), CHECK_EXCEL_FILES_BANNER)
                        arrangeListToSelectedSortOrder()
                    }
                }
            }
            7 -> {
                lifecycleScope.launch {
                    listViewModel.getPptList(this@FileListActivity)?.let {
                        changeUI(it.isEmpty())
//                        listAdapter?.submitList(it)
                        fileList.clear()
                        fileList = it
                        binding.categoryName.text = resources.getString(R.string.power_point)
                        isShowListBanner(fileList.isNotEmpty(), CHECK_PPT_FILES_BANNER)
                        arrangeListToSelectedSortOrder()
                    }
                }
            }
            8 -> {
                lifecycleScope.launch {
                    listViewModel.getTxtList(this@FileListActivity)?.let {
                        changeUI(it.isEmpty())
//                        listAdapter?.submitList(it)
                        fileList.clear()
                        fileList = it
                        binding.categoryName.text = resources.getString(R.string.text)
                        isShowListBanner(fileList.isNotEmpty(), CHECK_TEXT_FILES_BANNER)
                        arrangeListToSelectedSortOrder()
                    }
                }

            }
        }
    }

    private fun isShowListBanner(isNotEmpty: Boolean, adCondition: String) {
        if (isNotEmpty) {
            showBanner(adCondition)
        }
    }

    private fun arrangeListToSelectedSortOrder() {
        when (sortType) {
            ListSortType.SORT_DATE -> {
                sortOrderingByDate()
            }
            ListSortType.SORT_SIZE -> {
                sortOrderingBySize()
            }
            ListSortType.SORT_NAME -> {
                sortOrderingByName()
            }
            else -> {
                sortOrderingByName()
            }
        }
    }

    private fun changeUI(isEmpty: Boolean) {
        if (isEmpty) {
            binding.emptyTv.visibility = View.VISIBLE
            binding.filesRV.visibility = View.GONE
        } else {
            binding.emptyTv.visibility = View.GONE
            binding.filesRV.visibility = View.VISIBLE

        }
    }

    private var isName = false
    private var isSize = false
    private var isDate = false
    var dialog: Dialog? = null
    private fun showDialog() {
        dialog = Dialog(this)
        val window: Window? = dialog?.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.TOP or Gravity.END
        window.attributes = wlp

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.custom_layout)
        val bySize =dialog?.findViewById<TextView>(R.id.bySize)
        val byName = dialog?.findViewById<TextView>(R.id.byName)
        val byDate = dialog?.findViewById<TextView>(R.id.byDate)

        byName?.setOnClickListener {
            sortOrderingByName()
        }
        bySize?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                sortOrderingBySize()
            }

        })
        byDate?.setOnClickListener {
            sortOrderingByDate()
        }
        dialog?.show()
    }

    fun sortOrderingByName() {
        isName = if (isName) {
            sortType = ListSortType.SORT_NAME
//                fileList.sortedWith { o1, o2 -> o2.name.compareTo(o1.name) }
            lifecycleScope.launch {
                kotlin.runCatching {
                    if (fileList.isNotEmpty()) {
                        fileList = nameFilesSorting(fileList) as ArrayList<AllFileListModel>
                        listAdapter?.submitList(fileList.reversed())
                        smoothScroolFromStart()
                        withContext(Dispatchers.Main) {
                            dialog?.dismiss()
                        }
                    } else {
                        dialogDismis()
                    }
                }
            }
            false
        } else {
            sortType = ListSortType.SORT_NAME
            lifecycleScope.launch {
                kotlin.runCatching {
                    if (fileList.isNotEmpty()) {
                        fileList = nameFilesSorting(fileList) as ArrayList<AllFileListModel>
                        listAdapter?.submitList(fileList)
                        smoothScroolFromStart()
                        withContext(Dispatchers.Main) {
                            dialog?.dismiss()
                        }
                    } else {
                        dialogDismis()
                    }
                }
            }
            true
        }
    }

    fun dialogDismis() {
        if (dialog != null && dialog!!.isShowing)
            dialog?.dismiss()
    }

    fun smoothScroolFromStart() {
        if (fileList.isNotEmpty())
            binding.filesRV.smoothScrollToPosition(0)
    }

    fun sortOrderingBySize() {
        isSize = if (isSize) {
            sortType = ListSortType.SORT_SIZE
            lifecycleScope.launch {
                if (fileList.isNotEmpty()) {
                    fileList = sizeFilesSorting(fileList) as ArrayList<AllFileListModel>
                    listAdapter?.submitList(fileList)
                    smoothScroolFromStart()
                    withContext(Dispatchers.Main) {
                        dialog?.dismiss()
                    }
                } else {
                    dialogDismis()
                }
            }

            false
        } else {
            sortType = ListSortType.SORT_SIZE
            lifecycleScope.launch {
                kotlin.runCatching {
                    if (fileList.isNotEmpty()) {
                        fileList = sizeFilesSorting(fileList) as ArrayList<AllFileListModel>
                        listAdapter?.submitList(fileList)
                        smoothScroolFromStart()
                        withContext(Dispatchers.Main) {
                            dialog?.dismiss()
                        }
                    } else {
                        dialogDismis()
                    }
                }
            }
            true
        }
    }

    fun sortOrderingByDate() {
        isDate = if (isDate) {
            sortType = ListSortType.SORT_DATE
            lifecycleScope.launch {
                if (fileList.isNotEmpty()) {
                    fileList = dateFilesSorting(fileList) as ArrayList<AllFileListModel>
                    withContext(Dispatchers.Main) {
                        listAdapter?.submitList(fileList)
                        smoothScroolFromStart()
                        dialog?.dismiss()
                    }

                } else {
                    dialogDismis()
                }
            }
            false
        } else {
            sortType = ListSortType.SORT_DATE
            lifecycleScope.launch {
                kotlin.runCatching {
                    if (fileList.isNotEmpty()) {
                        fileList = dateFilesSorting(fileList) as ArrayList<AllFileListModel>
//                             listAdapter?.submitList(fileList)
                        withContext(Dispatchers.Main) {
                            listAdapter?.submitList(fileList)
                            smoothScroolFromStart()
                            dialog?.dismiss()
                        }
                    } else {
                        dialogDismis()
                    }
                }
            }
            true
        }
    }

    suspend fun sizeFilesSorting(files: Iterable<AllFileListModel>): List<AllFileListModel> {
        val metadataReadTasks: List<Deferred<fileWithMetadataSize>> = withContext(Dispatchers.IO)
        {
            files.map { file ->
                async {
                    fileWithMetadataSize(file)
                }
            }
        }
        val metadatas: List<fileWithMetadataSize> = metadataReadTasks.awaitAll()
        return metadatas
            .sorted()
            .map {
                it.file
            }
    }

    private class fileWithMetadataSize(
        val file: AllFileListModel
    ) : Comparable<fileWithMetadataSize> {
        private val nameSorting = file.path

        //        private val length = file.path
        override fun compareTo(other: fileWithMetadataSize): Int {
            return when (other.nameSorting) {
                nameSorting -> File(other.file.path).length()
                    .compareTo(File(this.file.path).length())
                else -> File(other.file.path).length()
                    .compareTo(File(this.file.path).length())
            }
        }
    }

    suspend fun nameFilesSorting(files: Iterable<AllFileListModel>): List<AllFileListModel> {
        val metadataReadTasks: List<Deferred<FileWithNameMetadata>> = withContext(Dispatchers.IO)
        {
            files.map { file ->
                async {
                    FileWithNameMetadata(file)
                }
            }
        }
        val metadatas: List<FileWithNameMetadata> = metadataReadTasks.awaitAll()
        return metadatas
            .sorted()
            .map {
                it.file
            }
    }

    private class FileWithNameMetadata(
        val file: AllFileListModel
    ) : Comparable<FileWithNameMetadata> {
        private val nameSorting = file.name

        //        private val length = file.path
        override fun compareTo(other: FileWithNameMetadata): Int {
            return when (other.nameSorting) {
                nameSorting -> other.file.name.compareTo(this.file.name)
                else -> other.file.name.compareTo(this.file.name)
            }
        }
    }

    suspend fun dateFilesSorting(files: Iterable<AllFileListModel>): List<AllFileListModel> {
        val metadataReadTasks: List<Deferred<FileWithDateMetadata>> = withContext(Dispatchers.IO)
        {
            files.map { file ->
                async {
                    FileWithDateMetadata(file)
                }
            }
        }
        val metadatas: List<FileWithDateMetadata> = metadataReadTasks.awaitAll()
        return metadatas
            .sorted()
            .map {
                it.file
            }
    }

    private class FileWithDateMetadata(
        val file: AllFileListModel
    ) : Comparable<FileWithDateMetadata> {
        private val nameSorting = file.path

        //        private val length = file.path
        override fun compareTo(other: FileWithDateMetadata): Int {
            return when (other.nameSorting) {
                nameSorting -> File(other.file.path).lastModified()
                    .compareTo(File(this.file.path).lastModified())
                else -> File(other.file.path).lastModified()
                    .compareTo(File(this.file.path).lastModified())
            }
        }
    }

    private fun searchFilter(s: String) {
        val searchList = ArrayList<AllFileListModel>()
        for (m in fileList) {
            if (m.name.lowercase(Locale.getDefault()).contains(s.lowercase(Locale.getDefault()))) {
                searchList.add(m)
            }
        }
        listAdapter?.submitList(searchList)
    }

    override fun onFileClick(path: String) {
        if (path.endsWith(".pdf")) {
            val intent = Intent(this@FileListActivity, PdfViewerActivity::class.java)
            intent.putExtra("filePath", path)
            startActivity(intent)
        } else {
            val intent = Intent(this@FileListActivity, AppActivity::class.java)
            intent.putExtra("filePath", path)
            startActivity(intent)
        }
    }

    override fun onFileDelete(path: AllFileListModel, pos: Int) {
        itemClickPosition = pos
        deleteImageDialog(path)

//        deleteImage(path)
//        deleteUtilsR?.deleteFile(path, object : DeleteCallBack {
//            override fun onDeleted(isdel: Boolean) {
//                if (isdel) {
//                    Toast.makeText(this@FileListActivity,
//                        "delete successfully",
//                        Toast.LENGTH_SHORT)
//                        .show()
//                } else {
//                    Toast.makeText(
//                        this@FileListActivity,
//                        "delete unsuccessfull",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//
//        })
    }

    override fun onDestroy() {
        super.onDestroy()
        listViewModel.onClear()
    }

    override fun onResume() {
        super.onResume()
    }
}