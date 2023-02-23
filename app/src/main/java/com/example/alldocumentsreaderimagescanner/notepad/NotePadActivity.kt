package com.example.alldocumentsreaderimagescanner.notepad

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.ads.BannerActivity
import com.example.alldocumentsreaderimagescanner.databinding.ActivityNotePadBinding
import com.example.alldocumentsreaderimagescanner.notepad.NotePadFragment.Companion.NOTE_ID_ARG
import com.example.alldocumentsreaderimagescanner.notepad.adapter.NotesAdapter
import com.example.alldocumentsreaderimagescanner.notepad.model.NotesViewModel
import com.example.alldocumentsreaderimagescanner.utils.Constant.CHECK_NOTEPAD_BANNER
import com.example.alldocumentsreaderimagescanner.utils.MyActionModeCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotePadActivity : BannerActivity() {
    private lateinit var binding: ActivityNotePadBinding
     val viewModel: NotesViewModel by viewModels()
    private lateinit var adapter: NotesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNotePadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        showBanner(CHECK_NOTEPAD_BANNER)
        adapter = NotesAdapter(onNoteClick = { note ->
            binding.frame.visibility = View.VISIBLE
            val bundle = Bundle()
            bundle.putInt(NOTE_ID_ARG, note.id)
            val fragment = NotePadFragment()
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().add(binding.frame.id, fragment)
                .addToBackStack("").commit()
            showHideMainView(true)
        }, onShowActionMode = {
            onShowActionMode()
        }, onNoteSelected = { isNoteSelected, noteId ->
            viewModel.setNoteSelected(isNoteSelected, noteId)
        })

        binding.recyclerView.adapter = this.adapter
        viewModel.getNotesCount().observe(this@NotePadActivity) {
            showEmptyView(it == 0)
        }
        viewModel.getAllNotes().observe(this@NotePadActivity) {
            adapter.submitData(lifecycle,it)
        }

        binding.createBtn.setOnClickListener {
            showHideMainView(true)
            binding.frame.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction().add(binding.frame.id, NotePadFragment())
                .addToBackStack("").commit()
        }
    }
    private fun showDeleteDialog() {
        val dialog = Dialog(this,R.style.MyAlertDialogTheme)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.delete_note)
        val btnYes = dialog.findViewById<TextView>(R.id.btnDelete)
        val btnNo = dialog.findViewById<TextView>(R.id.btnCancel)

        btnNo.setOnClickListener { dialog.dismiss() }
        btnYes.setOnClickListener {
            dialog.dismiss()
            try {
                viewModel.deleteSelectedNotes()
            } catch (e: Exception) {
            }
        }
        dialog.show()
    }

    private fun onShowActionMode() {
        startActionMode(MyActionModeCallback(
            deleteSelectedNotes = {
             showDeleteDialog()
            },
            onActionModeClosed = {
                adapter.disableActionMode()
            }
        ))
    }

    private fun showEmptyView(isEmpty: Boolean) {
        binding.recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    private fun showHideMainView(isEmpty: Boolean) {
        binding.layoutMain.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun onBackPressed() {
//        backPress()
        super.onBackPressed()
    }
    fun backPress() {
        val count = supportFragmentManager.backStackEntryCount
        if (count != 1) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStackImmediate()
            showHideMainView(false)
            binding.frame.visibility = View.GONE
        }
    }

}