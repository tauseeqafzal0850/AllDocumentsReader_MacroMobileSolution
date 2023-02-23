package com.example.alldocumentsreaderimagescanner.converter.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.converter.activity.ImageCropActivity
import com.example.alldocumentsreaderimagescanner.converter.activity.RearrangeImages
import com.example.alldocumentsreaderimagescanner.converter.adapter.MoreOptionImgToPdfAdapter
import com.example.alldocumentsreaderimagescanner.converter.interfaces.IConsumer
import com.example.alldocumentsreaderimagescanner.converter.interfaces.IOnItemClickListener
import com.example.alldocumentsreaderimagescanner.converter.interfaces.OnPDFCreatedInterface
import com.example.alldocumentsreaderimagescanner.converter.model.EnhancementOptionsEntity
import com.example.alldocumentsreaderimagescanner.converter.model.ImageToPDFOptions
import com.example.alldocumentsreaderimagescanner.converter.model.Watermark
import com.example.alldocumentsreaderimagescanner.converter.util.*
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.DEFAULT_BORDER_WIDTH
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.DEFAULT_COMPRESSION
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.DEFAULT_IMAGE_BORDER_TEXT
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.DEFAULT_IMAGE_SCALE_TYPE_TEXT
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.DEFAULT_PAGE_COLOR
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.DEFAULT_PAGE_SIZE
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.DEFAULT_PAGE_SIZE_TEXT
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.DEFAULT_QUALITY_VALUE
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.MASTER_PWD_STRING
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.OPEN_SELECT_IMAGES
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.RESULT
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.WRITE_PERMISSIONS
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.appName
import com.example.alldocumentsreaderimagescanner.converter.util.WatermarkUtils.Companion.getStyleNameFromFont
import com.example.alldocumentsreaderimagescanner.converter.util.WatermarkUtils.Companion.getStyleValueFromName
import com.example.alldocumentsreaderimagescanner.databinding.FragmentImageToPdfBinding
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputLayout
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Font
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE


