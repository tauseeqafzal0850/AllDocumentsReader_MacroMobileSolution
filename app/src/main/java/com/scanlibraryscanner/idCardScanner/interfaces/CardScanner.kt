package com.scanlibraryscanner.idCardScanner.interfaces

import android.net.Uri

interface CardScanner {
    fun onBitmapSelect(uri: Uri?, imageType: String)
    fun onScanFinish(uri: Uri?, imageType: String)

}