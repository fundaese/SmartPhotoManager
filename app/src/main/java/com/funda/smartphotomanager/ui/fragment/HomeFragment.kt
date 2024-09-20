package com.funda.smartphotomanager.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.funda.smartphotomanager.R
import com.funda.smartphotomanager.databinding.FragmentHomeBinding
import com.funda.smartphotomanager.ui.adapters.PhotoAdapter
import com.funda.smartphotomanager.ui.viewmodel.HomeViewModel
import com.funda.smartphotomanager.utils.MenuManager
import com.funda.smartphotomanager.utils.PermissionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: PhotoAdapter

    private val homeViewModel: HomeViewModel by viewModels()

    private val REQUEST_PERMISSION_CODE = 1001
    private val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observePhotos()
        checkPermissions()

        // MenuProvider
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu)
                MenuManager.setupSearch(
                    menu,
                    R.id.action_search,
                    "Search Photo",
                    homeViewModel::filterPhotosByName,
                    homeViewModel::filterPhotosByName
                )
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_sort -> {
                        Log.d(TAG, "Sort clicked")
                        MenuManager.setupSortMenu(
                            this@HomeFragment, R.id.action_sort,
                            homeViewModel::sortPhotosByName,
                            homeViewModel::sortPhotosByDate
                        )
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    // Binding toolbar
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView.layoutManager = layoutManager

        //navigate fullscreen fragment
        adapter = PhotoAdapter { photo ->
            val action = HomeFragmentDirections.actionHomeFragmentToFullScreenImageFragment2(photo.uri)
            findNavController().navigate(action)
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_gallery -> {
                    true
                }
                R.id.action_camera -> {
                    val action = HomeFragmentDirections.actionHomeFragmentToCameraFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }

        binding.recyclerView.adapter = adapter
    }

    // Observe Photos
    private fun observePhotos() {
        homeViewModel.photos.observe(viewLifecycleOwner) { photos ->
            if (photos.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                adapter.submitList(photos)
            }
        }
    }

    // Check Permissions
    private fun checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            PermissionManager.checkAndRequestPermission(
                this,
                android.Manifest.permission.READ_MEDIA_IMAGES,
                REQUEST_PERMISSION_CODE
            ) {
                homeViewModel.loadPhotos()
            }
        } else {
            PermissionManager.checkAndRequestPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                REQUEST_PERMISSION_CODE
            ) {
                homeViewModel.loadPhotos()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.loadPhotos()
        binding.bottomNavigation.selectedItemId = R.id.action_gallery
    }
}
