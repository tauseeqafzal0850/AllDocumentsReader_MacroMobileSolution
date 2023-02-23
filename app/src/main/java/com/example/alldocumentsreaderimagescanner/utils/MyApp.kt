package com.example.alldocumentsreaderimagescanner.utils

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.alldocumentsreaderimagescanner.BuildConfig
import com.example.alldocumentsreaderimagescanner.ads.InterstitialAdManager
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.onesignal.OneSignal
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

@HiltAndroidApp
class MyApp : Application() {


    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())
    private val ONESIGNAL_APP_ID = "41f70cac-5d1e-4b30-be4e-aa0d7ee91cfd"

    // private val ONESIGNAL_APP_ID = "0fa256cd-ba55-4d69-9145-b2981de3238b"
    private val TAG = "myapp"
    var counterActivityCreate = 0
         companion object{
             lateinit var applicationcontext:Context
         }
    override fun onCreate() {
        super.onCreate()
        applicationcontext=applicationContext

        /*val testDeviceIds = Arrays.asList("BB1E55C7291B10FF8E854CD2A7CBF3CC")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)*/
        scope.launch {
            // OneSignal Initialization
            OneSignal.initWithContext(this@MyApp)
            OneSignal.setAppId(ONESIGNAL_APP_ID)
            if (BuildConfig.DEBUG) {
                OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
            }

        }

//        System.loadLibrary("opencv_java3")
//        System.loadLibrary("Scanner")


//        registerLifeCycleObserver()
    }

    private fun registerLifeCycleObserver() {
        scope.launch {
            registerActivityLifecycleCallbacks(object: android.app.Application.ActivityLifecycleCallbacks{
                override fun onActivityCreated(activity: android.app.Activity, savedInstanceState: android.os.Bundle?) {
                    Log.e(TAG, "onActivityCreated: ${activity.javaClass.name}")
                    counterActivityCreate++
                }

                override fun onActivityStarted(activity: android.app.Activity) {
                    Log.e(TAG, "onActivityStarted: ${activity.javaClass.name}")
                }

                override fun onActivityResumed(activity: android.app.Activity) {
                    Log.e(TAG, "onActivityResumed: ${activity.javaClass.name}")
                }

                override fun onActivityPaused(activity: android.app.Activity) {
                    Log.e(TAG, "onActivityPaused: ${activity.javaClass.name}")
                }

                override fun onActivityStopped(activity: android.app.Activity) {
                    Log.e(TAG, "onActivityStopped: ${activity.javaClass.name}")
                }

                override fun onActivitySaveInstanceState(activity: android.app.Activity, outState: android.os.Bundle) {
                    Log.e(TAG, "onActivitySaveInstanceState: ${activity.javaClass.name}")
                }

                override fun onActivityDestroyed(activity: android.app.Activity) {
                    Log.e(TAG, "onActivityDestroyed: ${activity.javaClass.name}")
                    counterActivityCreate--
                    if(counterActivityCreate == 0){
//                        InterstitialAdManager.with(applicationContext).destoryInterstitialReference()
                        Log.e(TAG, "onActivityDestroyed: all activity destroyed")

                    }
                }

            })

        }
    }




}
