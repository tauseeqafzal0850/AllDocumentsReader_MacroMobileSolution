package com.example.alldocumentsreaderimagescanner.converter.util

import android.app.Activity
import com.afollestad.materialdialogs.MaterialDialog
import com.example.alldocumentsreaderimagescanner.R

class DialogUtils private constructor() {

    companion object {
        private var instance: DialogUtils? = null
        fun getInstance(): DialogUtils {
            if (instance == null) {
                instance = DialogUtils()
            }
            return instance!!
        }
    }

    /**
     * Creates a material dialog with `Warning` title
     * @param activity - activity instance
     * @param content - content resource id
     * @return - material dialog builder
     */
    fun createWarningDialog(activity: Activity?, content: Int): MaterialDialog.Builder {
        return MaterialDialog.Builder(activity!!)
            .title(R.string.warning)
            .content(content)
            .positiveText(android.R.string.ok)
            .negativeText(android.R.string.cancel)
    }

    /**
     * Creates a material dialog with `warning title` and overwrite message as content
     * @param activity - activity instance
     * @return - material dialog builder
     */
    fun createOverwriteDialog(activity: Activity?): MaterialDialog.Builder {
        return MaterialDialog.Builder(activity!!)
            .title(R.string.warning)
            .content(R.string.overwrite_message)
            .positiveText(android.R.string.ok)
            .negativeText(android.R.string.cancel)
    }

    /**
     * Creates a material dialog with given title & content
     * @param activity - activity instance
     * @param title - dialog title resource id
     * @param content - content resource id
     * @return - material dialog builder
     */
    fun createCustomDialog(
        activity: Activity?,
        title: Int, content: Int
    ): MaterialDialog.Builder {
        return MaterialDialog.Builder(activity!!)
            .title(title)
            .content(content)
            .positiveText(android.R.string.ok)
            .negativeText(android.R.string.cancel)
    }

    /**
     * Creates a material dialog with given title
     * @param activity - activity instance
     * @param title - dialog title resource id
     * @return - material dialog builder
     */
    fun createCustomDialogWithoutContent(
        activity: Activity?,
        title: Int
    ): MaterialDialog.Builder {
        return MaterialDialog.Builder(activity!!)
            .title(title)
            .positiveText(android.R.string.ok)
            .negativeText(android.R.string.cancel)
    }

    fun createAnimationDialog(activity: Activity?): MaterialDialog? {
        return MaterialDialog.Builder(activity!!)
            .customView(R.layout.lottie_anim_dialog, false)
            .cancelable(false)
            .build()
    }


}