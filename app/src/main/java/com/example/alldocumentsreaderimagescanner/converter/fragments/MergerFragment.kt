package com.example.alldocumentsreaderimagescanner.converter.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.converter.adapter.MergeSelectedFileAdapter
import com.example.alldocumentsreaderimagescanner.converter.interfaces.IOnItemClickListener
import com.example.alldocumentsreaderimagescanner.converter.interfaces.IRearrangeMergeFiles
import com.example.alldocumentsreaderimagescanner.converter.interfaces.MergeFilesListener
import com.example.alldocumentsreaderimagescanner.converter.util.*
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.MASTER_PWD_STRING
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.STORAGE_LOCATION
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.WRITE_PERMISSIONS
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.appName
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.pdfExtension
import com.example.alldocumentsreaderimagescanner.databinding.FragmentMergerBinding
import com.google.android.material.textfield.TextInputLayout
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfCopy
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.FileOutputStream
import java.util.*


class MergerFragment : Fragment(), IRearrangeMergeFiles, IOnItemClickListener, MergeFilesListener,
    PickiTCallbacks {
    private var mIsPDFMerged: Boolean=false
    private val TAG: String="MergerFragment"
    private val INTENT_REQUEST_PICK_FILE_CODE = 10
    private lateinit var binding: FragmentMergerBinding
    private var mFilePaths: ArrayList<String>? = null
    private var mFileUtils: FileUtils? = null
    private var mMergeSelectedFilesAdapter: MergeSelectedFileAdapter? = null
    private var mHomePath: String? = null
    private var mPasswordProtected = false
    private var mPassword: String? = null
    private var mSharedPrefs: SharedPreferences? = null
    private var mMaterialDialog: MaterialDialog? = null
    private var mStringUtils: StringUtils? = null
    private var lvMergingStart: MutableLiveData<Boolean> = MutableLiveData()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMergerBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createPdfCard.isEnabled = false
        mFilePaths = ArrayList()

        pickiT = PickiT(requireContext(), this, requireActivity())

        mMergeSelectedFilesAdapter = MergeSelectedFileAdapter(mFilePaths, this)
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        mHomePath = mSharedPrefs!!.getString(
            STORAGE_LOCATION,
            StringUtils.getInstance().defaultStorageLocation
        )
        binding.selectedFilesRV.adapter = mMergeSelectedFilesAdapter
        getRuntimePermissions()

        binding.selectMergeCard.setOnClickListener {

            val uri = Uri.parse(Environment.getRootDirectory().toString() + "/")
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setDataAndType(uri, "application/pdf")
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            try {
                startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.pdf_type)),
                    INTENT_REQUEST_PICK_FILE_CODE
                )
            } catch (ex: ActivityNotFoundException) {
                mStringUtils?.showInLayoutSnackbar(requireActivity(), R.string.install_file_manager,binding.laySnackBar)
            }
        }

        binding.createPdfCard.setOnClickListener {
            mergeFiles()
        }

        binding.passwordProtectCard.setOnClickListener {
            if (mFilePaths!!.size == 0) {
                StringUtils.getInstance()
                    .showInLayoutSnackbar(requireActivity(), R.string.snackbar_no_pdf_selected,binding.laySnackBar)
                return@setOnClickListener
            }
            setPassword()
        }
        lvMergingStart.observe(viewLifecycleOwner) { isPos ->
            if (isAdded) {
                if (isPos) {
                    job = CoroutineScope(Dispatchers.Main).launch {
                        mergePdfAsynnc(
                            inputName, mHomePath!!, mPasswordProtected,
                            mPassword, masterpwd
                        ).flowOn(Dispatchers.Main).collect {
                            when(it){
                                1->{
                                    Log.e(TAG, "mergeFiles: ${it}")
                                }
                                2->{
                                    Log.e(TAG, "mergeFiles: ${it}")
                                    resetValues(mIsPDFMerged, mFinPath)

                                }
                                3->{
                                    Log.e(TAG, "mergeFiles: ${it}")
                                    resetValues(mIsPDFMerged, mFinPath)

                                }

                            }

                        }
                    }
                } else {
                    job?.cancel()
                    job = CoroutineScope(Dispatchers.Main).launch {
                        mergePdfAsynnc(
                            inputName, mHomePath!!, mPasswordProtected,
                            mPassword, masterpwd!!
                        ).flowOn(Dispatchers.Main).collect {
                            when(it) {
                                1 -> {
                                    Log.e(TAG, "mergeFiles: ${it}")
                                }
                                2 -> {
                                    Log.e(TAG, "mergeFiles: ${it}")
                                    resetValues(mIsPDFMerged, mFinPath)

                                }
                                3 -> {
                                    Log.e(TAG, "mergeFiles: ${it}")
                                    resetValues(mIsPDFMerged, mFinPath)

                                }

                            }

                        }
                    }
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


    override fun viewFile(path: String) {
        mFileUtils!!.openFile(path, FileUtils.FileType.e_PDF)
    }

    override fun removeFile(path: String) {
        mFilePaths?.remove(path)
        mMergeSelectedFilesAdapter?.notifyDataSetChanged()
        StringUtils.getInstance()
            .showInLayoutSnackbar(requireActivity(), R.string.pdf_removed_from_list,binding.laySnackBar)
        if (mFilePaths!!.size < 2) {
            binding.createPdfCard.isClickable = true
            binding.createPdfCard.alpha = 1f
        }
    }

    override fun moveUp(position: Int) {
        Collections.swap(mFilePaths, position, position - 1)
        mMergeSelectedFilesAdapter?.notifyDataSetChanged()
    }

    override fun moveDown(position: Int) {
        Collections.swap(mFilePaths, position, position + 1)
        mMergeSelectedFilesAdapter?.notifyDataSetChanged()
    }

    override fun onItemClick(position: Int) {
        if (mFilePaths!!.size == 0) {
            StringUtils.getInstance()
                .showInLayoutSnackbar(requireActivity(), R.string.snackbar_no_pdf_selected,binding.laySnackBar)
            return
        }
        if (position == 0) {
            setPassword()
        }
    }

    private fun setPassword() {
        val builder: MaterialDialog.Builder =
            DialogUtils.getInstance().createCustomDialogWithoutContent(
                requireActivity(),
                R.string.set_password
            )
        val dialog = builder
            .customView(R.layout.custom_dialog, true)
            .neutralText(R.string.remove_dialog)
            .build()
        val positiveAction: View = dialog.getActionButton(DialogAction.POSITIVE)
        val neutralAction: View = dialog.getActionButton(DialogAction.NEUTRAL)
        val passwordInput = dialog.customView?.findViewById<EditText>(R.id.password)
        val passwordLayout = dialog.customView?.findViewById<TextInputLayout>(R.id.passwordlayout)


        passwordInput?.setText(mPassword)
        passwordInput?.addTextChangedListener(
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

                override fun afterTextChanged(input: Editable) {

                    if (StringUtils.getInstance().isEmpty(input)) {
                        passwordLayout?.hint = getString(R.string.example_pass_123)
                        StringUtils.getInstance()
                            .showInLayoutSnackbar(
                                requireActivity(),
                                R.string.snackbar_password_cannot_be_blank,binding.laySnackBar
                            )
                    } else {
                        passwordLayout?.hint = ""
                        mPassword = input.toString()
                        mPasswordProtected = true
//                        onPasswordStatusChanges(true)
                    }
                }
            })
        if (StringUtils.getInstance().isNotEmpty(mPassword)) {
            neutralAction.setOnClickListener {
                mPassword = null
//                onPasswordStatusChanges(false)
                mPasswordProtected = false
                dialog.dismiss()
                StringUtils.getInstance().showInLayoutSnackbar(requireActivity(), R.string.password_remove,binding.laySnackBar)
            }
            positiveAction.setOnClickListener {
//                if(mPasswordProtected)
////                onPasswordStatusChanges(true)
//                else
//                    onPasswordStatusChanges(false)
                dialog.dismiss()
            }
        }
        dialog.show()
        positiveAction.isEnabled = false
    }


    private fun onPasswordStatusChanges(passwordAdded: Boolean) {
//        binding.optionImg.setImageResource(if
//                (passwordAdded)
//            R.drawable.ic_base_done_blue
//        else
//            R.drawable.ic_password_protect)
        if (passwordAdded)
        Glide.with(requireContext()).asDrawable().load(R.drawable.ic_password_protect).into(binding.optionImg)
        else
            Glide.with(requireContext()).asDrawable().load(R.drawable.ic_password_protect).into(binding.optionImg)

    }



    var pickiT: PickiT? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null || resultCode != Activity.RESULT_OK || data.data == null) return
        if (requestCode == INTENT_REQUEST_PICK_FILE_CODE) {
            //Getting Absolute Path

            pickiT?.getPath(data.data, Build.VERSION.SDK_INT)

        }
    }

    private var mCheckbtClickTag: String? = ""
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            mCheckbtClickTag = savedInstanceState.getString("savText")
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(getString(R.string.btn_sav_text), mCheckbtClickTag)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mFileUtils = FileUtils(requireActivity())
        mStringUtils = StringUtils.getInstance()
    }

    override fun resetValues(isPDFMerged: Boolean, path: String?) {
        mMaterialDialog?.dismiss()
        try {
            if (isPDFMerged) {
                isEnabledisableMergeFileButton(false)
//                onPasswordStatusChanges(false)
                StringUtils.getInstance()
                    .getCusLayoutSnackbarwithAction(requireActivity(), R.string.pdf_merged,binding.laySnackBar)
                    .setAction(
                        R.string.snackbar_viewAction
                    ) { mFileUtils!!.openFile(path, FileUtils.FileType.e_PDF) }.show()
                MediaScannerConnection.scanFile(
                    requireContext(), arrayOf(path), arrayOf("application/pdf"),
                    null
                )

            } else {
                isEnabledisableMergeFileButton(false)
//                onPasswordStatusChanges(false)
                StringUtils.getInstance()
                    .showInLayoutSnackbar(requireActivity(), R.string.file_access_error,binding.laySnackBar)
            }
            mFilePaths?.clear()
            mMergeSelectedFilesAdapter?.notifyDataSetChanged()
            mPasswordProtected = false
        } catch (e: Exception) {
        }


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
    override fun mergeStarted() {
        try {
            mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(requireActivity())
            mMaterialDialog?.show()
        } catch (e: Exception) {
        }

    }


    override fun PickiTonUriReturned() {

    }

    override fun PickiTonStartListener() {

    }

    override fun PickiTonProgressUpdate(progress: Int) {

    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        // val path = RealPathUtil.getInstance().getRealPath(context,data.data)
        path?.let {
            try {
                mFilePaths!!.add(path)
                mMergeSelectedFilesAdapter?.notifyDataSetChanged()
                StringUtils.getInstance()
                    .showInLayoutSnackbar(requireActivity(), R.string.pdf_added_to_list,binding.laySnackBar)
                if (mFilePaths!!.size > 1) {
                    binding.createPdfCard.alpha = 1f
                    binding.createPdfCard.isEnabled = true
                }
            } catch (e: Exception) {
            }

        }

    }

    override fun PickiTonMultipleCompleteListener(
        paths: ArrayList<String>?,
        wasSuccessful: Boolean,
        Reason: String?
    ) {

    }

    override fun onDestroy() {
        super.onDestroy()
        pickiT?.deleteTemporaryFile(requireContext())
        if (job!=null){
            job?.cancel()
        }
    }
    val mutableStateFlow= MutableStateFlow(1)
    private var mFilename: String? = null
    private var mFinPath: String? = null
    private var job: Job? = null
    private var masterpwd: String? = null
    private var inputName: String = " "
    private fun mergeFiles() {
        val pdfpaths = mFilePaths
        masterpwd = mSharedPrefs!!.getString(MASTER_PWD_STRING, appName)
        MaterialDialog.Builder(requireContext())
            .title(R.string.creating_pdf)
            .content(R.string.enter_file_name)
            .input(
                getString(R.string.example), null
            ) { _: MaterialDialog?, input: CharSequence ->
                inputName=input.toString()
                if (StringUtils.getInstance().isEmpty(input)) {
                    StringUtils.getInstance()
                        .showInLayoutSnackbar(requireActivity(), R.string.snackbar_name_not_blank,binding.laySnackBar)
                } else {

//                    mergeStarted()
                    if (!mFileUtils!!.isFileExist("$input${getString(R.string.pdf_ext)}")) {
//                        MergePdf(
//                            input.toString(), mHomePath, mPasswordProtected,
//                            mPassword, this, masterpwd
//                        ).execute(pdfpaths)
                        if (job!=null)
                            job?.cancel()
                        lvMergingStart.postValue(false)

                    } else {
                        val builder =
                            DialogUtils.getInstance().createOverwriteDialog(requireActivity())
                        builder.onPositive { _: MaterialDialog?, _: DialogAction? ->
//                            MergePdf(
//                                input.toString(),
//                                mHomePath, mPasswordProtected, mPassword,
//                                this, masterpwd
//                            ).execute(pdfpaths)
                            if (job!=null)
                                job?.cancel()
                            lvMergingStart.postValue(true)

                        }
                            .onNegative { _: MaterialDialog?, _: DialogAction? ->

                            }.show()
                    }
                }
            }
            .show()
    }

    private fun mergePdfAsynnc( fileName:String,homePath:String,isPasswordProtected: Boolean,
                                password:String?,  masterpwd:String?):Flow<Int> {
        mergeStarted()
        lifecycleScope.launch(Dispatchers.IO) {
            val document = Document()
            try {

                var pdfreader: PdfReader
                // Create document object
                // Create pdf copy object to copy current document to the output merged result file
                mFilename = fileName + pdfExtension
                mFinPath = homePath + mFilename
                val copy = PdfCopy(document, FileOutputStream(mFinPath))
                // Open the document
                if (isPasswordProtected) {
                    copy.setEncryption(
                        password?.toByteArray(),
                        masterpwd?.toByteArray(),
                        PdfWriter.ALLOW_PRINTING or PdfWriter.ALLOW_COPY,
                        PdfWriter.ENCRYPTION_AES_128
                    )
                }
                document.open()
                var numOfPages: Int
                if (mFilePaths!=null && mFilePaths!!.size>=2) {
                    val paths: ArrayList<String> = mFilePaths!!
                    for (pdfPath in paths) {
                        // Create pdf reader object to read each input pdf file
                        pdfreader = PdfReader(pdfPath)
                        // Get the number of pages of the pdf file
                        numOfPages = pdfreader.numberOfPages
                        for (page in 1..numOfPages)  // Import all pages from the file to PdfCopy
                            copy.addPage(copy.getImportedPage(pdfreader, page))
                    }
                }else{
                    if (document.isOpen)
                    document.close()
                    mIsPDFMerged = false
                    mutableStateFlow.emit(3)
                }
                if (document.isOpen)
                document.close()
                mIsPDFMerged = true
// close the document
                mutableStateFlow.emit(2)
            } catch (e: java.lang.Exception) {
                if (document.isOpen)
                document.close()
                mIsPDFMerged = false
                 mutableStateFlow.emit(3)
            }
        }
        return mutableStateFlow
    }
}