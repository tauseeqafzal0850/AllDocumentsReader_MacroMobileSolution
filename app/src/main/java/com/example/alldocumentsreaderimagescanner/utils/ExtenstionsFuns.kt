package com.example.alldocumentsreaderimagescanner.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*


/**
 * Extension function to convert long to date string
 */

val datFormat = SimpleDateFormat("dd,MMM yyyy", Locale.US)  // day, Month(name) year

fun Long.toDate(): String {
    return datFormat.format(Date(this))
}

/**
 * Extension function to hide keyboard
 */
fun EditText.hideKeyboard() {
    val imm: InputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


fun View.setVisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}
fun Context.showToast(text:String)
{
    Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
}