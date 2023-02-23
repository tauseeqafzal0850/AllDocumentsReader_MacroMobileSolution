package com.example.alldocumentsreaderimagescanner

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.example.alldocumentsreaderimagescanner.ads.BannerActivity
import com.example.alldocumentsreaderimagescanner.ads.InterstitialActivity
import com.example.alldocumentsreaderimagescanner.ads.InterstitialAdManager
import com.example.alldocumentsreaderimagescanner.converter.util.Constants
import com.example.alldocumentsreaderimagescanner.converter.util.PermissionsUtils
import com.example.alldocumentsreaderimagescanner.databinding.ActivityHomeBinding
import com.example.alldocumentsreaderimagescanner.reader.activity.FileListActivity
import com.example.alldocumentsreaderimagescanner.reader.activity.QrCodeScannerActivity
import com.example.alldocumentsreaderimagescanner.reader.models.ListViewModel
import com.example.alldocumentsreaderimagescanner.utils.*
import com.example.alldocumentsreaderimagescanner.utils.Constant.APPLOVIN_MAIN_INTERSTITIAL_ENABLED
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_ALL_FILES_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_DOCUMENTS_SCANNER_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_EXCEL_FILES_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_EXCEL_TO_PDF_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_HOME_SCREEN_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_HOME_SCREEN_NATIVE
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_IMAGE_TO_PDF_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_NOTEPAD_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_PASSPORT_SCANNER_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_PDF_FILES_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_PDF_MERGE_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_QR_GENERATOR_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_TEXT_TO_PDF_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_WORD_FILES_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.KEY_ADMOB_MAIN_INTERSTITIAL
import com.example.alldocumentsreaderimagescanner.utils.PreDataStoreUtils.read
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.scanlibraryscanner.idCardScanner.activity.IdCardScannerActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeActivity : BannerActivity(), IRemoteConfigCallBack, InterstitialAdManager.AdCallback {
    val TAG = HomeActivity::class.java.name
    private val listViewModel: ListViewModel by viewModels()
    private lateinit var bindingLayout: ActivityHomeBinding
    var isCameraFeatures = false
    override fun onCreate(savedInstanceState: Bundle?) {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.app_blue)
        bindingLayout = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(bindingLayout.root)
        super.onCreate(savedInstanceState)
//        initAdmobInitilizer()
        showBanner(CHECK_HOME_SCREEN_BANNER)

        showNative()


        bindingLayout.hc.drawerIcon.setOnClickListener {
            bindingLayout.drawerLayout.openDrawer(GravityCompat.START)
        }
        bindingLayout.navView.itemIconTintList = null

        navigationClick()

        if (checkStoragePermission()) {

            lifecycleScope.launch {

                listViewModel.run {
                    getPdfList(this@HomeActivity)?.let {
                        bindingLayout.hc.pdfCount.text = "(${it.size})"
                    }
                    getWordList(this@HomeActivity)?.let {
                        bindingLayout.hc.wordCount.text = "(${it.size})"
                    }
                    getExcelList(this@HomeActivity)?.let {
                        bindingLayout.hc.excelCount.text = "(${it.size})"
                    }
                    getPptList(this@HomeActivity)?.let {
                        bindingLayout.hc.pptCount.text = "(${it.size})"
                    }
                    getTxtList(this@HomeActivity)?.let {
                        bindingLayout.hc.textCount.text = "(${it.size})"
                    }
                }

            }

        }
        bindingLayout.hc.imgToPdfCard.setOnClickListener {
            if (checkStoragePermission()) {
                showInterstitial(1, CHECK_IMAGE_TO_PDF_INTERSTITIALS)
            } else {
                if (SDK_INT >= Build.VERSION_CODES.M)
                    checkAndAskForStoragePermission()
            }
        }
        bindingLayout.hc.textToPdfCard.setOnClickListener {
            if (checkStoragePermission()) {
                showInterstitial(2, CHECK_TEXT_TO_PDF_INTERSTITIALS)
            } else {
                if (SDK_INT >= Build.VERSION_CODES.M)
                    checkAndAskForStoragePermission()
            }
        }
        bindingLayout.hc.excelToPdfCard.setOnClickListener {
            if (checkStoragePermission()) {
                showInterstitial(3, CHECK_EXCEL_TO_PDF_INTERSTITIALS)
            } else {
                if (SDK_INT >= Build.VERSION_CODES.M)
                    checkAndAskForStoragePermission()
            }
        }

        bindingLayout.hc.allFileCard.setOnClickListener {
            showInterstitial(0, CHECK_ALL_FILES_INTERSTITIALS)
        }
        bindingLayout.hc.pdfCard.setOnClickListener {
            showInterstitial(4, CHECK_PDF_FILES_INTERSTITIALS)
        }
        bindingLayout.hc.wordCard.setOnClickListener {
            showInterstitial(5, CHECK_WORD_FILES_INTERSTITIALS)
        }
        bindingLayout.hc.excelCard.setOnClickListener {
            showInterstitial(6, CHECK_EXCEL_FILES_INTERSTITIALS)
        }
        bindingLayout.hc.powerPointCard.setOnClickListener {
            if (checkStoragePermission()) {
                startActivity(
                    Intent(this, FileListActivity::class.java).putExtra(
                        "fileType",
                        7
                    )
                )
            } else {
                if (SDK_INT >= Build.VERSION_CODES.M)
                    checkAndAskForStoragePermission()
            }
        }

        bindingLayout.hc.textCard.setOnClickListener {
            if (checkStoragePermission()) {
                startActivity(
                    Intent(this, FileListActivity::class.java).putExtra(
                        "fileType",
                        8
                    )
                )
            } else {
                if (SDK_INT >= Build.VERSION_CODES.M)
                    checkAndAskForStoragePermission()
            }
        }

        bindingLayout.hc.scannerCard.setOnClickListener {
            isCameraFeatures = true
            if (checkCameraPermission())
//                startActivity(Intent(this, MainScannerActivity::class.java))
            showInterstitial(9, CHECK_DOCUMENTS_SCANNER_INTERSTITIALS)
            else requestCameraPermission()
        }
        bindingLayout.hc.scannerIdCard.setOnClickListener {
            if (checkCameraPermission())
                startActivity(Intent(this, IdCardScannerActivity::class.java))
            else requestCameraPermission()
        }
        bindingLayout.hc.passportCard.setOnClickListener {
            isCameraFeatures = true
            if (checkCameraPermission())
                showInterstitial(11, CHECK_PASSPORT_SCANNER_INTERSTITIALS)
            else requestCameraPermission()
        }

        bindingLayout.hc.qrCodeScannerCard.setOnClickListener {
            isCameraFeatures = true
            if (checkCameraPermission())
                startActivity(Intent(this, QrCodeScannerActivity::class.java))
            else requestCameraPermission()

        }
        bindingLayout.hc.qrCodeGeneratorCard.setOnClickListener {
            isCameraFeatures = true
            if (checkCameraPermission())
                showInterstitial(13, CHECK_QR_GENERATOR_INTERSTITIALS)
            else requestCameraPermission()

        }
        bindingLayout.hc.notepadCard.setOnClickListener {
            isCameraFeatures = true
//            if (checkCameraPermission())
//                showInterstitial(14, CHECK_NOTEPAD_INTERSTITIALS)
//            else requestCameraPermission()
            showInterstitial(14, CHECK_NOTEPAD_INTERSTITIALS)


        }
        bindingLayout.hc.mergeCard.setOnClickListener {
            if (checkStoragePermission()) {
                showInterstitial(15, CHECK_PDF_MERGE_INTERSTITIALS)
            } else checkAndAskForStoragePermission()

        }
    }
    private fun initAdmobInitilizer() {
        MobileAds.initialize(this@HomeActivity) {

        }
    }
    ////////////////////////////Native Ads/////////////////////////////////////////

    private fun showNative() {
        when (CHECK_HOME_SCREEN_NATIVE) {
//        when ("1") {
            Constant.NO_AD -> {
                bindingLayout.hc.nativeAdContainer.visibility = View.GONE
            }
            Constant.AD_ADMOB -> {
                switchToRequiredAdView(true)
                requestNativeAd()
            }
            Constant.AD_MAX -> {
                switchToRequiredAdView(false)
                requestApplovinAd(
                    bindingLayout.hc.flMaxAppadPlaceholder,
                    bindingLayout.hc.loadingCardMax
                )
            }
            else -> {
                bindingLayout.hc.nativeAdContainer.visibility = View.GONE
            }


        }
    }

    private var nativeAdLoader: MaxNativeAdLoader? = null
    private var maxNativeAdView: MaxNativeAdView? = null
    private var nativeAdMaxNative: MaxAd? = null
    private fun requestApplovinAd(nativeAdLayout: FrameLayout, loadingAdLay: RelativeLayout) {
        var adUnit = ""
        lifecycleScope.launch(Dispatchers.IO) {
            adUnit = read(Constant.KEY_APPLOVIN_NATIVE_AD_ID, "")
            withContext(Dispatchers.Main) {
                if (BuildConfig.DEBUG)
                    adUnit = read(Constant.KEY_APPLOVIN_NATIVE_AD_ID, "")
                if (adUnit.isEmpty()) {
                    return@withContext
                }
                val binder: MaxNativeAdViewBinder =
                    MaxNativeAdViewBinder.Builder(R.layout.max_unified_native_ad_view)
                        .setTitleTextViewId(R.id.title_text_view)
                        .setBodyTextViewId(R.id.body_text_view)
                        .setAdvertiserTextViewId(R.id.advertiser_textView)
                        .setIconImageViewId(R.id.icon_image_view)
                        .setMediaContentViewGroupId(R.id.media_view_container)
                        .setOptionsContentViewGroupId(R.id.options_view)
                        .setCallToActionButtonId(R.id.cta_button)
                        .build()
                maxNativeAdView = MaxNativeAdView(binder, this@HomeActivity)
                nativeAdLoader = MaxNativeAdLoader(adUnit, this@HomeActivity)
                nativeAdLoader?.setNativeAdListener(object : MaxNativeAdListener() {

                    override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd) {
//                Log.e(TAG, "MAX Native Ad Loaded")
                        // Cleanup any pre-existing native ad to prevent memory leaks.
                        if (nativeAdMaxNative != null) {
                            nativeAdLoader?.destroy(nativeAdMaxNative)
                        }
                        nativeAdMaxNative = ad
                        nativeAdLayout.removeAllViews()
                        nativeAdLayout.addView(nativeAdView)
                        bindingLayout.hc.loadingCardMax.visibility = View.GONE
                    }

                    override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
//                Log.e(TAG, "Failed to load MAX Native Ad: " + error.message + " " + error.code)
                    }

                    override fun onNativeAdClicked(ad: MaxAd) {}
                })
                nativeAdLoader?.loadAd(maxNativeAdView)
            }
        }
    }

    private var mNativeAd: NativeAd? = null

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     */

    @SuppressLint("InflateParams")
    private fun requestNativeAd() {
        var adUnit=""
        lifecycleScope.launch(Dispatchers.IO) {
            adUnit = read(Constant.ADMOB_NATIVE_AD_KEY, "")
            withContext(Dispatchers.Main) {
                if (BuildConfig.DEBUG)
                    adUnit = getString(R.string.admob_native)
                    if (adUnit.isEmpty()) {
                        return@withContext
                    }
                val builder = AdLoader.Builder(this@HomeActivity, adUnit)
                    .forNativeAd { nativeAd: NativeAd? ->
                        Log.e(TAG, "requestNativeAd: loadAd")
                        if (mNativeAd != null) {
                            mNativeAd!!.destroy()
                        }
                        mNativeAd = nativeAd
                        val frameLayout = findViewById<FrameLayout>(R.id.fl_adPlaceholder)
                        val adView =
                            layoutInflater.inflate(R.layout.ad_unified_layout, null) as NativeAdView
                        populateUnifiedNativeAdView(mNativeAd, adView)
                        frameLayout.removeAllViews()
                        frameLayout.addView(adView)
                        bindingLayout.hc.loadingCard?.visibility = View.GONE
                    }
                val adLoader = builder.withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        /*Toast.makeText(MainActivity.this, "Failed to load native ad: "
                                + errorCode, Toast.LENGTH_SHORT).show();*/
                        Log.e(TAG, "onAdFailedToLoad: ")
                    }
                }).build()
                adLoader.loadAd(AdRequest.Builder().build())

            }
        }

        }





    /**
     * Populates a [NativeAdView] object with data from a given
     * [NativeAd].
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView          the view to be populated
     */
    private fun populateUnifiedNativeAdView(nativeAd: NativeAd?, adView: NativeAdView) {
        adView.findViewById<View>(R.id.loading_layout).visibility = View.GONE
        // Set the media view.
        adView.mediaView =
            adView.findViewById<View>(R.id.ad_media) as MediaView
        // Set other ad assets.
        adView.headlineView =
            adView.findViewById(R.id.ad_headline)
        adView.bodyView =
            adView.findViewById(R.id.ad_body)
        adView.callToActionView =
            adView.findViewById(R.id.ad_call_to_action)
        adView.iconView =
            adView.findViewById(R.id.ad_app_icon)
        /*adView.setPriceView(adView.findViewById(R.id.ad_price));*/
        adView.starRatingView =
            adView.findViewById(R.id.ad_stars)
        /*adView.setStoreView(adView.findViewById(R.id.ad_store));*/
        adView.advertiserView =
            adView.findViewById(R.id.ad_advertiser)

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd!!.headline
        adView.mediaView?.setMediaContent(nativeAd.mediaContent!!)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
        }
        if (nativeAd.starRating == null) {
            adView.starRatingView?.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView?.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adView.advertiserView?.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView?.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
    }

    private fun switchToRequiredAdView(view: Boolean) {
        if (view) {
            bindingLayout.hc.rlMaxNativeContainer.visibility = View.GONE
            bindingLayout.hc.rlAdmobNativeContainer.visibility = View.VISIBLE
        } else {
            bindingLayout.hc.rlMaxNativeContainer.visibility = View.VISIBLE
            bindingLayout.hc.rlAdmobNativeContainer.visibility = View.GONE
        }
    }


    ////////////////////////////Native Ads/////////////////////////////////////////

    private fun checkAndAskForStoragePermission() {
        if (!PermissionsUtils.getInstance().checkRuntimePermissions(
                this,
                Constants.READ_PERMISSIONS
            )
        ) {
            getRuntimeStorageReadPermissions()
        } else {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    askStorageManagerPermission()
                }
            }
        }
    }

    private fun getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(
            this,
            Constants.WRITE_PERMISSIONS,
            Constants.REQUEST_CODE_FOR_WRITE_PERMISSION
        )
    }
    private fun getRuntimeStorageReadPermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(
            this,
            Constants.READ_PERMISSIONS,
            Constants.REQUEST_CODE_FOR_WRITE_PERMISSION
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
            requestCode, Constants.REQUEST_CODE_FOR_WRITE_PERMISSION
        ) {
            askStorageManagerPermission()
        }
    }

    private fun askStorageManagerPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                android.app.AlertDialog.Builder(this)
                    .setTitle(R.string.one_more_thing_text)
                    .setMessage(R.string.storage_manager_permission_rationale)
                    .setCancelable(false)
                    .setPositiveButton(R.string.allow_text) { dialog, _ ->
                        val intent =
                            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        val uri =
                            Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        try {
                            startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(this, "Your device is not supported", Toast.LENGTH_SHORT)
                                .show()
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.close_app_text) { _, _ -> finishAndRemoveTask() }
                    .show()
            }
        }
    }

    private fun nextActivity(count: Int, isAdmob: Boolean, isMax: Boolean) {
        if (checkStoragePermission()) {
            startActivity(
                Intent(this, InterstitialActivity::class.java).putExtra(
                    "activityPos",
                    count
                ).putExtra("isAdmob", isAdmob)
                    .putExtra("isMax", isMax)
            )

        } else {
            if (SDK_INT >= Build.VERSION_CODES.M)
                checkAndAskForStoragePermission()
        }
    }

    private fun showInterstitial(count: Int, ad: String) {
        when (ad) {
            Constant.AD_ADMOB -> nextActivity(count, isAdmob = true, isMax = false)
            Constant.AD_MAX -> nextActivity(count, isAdmob = false, isMax = true)
            else -> {
                nextActivity(count, isAdmob = false, isMax = false)
            }
        }
    }

    private fun openBackDialog() {

        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.app_exit_dialog)

        val btnRateus = dialog.findViewById<TextView>(R.id.btnRateus)
        val btnExit = dialog.findViewById<TextView>(R.id.btnExit)

        btnRateus.setOnClickListener {
            rateUs()
            dialog.dismiss() }

        btnExit.setOnClickListener {
            dialog.dismiss()
            finishAffinity()
        }

        dialog.show()
    }

    private fun rateUs() {
        try {
            val rateIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=${packageName}")
            )
            startActivity(rateIntent)
        } catch (e: Exception) {
        }
    }
    //    clicks On Navigation Items...............
    private fun navigationClick() {
        bindingLayout.navView.setNavigationItemSelectedListener { item ->

            bindingLayout.drawerLayout.closeDrawer(GravityCompat.START)

            when (item.itemId) {


                R.id.privacy_policy -> {
                    val tv =
                        "Do you want to read our Privacy Policy?"
                    openCustomMultiTypesDialog(
                        resources.getString(R.string.yes),
                        resources.getString(R.string.no), tv,
                        DialogType.dialogPrivacy
                    )
                }
                R.id.rate_us -> {
                    val tv =
                        "Love Enjoying ${resources.getString(R.string.app_name)}, Rate Us!"
                    openCustomMultiTypesDialog(
                        resources.getString(R.string.yes),
                        resources.getString(R.string.no), tv,
                        DialogType.dialogRate
                    )

                }

                R.id.more_app -> {
                    val tv =
                        "Do you want to visit to our other apps at Goole Play Store?"
                    openCustomMultiTypesDialog(
                        resources.getString(R.string.yes),
                        resources.getString(R.string.no), tv,
                        DialogType.dialogMore
                    )
                }

                R.id.quit_app -> {
                    openBackDialog()
                }


            }
            true
        }
    }

    private fun openCustomMultiTypesDialog(
        rightText: String,
        leftText: String,
        tv: String,
        dialogType: String
    ) {
        val progressDialogBack = Dialog(this)
        progressDialogBack.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialogBack.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialogBack.setCancelable(false)

        progressDialogBack.setContentView(R.layout.custom_multi_dialog)


        val buttonSave = progressDialogBack.findViewById<TextView>(R.id.buttonSave)
        buttonSave.text = rightText
        val buttonCancel = progressDialogBack.findViewById<TextView>(R.id.buttonCancel)
        buttonCancel.text = leftText

        val fileNameTV = progressDialogBack.findViewById<TextView>(R.id.fileNameTV)
        fileNameTV.visibility = View.VISIBLE
        fileNameTV.text = tv


        buttonSave.setOnClickListener {
            progressDialogBack.dismiss()

            when (dialogType) {
                DialogType.dialogMore -> {
                    try {
                        val url = "https://play.google.com/store/apps/dev?id=5614822529253661342"
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (e: Exception) {
                    }
                }
                DialogType.dialogRate -> {
                    try {
                        val rateIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=${packageName}")
                        )
                        startActivity(rateIntent)
                    } catch (e: Exception) {
                    }
                }
                DialogType.dialogPrivacy -> {
                    showPrivacyPolicy()

                }
            }

        }

        buttonCancel.setOnClickListener { progressDialogBack.dismiss() }
        progressDialogBack.show()
    }

    private fun showPrivacyPolicy() {
        var url=""
        lifecycleScope.launch(Dispatchers.IO) {
            url = read(Constant.PRIVACY_POLICY_KEY, "")
            withContext(Dispatchers.Main) {
//                if (BuildConfig.DEBUG)
//                    adUnit = getString(R.string.admob_native)
                if (url.isEmpty()) {
                    Toast.makeText(this@HomeActivity,resources.getString(R.string.privacyPolicy_not_found),Toast.LENGTH_SHORT).show()
                }
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch (e: Exception) {
                }

            }
        }

    }

    private fun checkStoragePermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

    }

    private fun checkCameraPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    }

    private fun requestCameraPermission() {
        Dexter.withContext(this)
            .withPermission(
                Manifest.permission.CAMERA
            ).withListener(object : PermissionListener {
                override fun onPermissionGranted(p: PermissionGrantedResponse?) {
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    if (p0?.isPermanentlyDenied == true) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(this, "Error occurred ", Toast.LENGTH_SHORT).show()
            }.onSameThread()
            .check()
    }


    private fun showSettingsDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("This app needs permission to manage documents. You can grant them in app settings.")
        dialog.setTitle("Need Permission")
        dialog.setPositiveButton("Setting") { dialogInterface, _ ->
            dialogInterface.cancel()
            openSetting()
        }
        dialog.setNegativeButton("Cancel") { dialogInterface, _ ->
            dialogInterface.cancel()
        }
        dialog.show()
    }

    private fun openSetting() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", this.packageName, null)
            intent.data = uri
            startActivityForResult(intent, 420)
        } catch (e: Exception) {
        }

    }


    override fun onBackPressed() {
        openBackDialog()
    }

    override fun onDestroy() {
        if (nativeAdMaxNative != null) {
            nativeAdLoader?.destroy(nativeAdMaxNative)
        }
        if (mNativeAd != null)
            mNativeAd?.destroy()
        super.onDestroy()
        listViewModel.onClear()
    }

    var ispause = false
    override fun onPause() {
        ispause = true
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
         requestInterstitialAds()
        if (ispause) {
            ispause = false
            updateListsSizesCount()
        }

    }

    private fun requestInterstitialAds() {
        InterstitialAdManager.with().setCallbackListener(this@HomeActivity)
        var adUnitMax=""
        var adUnitAdmob=""
            lifecycleScope.launch(Dispatchers.IO) {
                adUnitAdmob = read(Constant.ADMOB_INTERSTITIAL_AD_KEY, "")
                adUnitMax = read(Constant.KEY_APPLOVIN_INTERSTITIAL_AD_ID, "")
                withContext(Dispatchers.Main) {
                if (BuildConfig.DEBUG)
                    adUnitAdmob = getString(R.string.admob_interstitial)
                    if (adUnitMax.isEmpty() && adUnitAdmob.isEmpty()) {
                        return@withContext
                    }
                    InterstitialAdManager.with().loadInterstitial(
                        KEY_ADMOB_MAIN_INTERSTITIAL,
                        APPLOVIN_MAIN_INTERSTITIAL_ENABLED, this@HomeActivity,adUnitMax,adUnitAdmob
                    )
                }
            }
    }

    private fun updateListsSizesCount() {
        bindingLayout.hc.pdfCount.text = "(${Constant.txtSizeCount})"
        bindingLayout.hc.wordCount.text = "(${Constant.wordSizeCount})"
        bindingLayout.hc.excelCount.text = "(${Constant.excelSizeCount})"
        bindingLayout.hc.pptCount.text = "(${Constant.pptSizeCount})"
        bindingLayout.hc.pdfCount.text = "(${Constant.pdfSizeCount})"


    }

    override fun onSuccess() {

    }

    override fun onFailed() {

    }

    /*override fun onAdShow() {
        showLog("onAdShow")
    }*/

    override fun onAdClosed() {
        showLog("onAdClosed")
    }

    /*    override fun onAdFailed() {
            showLog("onAdFailed")

        }

        override fun onAdFailedToShow() {
            showLog("onAdFailedToShow")
        }*/
    @SuppressLint("LogNotTimber")
    private fun showLog(label: String) {
//        Log.e(InterstitialAdManager.TAG, "showLabelHome: $label")
    }
    private fun uiReadyForShowAd() {
        updateLoadingAdUi(false)
        bindingLayout.hc.blankView!!.visibility = View.VISIBLE
    }
    private fun updateLoadingAdUi(isShow: Boolean) {
        if (isShow){
            bindingLayout.hc.layoutLoadingAd!!.visibility=View.VISIBLE
            bindingLayout.hc.homeContent.visibility=View.GONE
//            splashLayoutBinding.btnStarted.visibility=View.GONE

        }else{
            bindingLayout.hc.layoutLoadingAd!!.visibility=View.GONE
            bindingLayout.hc.homeContent.visibility=View.VISIBLE
//            splashLayoutBinding.btnStarted.visibility=View.GONE
        }
    }
}