package com.scanlibraryscanner.idCardScanner.helpers


import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.alldocumentsreaderimagescanner.R
import com.google.android.material.button.MaterialButton
import com.scanlibraryscanner.ScanConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object DialogUtil {
    fun askUserFilaname(
        context: Context?,
        promptFileName: String?,
        callback: DialogUtilCallback
    ) {
        val TAG = "MainScannerFragment"

//        Log.d(TAG, "savePdf: khan 33")

        val sharedPreferences: SharedPreferences =
            context!!.getSharedPreferences(ScanConstants.addPdfSharedKey, MODE_PRIVATE)

        val myEdit = sharedPreferences.edit()


        val progressDialogBack: Dialog = Dialog(context)
        progressDialogBack.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialogBack.setCancelable(false)
        progressDialogBack.setContentView(R.layout.file_input_dialog_box)

        val fileNameText = progressDialogBack.findViewById<EditText>(R.id.userInputDialog)
        val saveButton = progressDialogBack.findViewById<MaterialButton>(R.id.save)
        val cancelButton = progressDialogBack.findViewById<MaterialButton>(R.id.cancel)
        val progress = progressDialogBack.findViewById<ProgressBar>(R.id.progress)
//        Log.d(TAG, "savePdf: khan 44")


        saveButton.setOnClickListener {
//            Log.d(TAG, "savePdf: khan 55")

            progress.visibility = View.VISIBLE

            CoroutineScope(Dispatchers.IO).launch {
                callback.onSave(fileNameText.text.toString())

//                Log.d(TAG, "savePdf: khan 66")

                // galleryAddPic(context, currentPhotoPath,promptFileName)


                myEdit.putString(
                    ScanConstants.addPdfSharedKeyValueKey,
                    ScanConstants.addPdfSharedKeyValueKey
                )
                myEdit.commit()

                withContext(Dispatchers.Main) {
                    progress.visibility = View.GONE
                    progressDialogBack.dismiss()
                    Toast.makeText(context, "Saved Succesfully", Toast.LENGTH_SHORT).show()

                }


            }



//            Log.d(TAG, "savePdf: khan 77")

        }

        cancelButton.setOnClickListener {
            progressDialogBack.dismiss()
        }

        progressDialogBack.show()

//        val layoutInflaterAndroid = LayoutInflater.from(context)
//        val mView: View = layoutInflaterAndroid.inflate(R.layout.file_input_dialog_box, null)
//        val alertDialogBuilderUserInput = AlertDialog.Builder(context)
//        alertDialogBuilderUserInput.setView(mView)
////        val fileNameText = mView.findViewById<EditText>(R.id.userInputDialog)
////        val saveButton = mView.findViewById<MaterialButton>(R.id.save)
////        val cancelButton = mView.findViewById<MaterialButton>(R.id.cancel)
//        fileNameText.setText(promptFileName)
//
//
//        cancelButton.setOnClickListener { }
//        saveButton.setOnClickListener {
//
//            callback.onSave(
//                fileNameText.text.toString()
//            )
//            // galleryAddPic(context, currentPhotoPath,promptFileName)
//            Toast.makeText(context, "Saved Succesfully", Toast.LENGTH_SHORT).show()
//
//        }
//
////        alertDialogBuilderUserInput
////            .setCancelable(false)
////            .setPositiveButton("Save") { _, _ ->
////                callback.onSave(
////                    fileNameText.text.toString()
////                )
////                // galleryAddPic(context, currentPhotoPath,promptFileName)
////                Toast.makeText(context, "Saved Succesfully", Toast.LENGTH_SHORT).show()
////            }
////            .setNegativeButton(
////                "Cancel"
////            ) { dialogBox, _ -> dialogBox.cancel() }
//        val alertDialogAndroid = alertDialogBuilderUserInput.create()
//
//        alertDialogAndroid.show()
    }


}
