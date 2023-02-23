package com.example.alldocumentsreaderimagescanner.ads

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.converter.activity.ConverterMainActivity
import com.example.alldocumentsreaderimagescanner.notepad.NotePadActivity
import com.example.alldocumentsreaderimagescanner.reader.activity.FileListActivity
import com.example.alldocumentsreaderimagescanner.reader.activity.QrCodeGeneratorActivity
import com.example.alldocumentsreaderimagescanner.utils.Constant
import com.example.alldocumentsreaderimagescanner.utils.Constant.SHOW_AD
import com.scanlibraryscanner.idCardScanner.activity.PassportScanActivity
import com.scanlibraryscanner.idCardScanner.activity.scanner.MainScannerActivity
import kotlinx.android.synthetic.main.activity_fbactivity.*
import kotlinx.coroutines.launch

class InterstitialActivity : AppCompatActivity(), InterstitialAdManager.AdCallback {
    private var nextActivityPos: Int? = 0
    private var fileType: Int? = 0
    private var isAdmob = false
    private var isMax = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fbactivity)
//        InterstitialAdManager.with(applicationContext).setCallbackListener(this@InterstitialActivity)
        InterstitialAdManager.with().setCallbackListener(this@InterstitialActivity)

        nextActivityPos = intent?.getIntExtra("activityPos", -1)
        fileType = intent?.getIntExtra("fileType", -1)
        isAdmob = intent?.getBooleanExtra("isAdmob", false)!!
        isMax = intent?.getBooleanExtra("isMax", false)!!

        /*lifecycleScope.launch {*/
        if (SHOW_AD) {
            if (isMax || isAdmob) {
                showUi()
                Handler(Looper.getMainLooper()).postDelayed({
                    InterstitialAdManager.with()
                        .showInterstitial(this@InterstitialActivity, isAdmob, isMax)
                }, 2000)

            } else {
                changeUi()
                nextActivity()
            }

        } else {
            changeUi()
            nextActivity()
        }
    }
//    }

    private fun showUi() {
        adProgressbar.isIndeterminate = true
        adProgressbar.visibility = View.VISIBLE
        loadingAdvertisement.visibility = View.VISIBLE
    }

    private fun changeUi() {
        adProgressbar.isIndeterminate = false
        adProgressbar.visibility = View.GONE
        loadingAdvertisement.visibility = View.GONE
    }

    /*   override fun onAdShow() {
           changeUi()
       }*/

    /*   override fun onAdFailed() {
           changeUi()
           nextActivity()
       }*/

    /*  override fun onAdFailedToShow() {

      }*/

    override fun onAdClosed() {
        changeUi()
        nextActivity()
    }

    private fun nextActivity() {
        when (nextActivityPos) {
            0 -> fileListActivity(0)
            1 -> converterActivity(1)
            2 -> converterActivity(2)
            3 -> converterActivity(3)
            15 -> converterActivity(4)
            4 -> fileListActivity(4)
            5 -> fileListActivity(5)
            6 -> fileListActivity(6)
            9 -> startActivity(Intent(this, MainScannerActivity::class.java))
            11 -> startActivity(Intent(this, PassportScanActivity::class.java))
            13 -> startActivity(Intent(this, QrCodeGeneratorActivity::class.java))
            14 -> startActivity(Intent(this, NotePadActivity::class.java))
        }
        SHOW_AD = true
        finish()
    }

    private fun fileListActivity(extra: Int) {
        startActivity(
            Intent(this, FileListActivity::class.java).putExtra(
                "fileType",
                extra
            )
        )
    }

    private fun converterActivity(extra: Int) {
        startActivity(
            Intent(this, ConverterMainActivity::class.java).putExtra(
                "fragment",
                extra
            )
        )
    }


    override fun onResume() {
        super.onResume()
        InterstitialAdManager.with().setCallbackListener(this@InterstitialActivity)
    }

    override fun onBackPressed() {

    }


}