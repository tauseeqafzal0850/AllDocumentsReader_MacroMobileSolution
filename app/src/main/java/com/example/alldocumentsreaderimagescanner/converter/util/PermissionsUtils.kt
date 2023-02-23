package com.example.alldocumentsreaderimagescanner.converter.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.READ_PERMISSIONS
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION
import com.example.alldocumentsreaderimagescanner.converter.util.Constants.WRITE_PERMISSIONS

/**
 * !! IMPORTANT !!
 * permission arrays are defined in Constants.java file. we have two types of permissions:
 * READ_WRITE_PERMISSIONS and READ_WRITE_CAMERA_PERMISSIONS
 * use these constants in project whenever required.
 */
class PermissionsUtils {

    companion object {
        private var instance: PermissionsUtils? = null
        fun getInstance(): PermissionsUtils {
            if (instance == null) {
                instance = PermissionsUtils()
            }
            return instance!!
        }
    }

    /**
     * checkRuntimePermissions takes in an Object instance(can be of type Activity or Fragment),
     * an array of permission and checks for if all the permissions are granted or not
     *
     * @param context     can be of type Activity or Fragment
     * @param permissions string array of permissions
     * @return true if all permissions are granted, otherwise false
     */
    fun checkRuntimePermissions(context: Any, permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        retrieveContext(context),
                        permission
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * requestRuntimePermissions takes in an Object instance(can be of type Activity or Fragment),
     * a String array of permissions and
     * a permission request code and requests for the permission
     *
     * @param context     can be of type Activity or Fragment
     * @param permissions string array of permissions
     * @param requestCode permission request code
     */
    fun requestRuntimePermissions(
        context: Any?, permissions: Array<String>?,
        requestCode: Int
    ) {
        if (context is Activity) {
            ActivityCompat.requestPermissions(
                (context as AppCompatActivity?)!!,
                permissions!!, requestCode
            )
        } else if (context is Fragment) {
            context.requestPermissions(permissions!!, requestCode)
        }
    }

    /**
     * retrieves context of passed in non-null object, context can be of type
     * AppCompatActivity or Fragment
     *
     * @param context can be of type AppCompatActivity or Fragment
     */
    private fun retrieveContext(context: Any): Context {
        return if (context is AppCompatActivity) {
            context.applicationContext
        } else {
            (context as Fragment).requireActivity()
        }
    }

    /**
     * Handle a RequestPermissionResult by checking if the first permission is granted
     * and executing a Runnable when permission is granted
     *
     * @param grantResults    the GrantResults Array
     * @param requestCode
     * @param expectedRequest
     * @param whenSuccessful  the Runnable to call when permission is granted
     */
    fun handleRequestPermissionsResult(
        context: Activity, grantResults: IntArray,
        requestCode: Int, expectedRequest: Int, whenSuccessful: Runnable
    ) {
        if (requestCode == expectedRequest && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                whenSuccessful.run()
            } else {
                showPermissionDenyDialog(context, requestCode)
            }
        }
    }

    private fun showPermissionDenyDialog(activity: Activity, requestCode: Int) {
        val permission: Array<String>
        if (requestCode == REQUEST_CODE_FOR_WRITE_PERMISSION) {
            permission = WRITE_PERMISSIONS
        } else {
            permission = READ_PERMISSIONS
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[0])) {
            AlertDialog.Builder(activity)
                .setTitle(R.string.permission_denied_text)
                .setMessage(R.string.storage_need_rationale_description)
                .setPositiveButton(R.string.ask_again_text) { dialog: DialogInterface, which: Int ->
                    requestRuntimePermissions(
                        activity,
                        permission,
                        REQUEST_CODE_FOR_WRITE_PERMISSION
                    )
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel_text) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                .show()
        } else if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[0])) {
            AlertDialog.Builder(activity)
                .setTitle(R.string.permission_denied_text)
                .setMessage(R.string.storage_need_rationale_for_not_ask_again_flag)
                .setPositiveButton(R.string.enable_from_settings_text) { dialog: DialogInterface, which: Int ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", activity.packageName, null)
                    intent.data = uri
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    activity.startActivity(intent)
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel_text) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                .show()
        }
    }

}