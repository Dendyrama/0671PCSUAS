package com.github.UAS0671.view

import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.UAS0671.R
import com.github.UAS0671.db.DatabaseUas.FavColumns.Companion.CONTENT_URI
import com.github.UAS0671.helper.MappingHelper
import com.github.UAS0671.model.Favourite
import com.github.UAS0671.viewModel.FavouriteAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_fav.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavActivity : AppCompatActivity() {

    private lateinit var adapter: FavouriteAdapter

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav)
        setActionBarTitle()

        recycleViewFav.layoutManager = LinearLayoutManager(this)
        recycleViewFav.setHasFixedSize(true)
        adapter = FavouriteAdapter(this)
        recycleViewFav.adapter = adapter

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        val myObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                loadNotesAsync()
            }
        }

        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)

        if (savedInstanceState == null) {
            loadNotesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Favourite>(EXTRA_STATE)
            if (list != null) {
                adapter.listNotes = list
            }
        }
    }

    // change action bar title
    private fun setActionBarTitle() {
        if (supportActionBar != null) {
            supportActionBar?.title = "Favourite Users"
        }
    }

    // get data and set it to adapter from SQLite database
    private fun loadNotesAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            progressBarFav.visibility = View.VISIBLE
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = contentResolver?.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val favData = deferredNotes.await()
            progressBarFav.visibility = View.INVISIBLE
            if (favData.size > 0) {
                adapter.listNotes = favData
            } else {
                adapter.listNotes = ArrayList()
                showSnackbarMessage()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listNotes)
    }

    private fun showSnackbarMessage() {
        Snackbar.make(recycleViewFav, "Tidak ada data saat ini", Snackbar.LENGTH_SHORT).show()
    }

    // run this func when open again for refresh data
    override fun onResume() {
        super.onResume()
        loadNotesAsync()
    }
}
