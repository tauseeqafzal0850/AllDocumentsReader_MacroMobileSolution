package com.example.alldocumentsreaderimagescanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.Config
import com.applovin.mediation.ads.MaxInterstitialAd
import com.example.alldocumentsreaderimagescanner.ads.AppOpenAdManager
import com.example.alldocumentsreaderimagescanner.ads.OnShowAdCompleteListener
import com.example.alldocumentsreaderimagescanner.databinding.ActivitySplashScreenBinding
import com.example.alldocumentsreaderimagescanner.inappupdater.AppUpdateInstallerListener
import com.example.alldocumentsreaderimagescanner.inappupdater.AppUpdateInstallerManager
import com.example.alldocumentsreaderimagescanner.inappupdater.InAppUpdateInstallerManager
import com.example.alldocumentsreaderimagescanner.utils.Constant
import com.example.alldocumentsreaderimagescanner.utils.Constant.SHOW_AD
import com.example.alldocumentsreaderimagescanner.utils.PreDataStoreUtils.read
import com.example.alldocumentsreaderimagescanner.utils.SplashViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.install.model.AppUpdateType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AppSplashScreen : AppCompatActivity(), OnShowAdCompleteListener{

    private val TAG = "SplashScreen"
    private val splashViewModel: SplashViewModel by viewModels()
    private  var appOpenAdManager: AppOpenAdManager?=null
    private var mMaxInterstitialAd: MaxInterstitialAd? = null
    private var mInterstitialAd: InterstitialAd? = null
    var isRemoteConfigTrigger=true
    var isResumeConditionHandle=false
    lateinit var splashLayoutBinding: ActivitySplashScreenBinding


    private val appUpdateInstallerManager: AppUpdateInstallerManager by lazy {
        InAppUpdateInstallerManager(this)
    }

    private val appUpdateInstallerListener by lazy {
        object : AppUpdateInstallerListener() {
            // On downloaded but not installed.
            override fun onDownloadedButNotInstalled() {
                Log.e(TAG, "onDownloadedButNotInstalled: ")
                popupSnackBarForCompleteUpdate()
            }

            // On failure
            override fun onFailure(e: Exception) {
                Log.e(TAG, "onFailure: ")
                navigateToAdsContent()
            }

            // On not update
            override fun onNotUpdate() {
                Log.e(TAG, "onNotUpdate: ")
                navigateToAdsContent()
            }

            // On cancelled update
            override fun onCancelled() {
                Log.e(TAG, "onCancelled: ")
                navigateToAdsContent()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashLayoutBinding=ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(splashLayoutBinding.root)
        SHOW_AD = true
        initAdmobInitilizer()
        initInAppUpdater()
        splashViewModel.remoteConfigLiveData.observe(this, Observer {
            splashViewModel.initIsSucessRemoteData(it,this@AppSplashScreen)
        })
        splashViewModel.mMaxInterstitialAdLiveData.observe(this, Observer {
            mMaxInterstitialAd = it
            showInterstitial()

        })
        splashViewModel.mAdmobSplashInterstitialAdLiveData.observe(this, Observer {
            mInterstitialAd = it
            showInterstitial()

        })
        splashViewModel.appOpenAdManagerLiveData.observe(this, Observer {
            appOpenAdManager = it
            showInterstitial()
        })
        splashViewModel.updateUILiveData.observe(this, Observer {
            if (it) {
                nextActivity()
            }
        })
        splashViewModel.showStartedButtonLiveData.observe(this, Observer{
                updateUI()

        })
        splashViewModel.nextActivityLiveData.observe(this, Observer {
            if (it) {
                nextActivity()
            }
        })
          updateLoadingAdUi(false)
        splashLayoutBinding.btnStarted.setOnClickListener {

            updateLoadingAdUi(true)
            splashViewModel.onSuccess(this)
        }
    }

    private fun initAdmobInitilizer() {
            MobileAds.initialize(this@AppSplashScreen){
            }


    }

    private fun updateLoadingAdUi(isShow: Boolean) {
        if (isShow){
            splashLayoutBinding.layoutLoadingAd.visibility=View.VISIBLE
            splashLayoutBinding.btnStarted.isEnabled=false
//            splashLayoutBinding.btnStarted.visibility=View.GONE

        }else{
            splashLayoutBinding.layoutLoadingAd.visibility=View.GONE
            splashLayoutBinding.btnStarted.isEnabled=false
//            splashLayoutBinding.btnStarted.visibility=View.GONE
        }
    }

    //App updater checker
    private fun initInAppUpdater() {
        appUpdateInstallerManager.addAppUpdateListener(appUpdateInstallerListener)
        appUpdateInstallerManager.startCheckUpdate()
    }

    private fun showAdmobInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null) {
            showAdmobAdcallbackListener()
            mInterstitialAd!!.show(this)
        } else {
            nextActivity()
        }
    }
     fun showAdmobAdcallbackListener() {
            mInterstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Log.e(TAG, "The ad was dismissed.")
                    Constant.SHOW_AD = false
                    nextActivity()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Called when fullscreen content failed to show.
                   Log.e(TAG, "The ad failed to show.")
                    Constant.SHOW_AD = false
                    nextActivity()
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null
                    Constant.SHOW_AD = false
                    Log.e(TAG, "The ad was shown.")
                }
            }



    }

    private fun showAdIfAvailable(
        activity: Activity,
        onShowAdCompleteListener: OnShowAdCompleteListener
    ) {
        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
        // class.
        appOpenAdManager?.showAdIfAvailable(activity, onShowAdCompleteListener)

    }

    private fun nextActivity() {
        lifecycleScope.launch(Dispatchers.IO) {
            val isSwitchToHome=read(Constant.PERMISSION_SHOW_KEY,false)
            withContext(Dispatchers.Main){
                if (isSwitchToHome){
                    Log.e(TAG, "nextActivity: ${isSwitchToHome}")
                    gotoHomeActivity()
                }else{
                    Log.e(TAG, "nextActivity: ${isSwitchToHome}")
                    gotoPermissionActivity()
                }
            }
        }


    }
        private fun gotoHomeActivity(){
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            this.finish()
        }
    private fun gotoPermissionActivity() {
        val intent = Intent(this, PermissionScreen::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        this.finish()
    }

    private fun updateUI() {
        splashLayoutBinding.btnStarted.isEnabled=true
        splashLayoutBinding.btnStarted.visibility = View.VISIBLE
        splashLayoutBinding.animationView.cancelAnimation()
        splashLayoutBinding.animationView.visibility = View.GONE
    }


    private fun showInterstitial() {
        if(mInterstitialAd!=null){
            uiReadyForShowAd()
            showAdmobInterstitial()
        }
       else if (mMaxInterstitialAd != null && mMaxInterstitialAd!!.isReady) {
            uiReadyForShowAd()
            mMaxInterstitialAd?.showAd()
        } else if (appOpenAdManager!=null ){
            uiReadyForShowAd()
            showAdIfAvailable(this,this)
        }else{
            nextActivity()
        }

    }

    private fun uiReadyForShowAd() {
        updateLoadingAdUi(false)
        splashLayoutBinding.animationView.cancelAnimation()
//        splashLayoutBinding.animationView.visibility = View.GONE
        splashLayoutBinding.blankView.visibility = View.VISIBLE
//        splashLayoutBinding.btnStarted.visibility = View.GONE
    }
    var isPause=false
    override fun onPause() {
        super.onPause()
        isPause=true
    }
    override fun onResume() {
        super.onResume()
        if (isPause){
            isPause=false
            updateUI()
        }
        appUpdateInstallerManager.resumeCheckUpdate(AppUpdateType.FLEXIBLE)
    }

    override fun onShowAdComplete() {
        SHOW_AD = false
        nextActivity()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        appUpdateInstallerManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
/*
        super.onBackPressed()
*/
    }
    private fun navigateToAdsContent() {
        if (isRemoteConfigTrigger){
            splashViewModel.getRemoteConfig(this@AppSplashScreen)
            isRemoteConfigTrigger=false
        }
    }

    private fun popupSnackBarForCompleteUpdate() {
        val snackBar = Snackbar.make(
            findViewById(android.R.id.content),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction("RESTART") {
            appUpdateInstallerManager.completeUpdate()
            finish()
        }
        snackBar.setActionTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        snackBar.show()
    }
}