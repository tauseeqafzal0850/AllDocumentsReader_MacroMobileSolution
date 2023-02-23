package com.example.alldocumentsreaderimagescanner.converter.util

import android.content.SharedPreferences


object SharedPreferencesUtil {


    /**
     * Set the default Page numbering style
     * @param editor the [SharedPreferences.Editor] to use for editing
     * @param pageNumStyle the page numbering style as defined in [Constants]
     * @param id the id of the style
     */
    fun setDefaultPageNumStyle(editor: SharedPreferences.Editor, pageNumStyle: String?, id: Int) {
        editor.putString(Constants.PREF_PAGE_STYLE, pageNumStyle)
        editor.putInt(Constants.PREF_PAGE_STYLE_ID, id)
        editor.apply()
    }

    /**
     * Clear the default Page numbering style
     * @param editor the [SharedPreferences.Editor] to use for editing
     */
    fun clearDefaultPageNumStyle(editor: SharedPreferences.Editor) {
        setDefaultPageNumStyle(editor, null, -1)
    }

}