package com.example.alldocumentsreaderimagescanner.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.example.alldocumentsreaderimagescanner.HomeActivity
import com.example.alldocumentsreaderimagescanner.utils.Constant
import com.example.alldocumentsreaderimagescanner.utils.MyApp
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class InterstitialAdManager private constructor(private val context: Context) {

    private var interstitialAdMax: MaxInterstitialAd? = null
    private var mAdmobEnable = false
    private var mMaxEnable = false
    private var isAdmobAdAlreadyLoaded = false
    private var interstitialAdListener: AdCallback? = null

    private var mInterstitialAdAdMob: com.google.android.gms.ads.interstitial.InterstitialAd? = null
    public var isShowing = false

    interface AdCallback {
        /*fun*//**//* onAdShow()*/
        fun onAdClosed()
       /* fun onAdFailed()
        fun onAdFailedToShow()*/
    }

    companion object {
        const val TAG = "InterstitialAdManager"
        private var instance: InterstitialAdManager? = null
//        fun with(context: Context): InterstitialAdManager {
        fun with(): InterstitialAdManager {
            if (instance == null) {
                instance = InterstitialAdManager(MyApp.applicationcontext)
            }
            return instance!!
        }

        public var isLoaded = false
    }

     fun loadInterstitial(
         admobEnabled: Boolean,
         maxEnabled: Boolean,
         formaxhomeActivity: HomeActivity,
         adUnitMax: String,
         adUnitAdmob: String
     ) {
        mAdmobEnable = admobEnabled
        mMaxEnable = maxEnabled
        if (admobEnabled && mInterstitialAdAdMob==null && Constant.SHOW_AD ) {
//            Log.e(TAG, "loadInterstitial: ${Constant.SHOW_AD}")
            admobInterstitial(context,adUnitAdmob)
        }
        if (mMaxEnable) {
            maxInterstitial(formaxhomeActivity,adUnitMax)
        }

    }

    fun setCallbackListener(callback: AdCallback) {
        interstitialAdListener = callback
    }


    @SuppressLint("LogNotTimber")
    private  fun maxInterstitial(context: Context, adUnit: String) {
//        withContext(Dispatchers.IO) {
            if (interstitialAdMax == null) {
                val interstitialId: String =adUnit
                if (interstitialId.isNotEmpty()) {

                    interstitialAdMax = MaxInterstitialAd(interstitialId, context as Activity?)
                    interstitialAdMax!!.setListener(object : MaxAdListener {
                        override fun onAdLoaded(ad: MaxAd) {
                            isLoaded = true
//                            Log.e(TAG, "MAX Interstitial Loaded")
                        }

                        override fun onAdDisplayed(ad: MaxAd) {
                            interstitialAdMax = null
//                            interstitialAdListener?.onAdShow()
//                            Log.e(TAG, "MAX Interstitial Displayed")
                        }

                        override fun onAdHidden(ad: MaxAd) {
                            interstitialAdMax?.destroy()
                            interstitialAdMax = null
                            isLoaded = false
                            isShowing = false
                            interstitialAdListener?.onAdClosed()
//                            Log.e(TAG, "MAX Interstitial Hidden")
                        }

                        override fun onAdClicked(ad: MaxAd) {
//                            Log.e(TAG, "MAX Interstitial Clicked")
                        }

                        override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                            isShowing = false
//                            interstitialAdListener?.onAdFailed()
                            interstitialAdMax?.destroy()
                            interstitialAdMax = null
//                            Log.e(
//                                TAG,
//                                "MAX Interstitial Failed to load: " + error.message + " " + error.code
//                            )
                        }

                        override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
//                            Log.e(
//                                TAG,
//                                "MAX Interstitial Failed to Display: " + error.message + " " + error.code
//                            )
                            interstitialAdListener?.onAdClosed()
                            interstitialAdMax = null
                        }
                    })
                    interstitialAdMax!!.loadAd()
//                    Log.e(TAG, "MAX Interstitial Requested $interstitialAdMax")
                }

            }
//        }
    }
//     var counterChecker=0
    private fun admobInterstitial(context: Context, adUnit: String) {
        val interstitialId: String =adUnit
        if (interstitialId.isEmpty()) {
            mInterstitialAdAdMob = null
            return
        }
        val adRequest = AdRequest.Builder().build()

        com.google.android.gms.ads.interstitial.InterstitialAd.load(context,
            interstitialId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
                    // The mInterstitialAdAdMob reference will be null until
                    // an ad is loaded.
                    isAdmobAdAlreadyLoaded = true
                    mInterstitialAdAdMob = interstitialAd
                    mInterstitialAdAdMob!!.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
//                               showLog("onAdDismissedFullScreenContent")
                                interstitialAdListener?.onAdClosed()
                                mInterstitialAdAdMob = null
                                isLoaded = false
                                isAdmobAdAlreadyLoaded = false
                                isShowing = false
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                                // Called when fullscreen content failed to show.
//                                showLog("onAdFailedToShowFullScreenContent")
                                mInterstitialAdAdMob = null
                                interstitialAdListener?.onAdClosed()
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
//                                showLog("onAdShowedFullScreenContent")

/*
                                mInterstitialAdAdMob = null
*/
                                //  mLoadingAdLay!!.visibility = View.GONE
/*
                                interstitialAdListener?.onAdShow()
*/
                            }
                        }
                   showLog("admob interstitial loaded")
                    isLoaded = true
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    //isLoaded = false
                    isShowing = false
                    isAdmobAdAlreadyLoaded = false
                   showLog("interstitialfailedtoloaded ${loadAdError.message}")
//                    interstitialAdListener?.onAdFailed()
                    mInterstitialAdAdMob = null
                }
            })

    }


    fun showInterstitial(activity: Activity, isAdmob: Boolean, isFb: Boolean) {
        isShowing = if (mAdmobEnable && isAdmob && mInterstitialAdAdMob != null && Constant.SHOW_AD) {
           showLog("admob interstitial show call")
            mInterstitialAdAdMob?.show(activity)
            true
        } else if (mMaxEnable && isFb && interstitialAdMax != null && interstitialAdMax!!.isReady) {
            showLog("max interstitial show call")
            interstitialAdMax?.showAd()
            true
        } else {
            showLog("nothing to show else call")
            interstitialAdListener?.onAdClosed()
            false
        }

    }

    @SuppressLint("LogNotTimber")
    private fun showLog(label: String) {
       Log.e(TAG, "showLabel: $label")
    }

//    fun destoryInterstitialReference() {
//        mInterstitialAdAdMob=null
//        interstitialAdMax=null
//        if (instance!=null)
//        instance=null
//    }

}