package com.example.alldocumentsreaderimagescanner.converter.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QrScannerResult(val text:String,val image:Int): Parcelable