class ImageToPdfFragment : Fragment(), IOnItemClickListener, OnPDFCreatedInterface, PickiTCallbacks {
    private val mUnarrangedImagesUri = ArrayList<String>()
    private var mPath: String? = null
    private var mSharedPreferences: SharedPreferences? = null
    private var mFileUtils: FileUtils? = null
    private var mPageSizeUtils: PageSizeUtils? = null
    private var mPageColor = 0
    private var mIsButtonAlreadyClicked = false
    private var mPdfOptions: ImageToPDFOptions? = null
    private var mMaterialDialog: MaterialDialog? = null
    private var mHomePath: String? = null
    private var mMarginTop = 50
    private var mMarginBottom = 38
    private var mMarginLeft = 50
    private var mMarginRight = 38
    private var mPageNumStyle: String? = null
    private var mChoseId = 0
    private  var _binding: FragmentImageToPdfBinding?=null
    private  val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentImageToPdfBinding.inflate(inflater, container, false)
        return binding.root

    }

    var pickiT: PickiT? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickiT = PickiT(requireContext(), this, requireActivity())


        mFileUtils = FileUtils(requireActivity())
        mPageSizeUtils = PageSizeUtils(requireActivity())

        // Initialize variables
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        mHomePath = StringUtils.getInstance().defaultStorageLocation
        // Get default values & show enhancement options
        resetValues()
        //  binding.createPdfCard.isEnabled = false
        // Check for the images received
        checkForImagesInBundle()


        if (mImagesUri.size > 0) {
            binding.tvNoOfImages.text = String.format(
                getString(R.string.images_selected), mImagesUri.size
            )
            binding.tvNoOfImages.visibility = View.VISIBLE
            binding.createPdfCard.isEnabled = true
            binding.createPdfCard.alpha = 1f
            StringUtils.getInstance().showInLayoutSnackbar(requireActivity(), R.string.successToast,binding.laySnackBar)
        } else {
            binding.tvNoOfImages.visibility = View.GONE

        }


        binding.selectImagesCard.setOnClickListener {
            startAddingImages()
        }
        binding.createPdfCard.setOnClickListener {
            createPdf()

        }

    }

    /**
     * Resets pdf creation related values & show enhancement options
     */
    private fun resetValues() {
        mPdfOptions = ImageToPDFOptions()
        mPdfOptions?.borderWidth = mSharedPreferences!!.getInt(
            DEFAULT_IMAGE_BORDER_TEXT,
            DEFAULT_BORDER_WIDTH
        )
        mPdfOptions?.setQualityString(
            mSharedPreferences?.getInt(
                DEFAULT_COMPRESSION,
                DEFAULT_QUALITY_VALUE
            ).toString()
        )

        mPdfOptions?.pageSize =
            mSharedPreferences?.getString(
                DEFAULT_PAGE_SIZE_TEXT,
                DEFAULT_PAGE_SIZE
            )

        mPdfOptions?.isPasswordProtected = false
        mPdfOptions?.mWatermarkAdded = false
        mImagesUri.clear()
        showEnhancementOptions()
        binding.tvNoOfImages.visibility = View.GONE
        ImageUtils.getInstance().mImageScaleType = mSharedPreferences?.getString(
            DEFAULT_IMAGE_SCALE_TYPE_TEXT,
            IMAGE_SCALE_TYPE_ASPECT_RATIO
        )
        mPdfOptions?.setMargins(0, 0, 0, 0)
        mPageNumStyle = mSharedPreferences?.getString(Constants.PREF_PAGE_STYLE, null)
        mSharedPreferences?.let {
            mPageColor = it.getInt(
                Constants.DEFAULT_PAGE_COLOR_ITP,
                DEFAULT_PAGE_COLOR
            )
        }

    }

    /**
     * Opens the dialog to select a save name
     */


    private fun createPdf() {
        val preFillName = mFileUtils?.getLastFileName(mImagesUri)
        val ext = getString(R.string.pdf_ext)
        mFileUtils?.openSaveDialog(preFillName, ext, object : IConsumer<String> {
            override fun accept(t: String) {
                save(t)
            }
        })
    }

    private fun save(filename: String) {
        mPdfOptions!!.setImagesUri(mImagesUri)
        mPdfOptions?.pageSize = PageSizeUtils.mPageSize
        mPdfOptions!!.setImageScaleType(ImageUtils.getInstance().mImageScaleType)
        mPdfOptions!!.setPageNumStyle(mPageNumStyle)
        mPdfOptions!!.setMasterPwd(mSharedPreferences!!.getString(MASTER_PWD_STRING, appName))
        mPdfOptions?.pageColor = (mPageColor)
        mPdfOptions?.outFileName = filename
        CreatePdf(mPdfOptions, mHomePath, this@ImageToPdfFragment).execute()
    }


    /**
     * Shows enhancement options
     */
    private fun showEnhancementOptions() {
        val mGridLayoutManager = GridLayoutManager(requireContext(), 3)
        val imageEnhancementOptionsUtilsInstance = ImageEnhancementOptionsUtils.getInstance()
        val list: ArrayList<EnhancementOptionsEntity> =
            imageEnhancementOptionsUtilsInstance.getEnhancementOptions(
                requireContext(),
                mPdfOptions
            )
        val mAdapter = MoreOptionImgToPdfAdapter(list, this@ImageToPdfFragment)
        binding.moreOptionsRV.run {
            layoutManager = mGridLayoutManager
            adapter = mAdapter
        }

    }


    /**
     * Adds images (if any) received in the bundle
     */
    private fun checkForImagesInBundle() {
        val bundle = arguments ?: return
        if (bundle.getBoolean(OPEN_SELECT_IMAGES)) startAddingImages()
        val uris: java.util.ArrayList<Parcelable> =
            bundle.getParcelableArrayList(getString(R.string.bundleKey))
                ?: return
        for (p in uris) {
            val uriRealPath = mFileUtils!!.getUriRealPath(p as Uri)
            if (uriRealPath == null) {
                StringUtils.getInstance().showInLayoutSnackbar(requireActivity(),
                    R.string.whatsappToast,binding.laySnackBar)
            } else {
                mImagesUri.add(uriRealPath)
            }
        }
    }

    private fun startAddingImages() {
        if (!mIsButtonAlreadyClicked) {
//            if (PermissionsUtils.getInstance().checkRuntimePermissions(this, WRITE_PERMISSIONS)) {
                selectImages()
                mIsButtonAlreadyClicked = true
            } else {
//                getRuntimePermissions()
            }
    }

    private fun getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(
            this,
            WRITE_PERMISSIONS,
            REQUEST_CODE_FOR_WRITE_PERMISSION
        )
    }


    /**
     * Opens Matisse activity to select Images
     */
    private fun selectImages() {
        ImagePicker.with(this)
//             .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()

//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        intent.type = "image/*"
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//        startActivityForResult(
//            intent,
//            INTENT_REQUEST_GET_IMAGES
//        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mIsButtonAlreadyClicked = false
        if (resultCode != Activity.RESULT_OK || data == null) return
        when (requestCode) {
            INTENT_REQUEST_GET_IMAGES -> {
                //   mImagesUri.clear()
                if (data.data != null) {
                    pickiT?.getPath(data.data, Build.VERSION.SDK_INT)
                } else if (data.clipData != null) {
                    val count =
                        data.clipData!!.itemCount
                    for (i in 0 until count) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        pickiT?.getPath(imageUri, Build.VERSION.SDK_INT)
                    }
                }
            }

            CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val croppedImageUris: HashMap<Int, Uri?>? =
                    data.getSerializableExtra(CropImage.CROP_IMAGE_EXTRA_RESULT) as HashMap<Int, Uri?>?
                for (i in mImagesUri.indices) {
                    if (croppedImageUris!![i] != null) {
                        mImagesUri[i] = croppedImageUris[i]!!.path!!
                        StringUtils.getInstance()
                            .showInLayoutSnackbar(requireActivity(), R.string.snackbar_imagecropped,binding.laySnackBar)
                    }
                }
            }

            INTENT_REQUEST_REARRANGE_IMAGE -> {
                data.getStringArrayListExtra(RESULT)?.let {
                    mImagesUri = it
                }
                if (mUnarrangedImagesUri != mImagesUri && mImagesUri.size > 0) {
                    binding.tvNoOfImages.text = String.format(
                        getString(R.string.images_selected),
                        mImagesUri.size
                    )
                    StringUtils.getInstance()
                        .showInLayoutSnackbar(requireActivity(), R.string.images_rearranged,binding.laySnackBar)
                    mUnarrangedImagesUri.clear()
                    mUnarrangedImagesUri.addAll(mImagesUri)
                }
                if (mImagesUri.size == 0) {
                    binding.tvNoOfImages.visibility = View.GONE
                    binding.createPdfCard.isEnabled = false
                    binding.createPdfCard.alpha = 0.4f
                }
            }
        }


    }


    private fun addWatermark() {
        val dialog: MaterialDialog = MaterialDialog.Builder(requireContext())
            .title(R.string.add_watermark)
            .customView(R.layout.add_watermark_dialog, true)
            .positiveText(android.R.string.ok)
            .negativeText(android.R.string.cancel)
            .neutralText(R.string.remove_dialog)
            .build()
        val positiveAction: View = dialog.getActionButton(DialogAction.POSITIVE)
        val neutralAction: View = dialog.getActionButton(DialogAction.NEUTRAL)
        val watermark = Watermark()
        val watermarkTextInput = dialog.customView!!.findViewById<EditText>(R.id.watermarkText)
        val angleInput = dialog.customView!!.findViewById<EditText>(R.id.watermarkAngle)
        val colorPickerInput: ColorPickerView =
            dialog.customView!!.findViewById(R.id.watermarkColor)
        val fontSizeInput = dialog.customView!!.findViewById<EditText>(R.id.watermarkFontSize)
        val fontFamilyInput = dialog.customView!!.findViewById<Spinner>(R.id.watermarkFontFamily)
        val styleInput = dialog.customView!!.findViewById<Spinner>(R.id.watermarkStyle)
        val fontFamilyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item, Font.FontFamily.values()
        )
        fontFamilyInput.adapter = fontFamilyAdapter
        val styleAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_dropdown_item,
            requireActivity().resources.getStringArray(R.array.fontStyles)
        )
        styleInput.adapter = styleAdapter
        if (mPdfOptions!!.mWatermarkAdded) {
            watermarkTextInput.setText(mPdfOptions!!.mWatermark?.watermarkText)
            mPdfOptions!!.mWatermark?.rotationAngle?.let {
                angleInput.setText(
                    java.lang.String.valueOf(
                        it
                    )
                )
            }
            mPdfOptions!!.mWatermark?.textSize?.let {
                try {
                    fontSizeInput.setText(
                        java.lang.String.valueOf(
                            it
                        )
                    )
                } catch (e: Exception) {
                }

            }

            val color: BaseColor = mPdfOptions!!.mWatermark?.textColor!!
            //color.getRGB() returns an ARGB color
            colorPickerInput.color = color.rgb
            fontFamilyInput.setSelection(
                fontFamilyAdapter.getPosition(
                    mPdfOptions!!.mWatermark?.fontFamily
                )
            )
            styleInput.setSelection(
                styleAdapter.getPosition(
                    getStyleNameFromFont(mPdfOptions!!.mWatermark!!.fontStyle)
                )
            )
        } else {
            angleInput.setText("0")
            fontSizeInput.setText("50")
        }
        watermarkTextInput.addTextChangedListener(
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

                        StringUtils.getInstance()
                            .showInLayoutSnackbar(requireActivity(), R.string.snackbar_cannot_be_blank,binding.laySnackBar)
                    } else {

                        watermark.watermarkText = (input.toString())
                        showEnhancementOptions()
                    }
                }
            })
        neutralAction.isEnabled = mPdfOptions!!.mWatermarkAdded
        positiveAction.isEnabled = mPdfOptions!!.mWatermarkAdded
        neutralAction.setOnClickListener { v: View? ->
            mPdfOptions!!.mWatermarkAdded = (false)
            showEnhancementOptions()
            dialog.dismiss()
            StringUtils.getInstance().showInLayoutSnackbar(requireActivity(), R.string.watermark_remove,binding.laySnackBar)
        }
        positiveAction.setOnClickListener { v: View? ->
            watermark.watermarkText = (watermarkTextInput.text.toString())
            watermark.fontFamily = (fontFamilyInput.selectedItem as Font.FontFamily)
            watermark.fontStyle = (getStyleValueFromName(styleInput.selectedItem as String))
            watermark.rotationAngle = (
                    StringUtils.getInstance().parseIntOrDefault(angleInput.text, 0)
                    )
            watermark.textSize = (
                    StringUtils.getInstance().parseIntOrDefault(fontSizeInput.text, 50)
                    )
            watermark.textColor = (
                    BaseColor(
                        Color.red(colorPickerInput.color),
                        Color.green(colorPickerInput.color),
                        Color.blue(colorPickerInput.color),
                        Color.alpha(colorPickerInput.color)
                    )
                    )
            mPdfOptions!!.mWatermark = (watermark)
            mPdfOptions!!.mWatermarkAdded = (true)
            showEnhancementOptions()
            dialog.dismiss()
            StringUtils.getInstance().showInLayoutSnackbar(requireActivity(), R.string.watermark_added,binding.laySnackBar)
        }
        dialog.show()
    }

    companion object {
        var mImagesUri = ArrayList<String>()
        private const val INTENT_REQUEST_REARRANGE_IMAGE = 12
        private const val INTENT_REQUEST_GET_IMAGES = 2404
    }

    override fun onItemClick(position: Int) {
        if (mImagesUri.size == 0) {
            StringUtils.getInstance().showInLayoutSnackbar(requireActivity(), R.string.snackbar_no_images,binding.laySnackBar)
            return
        }
        when (position) {
            0 -> passwordProtectPDF()
            1 -> cropImage()
            2 -> mPageSizeUtils!!.showPageSizeDialog(false)
//            3 -> ImageUtils.getInstance().showImageScaleTypeDialog(requireActivity(), false)
            3 -> startActivityForResult(
                RearrangeImages.getStartIntent(requireActivity(), mImagesUri),
                INTENT_REQUEST_REARRANGE_IMAGE)

            4 ->addMargins()
            5 -> addPageNumbers()
            6 ->  addWatermark()
            7 -> setPageColor()
        }
    }

    private fun setPageColor() {
        val materialDialog: MaterialDialog = MaterialDialog.Builder(requireContext())
            .title(R.string.page_color)
            .customView(R.layout.dialog_color_chooser, true)
            .positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .onPositive { dialog: MaterialDialog, which: DialogAction? ->
                val view = dialog.customView
                val colorPickerView: ColorPickerView = view!!.findViewById(R.id.color_picker)
                val defaultCheckbox = view.findViewById<CheckBox>(R.id.set_default)
                mPageColor = colorPickerView.color
                if (defaultCheckbox.isChecked) {
                    val editor = mSharedPreferences!!.edit()
                    editor.putInt(Constants.DEFAULT_PAGE_COLOR_ITP, mPageColor)
                    editor.apply()
                }
            }
            .build()
        val colorPickerView: ColorPickerView =
            materialDialog.customView!!.findViewById(R.id.color_picker)
        colorPickerView.color = mPageColor
        materialDialog.show()
    }

    private fun addPageNumbers() {
        val editor = mSharedPreferences!!.edit()
        mPageNumStyle = mSharedPreferences!!.getString(Constants.PREF_PAGE_STYLE, null)
        mChoseId = mSharedPreferences!!.getInt(Constants.PREF_PAGE_STYLE_ID, -1)
        val dialogLayout = layoutInflater
            .inflate(R.layout.add_pgnum_dialog, null) as RelativeLayout
        val rbOpt1 = dialogLayout.findViewById<RadioButton>(R.id.page_num_opt1)
        val rbOpt2 = dialogLayout.findViewById<RadioButton>(R.id.page_num_opt2)
        val rbOpt3 = dialogLayout.findViewById<RadioButton>(R.id.page_num_opt3)
        val rg = dialogLayout.findViewById<RadioGroup>(R.id.radioGroup)
        val cbDefault = dialogLayout.findViewById<CheckBox>(R.id.set_as_default)
        if (mChoseId > 0) {
            cbDefault.isChecked = true
            rg.clearCheck()
            rg.check(mChoseId)
        }
        val materialDialog: MaterialDialog = MaterialDialog.Builder(requireContext())
            .title(R.string.choose_page_number_style)
            .customView(dialogLayout, false)
            .positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .neutralText(R.string.remove_dialog)
            .onPositive(SingleButtonCallback { dialog: MaterialDialog?, which: DialogAction? ->
                val checkedRadioButtonId = rg.checkedRadioButtonId
                mChoseId = checkedRadioButtonId
                if (checkedRadioButtonId == rbOpt1.id) {
                    mPageNumStyle = Constants.PG_NUM_STYLE_PAGE_X_OF_N
                } else if (checkedRadioButtonId == rbOpt2.id) {
                    mPageNumStyle = Constants.PG_NUM_STYLE_X_OF_N
                } else if (checkedRadioButtonId == rbOpt3.id) {
                    mPageNumStyle = Constants.PG_NUM_STYLE_X
                }
                if (cbDefault.isChecked) {
                    SharedPreferencesUtil
                        .setDefaultPageNumStyle(editor, mPageNumStyle, mChoseId)
                } else {
                    SharedPreferencesUtil.clearDefaultPageNumStyle(editor)
                }
            })
            .onNeutral { dialog: MaterialDialog?, which: DialogAction? ->
                mPageNumStyle = null
            }
            .build()
        materialDialog.show()
    }

    private fun addMargins() {
        val materialDialog: MaterialDialog = MaterialDialog.Builder(requireContext())
            .title(R.string.add_margins)
            .customView(R.layout.add_margins_dialog, false)
            .positiveText(R.string.ok)
            .negativeText(R.string.cancel)
            .onPositive { dialog: MaterialDialog, which: DialogAction? ->
                val view = dialog.customView
                val top = view!!.findViewById<EditText>(R.id.topMarginEditText)
                val bottom = view.findViewById<EditText>(R.id.bottomMarginEditText)
                val right = view.findViewById<EditText>(R.id.rightMarginEditText)
                val left = view.findViewById<EditText>(R.id.leftMarginEditText)
                try {
                    mMarginTop = StringUtils.getInstance().parseIntOrDefault(top.text, 0)
                    mMarginBottom = StringUtils.getInstance().parseIntOrDefault(bottom.text, 0)
                    mMarginRight = StringUtils.getInstance().parseIntOrDefault(right.text, 0)
                    mMarginLeft = StringUtils.getInstance().parseIntOrDefault(left.text, 0)
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }

                mPdfOptions!!.setMargins(mMarginTop, mMarginBottom, mMarginRight, mMarginLeft)
            }.build()
        materialDialog.show()
    }


    private fun passwordProtectPDF() {
        val dialog: MaterialDialog = MaterialDialog.Builder(requireActivity())
            .title(R.string.set_password)
            .customView(R.layout.custom_dialog, true)
            .positiveText(android.R.string.ok)
            .negativeText(android.R.string.cancel)
            .neutralText(R.string.remove_dialog)
            .build()
        val positiveAction: View = dialog.getActionButton(DialogAction.POSITIVE)
        val neutralAction: View = dialog.getActionButton(DialogAction.NEUTRAL)
        val passwordInput = dialog.customView!!.findViewById<EditText>(R.id.password)
        val passwordLayout = dialog.customView?.findViewById<TextInputLayout>(R.id.passwordlayout)
        passwordInput.setText(mPdfOptions?.password)
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
                    if (s.toString().length > 1) {
                        passwordLayout?.hint = ""
                    } else {
                        passwordLayout?.hint = getString(R.string.example_pass_123)
                    }
                }
            })
        positiveAction.setOnClickListener {
            if (StringUtils.getInstance().isEmpty(passwordInput.text)) {
                StringUtils.getInstance()
                    .showInLayoutSnackbar(requireActivity(), R.string.snackbar_password_cannot_be_blank,binding.laySnackBar)
            } else {
                mPdfOptions?.password = (passwordInput.text.toString())
                mPdfOptions?.isPasswordProtected = (true)
                showEnhancementOptions()
                dialog.dismiss()
            }
        }
        if (StringUtils.getInstance().isNotEmpty(mPdfOptions?.password)) {
            neutralAction.setOnClickListener {
                mPdfOptions?.password = null
                mPdfOptions?.isPasswordProtected = (false)
                showEnhancementOptions()
                dialog.dismiss()
                StringUtils.getInstance().showInLayoutSnackbar(requireActivity(), R.string.password_remove,binding.laySnackBar)
            }
        }
        dialog.show()
        positiveAction.isEnabled = false
    }

    private fun cropImage() {
        val intent = Intent(requireContext(), ImageCropActivity::class.java)
        startActivityForResult(intent, CROP_IMAGE_ACTIVITY_REQUEST_CODE)
    }

    override fun onPDFCreationStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(requireActivity())
        mMaterialDialog?.show()
    }

    override fun onPDFCreated(success: Boolean, path: String?) {
        if (mMaterialDialog != null && mMaterialDialog!!.isShowing) mMaterialDialog!!.dismiss()
        if (!success) {
            StringUtils.getInstance()
                .showInLayoutSnackbar(requireActivity(), R.string.snackbar_folder_not_created,binding.laySnackBar)
            return
        }
        mPath = path
        StringUtils.getInstance()
            .getCusLayoutSnackbarwithAction(requireActivity(), R.string.snackbar_pdfCreated,binding.laySnackBar)
            .setAction(
                R.string.snackbar_viewAction
            ) { mFileUtils!!.openFile(mPath, FileUtils.FileType.e_PDF) }.show()
        MediaScannerConnection.scanFile(
            requireContext(), arrayOf(path), arrayOf("application/pdf"),
            null
        )
        isCreateCardEnable(false)
        //binding.createPdfCard.isClickable = false

        resetValues()
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
        path?.let {
            if (wasSuccessful) {
                mImagesUri.add(it)
                //    mUnarrangedImagesUri.clear()
                mUnarrangedImagesUri.addAll(mImagesUri)
                if (mImagesUri.size > 0) {
                    binding.tvNoOfImages.text =
                        String.format(getString(R.string.images_selected), mImagesUri.size)
                    binding.tvNoOfImages.visibility = View.VISIBLE
                    binding.createPdfCard.isEnabled = true
                    binding.createPdfCard.alpha = 1f
                }
            }

        }

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
    override fun PickiTonMultipleCompleteListener(
        paths: java.util.ArrayList<String>?,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        paths?.let {
            if (wasSuccessful) {
                for (i in it.indices) {
                    mImagesUri.add(it[i])
                }
                mUnarrangedImagesUri.clear()
                mUnarrangedImagesUri.addAll(mImagesUri)
                if (mImagesUri.size > 0) {
                    binding.tvNoOfImages.text =
                        String.format(getString(R.string.images_selected), it.size)
                    binding.tvNoOfImages.visibility = View.VISIBLE
                    binding.createPdfCard.isEnabled = true
                    binding.createPdfCard.alpha = 1f
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        pickiT?.deleteTemporaryFile(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}