package com.example.alldocumentsreaderimagescanner.converter.util


import android.app.Activity
import android.os.Environment
import android.util.Log
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.PATH_SEPERATOR
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.pdfDirectory
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.util.*

/**
 * Created by anandparmar on 18/06/18.
 */
class StringUtils private constructor() {

    companion object {
        private var instance: StringUtils? = null
        fun getInstance(): StringUtils {
            if (instance == null) {
                instance = StringUtils()
            }
            return instance!!
        }
    }

    fun isEmpty(s: CharSequence?): Boolean {
        return s == null || s.toString().trim { it <= ' ' } == ""
    }

    fun isNotEmpty(s: CharSequence?): Boolean {
        return s != null && s.toString().trim { it <= ' ' } != ""
    }

    fun showSnackbar(context: Activity, resID: Int) {
        Snackbar.make(
            Objects.requireNonNull(context).findViewById(android.R.id.content),
            resID, Snackbar.LENGTH_LONG
        ).show()
    }
    fun showInLayoutSnackbar(context: Activity, resID: Int, laySnackBar: CoordinatorLayout) {
        Snackbar.make(
            laySnackBar,
            resID, Snackbar.LENGTH_LONG
        ).show()
    }

    fun showSnackbar(context: Activity, resID: String?) {
        Snackbar.make(
            Objects.requireNonNull(context).findViewById(android.R.id.content),
            resID!!, Snackbar.LENGTH_LONG
        ).show()
    }


    fun getSnackbarwithAction(context: Activity, resID: Int): Snackbar {
        return Snackbar.make(
            Objects.requireNonNull(context).findViewById(android.R.id.content),
            resID, Snackbar.LENGTH_LONG
        )
    }
    fun getCusLayoutSnackbarwithAction(
        context: Activity,
        resID: Int,
        laySnackBar: CoordinatorLayout
    ): Snackbar {
        return Snackbar.make(
            laySnackBar,
            resID, Snackbar.LENGTH_LONG
        )
    }


    val defaultStorageLocation: String
        get() {
            val dir = File(
                Environment.getExternalStorageDirectory().absolutePath,
                pdfDirectory
            )
            if (!dir.exists()) {
                val isDirectoryCreated = dir.mkdir()
                if (!isDirectoryCreated) {
                    Log.e("Error", "Directory could not be created")
                }
            }
            return dir.absolutePath + PATH_SEPERATOR
        }

    /**
     * if text is empty according to [StringUtils.isEmpty] returns the default,
     * if text is not empty, parses the text according to [Integer.parseInt]
     * @param text the input text
     * @param def the default value
     * @return the text parsed to an int or the default value
     * @throws NumberFormatException if the text is not empty and not formatted as an int
     */
    @Throws(NumberFormatException::class)
    fun parseIntOrDefault(text: CharSequence, def: Int): Int {
        return if (isEmpty(text)) def else text.toString().toInt()
    }

}