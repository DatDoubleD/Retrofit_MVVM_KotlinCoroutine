package com.example.noteapp.ui.note

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteapp.R
import com.example.noteapp.data.Note
import com.example.noteapp.ui.adapter.NoteAdapter
import com.example.noteapp.ui.viewmodel.NoteViewModel
import com.example.noteapp.ultis.Status
import kotlinx.android.synthetic.main.fragment_note_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteListFragment : Fragment(R.layout.fragment_note_list) {
    private val noteViewModel: NoteViewModel by lazy {
        ViewModelProvider(
            this, NoteViewModel.NoteViewModelFactory(requireActivity().application)
        )[NoteViewModel::class.java]
    }
    private val noteAdapter: NoteAdapter by lazy {
        NoteAdapter(requireActivity().application, onItemClick, onItemDelete)
    }

    private val onItemClick: (Note) -> Unit = {

    }
    private val onItemDelete: (Note) -> Unit = {

    }

    private val controller:NavController by lazy {
        val NavHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        NavHostFragment.findNavController()
    }

    // nằm giữa onCreateView và OnActivityCreate, cách viết mới: truyền ID Fragment vào constructor khi extendFragment
    //nên k cần khởi tạo view trong oncreateView,
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true) // this line to set menu cho fragment

        rv_note.setHasFixedSize(true)
        rv_note.layoutManager = LinearLayoutManager(requireContext())
        rv_note.adapter = noteAdapter

        //set event + gọi refreshdata khi mới mở app
        swipe_layout.setOnRefreshListener {
            refreshData()
        }
        refreshData()
        btn_open_add_activity.setOnClickListener {
            controller.navigate(R.id.addNoteFragment) // id trong nav_graph
        }
    }

    private fun refreshData() {
        noteViewModel.getNoteFromApi().observe(viewLifecycleOwner, {
            if (it != null) {
                when (it.status) {
                    Status.SUCCESS -> {
                        swipe_layout.isRefreshing = false
                        if (it.data != null) {
                            noteAdapter.setNotes(it.data)
                        }
                    }
                    Status.ERROR -> {
                        swipe_layout.isRefreshing = false
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        //lỗi lấy từ server -> lấy data từ room ở đây vì data từ server bị lỗi
                       noteViewModel.getAllNote().observe(viewLifecycleOwner, {
                           noteAdapter.setNotes(it)
                       })
                    }
                    Status.LOADING -> {
                        swipe_layout.isRefreshing = true
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //lấy data từ sever về + lưu vào room(xóa room + lưu mới)
        if (item.itemId == R.id.mnu_sync){
            Toast.makeText(requireContext(), "Syncing...", Toast.LENGTH_LONG).show()
            noteViewModel.getNoteFromApi().observe(viewLifecycleOwner, {
                if (it != null) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            swipe_layout.isRefreshing = false
                            noteViewModel.deleteAllNote()
                            if (it.data != null) {
                                GlobalScope.launch {
                                    val job = noteViewModel.insertAllNote(it.data)
                                    job.join()
                                    withContext(Dispatchers.Main){
                                        Toast.makeText(requireContext(), "Sync finish", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                        Status.ERROR -> {
                            swipe_layout.isRefreshing = false
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }
                        Status.LOADING -> {
                            swipe_layout.isRefreshing = true
                        }
                    }
                }
            })
        }
        return super.onOptionsItemSelected(item)

    }
}