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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.converter.interfaces.OnPDFCreatedInterface
import com.example.alldocumentsreaderimagescanner.converter.util.*
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.STORAGE_LOCATION
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.WRITE_PERMISSIONS
import com.example.alldocumentsreaderimagescanner.databinding.FragmentExcelToPdfBinding
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.util.*

class ExcelToPdfFragment : Fragment(), OnPDFCreatedInterface,IPrintAction {
    lateinit var binding: FragmentExcelToPdfBinding
    private var mFileUtils: FileUtils? = null
    private var mExcelFileUri: Uri? = null
    private var mFilePath: String? = null
    private var mSavePath: String? = null
    private var mStringUtils: StringUtils? = null
    private var mButtonClicked = false
    private val mFileSelectCode = 0
    private var mMaterialDialog: MaterialDialog? = null
    private var mSharedPreferences: SharedPreferences? = null
    private var mPasswordProtected = false
    private var mPassword: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mFileUtils = FileUtils(requireActivity())
        mStringUtils = StringUtils.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExcelToPdfBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        binding.createPdfCard.isEnabled = false

        binding.selectExcelCard.setOnClickListener {
            selectExcelFile()
        }

        binding.passwordProtectCard.setOnClickListener {
            if (!binding.createPdfCard.isEnabled) {
                mStringUtils?.showInLayoutSnackbar(requireActivity(), R.string.no_excel_file,binding.laySnackBar)
                return@setOnClickListener
            }
            setPassword()
        }
        binding.createPdfCard.setOnClickListener {
            openExcelToPdfDialog()
        }

    }

    private fun selectExcelFile() {
        if (!mButtonClicked) {
            val uri = Uri.parse(Environment.getRootDirectory().toString() + "/")
            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                uri = FileProvider.getUriForFile(requireContext(), "com.camerascanner.documents.pdf.viewer.provider", Environment.getRootDirectory())
//            } else {
//                uri = Uri.fromFile(Environment.getRootDirectory());
//            }
//            var type = "application/vnd.ms-excel"
//            if (pathToFile.endsWith(".xls")) {
//                type = "application/vnd.ms-excel"
//            } else if (pathToFile.endsWith(".xlsx")) {
//                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
//            }
           intent.setDataAndType(uri,"*/*")
//           intent.setType("*/*")
            val mimeTypes = arrayOf(
               "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.template"
            )
//
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            try {
                startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.select_excel)),
                    mFileSelectCode
                )
            } catch (ex: ActivityNotFoundException) {
                mStringUtils?.showInLayoutSnackbar(requireActivity(), R.string.install_file_manager,binding.laySnackBar)
            }
            mButtonClicked = true
        }
    }

    private fun openExcelToPdfDialog() {
        if (PermissionsUtils.getInstance().checkRuntimePermissions(this, WRITE_PERMISSIONS)) {
            openExcelToPdf()
        } else {
            getRuntimePermissions()
        }
    }

    private fun openExcelToPdf() {
        MaterialDialog.Builder(requireContext())
            .title(R.string.creating_pdf)
            .content(R.string.enter_file_name)
            .input(
                getString(R.string.example), null
            ) { dialog: MaterialDialog?, input: CharSequence ->
                if (mStringUtils!!.isEmpty(input)) {
                    mStringUtils!!.showInLayoutSnackbar(requireActivity(), R.string.snackbar_name_not_blank,binding.laySnackBar)
                } else {
                    val inputName = input.toString()
                    if (!mFileUtils!!.isFileExist(inputName + getString(R.string.pdf_ext))) {
                        convertToPdf(inputName)
                    } else {
                        val builder: MaterialDialog.Builder =
                            DialogUtils.getInstance().createOverwriteDialog(requireActivity())
                        builder.onPositive { dialog12: MaterialDialog?, which: DialogAction? ->
                            convertToPdf(
                                inputName
                            )
                        }
                            .onNegative { dialog1: MaterialDialog?, which: DialogAction? -> openExcelToPdf() }
                            .show()
                    }
                }
            }
            .show()
    }

    private fun convertToPdf(mFilename: String) {
        val mStorePath = mSharedPreferences!!.getString(
            STORAGE_LOCATION,
            mStringUtils?.defaultStorageLocation
        )
        mSavePath = mStorePath + mFilename + getString(R.string.pdf_ext)
        ExcelToPDFAsync(
            mFilePath,
            mSavePath,
            this,
            mPasswordProtected,
            mPassword
        ).execute()
//        ExcelToPdfConverter.getInstance().JodConvertFileToSheetFiles(File(mFilePath.toString()),File(mSavePath.toString()))
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
        permissions: Array<String?>, grantResults: IntArray
    ) {
        PermissionsUtils.getInstance().handleRequestPermissionsResult(
            requireActivity(), grantResults,
            requestCode, REQUEST_CODE_FOR_WRITE_PERMISSION
        ) { this.openExcelToPdf() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mButtonClicked = false
        if (requestCode == mFileSelectCode) {
            if (resultCode == Activity.RESULT_OK) {
                mExcelFileUri = data?.data
//               mRealPath = RealPathUtil.getInstance().getRealPath(context, mExcelFileUri)
                mExcelFileUri?.let {
                    mFilePath = FileUtilsForFd.pathCreateFile(requireContext(),mExcelFileUri!!).toString()
                }
                processUri()

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setPassword() {
        val builder = DialogUtils.getInstance()
            .createCustomDialogWithoutContent(requireActivity(), R.string.set_password)
        val dialog = builder
            .customView(R.layout.custom_dialog, true)
            .neutralText(R.string.remove_dialog)
            .build()
        val positiveAction: View = dialog.getActionButton(DialogAction.POSITIVE)
        val neutralAction: View = dialog.getActionButton(DialogAction.NEUTRAL)
        val passwordLayout = dialog.customView?.findViewById<TextInputLayout>(R.id.passwordlayout)
        val passwordInput =
            Objects.requireNonNull(dialog.customView)?.findViewById<EditText>(R.id.password)
        passwordInput?.setText(mPassword)
        passwordInput?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                positiveAction.isEnabled = s.toString().trim { it <= ' ' }.isNotEmpty()
            }

            override fun afterTextChanged(input: Editable?) {
                if (mStringUtils!!.isEmpty(input)) {
                    passwordLayout?.hint = getString(R.string.example_pass_123)
                    mStringUtils!!.showInLayoutSnackbar(
                        requireActivity(),
                        R.string.snackbar_password_cannot_be_blank,
                        binding.laySnackBar
                    )
                } else {
                    passwordLayout?.hint = ""
                    mPassword = input.toString()
                    mPasswordProtected = true
                    binding.optionImg.setImageResource(R.drawable.ic_password_protect)
                }
            }
        })
        if (mStringUtils!!.isNotEmpty(mPassword)) {
            neutralAction.setOnClickListener {
                mPassword = null
                binding.optionImg.setImageResource(R.drawable.ic_password_protect)
                mPasswordProtected = false
                dialog.dismiss()
                mStringUtils!!.showInLayoutSnackbar(requireActivity(), R.string.password_remove,binding.laySnackBar)
            }
        }
        dialog.show()
        positiveAction.isEnabled = false
    }



    private fun processUri() {
        mStringUtils!!.showInLayoutSnackbar(requireActivity(), R.string.excel_selected,binding.laySnackBar)
        var fileName = mFileUtils!!.getFileName(mExcelFileUri!!)
        if (fileName != null && !fileName.endsWith(Constants.excelExtension) &&
            !fileName.endsWith(Constants.excelWorkbookExtension)
        ) {
            mStringUtils?.showInLayoutSnackbar(requireActivity(), R.string.extension_not_supported,binding.laySnackBar)
            return
        }
        fileName = resources.getString(R.string.excel_selected) + fileName
        binding.tvExcelFileNameBottom.text = fileName
        binding.tvExcelFileNameBottom.visibility = View.VISIBLE
        isCreateCardEnable(true)
    }

    override fun onPDFCreationStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(requireActivity())
        mMaterialDialog?.show()
    }
    fun isCreateCardEnable(isEnable:Boolean){
        if(isEnable) {
            binding.createPdfCard.isEnabled = true
            binding.createPdfCard.alpha = 1F
        }else{
            binding.createPdfCard.isEnabled = false
            binding.createPdfCard.alpha = 0.3F
        }
    }
    override fun onPDFCreated(success: Boolean, path: String?) {
        if (mMaterialDialog != null && mMaterialDialog!!.isShowing) mMaterialDialog!!.dismiss()
        if (!success) {
            mStringUtils?.showInLayoutSnackbar(requireActivity(), R.string.error_pdf_not_created,binding.laySnackBar)
            binding.tvExcelFileNameBottom.visibility = View.GONE
            isCreateCardEnable(false)
            mExcelFileUri = null
            return
        }
        mStringUtils!!.getCusLayoutSnackbarwithAction(requireActivity(), R.string.snackbar_pdfCreated,binding.laySnackBar)
            .setAction(
                R.string.snackbar_viewAction
            ) { mFileUtils!!.openFile(mSavePath, FileUtils.FileType.e_PDF) }
            .show()
        MediaScannerConnection.scanFile(
            requireContext(), arrayOf(path), arrayOf("application/pdf"),
            null
        )
        isCreateCardEnable(false)
        binding.tvExcelFileNameBottom.visibility = View.GONE
        mExcelFileUri = null
        mPasswordProtected = false
    }

    override fun print(success: Boolean, str: String?) {
        onPDFCreated(success,str)
    }


    /*fun convertExcelToPdf(excelFilePath: String?, pdfFilePath: String?) {
        // Load the Excel file
        val workbook: Workbook = WorkbookFactory.create(File(excelFilePath))

        // Create a PDF document
        val document = Document()
        PdfWriter.getInstance(document, FileOutputStream(pdfFilePath))
        document.open()

        // Iterate through the sheets in the Excel file
        for (i in 0 until workbook.getNumberOfSheets()) {
            val sheet: Sheet = workbook.getSheetAt(i)

            // Create a table
            val table = PdfPTable(sheet.getRow(0).getLastCellNum())

            // Iterate through the rows in the sheet
            for (row in sheet) {
                // Iterate through the cells in the row
                for (cell in row) {
                    // Add the cell's value to the table
                    table.addCell(cell.toString())
                }
            }

            // Add the table to the PDF document
            document.add(table)
        }

        // Close the document
        document.close()

        // Close the workbook
        workbook.close()
    }*/

}