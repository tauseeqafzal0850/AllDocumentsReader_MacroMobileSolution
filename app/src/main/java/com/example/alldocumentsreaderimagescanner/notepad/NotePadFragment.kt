package com.example.alldocumentsreaderimagescanner.notepad

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.azeesoft.lib.colorpicker.ColorPickerDialog
import com.example.alldocumentsreaderimagescanner.R
import com.example.alldocumentsreaderimagescanner.databinding.FragmentNotePadBinding
import com.example.alldocumentsreaderimagescanner.notepad.model.NotesViewModel
import com.example.alldocumentsreaderimagescanner.utils.hideKeyboard
import com.example.alldocumentsreaderimagescanner.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotePadFragment : Fragment() {
    // create and inject view model
    lateinit var viewModel: NotesViewModel

    @Inject
    lateinit var colorPickerDialog: ColorPickerDialog
    private var noteId: Int? = null
    private lateinit var binding: FragmentNotePadBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNotePadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = (activity as NotePadActivity).viewModel
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showDisCardDialog()
            }
        })
        noteId = arguments?.getInt(NOTE_ID_ARG)

        if (noteId != null) {
            // here we have suspend function so we need coroutines scope to run it
            // Main Thread
            lifecycleScope.launch(Dispatchers.Main) {
                val note = viewModel.getNoteById(noteId!!)

                viewModel.saveNoteColor(note.color) // change color note in view model

                //noteInputView.setText(note.text)
                binding.notesET.setText(note.text)
                binding.title.setText(note.title)

            }
        } else {
            binding.notesET.text.clear()
            binding.title.text.clear()
        }

        binding.saveBtn.setOnClickListener {
            onSaveNoteClick()
        }

        binding.colorPicker.setOnClickListener {
            showColorDialog()
        }

    }

    /**
     * Override this fun to add menu xml file to the fragment
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.create_note_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save_note -> {
                onSaveNoteClick()
            }
            R.id.action_pick_note_color -> {
                showColorDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onSaveNoteClick() {

        // hide keyboard  , i will use extension function , let's see google :D
        binding.notesET.hideKeyboard()

        val text = binding.notesET.text.toString() // get text as html not as normal string
        val title = binding.title.text.toString()
        if(title.isEmpty())
        {
            requireContext().showToast("Enter Notepad Title")

        }
        else if(text.isEmpty())
        {
            requireContext().showToast("Enter Notepad Text")

        }
        else{
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.saveNote(noteId, title, text) // save note
                (activity as NotePadActivity).backPress()
            }
        }




    }

    private fun showColorDialog() {
        colorPickerDialog.setOnColorPickedListener { _, hexVal ->
            // send hexVal to viewModel
            viewModel.saveNoteColor(hexVal)
        }
        colorPickerDialog.show()
    }

    companion object {
        const val NOTE_ID_ARG = "note_id"

    }

    override fun onResume() {
        super.onResume()
    }

    private fun showDisCardDialog() {
        val dialog = Dialog(requireContext(),R.style.MyAlertDialogTheme)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_discard_changes_layout)
        val btnYes = dialog.findViewById<TextView>(R.id.btnYes)
        val btnNo = dialog.findViewById<TextView>(R.id.btnNo)

        btnNo.setOnClickListener { dialog.dismiss() }
        btnYes.setOnClickListener {
            dialog.dismiss()
            try {
                (activity as NotePadActivity).backPress()
            } catch (e: Exception) {
            }

        }

        dialog.show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onDestroy() {
        super.onDestroy()
        if (colorPickerDialog.isShowing) {
            colorPickerDialog.dismiss()
        }
    }

}