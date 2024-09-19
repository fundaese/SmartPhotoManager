package com.funda.smartphotomanager.utils

import android.view.Menu
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.funda.smartphotomanager.R

object MenuManager {

    fun setupSearch(
        menu: Menu,
        searchItemId: Int,
        searchHint: String,
        onQueryTextSubmit: (String) -> Unit,
        onQueryTextChange: (String) -> Unit
    ) {
        val searchItem = menu.findItem(searchItemId)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = searchHint

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { onQueryTextSubmit(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { onQueryTextChange(it) }
                return true
            }
        })
    }

    fun setupSortMenu(
        fragment: Fragment,
        sortMenuItemId: Int,
        onSortByName: () -> Unit,
        onSortByDate: () -> Unit
    ) {
        val popup = PopupMenu(fragment.requireContext(), fragment.requireActivity().findViewById(sortMenuItemId))
        popup.menuInflater.inflate(R.menu.sort_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.sort_by_name -> onSortByName()
                R.id.sort_by_date -> onSortByDate()
            }
            true
        }
        popup.show()
    }
}
