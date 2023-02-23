package com.example.alldocumentsreaderimagescanner.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.example.alldocumentsreaderimagescanner.AppSplashScreen
import com.example.alldocumentsreaderimagescanner.BuildConfig
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.ads.AppOpenAdManager
import com.example.alldocumentsreaderimagescanner.ads.OnAddLoadedListener
import com.example.alldocumentsreaderimagescanner.utils.PreDataStoreUtils.read
import com.example.alldocumentsreaderimagescanner.utils.PreDataStoreUtils.write
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel : ViewModel() {

    private var appOpenAdManager: AppOpenAdManager? = null
    private var mMaxInterstitialAd: MaxInterstitialAd? = null
    private var mInterstitialAd: InterstitialAd? = null

    var appOpenAdManagerLiveData = MutableLiveData<AppOpenAdManager>()
    var mMaxInterstitialAdLiveData = MutableLiveData<MaxInterstitialAd?>()
    var mAdmobSplashInterstitialAdLiveData = MutableLiveData<InterstitialAd?>()
    var nextActivityLiveData = MutableLiveData<Boolean>()
    var updateUILiveData = MutableLiveData<Boolean>()
    var showStartedButtonLiveData = MutableLiveData<Boolean>()

    var remoteConfigLiveData: LiveData<com.google.firebase.remoteconfig.FirebaseRemoteConfig?> =
        FirebaseRemoteConfigTrigger.remoteLiveData

    fun getRemoteConfig(activity: AppCompatActivity) = viewModelScope.launch {
        AppLovinSdk.getInstance(activity).mediationProvider = AppLovinMediationProvider.MAX
        if (BuildConfig.DEBUG) {
            AppLovinSdk.getInstance(activity).settings.testDeviceAdvertisingIds =
                listOf("9722104d-0763-4200-97c8-8e9724360a53")
        }

        AppLovinSdk.getInstance(activity).initializeSdk { }
        FirebaseRemoteConfigTrigger.fireBaseRemoteFetch(activity)
    }

    fun onSuccess(activity: AppCompatActivity) {

            when {
                Constant.ADMOB_LOADING_INTERSTITIAL_AD_ID.isNotEmpty() -> {
                    loadInterstitial(activity)
                }
                Constant.ADMOB_LOADING_APP_OPEN_AD_ID.isNotEmpty() -> {
                    appOpenAdManager = AppOpenAdManager()
                    appOpenAdManager?.loadAd(
                        activity,
                        Constant.ADMOB_LOADING_APP_OPEN_AD_ID,
                        object : OnAddLoadedListener {
                            override fun onAppOpenAddLoaded() {
                                appOpenAdManager?.let {
                                    appOpenAdManagerLiveData.value = it
                                }
                            }

                            override fun onAppOpenAddFailed() {
                                updateUI()
                            }
                        }
                    )
                }

                Constant.APPLOVIN_LOADING_INTERSTITIAL_AD_ID.isNotEmpty() ->
                    viewModelScope.launch {
                        loadMaxInterstitial(activity)
                    }
                else -> updateUI()
            }
        }


    private val TAG = "SplashViewModel"

    @SuppressLint("LogNotTimber")
    private suspend fun loadMaxInterstitial(activity: Activity) {
        withContext(Dispatchers.IO) {
            mMaxInterstitialAd = MaxInterstitialAd(
                Constant.APPLOVIN_LOADING_INTERSTITIAL_AD_ID, activity
            )
            mMaxInterstitialAd!!.setListener(object : MaxAdListener {
                override fun onAdLoaded(ad: MaxAd) {
                    mMaxInterstitialAdLiveData.postValue(mMaxInterstitialAd)
//                    Log.d(TAG, "MAX Loading Interstitial Loaded")
                }

                override fun onAdDisplayed(ad: MaxAd) {
                    mMaxInterstitialAd = null
//                    Log.d(TAG, "MAX Loading Interstitial Displayed")
                }

                override fun onAdHidden(ad: MaxAd) {
                    Constant.SHOW_AD = false
                    nextActivity()
//                    Log.d(TAG, "MAX Loading Interstitial Hidden")
                }

                override fun onAdClicked(ad: MaxAd) {
//                    Log.d(
////                        TAG, "MAX Loading Interstitial Clicked"
//                    )
                }

                override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                    mMaxInterstitialAd = null
                    updateUI()
//                    Log.e(
//                        TAG,
//                        "MAX Loading Interstitial Failed to load: " + error.message + " " + error.code
//                    )
                }

                override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
//                    Log.e(
//                        TAG,
//                        "MAX Loading Interstitial Failed to Display: " + error.message + " " + error.code
//                    )
                    mMaxInterstitialAd = null
                }
            })
            mMaxInterstitialAd!!.loadAd()
//            Log.d(TAG, "MAX Loading Interstitial Requested")
        }
    }

    var adUnitId: String = ""
     fun loadInterstitial(activity: Activity) {
            adUnitId = if (BuildConfig.DEBUG)
                activity.resources.getString(R.string.admob_interstitial)
            else
                Constant.ADMOB_LOADING_INTERSTITIAL_AD_ID
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(activity, adUnitId, adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        Log.e(TAG, "onAdLoaded: ")
                        mAdmobSplashInterstitialAdLiveData.postValue(interstitialAd)
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        // Handle the error
                        Log.e(TAG, "onAdFailedToLoad: ")
                        mInterstitialAd = null
                        updateUI()
                    }
                })

        }


    private fun updateUI() {
        updateUILiveData.value = true
    }

    private fun nextActivity() {
        nextActivityLiveData.postValue(true)
    }

    fun initIsSucessRemoteData(
        remoteConfig: FirebaseRemoteConfig?,
        appSplashScreen: AppSplashScreen
    ) {
        if (remoteConfig != null) {
            setSaveRemoteData(remoteConfig, appSplashScreen)
        } else {
            getSavedRemoteData(appSplashScreen)
        }

    }

    private fun setSaveRemoteData(
        remoteConfig: FirebaseRemoteConfig,
        appSplashScreen: AppSplashScreen
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            appSplashScreen.write(
                Constant.PRIVACY_POLICY_KEY,
                remoteConfig.getString(Constant.PRIVACY_POLICY_KEY)
            )

            ////////////////////////Admob Ads Data//////////////////////////////////
            //////////////////////Splash Ads Admob//////////////////////////////////
            Constant.ADMOB_LOADING_INTERSTITIAL_AD_ID =
                remoteConfig.getString(Constant.ADMOB_INTERSTITAL_LOADING_KEY)
            Constant.ADMOB_LOADING_APP_OPEN_AD_ID =
                remoteConfig.getString(Constant.ADMOB_APP_OPEN_LOADING_KEY)
            appSplashScreen.write(
                Constant.ADMOB_INTERSTITAL_LOADING_KEY,
                remoteConfig.getString(Constant.ADMOB_INTERSTITAL_LOADING_KEY)
            )
            appSplashScreen.write(
                Constant.ADMOB_APP_OPEN_LOADING_KEY,
                remoteConfig.getString(Constant.ADMOB_APP_OPEN_LOADING_KEY)

            )
            //////////////////////////////Splash Ads Finish////////////////////////////////////////////


            ////////////////////////////Banner Ads Data/////////////////////////////////////

            appSplashScreen.write(
                Constant.ADMOB_BANNER_AD_KEY,
                remoteConfig.getString(Constant.ADMOB_BANNER_AD_KEY)
            )

            //////////////////////////////Banner Ads Data/////////////////////////////////////////

            //////////////////////////Main Interstitial Ads Data/////////////////////////////////////

            appSplashScreen.write(
                Constant.ADMOB_INTERSTITIAL_AD_KEY,
                remoteConfig.getString(Constant.ADMOB_INTERSTITIAL_AD_KEY)
            )

            //////////////////////////////Main Interstitial Ads Data/////////////////////////////////////////

            ////////////////////Main Native Ads Data/////////////////////////////////////

            appSplashScreen.write(
                Constant.ADMOB_NATIVE_AD_KEY,
                remoteConfig.getString(Constant.ADMOB_NATIVE_AD_KEY)
            )

            //////////////////////////////Main Native Ads Data/////////////////////////////////////////

            ////////////////////////Admob Ads Data//////////////////////////////////


            ////////////////////////Applovin Ads Data//////////////////////////////////
            //////////////////////Splash Ads Applovin//////////////////////////////////
            Constant.APPLOVIN_LOADING_INTERSTITIAL_AD_ID =
                remoteConfig.getString(Constant.KEY_APPLOVIN_INTERSTITIAL_LOADING_AD_ID)

            appSplashScreen.write(
                Constant.KEY_APPLOVIN_INTERSTITIAL_LOADING_AD_ID,
                remoteConfig.getString(Constant.KEY_APPLOVIN_INTERSTITIAL_LOADING_AD_ID)
            )
            //////////////////////////////Splash Ads Finish////////////////////////////////////////////


            ////////////////////////////Banner Ads Data/////////////////////////////////////

            appSplashScreen.write(
                Constant.KEY_APPLOVIN_BANNER_AD_ID,
                remoteConfig.getString(Constant.KEY_APPLOVIN_BANNER_AD_ID)
            )

            //////////////////////////////Banner Ads Data/////////////////////////////////////////

            //////////////////////////Main Interstitial Ads Data/////////////////////////////////////

            appSplashScreen.write(
                Constant.KEY_APPLOVIN_INTERSTITIAL_AD_ID,
                remoteConfig.getString(Constant.KEY_APPLOVIN_INTERSTITIAL_AD_ID)
            )

            //////////////////////////////Main Interstitial Ads Data/////////////////////////////////////////

            ////////////////////Main Native Ads Data/////////////////////////////////////

            appSplashScreen.write(
                Constant.KEY_APPLOVIN_NATIVE_AD_ID,
                remoteConfig.getString(Constant.KEY_APPLOVIN_NATIVE_AD_ID)
            )

            //////////////////////////////Main Native Ads Data/////////////////////////////////////////

            ////////////////////////Applovin Ads Data//////////////////////////////////


            /** Admob Main Controls */
            Constant.KEY_ADMOB_MAIN_INTERSTITIAL =
                remoteConfig.getBoolean(Constant.KEY_ADMOB_MAIN_INTERSTITIAL_ENABLED)
            appSplashScreen.write(
                Constant.KEY_ADMOB_MAIN_INTERSTITIAL_ENABLED,
                remoteConfig.getBoolean(Constant.KEY_ADMOB_MAIN_INTERSTITIAL_ENABLED)
            )

            Constant.KEY_ADMOB_MAIN_BANNER =
                remoteConfig.getBoolean(Constant.KEY_ADMOB_MAIN_BANNER_ENABLED)
            appSplashScreen.write(
                Constant.KEY_ADMOB_MAIN_BANNER_ENABLED,
                remoteConfig.getBoolean(Constant.KEY_ADMOB_MAIN_BANNER_ENABLED)
            )

            /** Applovin Main Controls */
            Constant.APPLOVIN_MAIN_INTERSTITIAL_ENABLED =
                remoteConfig.getBoolean(Constant.KEY_APPLOVIN_MAIN_INTERSTITIAL_ENABLED)
            appSplashScreen.write(
                Constant.KEY_APPLOVIN_MAIN_INTERSTITIAL_ENABLED,
                remoteConfig.getBoolean(Constant.KEY_APPLOVIN_MAIN_INTERSTITIAL_ENABLED)
            )


            Constant.APPLOVIN_MAIN_BANNER_ENABLED =
                remoteConfig.getBoolean(Constant.KEY_APPLOVIN_MAIN_BANNER_ENABLED)
            appSplashScreen.write(
                Constant.KEY_APPLOVIN_MAIN_BANNER_ENABLED,
                remoteConfig.getBoolean(Constant.KEY_APPLOVIN_MAIN_BANNER_ENABLED)
            )

            /**
             *  Contains 0 OR 1 OR 2 (0 for disabled, 1 for admob, 2 for applovin)
             * */

            /**
             *  Interstitial Ad
             * */
            Constant.CHECK_ALL_FILES_INTERSTITIALS =
                remoteConfig.getString(Constant.ALL_FILES_INTERSTITIALS)
            appSplashScreen.write(
                Constant.ALL_FILES_INTERSTITIALS,
                remoteConfig.getString(Constant.ALL_FILES_INTERSTITIALS)
            )

            Constant.CHECK_PDF_FILES_INTERSTITIALS =
                remoteConfig.getString(Constant.PDF_FILES_INTERSTITIALS)
            appSplashScreen.write(
                Constant.PDF_FILES_INTERSTITIALS,
                remoteConfig.getString(Constant.PDF_FILES_INTERSTITIALS)
            )

            Constant.CHECK_WORD_FILES_INTERSTITIALS =
                remoteConfig.getString(Constant.WORD_FILES_INTERSTITIALS)
            appSplashScreen.write(
                Constant.WORD_FILES_INTERSTITIALS,
                remoteConfig.getString(Constant.WORD_FILES_INTERSTITIALS)
            )

            Constant.CHECK_EXCEL_FILES_INTERSTITIALS =
                remoteConfig.getString(Constant.EXCEL_FILES_INTERSTITIALS)
            appSplashScreen.write(
                Constant.EXCEL_FILES_INTERSTITIALS,
                remoteConfig.getString(Constant.EXCEL_FILES_INTERSTITIALS)
            )

            Constant.CHECK_DOCUMENTS_SCANNER_INTERSTITIALS =
                remoteConfig.getString(Constant.DOCUMENTS_SCANNER_INTERSTITIALS)
            appSplashScreen.write(
                Constant.DOCUMENTS_SCANNER_INTERSTITIALS,
                remoteConfig.getString(Constant.DOCUMENTS_SCANNER_INTERSTITIALS)
            )

            Constant.CHECK_PASSPORT_SCANNER_INTERSTITIALS =
                remoteConfig.getString(Constant.PASSPORT_SCANNER_INTERSTITIALS)
            appSplashScreen.write(
                Constant.PASSPORT_SCANNER_INTERSTITIALS,
                remoteConfig.getString(Constant.PASSPORT_SCANNER_INTERSTITIALS)
            )

            Constant.CHECK_QR_GENERATOR_INTERSTITIALS =
                remoteConfig.getString(Constant.QR_GENERATOR_INTERSTITIALS)
            appSplashScreen.write(
                Constant.QR_GENERATOR_INTERSTITIALS,
                remoteConfig.getString(Constant.QR_GENERATOR_INTERSTITIALS)
            )

            Constant.CHECK_NOTEPAD_INTERSTITIALS =
                remoteConfig.getString(Constant.NOTEPAD_INTERSTITIALS)
            appSplashScreen.write(
                Constant.NOTEPAD_INTERSTITIALS,
                remoteConfig.getString(Constant.NOTEPAD_INTERSTITIALS)
            )

            Constant.CHECK_PDF_MERGE_INTERSTITIALS =
                remoteConfig.getString(Constant.PDF_MERGE_INTERSTITIALS)
            appSplashScreen.write(
                Constant.PDF_MERGE_INTERSTITIALS,
                remoteConfig.getString(Constant.PDF_MERGE_INTERSTITIALS)
            )

            Constant.CHECK_IMAGE_TO_PDF_INTERSTITIALS =
                remoteConfig.getString(Constant.IMAGE_TO_PDF_INTERSTITIALS)
            appSplashScreen.write(
                Constant.IMAGE_TO_PDF_INTERSTITIALS,
                remoteConfig.getString(Constant.IMAGE_TO_PDF_INTERSTITIALS)
            )

            Constant.CHECK_TEXT_TO_PDF_INTERSTITIALS =
                remoteConfig.getString(Constant.TEXT_TO_PDF_INTERSTITIALS)
            appSplashScreen.write(
                Constant.TEXT_TO_PDF_INTERSTITIALS,
                remoteConfig.getString(Constant.TEXT_TO_PDF_INTERSTITIALS)
            )

            Constant.CHECK_EXCEL_TO_PDF_INTERSTITIALS =
                remoteConfig.getString(Constant.EXCEL_TO_PDF_INTERSTITIALS)
            appSplashScreen.write(
                Constant.EXCEL_TO_PDF_INTERSTITIALS,
                remoteConfig.getString(Constant.EXCEL_TO_PDF_INTERSTITIALS)
            )


            /**
             *  native Ad
             * */
            Constant.CHECK_HOME_SCREEN_NATIVE = remoteConfig.getString(Constant.HOME_SCREEN_NATIVE)
            appSplashScreen.write(
                Constant.HOME_SCREEN_NATIVE,
                remoteConfig.getString(Constant.HOME_SCREEN_NATIVE)
            )


            /**
             *  banner Ad
             * */
            Constant.CHECK_HOME_SCREEN_BANNER = remoteConfig.getString(Constant.HOME_SCREEN_BANNER)
            appSplashScreen.write(
                Constant.HOME_SCREEN_NATIVE,
                remoteConfig.getString(Constant.HOME_SCREEN_NATIVE)
            )

            Constant.CHECK_ALL_FILES_BANNER = remoteConfig.getString(Constant.ALL_FILES_BANNER)
            appSplashScreen.write(
                Constant.ALL_FILES_BANNER,
                remoteConfig.getString(Constant.ALL_FILES_BANNER)
            )

            Constant.CHECK_IMAGE_TO_PDF_BANNER =
                remoteConfig.getString(Constant.IMAGE_TO_PDF_BANNER)
            appSplashScreen.write(
                Constant.IMAGE_TO_PDF_BANNER,
                remoteConfig.getString(Constant.IMAGE_TO_PDF_BANNER)
            )

            Constant.CHECK_TEXT_TO_PDF_BANNER = remoteConfig.getString(Constant.TEXT_TO_PDF_BANNER)
            appSplashScreen.write(
                Constant.TEXT_TO_PDF_BANNER,
                remoteConfig.getString(Constant.TEXT_TO_PDF_BANNER)
            )

            Constant.CHECK_EXCEL_TO_PDF_BANNER =
                remoteConfig.getString(Constant.EXCEL_TO_PDF_BANNER)
            appSplashScreen.write(
                Constant.EXCEL_TO_PDF_BANNER,
                remoteConfig.getString(Constant.EXCEL_TO_PDF_BANNER)
            )

            Constant.CHECK_PDF_FILES_BANNER = remoteConfig.getString(Constant.PDF_FILES_BANNER)
            appSplashScreen.write(
                Constant.EXCEL_TO_PDF_BANNER,
                remoteConfig.getString(Constant.EXCEL_TO_PDF_BANNER)
            )

            Constant.CHECK_WORD_FILES_BANNER = remoteConfig.getString(Constant.WORD_FILES_BANNER)
            appSplashScreen.write(
                Constant.WORD_FILES_BANNER,
                remoteConfig.getString(Constant.WORD_FILES_BANNER)
            )

            Constant.CHECK_EXCEL_FILES_BANNER = remoteConfig.getString(Constant.EXCEL_FILES_BANNER)
            appSplashScreen.write(
                Constant.EXCEL_FILES_BANNER,
                remoteConfig.getString(Constant.EXCEL_FILES_BANNER)
            )

            Constant.CHECK_PPT_FILES_BANNER = remoteConfig.getString(Constant.PPT_FILES_BANNER)
            appSplashScreen.write(
                Constant.PPT_FILES_BANNER,
                remoteConfig.getString(Constant.PPT_FILES_BANNER)
            )

            Constant.CHECK_TEXT_FILES_BANNER = remoteConfig.getString(Constant.TEXT_FILES_BANNER)
            appSplashScreen.write(
                Constant.TEXT_FILES_BANNER,
                remoteConfig.getString(Constant.TEXT_FILES_BANNER)
            )

            Constant.CHECK_DOCUMENTS_SCANNER_BANNER =
                remoteConfig.getString(Constant.DOCUMENTS_SCANNER_BANNER)
            appSplashScreen.write(
                Constant.DOCUMENTS_SCANNER_BANNER,
                remoteConfig.getString(Constant.DOCUMENTS_SCANNER_BANNER)
            )

            Constant.CHECK_ID_CARD_SCANNER_BANNER =
                remoteConfig.getString(Constant.ID_CARD_SCANNER_BANNER)
            appSplashScreen.write(
                Constant.ID_CARD_SCANNER_BANNER,
                remoteConfig.getString(Constant.ID_CARD_SCANNER_BANNER)
            )

            Constant.CHECK_PASSPORT_SCANNER_BANNER =
                remoteConfig.getString(Constant.PASSPORT_SCANNER_BANNER)
            appSplashScreen.write(
                Constant.PASSPORT_SCANNER_BANNER,
                remoteConfig.getString(Constant.PASSPORT_SCANNER_BANNER)
            )

            Constant.CHECK_QR_GENERATOR_BANNER =
                remoteConfig.getString(Constant.QR_GENERATOR_BANNER)
            appSplashScreen.write(
                Constant.QR_GENERATOR_BANNER,
                remoteConfig.getString(Constant.QR_GENERATOR_BANNER)
            )

            Constant.CHECK_NOTEPAD_BANNER = remoteConfig.getString(Constant.NOTEPAD_BANNER)
            appSplashScreen.write(
                Constant.NOTEPAD_BANNER,
                remoteConfig.getString(Constant.NOTEPAD_BANNER)
            )

            Constant.CHECK_PDF_MERGE_BANNER = remoteConfig.getString(Constant.PDF_MERGE_BANNER)
            appSplashScreen.write(
                Constant.PDF_MERGE_BANNER,
                remoteConfig.getString(Constant.PDF_MERGE_BANNER)
            )
            showStartedButtonLiveData.postValue(true)

        }


    }

    fun getSavedRemoteData(appSplashScreenContext: AppSplashScreen) {
        viewModelScope.launch(Dispatchers.IO){

            ////////////////////////Admob Ads Data//////////////////////////////////
            //////////////////////Splash Ads Admob//////////////////////////////////

            Constant.ADMOB_LOADING_INTERSTITIAL_AD_ID =
                appSplashScreenContext.read(Constant.ADMOB_INTERSTITAL_LOADING_KEY,"")
            Log.e(TAG, "getSavedRemoteData: ${Constant.ADMOB_LOADING_INTERSTITIAL_AD_ID}")
            Constant.ADMOB_LOADING_APP_OPEN_AD_ID =
            appSplashScreenContext.read(Constant.ADMOB_APP_OPEN_LOADING_KEY,"")
            Log.e(TAG, "getSavedRemoteData: ${Constant.ADMOB_LOADING_APP_OPEN_AD_ID}")




            /** Admob Main Controls */
            Constant.KEY_ADMOB_MAIN_INTERSTITIAL =
                appSplashScreenContext.read(Constant.KEY_ADMOB_MAIN_INTERSTITIAL_ENABLED,true)


            Constant.KEY_ADMOB_MAIN_BANNER =
                appSplashScreenContext.read(Constant.KEY_ADMOB_MAIN_BANNER_ENABLED,true)



            /** Applovin Main Controls */

            Constant.APPLOVIN_MAIN_INTERSTITIAL_ENABLED =
                appSplashScreenContext.read(Constant.KEY_APPLOVIN_MAIN_INTERSTITIAL_ENABLED,true)


            Constant.APPLOVIN_MAIN_BANNER_ENABLED =
                appSplashScreenContext.read(Constant.KEY_APPLOVIN_MAIN_BANNER_ENABLED,true)



            /**
             *  Contains 0 OR 1 OR 2 (0 for disabled, 1 for admob, 2 for applovin)
             * */

            /**
             *  Interstitial Ad
             * */
            Constant.CHECK_ALL_FILES_INTERSTITIALS =
                appSplashScreenContext.read(Constant.ALL_FILES_INTERSTITIALS,"0")


            Constant.CHECK_PDF_FILES_INTERSTITIALS =
                appSplashScreenContext.read(Constant.PDF_FILES_INTERSTITIALS,"0")



            Constant.CHECK_WORD_FILES_INTERSTITIALS =
                appSplashScreenContext.read(Constant.WORD_FILES_INTERSTITIALS,"0")


            Constant.CHECK_EXCEL_FILES_INTERSTITIALS =
                appSplashScreenContext.read(Constant.EXCEL_FILES_INTERSTITIALS,"0")


            Constant.CHECK_DOCUMENTS_SCANNER_INTERSTITIALS =
                appSplashScreenContext.read(Constant.DOCUMENTS_SCANNER_INTERSTITIALS,"0")


            Constant.CHECK_PASSPORT_SCANNER_INTERSTITIALS =
                appSplashScreenContext.read(Constant.PASSPORT_SCANNER_INTERSTITIALS,"0")


            Constant.CHECK_QR_GENERATOR_INTERSTITIALS =
                appSplashScreenContext.read(Constant.QR_GENERATOR_INTERSTITIALS,"0")


            Constant.CHECK_NOTEPAD_INTERSTITIALS =
                appSplashScreenContext.read(Constant.NOTEPAD_INTERSTITIALS,"0")


            Constant.CHECK_PDF_MERGE_INTERSTITIALS =
                appSplashScreenContext.read(Constant.PDF_MERGE_INTERSTITIALS,"0")


            Constant.CHECK_IMAGE_TO_PDF_INTERSTITIALS =
                appSplashScreenContext.read(Constant.IMAGE_TO_PDF_INTERSTITIALS,"0")


            Constant.CHECK_TEXT_TO_PDF_INTERSTITIALS =
                appSplashScreenContext.read(Constant.TEXT_TO_PDF_INTERSTITIALS,"0")



            Constant.CHECK_EXCEL_TO_PDF_INTERSTITIALS =
                appSplashScreenContext.read(Constant.EXCEL_TO_PDF_INTERSTITIALS,"0")




            /**
             *  native Ad
             * */
            Constant.CHECK_HOME_SCREEN_NATIVE =
                appSplashScreenContext.read(Constant.HOME_SCREEN_NATIVE,"0")



            /**
             *  banner Ad
             * */
            Constant.CHECK_HOME_SCREEN_BANNER =
                appSplashScreenContext.read(Constant.HOME_SCREEN_NATIVE,"0")



            Constant.CHECK_ALL_FILES_BANNER =
                appSplashScreenContext.read(Constant.ALL_FILES_BANNER,"0")



            Constant.CHECK_IMAGE_TO_PDF_BANNER =
                appSplashScreenContext.read(Constant.IMAGE_TO_PDF_BANNER,"0")



            Constant.CHECK_TEXT_TO_PDF_BANNER =
                appSplashScreenContext.read(Constant.TEXT_TO_PDF_BANNER,"0")



            Constant.CHECK_EXCEL_TO_PDF_BANNER =
                appSplashScreenContext.read(Constant.EXCEL_TO_PDF_BANNER,"0")



            Constant.CHECK_PDF_FILES_BANNER =
                appSplashScreenContext.read(Constant.EXCEL_TO_PDF_BANNER,"0")



            Constant.CHECK_WORD_FILES_BANNER =
                appSplashScreenContext.read(Constant.WORD_FILES_BANNER,"0")



            Constant.CHECK_EXCEL_FILES_BANNER =
                appSplashScreenContext.read(Constant.EXCEL_FILES_BANNER,"0")


            Constant.CHECK_PPT_FILES_BANNER =
                appSplashScreenContext.read(Constant.PPT_FILES_BANNER,"0")



            Constant.CHECK_TEXT_FILES_BANNER =
                appSplashScreenContext.read(Constant.TEXT_FILES_BANNER,"0")



            Constant.CHECK_DOCUMENTS_SCANNER_BANNER =
                appSplashScreenContext.read(Constant.DOCUMENTS_SCANNER_BANNER,"0")



            Constant.CHECK_ID_CARD_SCANNER_BANNER =
                appSplashScreenContext.read(Constant.ID_CARD_SCANNER_BANNER,"0")



            Constant.CHECK_PASSPORT_SCANNER_BANNER =
                appSplashScreenContext.read(Constant.PASSPORT_SCANNER_BANNER,"0")



            Constant.CHECK_QR_GENERATOR_BANNER =
                appSplashScreenContext.read(Constant.QR_GENERATOR_BANNER,"0")



            Constant.CHECK_NOTEPAD_BANNER =
                appSplashScreenContext.read(Constant.NOTEPAD_BANNER,"0")


            Constant.CHECK_PDF_MERGE_BANNER =
                appSplashScreenContext.read(Constant.PDF_MERGE_BANNER,"0")

            showStartedButtonLiveData.postValue(true)

        }
    }

    override fun onCleared() {

        super.onCleared()
    }
}