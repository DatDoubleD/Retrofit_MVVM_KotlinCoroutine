package com.example.noteapp.ui.note

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.noteapp.R
import com.example.noteapp.data.Note
import com.example.noteapp.ui.viewmodel.NoteViewModel
import com.example.noteapp.ultis.Status
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.android.synthetic.main.fragment_note_list.*

class AddNoteFragment:Fragment(R.layout.fragment_add_note) {
    private val noteViewModel: NoteViewModel by lazy {
        ViewModelProvider(
            this, NoteViewModel.NoteViewModelFactory(requireActivity().application)
        )[NoteViewModel::class.java]
    }
    private val controller: NavController by lazy {
        val NavHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        NavHostFragment.findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_add.setOnClickListener {
            val note:Note = Note(edt_note_title.text.toString(), edt_note_des.text.toString())
            noteViewModel.addNoteToServer(note).observe(viewLifecycleOwner, {
                if (it != null) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            if (it.data != null) {
                                Toast.makeText(requireContext(), it.data.toString(), Toast.LENGTH_LONG).show()
                                controller.popBackStack() // tro ve
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }
                        Status.LOADING -> {
                            //
                        }
                    }
                }
            })
        }
    }
}