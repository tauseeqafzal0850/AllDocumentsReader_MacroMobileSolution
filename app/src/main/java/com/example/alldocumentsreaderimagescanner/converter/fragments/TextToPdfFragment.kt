package com.example.alldocumentsreaderimagescanner.converter.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.converter.adapter.MoreOptionImgToPdfAdapter
import com.example.alldocumentsreaderimagescanner.converter.interfaces.Enhancer
import com.example.alldocumentsreaderimagescanner.converter.interfaces.IOnItemClickListener
import com.example.alldocumentsreaderimagescanner.converter.interfaces.OnTextToPdfInterface
import com.example.alldocumentsreaderimagescanner.converter.model.EnhancementOptionsEntity
import com.example.alldocumentsreaderimagescanner.converter.model.TextToPDFOptions
import com.example.alldocumentsreaderimagescanner.converter.txttopdf.Enhancers
import com.example.alldocumentsreaderimagescanner.converter.txttopdf.TextToPdfContract
import com.example.alldocumentsreaderimagescanner.converter.util.*
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.WRITE_PERMISSIONS
import com.example.alldocumentsreaderimagescanner.databinding.FragmentTextToPdfBinding
import com.itextpdf.text.*
import kotlinx.android.synthetic.main.fragment_text_to_pdf.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class TextToPdfFragment : Fragment(), IOnItemClickListener, OnTextToPdfInterface,
    TextToPdfContract.View {

    private var mFileUtils: FileUtils? = null
    private var mDirectoryUtils: DirectoryUtils? = null
    private val TAG: String="TextToPdfFragment"

    private val mFileSelectCode = 0
    private var mTextFileUri: Uri? = null
    private var mFileExtension: String? = null
    private var mButtonClicked = 0
    private var mMaterialDialog: MaterialDialog? = null
    private var mFileNameWithType: String? = null
    private var moreOptionImgToPdfAdapter: MoreOptionImgToPdfAdapter? = null
    private var mPath: String? = null
    private var mEnhancerList: ArrayList<Enhancer>? = null
    private var mBuilder: TextToPDFOptions.Builder? = null
    private lateinit var binding: FragmentTextToPdfBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mFileUtils = FileUtils(requireActivity())
        mDirectoryUtils = DirectoryUtils()
        binding = FragmentTextToPdfBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBuilder = TextToPDFOptions.Builder(requireActivity())
        addEnhancements()
        //mEnhancerList = ArrayList()
        showEnhancementOptions()
        binding.createPdfCard.isEnabled = false

        binding.createPdfCard.setOnClickListener {
            openCreateTextPdf()
        }
        binding.selectTextCard.setOnClickListener {
            selectTextFile()
        }
        lifecycleScope.launch{
            mutableStateFlow.collect{
                    when(it){
                        0->{
                            Log.e(TAG, "onViewCreated: ${it}" )
                        }
                        1->{
                            Log.e(TAG, "onViewCreated: ${it}" )
                            dismissMaterialDialog()
                            onPDFCreated(true)
                        }
                        2->{
                            Log.e(TAG, "onViewCreated: ${it}" )
                            dismissMaterialDialog()
                            onPDFCreated(false)
                        }
                    }
            }

        }


    }

    private fun selectTextFile() {
//        if (PermissionsUtils.getInstance().checkRuntimePermissions(this, WRITE_PERMISSIONS)) {
//            selectFile()
//        } else {
//            getRuntimePermissions()
//        }
        selectFile()

    }


    private fun addEnhancements() {
        mEnhancerList = ArrayList()
        for (enhancer in Enhancers.values()) {
            mEnhancerList!!.add(enhancer.getEnhancer(requireActivity(), this, mBuilder))
        }
    }

    private fun showEnhancementOptions() {
        val mGridLayoutManager = GridLayoutManager(requireContext(), 3)

        val optionsEntityist: MutableList<EnhancementOptionsEntity> =
            ArrayList()
        for (enhancer in mEnhancerList!!) {
            enhancer.enhancementOptionsEntity?.let { optionsEntityist.add(it) }
        }
        moreOptionImgToPdfAdapter = MoreOptionImgToPdfAdapter(optionsEntityist, this)
        binding.moreOptionsRV.run {
            layoutManager = mGridLayoutManager
            adapter = moreOptionImgToPdfAdapter
        }
    }

    override fun onItemClick(position: Int) {
        val enhancer = mEnhancerList!![position]
        enhancer.enhance()
    }


    private fun openCreateTextPdf() {
        MaterialDialog.Builder(requireContext())
            .title(R.string.creating_pdf)
            .content(R.string.enter_file_name)
            .input(
                getString(R.string.example), mFileNameWithType
            ) { dialog: MaterialDialog?, input: CharSequence ->
                if (StringUtils.getInstance().isEmpty(input)) {
                    StringUtils.getInstance()
                        .showInLayoutSnackbar(requireActivity(), R.string.snackbar_name_not_blank,binding.laySnackBar)
                } else {
                    val inputName = input.toString()
                    if (!mFileUtils!!.isFileExist(inputName + getString(R.string.pdf_ext))) {
                        createPdf(inputName)
                    } else {
                        val builder: MaterialDialog.Builder = DialogUtils.getInstance()
                            .createOverwriteDialog(requireActivity())
                        builder.onPositive { dialog12: MaterialDialog?, which: DialogAction? ->
                            createPdf(
                                inputName
                            )
                        }
                            .onNegative { dialog1: MaterialDialog?, which: DialogAction? -> openCreateTextPdf() }
                            .show()
                    }
                }
            }
            .show()
    }

    private fun createPdf(mFilename: String) {
        mDirectoryUtils!!.orCreatePdfDirectory.let {
            mPath = it.path
            mPath = mPath + "/" + mFilename + getString(R.string.pdf_ext)
            val options = mBuilder!!.setFileName(mFilename)
                .setPageSize(PageSizeUtils.mPageSize)
                .setInFileUri(mTextFileUri)
                .build()
            val fileUtil = TextToPDFUtils(requireActivity())
//            TextToPdfAsync(
//                fileUtil, options, mFileExtension,
//                this@TextToPdfFragment
//            ).execute()
            txtToPdfAsync(fileUtil, options, mFileExtension)
   }

    }

    private fun selectFile() {
        if (mButtonClicked == 0) {
            val uri = Uri.parse(Environment.getRootDirectory().toString() + "/")
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.setDataAndType(uri, "*/*")
            val mimeTypes = arrayOf(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/msword", getString(R.string.text_type)
            )
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            try {
                startActivityForResult(
                    Intent.createChooser(intent, java.lang.String.valueOf(R.string.select_file)),
                    mFileSelectCode
                )
            } catch (ex: ActivityNotFoundException) {
                StringUtils.getInstance()
                    .showInLayoutSnackbar(requireActivity(), R.string.install_file_manager,binding.laySnackBar)
            }
            mButtonClicked = 1
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mButtonClicked = 0
        if (requestCode == mFileSelectCode) {
            if (resultCode == Activity.RESULT_OK) {
                mTextFileUri = data?.data
                StringUtils.getInstance()
                    .showInLayoutSnackbar(requireActivity(), R.string.text_file_selected,binding.laySnackBar)
                val fileName: String? = mTextFileUri?.let { mFileUtils!!.getFileName(it) }
                if (fileName != null) {
                    mFileExtension =
                        when {
                            fileName.endsWith(Constants.textExtension) -> Constants.textExtension
                            fileName.endsWith(
                                Constants.docxExtension
                            ) -> Constants.docxExtension
                            fileName.endsWith(Constants.docExtension) -> Constants.docExtension
                            else -> {
                                StringUtils.getInstance()
                                    .showInLayoutSnackbar(
                                        requireActivity(),
                                        R.string.extension_not_supported,binding.laySnackBar
                                    )
                                return
                            }
                        }
                }
                mFileNameWithType =
                    mFileUtils?.stripExtension(fileName) + getString(R.string.pdf_suffix)
                binding.selectTextFile.text = getString(R.string.text_file_name) + fileName
                binding.createPdfCard.isEnabled = true
                binding.createPdfCard.alpha = 1f
                isEnabledisableMergeFileButton(true)
            }
        }

    }

    override fun updateView() {
        moreOptionImgToPdfAdapter?.notifyDataSetChanged()
    }

    private fun getRuntimePermissions() {
        if (Build.VERSION.SDK_INT < 29) {
            PermissionsUtils.getInstance().requestRuntimePermissions(
                this,
                WRITE_PERMISSIONS,
                REQUEST_CODE_FOR_WRITE_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>, grantResults: IntArray
    ) {
        PermissionsUtils.getInstance().handleRequestPermissionsResult(
            requireActivity(), grantResults,
            requestCode, REQUEST_CODE_FOR_WRITE_PERMISSION
        ) { selectFile() }
    }

    override fun onPDFCreationStarted() {
//        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(requireActivity())
//        mMaterialDialog!!.show()
    }
   fun dismissMaterialDialog(){
       if (mMaterialDialog != null && mMaterialDialog!!.isShowing) mMaterialDialog!!.dismiss()
   }
    override fun onPDFCreated(success: Boolean) {
        if (mMaterialDialog != null && mMaterialDialog!!.isShowing) mMaterialDialog!!.dismiss()
        if (!success) {
            StringUtils.getInstance()
                .showInLayoutSnackbar(requireActivity(), R.string.error_pdf_not_created,binding.laySnackBar)
            isEnabledisableMergeFileButton(false)
            reCreatedInstance(true)
            mTextFileUri = null
            mButtonClicked = 0
            return
        }
        StringUtils.getInstance()
            .getCusLayoutSnackbarwithAction(requireActivity(), R.string.snackbar_pdfCreated,binding.laySnackBar)
            .setAction(
                R.string.snackbar_viewAction
            ) { v -> mFileUtils!!.openFile(mPath, FileUtils.FileType.e_PDF) }.show()
        MediaScannerConnection.scanFile(
            requireContext(), arrayOf(mPath), arrayOf("application/pdf"),
            null
        )
        isEnabledisableMergeFileButton(false)
        reCreatedInstance(false)
        binding.selectTextFile.setText(R.string.select_text_file)
        binding.createPdfCard.isEnabled = false
        mTextFileUri = null
        mButtonClicked = 0
        mBuilder = TextToPDFOptions.Builder(requireActivity())
    }
    fun isEnabledisableMergeFileButton(isEnable:Boolean){
        if(isEnable) {
            binding.createPdfCard.isEnabled = true
            binding.createPdfCard.alpha = 1F
        }else{
            binding.createPdfCard.isEnabled = false
            binding.createPdfCard.alpha = 0.3F
        }
    }
    fun reCreatedInstance(isEnable:Boolean){
        mBuilder = TextToPDFOptions.Builder(requireActivity())
        addEnhancements()
        //mEnhancerList = ArrayList()
        showEnhancementOptions()
    }
    fun mergeStartedDialog() {
        try {
            mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(requireActivity())
            mMaterialDialog?.show()
        } catch (e: Exception) {
        }

    }
    val mutableStateFlow= MutableSharedFlow<Int>()
    private var job: Job? = null

    private fun txtToPdfAsync(
        fileUtil: TextToPDFUtils,
        options: TextToPDFOptions,
        mFileExtension: String?
    ): Flow<Int> {
//        job?.cancel()
        mergeStartedDialog()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                fileUtil.createPdfFromTextFile(options,
                    this@TextToPdfFragment.mFileExtension
                )
                mutableStateFlow.emit(1)
            } catch (e: java.lang.Exception) {
                mutableStateFlow.emit(2)
                e.printStackTrace()
            }

        }
        return mutableStateFlow
    }

    override fun onResume() {
        if (isPause) {
            isPause=false
//            mBuilder = TextToPDFOptions.Builder(requireActivity())
//            addEnhancements()
//            //mEnhancerList = ArrayList()
//            showEnhancementOptions()
        }
        super.onResume()
    }
   var isPause=false
    override fun onPause() {
        isPause=true
        super.onPause()
    }
    /**
     * Create a PDF from a Text File
     *
     * @param mTextToPDFOptions TextToPDFOptions Object
     * @param fileExtension     file extension represented as string
     */
    override fun onDestroy() {
        if (job!=null)
            job?.cancel()
        super.onDestroy()
    }
}