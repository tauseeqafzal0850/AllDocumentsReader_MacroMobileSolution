package com.example.alldocumentsreaderimagescanner.ads

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinSdkUtils
import com.example.alldocumentsreaderimagescanner.BuildConfig
import com.example.alldocumentsreaderimagescanner.BuildConfig.DEBUG
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.utils.Constant
import com.example.alldocumentsreaderimagescanner.utils.Constant.APPLOVIN_MAIN_BANNER_ENABLED
import com.example.alldocumentsreaderimagescanner.utils.Constant.KEY_ADMOB_MAIN_BANNER
import com.example.alldocumentsreaderimagescanner.utils.PreDataStoreUtils.read
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BannerActivity : AppCompatActivity() {

    private val TAG = "BannerActivity"
    private  var mAdView: AdView?=null
    private var adViewMax: MaxAdView? = null
    private var bannerAdContainer: FrameLayout? = null
    private var bannerAdCard: CardView? = null
    private val adSize: com.google.android.gms.ads.AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = bannerAdContainer!!.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bannerAdContainer = findViewById(R.id.bannerAdContainer)
        bannerAdCard = findViewById(R.id.bannerLl)


    }

    fun showBanner(whichValue: String) {
        if (KEY_ADMOB_MAIN_BANNER || APPLOVIN_MAIN_BANNER_ENABLED) {
            when (whichValue) {
//            when ("1") {
                Constant.NO_AD->bannerAdCard?.visibility = View.GONE
                Constant.AD_ADMOB -> loadAdmobBanner()
                Constant.AD_MAX -> loadMaxBanner()
                else -> bannerAdCard?.visibility = View.GONE
            }
        } else {
            bannerAdCard?.visibility = View.GONE
        }
    }

    private fun loadAdmobBanner() {
        if (!KEY_ADMOB_MAIN_BANNER) {
            return
        }
        var bannerId=""
        lifecycleScope.launch(Dispatchers.IO) {
            bannerId = read(Constant.ADMOB_BANNER_AD_KEY, "")
            withContext(Dispatchers.Main) {
                if (BuildConfig.DEBUG)
                    bannerId = getString(R.string.admob_banner)

                if(bannerId.isEmpty()){
                    return@withContext
                }
                if (mAdView == null) {
                    mAdView = AdView(this@BannerActivity)
                    bannerAdContainer?.addView(mAdView)
                    mAdView?.adUnitId = bannerId

                    mAdView?.setAdSize(adSize)

                    // Create an ad request. Check your logcat output for the hashed device ID to
                    // get test ads on a physical device, e.g.,
                    // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this device."
                    val adRequest = AdRequest
                        .Builder().build()

                    mAdView!!.setAdListener(object : AdListener() {
                        override fun onAdClosed() {
                            super.onAdClosed()
                            Log.e(TAG, "onAdClosed: ")

                        }

                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            Log.e(TAG, "onAdFailedToLoad: ")
                            mAdView = null
                        }

                        override fun onAdOpened() {
                            super.onAdOpened()
                        }

                        override fun onAdLoaded() {
                            super.onAdLoaded()
                            Log.e(TAG, "onAdLoaded: ")
                        }

                        override fun onAdClicked() {
                            super.onAdClicked()
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()
                        }
                    })

                    // Start loading the ad in the background.
                    Log.e(TAG, "BannerRequested" )
                    mAdView?.loadAd(adRequest)
                }
            }
        }

    }

    private fun loadMaxBanner() {
        if (!APPLOVIN_MAIN_BANNER_ENABLED) {
            return
        }
        var bannerId=""
        lifecycleScope.launch(Dispatchers.IO) {
            bannerId = read(Constant.KEY_APPLOVIN_BANNER_AD_ID, "")
            withContext(Dispatchers.Main) {
                if (bannerId.isEmpty()) {
                    return@withContext
                }
                if (adViewMax == null) {
                    adViewMax = MaxAdView(bannerId, this@BannerActivity)
                    adViewMax!!.setListener(maxAdListener)
                }

                bannerAdContainer!!.removeAllViews()
                if (adViewMax == null) {
                    return@withContext
                }
                (adViewMax!!.parent as? ViewGroup)?.removeView(adViewMax)

                // Set the height of the banner ad based on the device type.

                // Set the height of the banner ad based on the device type.
                val isTablet = AppLovinSdkUtils.isTablet(this@BannerActivity)
                val heightPx = AppLovinSdkUtils.dpToPx(this@BannerActivity, if (isTablet) 90 else 50)
                // Banner width must match the screen to be fully functional.
                // Banner width must match the screen to be fully functional.
                adViewMax!!.layoutParams =
                    FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPx)
                // Need to set the background or background color for banners to be fully functional.
                //adView.setBackgroundColor( Color.BLACK );

                // Need to set the background or background color for banners to be fully functional.
                //adView.setBackgroundColor( Color.BLACK );
                bannerAdContainer!!.addView(adViewMax)

                adViewMax!!.loadAd()
            }
        }

    }

    @SuppressLint("LogNotTimber")
    var maxAdListener: MaxAdViewAdListener = object : MaxAdViewAdListener {
        override fun onAdLoaded(ad: MaxAd) {
//            Log.d(TAG, "MAX Banner Ad Loaded")
        }

        override fun onAdDisplayed(ad: MaxAd) {}
        override fun onAdHidden(ad: MaxAd) {}
        override fun onAdClicked(ad: MaxAd) {}
        override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
//            Log.e(TAG, "MAX Banner Ad Load Error: " + error.message + " Error Code: " + error.code)
        }

        override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
        override fun onAdExpanded(ad: MaxAd) {}
        override fun onAdCollapsed(ad: MaxAd) {}
    }

    @SuppressLint("LogNotTimber")
    private fun showLog(log: String) {
        if (DEBUG) {
//            Log.d(TAG, "showLog: $log")
        }
    }

    override fun onPause() {
        if (mAdView!= null) {
            mAdView?.pause()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (mAdView!= null) {
            mAdView?.resume()
        }
    }
    override fun onDestroy() {
        //    Log.d(TAG, "onDestroy: #BannerActivity")
        if (adViewMax != null) {
            adViewMax?.destroy()
            adViewMax = null
        }
        if (mAdView!= null) {
            mAdView?.destroy()
            mAdView = null
        }
        super.onDestroy()
    }

}