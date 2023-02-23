package com.scanlibraryscanner

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class SingleButtonDialogFragment(
    private val positiveButtonTitle: Int,
    private val message: String?, private val title: String?, private val isCancelable: Boolean?
) : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
            .setTitle(title)
            .setCancelable(isCancelable!!)
            .setMessage(message)
            .setPositiveButton(
                positiveButtonTitle
            ) { _, _ -> }
        return builder.create()
    }

}