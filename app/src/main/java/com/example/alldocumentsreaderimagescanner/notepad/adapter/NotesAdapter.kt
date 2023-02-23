package com.example.alldocumentsreaderimagescanner.notepad.adapter

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.graphics.toColorInt
import androidx.core.text.parseAsHtml
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.alldocumentsreaderimagescanner.databinding.NoteItemLayoutBinding
import com.example.alldocumentsreaderimagescanner.notepad.model.NotepadEntity
import com.example.alldocumentsreaderimagescanner.utils.toDate

class NotesAdapter(
    private val onNoteClick: (note: NotepadEntity) -> Unit,
    private val onShowActionMode: () -> Unit,
    private val onNoteSelected: (isNoteSelected: Boolean, noteId: Int) -> Unit
) : PagingDataAdapter<NotepadEntity, NotesAdapter.NoteHolder>(notesDiffItem) {
    private var checkMode: Boolean = false

    inner class NoteHolder(val binding: NoteItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val noteCardView: CardView = binding.root
        fun bind(
            response: NotepadEntity,
            isCheckMode: Boolean,
            onNoteSelected: (isNoteSelected: Boolean) -> Unit
        ) {
            binding.run {
                noteTitle.text = response.title.parseAsHtml()
                noteText.text = response.text.parseAsHtml()
                noteDate.text = response.updatedDate.toDate()
                noteCheckBox.visibility = if (isCheckMode) VISIBLE else GONE
                noteCheckBox.setOnClickListener {
                    if(noteCheckBox.isChecked)
                    {
                        onNoteSelected.invoke(true)
                    }
                    else
                    {
                        onNoteSelected.invoke(false)
                    }
                }
                // if it's not a white color
                if (!response.color.equals("#ffffff", true))
                    noteCardView.setCardBackgroundColor(response.color.toColorInt()) // change card view color with note color
            }
        }

        fun performChecked() {
            binding.noteCheckBox.performClick()
        }

    }

    fun disableActionMode() {
        this.checkMode = false
        notifyDataSetChanged()
    }


    companion object {
        // create DiffItem object for PagingAdapter
        private val notesDiffItem = object : DiffUtil.ItemCallback<NotepadEntity>() {
            override fun areItemsTheSame(oldItem: NotepadEntity, newItem: NotepadEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: NotepadEntity,
                newItem: NotepadEntity
            ): Boolean {
                return oldItem == newItem
            }

        }

    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        getItem(position)?.let { note ->
            holder.bind(note, checkMode) { isNoteSelected ->
                onNoteSelected.invoke(isNoteSelected, note.id)
            }
            holder.itemView.setOnLongClickListener {
                checkMode = true
                notifyDataSetChanged()
                holder.performChecked()
                onShowActionMode.invoke()
                false
            }
            holder.itemView.setOnClickListener {
                if (checkMode) {
                    holder.performChecked()
                } else this.onNoteClick.invoke(note)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        return NoteHolder(
            NoteItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}