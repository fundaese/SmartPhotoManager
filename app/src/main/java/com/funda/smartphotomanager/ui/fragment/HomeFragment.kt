package com.funda.smartphotomanager.ui.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.funda.smartphotomanager.databinding.FragmentHomeBinding
import com.funda.smartphotomanager.ui.adapters.PhotoAdapter
import com.funda.smartphotomanager.ui.viewmodel.HomeViewModel
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
        Log.d(TAG, "onCreateView: Fragment açıldı.")
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // RecyclerView & Adapter settings
        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView.layoutManager = layoutManager

        adapter = PhotoAdapter { photo ->
            Log.d(TAG, "Photo clicked: $photo")
        }
        binding.recyclerView.adapter = adapter

        // Observe photos
        homeViewModel.photos.observe(viewLifecycleOwner) { photos ->
            Log.d(TAG, "Photos observed: ${photos.size} fotoğraf yüklendi.")
            if (photos.isNullOrEmpty()) {
                Log.d(TAG, "No photos found.")
                binding.recyclerView.visibility = View.GONE
            } else {
                Log.d(TAG, "Photos loaded.")
                binding.recyclerView.visibility = View.VISIBLE
                adapter.submitList(photos)
            }
        }

        // Check permissions
        checkPermissions()

        return binding.root
    }

    private fun checkPermissions() {
        Log.d(TAG, "checkPermissions: İzin kontrol ediliyor.")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Android 13+: İzin verilmemiş. İzin isteniyor.")
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                    REQUEST_PERMISSION_CODE
                )
            } else {
                Log.d(TAG, "Android 13+: İzin verilmiş. Fotoğraflar yükleniyor.")
                homeViewModel.loadPhotos()
            }
        } else {
            // Android 12-
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Android 12 ve altı: İzin verilmemiş. İzin isteniyor.")
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION_CODE
                )
            } else {
                Log.d(TAG, "Android 12 ve altı: İzin verilmiş. Fotoğraflar yükleniyor.")
                homeViewModel.loadPhotos()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "İzin verildi. Fotoğraflar yükleniyor.")
            homeViewModel.loadPhotos()
        } else {
            Log.i(TAG, "İzin verilmedi. Kullanıcı izni reddetti.")
        }
    }


}
