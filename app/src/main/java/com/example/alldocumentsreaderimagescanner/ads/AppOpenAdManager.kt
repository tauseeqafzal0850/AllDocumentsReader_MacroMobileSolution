package com.example.alldocumentsreaderimagescanner.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.alldocumentsreaderimagescanner.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.*

/** Inner class that loads and shows app open ads. */
class AppOpenAdManager {

    private var appOpenAd: AppOpenAd? = null
     var isLoadingAd = false
    var isShowingAd = false

    /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
    private var loadTime: Long = 0

    /**
     * Load an ad.
     *
     * @param context the context of the activity that loads the ad
     */


    private var testAdUnitId = "ca-app-pub-3940256099942544/3419835294"
    fun loadAd(context: Context, adUnitId: String, onAddLoadedListener: OnAddLoadedListener) {
        // Do not load ad if there is an unused ad or one is already loading.
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        isLoadingAd = true
        val adId=if (BuildConfig.DEBUG) testAdUnitId else adUnitId
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            adId,
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                /**
                 * Called when an app open ad has loaded.
                 *
                 * @param ad the loaded app open ad.
                 */
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    onAddLoadedListener.onAppOpenAddLoaded()
                }

                /**
                 * Called when an app open ad has failed to load.
                 *
                 * @param loadAdError the error.
                 */
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    onAddLoadedListener.onAppOpenAddFailed()
//                    Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.message)
                }
            })
    }

    /** Check if ad was loaded more than n hours ago. */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /** Check if ad exists and can be shown. */
    private fun isAdAvailable(): Boolean {
        // Ad references in the app open beta will time out after four hours, but this time limit
        // may change in future beta versions. For details, see:
        // https://support.google.com/admob/answer/9341964?hl=en
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    fun showAdIfAvailable(
        activity: Activity,
        onShowAdCompleteListener: OnShowAdCompleteListener
    ) {
        appOpenAd!!.setFullScreenContentCallback(
            object : FullScreenContentCallback() {
                /** Called when full screen content is dismissed. */
                override fun onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    appOpenAd = null
                    isShowingAd = false
//                    Log.d(LOG_TAG, "onAdDismissedFullScreenContent.")
                    onShowAdCompleteListener.onShowAdComplete()
                }

                /** Called when fullscreen content failed to show. */
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    isShowingAd = false
//                    Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.message)
                }

                /** Called when fullscreen content is shown. */
                override fun onAdShowedFullScreenContent() {
//                    Log.d(LOG_TAG, "onAdShowedFullScreenContent.")
                }
            })
        isShowingAd = true
        appOpenAd!!.show(activity)
    }
}


private const val LOG_TAG = "AppOpenAdManager"


/**
 * Interface definition for a callback to be invoked when an app open ad is complete
 * (i.e. dismissed or fails to show).
 */
interface OnShowAdCompleteListener {
    fun onShowAdComplete()
}

interface OnAddLoadedListener {
    fun onAppOpenAddLoaded()
    fun onAppOpenAddFailed()
}