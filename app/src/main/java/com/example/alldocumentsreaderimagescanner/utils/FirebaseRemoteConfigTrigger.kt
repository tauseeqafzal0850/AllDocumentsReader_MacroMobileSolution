package com.example.alldocumentsreaderimagescanner.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.alldocumentsreaderimagescanner.BuildConfig
import com.example.alldocumentsreaderimagescanner.utils.Constant.ADMOB_APP_OPEN_LOADING_KEY
import com.example.alldocumentsreaderimagescanner.utils.Constant.ADMOB_BANNER_AD_KEY
import com.example.alldocumentsreaderimagescanner.utils.Constant.ADMOB_INTERSTITIAL_AD_KEY
import com.example.alldocumentsreaderimagescanner.utils.Constant.ALL_FILES_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.ALL_FILES_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.DOCUMENTS_SCANNER_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.DOCUMENTS_SCANNER_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.EXCEL_FILES_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.EXCEL_FILES_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.EXCEL_TO_PDF_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.EXCEL_TO_PDF_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.HOME_SCREEN_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.ID_CARD_SCANNER_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.IMAGE_TO_PDF_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.IMAGE_TO_PDF_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.IS_CONFIG_FETCH_SUCCESS
import com.example.alldocumentsreaderimagescanner.utils.Constant.KEY_ADMOB_MAIN_BANNER_ENABLED
import com.example.alldocumentsreaderimagescanner.utils.Constant.KEY_ADMOB_MAIN_INTERSTITIAL_ENABLED
import com.example.alldocumentsreaderimagescanner.utils.Constant.KEY_APPLOVIN_BANNER_AD_ID
import com.example.alldocumentsreaderimagescanner.utils.Constant.KEY_APPLOVIN_INTERSTITIAL_AD_ID
import com.example.alldocumentsreaderimagescanner.utils.Constant.KEY_APPLOVIN_INTERSTITIAL_LOADING_AD_ID
import com.example.alldocumentsreaderimagescanner.utils.Constant.KEY_APPLOVIN_MAIN_BANNER_ENABLED
import com.example.alldocumentsreaderimagescanner.utils.Constant.KEY_APPLOVIN_MAIN_INTERSTITIAL_ENABLED
import com.example.alldocumentsreaderimagescanner.utils.Constant.NOTEPAD_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.NOTEPAD_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.PASSPORT_SCANNER_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.PASSPORT_SCANNER_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.PDF_FILES_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.PDF_FILES_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.PDF_MERGE_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.PDF_MERGE_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.PPT_FILES_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.PRIVACY_POLICY_KEY
import com.example.alldocumentsreaderimagescanner.utils.Constant.QR_GENERATOR_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.QR_GENERATOR_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.TEXT_FILES_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.TEXT_TO_PDF_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.TEXT_TO_PDF_INTERSTITIALS
import com.example.alldocumentsreaderimagescanner.utils.Constant.WORD_FILES_BANNER
import com.example.alldocumentsreaderimagescanner.utils.Constant.WORD_FILES_INTERSTITIALS
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class FirebaseRemoteConfigTrigger {

    companion object {
        const val TAG = "FirebaseRemoteConfig"
        var remoteLiveData = MutableLiveData<FirebaseRemoteConfig?>()

        @SuppressLint("LogNotTimber")
        fun fireBaseRemoteFetch(
            activity: Activity
        ) {
            FirebaseApp.initializeApp(activity)
            val remoteConfig = Firebase.remoteConfig
            var cacheInterval: Long = 3600
            if (BuildConfig.DEBUG) {
                cacheInterval = 0
            }
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = cacheInterval
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.fetchAndActivate()
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        IS_CONFIG_FETCH_SUCCESS = true
//                        Log.e(TAG, "fireBaseRemoteFetch: ${task.isSuccessful}", )
                        remoteLiveData.value = remoteConfig
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:PRIVACY_POLICY_KEY ${
//                                remoteConfig.getString(PRIVACY_POLICY_KEY)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:ADMOB_APP_OPEN_LOADING_KEY ${
//                                remoteConfig.getString(ADMOB_APP_OPEN_LOADING_KEY)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:ADMOB_BANNER_AD_KEY ${
//                                remoteConfig.getString(ADMOB_BANNER_AD_KEY)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:ADMOB_INTERSTITIAL_AD_KEY ${
//                                remoteConfig.getString(ADMOB_INTERSTITIAL_AD_KEY)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:KEY_APPLOVIN_BANNER_AD_ID ${
//                                remoteConfig.getString(KEY_APPLOVIN_BANNER_AD_ID)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:KEY_APPLOVIN_INTERSTITIAL_AD_ID ${
//                                remoteConfig.getString(KEY_APPLOVIN_INTERSTITIAL_AD_ID)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:KEY_APPLOVIN_INTERSTITIAL_LOADING_AD_ID ${
//                                remoteConfig.getString(KEY_APPLOVIN_INTERSTITIAL_LOADING_AD_ID)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:KEY_ADMOB_MAIN_INTERSTITIAL_ENABLED ${
//                                remoteConfig.getString(KEY_ADMOB_MAIN_INTERSTITIAL_ENABLED)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:KEY_ADMOB_MAIN_BANNER_ENABLED ${
//                                remoteConfig.getString(KEY_ADMOB_MAIN_BANNER_ENABLED)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:KEY_APPLOVIN_MAIN_INTERSTITIAL_ENABLED ${
//                                remoteConfig.getString(KEY_APPLOVIN_MAIN_INTERSTITIAL_ENABLED)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:KEY_APPLOVIN_MAIN_BANNER_ENABLED ${
//                                remoteConfig.getString(KEY_APPLOVIN_MAIN_BANNER_ENABLED)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:ALL_FILES_INTERSTITIALS ${
//                                remoteConfig.getString(ALL_FILES_INTERSTITIALS)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:PDF_FILES_INTERSTITIALS ${
//                                remoteConfig.getString(PDF_FILES_INTERSTITIALS)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:WORD_FILES_INTERSTITIALS ${
//                                remoteConfig.getString(WORD_FILES_INTERSTITIALS)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:EXCEL_FILES_INTERSTITIALS ${
//                                remoteConfig.getString(EXCEL_FILES_INTERSTITIALS)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:DOCUMENTS_SCANNER_INTERSTITIALS ${
//                                remoteConfig.getString(DOCUMENTS_SCANNER_INTERSTITIALS)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:PASSPORT_SCANNER_INTERSTITIALS ${
//                                remoteConfig.getString(PASSPORT_SCANNER_INTERSTITIALS)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:QR_GENERATOR_INTERSTITIALS ${
//                                remoteConfig.getString(QR_GENERATOR_INTERSTITIALS)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:NOTEPAD_INTERSTITIALS ${
//                                remoteConfig.getString(NOTEPAD_INTERSTITIALS)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:PDF_MERGE_INTERSTITIALS ${
//                                remoteConfig.getString(PDF_MERGE_INTERSTITIALS)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:IMAGE_TO_PDF_INTERSTITIALS ${
//                                remoteConfig.getString(IMAGE_TO_PDF_INTERSTITIALS)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:TEXT_TO_PDF_INTERSTITIALS ${
//                                remoteConfig.getString(TEXT_TO_PDF_INTERSTITIALS)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:EXCEL_TO_PDF_INTERSTITIALS ${
//                                remoteConfig.getString(EXCEL_TO_PDF_INTERSTITIALS)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:HOME_SCREEN_BANNER ${
//                                remoteConfig.getString(HOME_SCREEN_BANNER)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:ALL_FILES_BANNER ${
//                                remoteConfig.getString(ALL_FILES_BANNER)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:IMAGE_TO_PDF_BANNER ${
//                                remoteConfig.getString(IMAGE_TO_PDF_BANNER)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:TEXT_TO_PDF_BANNER ${
//                                remoteConfig.getString(TEXT_TO_PDF_BANNER)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:EXCEL_TO_PDF_BANNER ${
//                                remoteConfig.getString(EXCEL_TO_PDF_BANNER)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:PDF_FILES_BANNER ${
//                                remoteConfig.getString(PDF_FILES_BANNER)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:WORD_FILES_BANNER ${
//                                remoteConfig.getString(WORD_FILES_BANNER)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:EXCEL_FILES_BANNER ${
//                                remoteConfig.getString(EXCEL_FILES_BANNER)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:PPT_FILES_BANNER ${
//                                remoteConfig.getString(PPT_FILES_BANNER)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:TEXT_FILES_BANNER ${
//                                remoteConfig.getString(TEXT_FILES_BANNER)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:DOCUMENTS_SCANNER_BANNER ${
//                                remoteConfig.getString(DOCUMENTS_SCANNER_BANNER)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:ID_CARD_SCANNER_BANNER ${
//                                remoteConfig.getString(ID_CARD_SCANNER_BANNER)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:PASSPORT_SCANNER_BANNER ${
//                                remoteConfig.getString(PASSPORT_SCANNER_BANNER)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:QR_GENERATOR_BANNER ${
//                                remoteConfig.getString(QR_GENERATOR_BANNER)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:NOTEPAD_BANNER ${
//                                remoteConfig.getString(NOTEPAD_BANNER)
//                            }"
//                        )
//                        Log.e(
//                            TAG,
//                            "fireBaseRemoteFetch:PDF_MERGE_BANNER ${
//                                remoteConfig.getString(PDF_MERGE_BANNER)
//                            }"
//                        )

                    } else {
                        //   Log.e(TAG, "fireBaseRemoteFetch: Fail")
                        // iRemoteConfigCallBack.onFailed()
                        IS_CONFIG_FETCH_SUCCESS = true
                        Log.e(TAG, "fireBaseRemoteFetch: ${task.isSuccessful}", )
                        remoteLiveData.value = null
                    }

                }
        }

    }
}