package com.funda.smartphotomanager.ui.fragment.camera

import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.funda.smartphotomanager.databinding.FragmentCameraBinding
import com.funda.smartphotomanager.utils.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var currentPhotoPath: String
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>

    private val REQUEST_CAMERA_PERMISSION = 1002
    private val TAG = "CameraFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)

        // ActivityResultLauncher for take a photo
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                Log.d(TAG, "Photo captured successfully: $currentPhotoPath")
                addPhotoToGallery()
            } else {
                Log.e(TAG, "Failed to capture photo.")
            }
        }

        binding.captureButton.setOnClickListener {
            checkCameraPermission()
        }

        return binding.root
    }

    // Check Camera Permission Control
    private fun checkCameraPermission() {
        PermissionManager.checkAndRequestPermission(
            this,
            android.Manifest.permission.CAMERA,
            REQUEST_CAMERA_PERMISSION
        ) {
            dispatchTakePictureIntent()
        }
    }

    // Start take photo intent
    private fun dispatchTakePictureIntent() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            Log.e(TAG, "Error occurred while creating the file", ex)
            null
        }

        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.funda.smartphotomanager.fileprovider",
                it
            )
            takePictureLauncher.launch(photoURI)
        }
    }

    // Create Image File
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }


    // Add photo to gallery
    private fun addPhotoToGallery() {
        MediaScannerConnection.scanFile(
            requireContext(),
            arrayOf(currentPhotoPath),
            null
        ) { path, uri ->
            Log.d(TAG, "Photo added to gallery: $path, uri: $uri")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            PermissionManager.handlePermissionResult(
                grantResults,
                onPermissionGranted = {
                    dispatchTakePictureIntent()
                },
                onPermissionDenied = {
                    Log.e(TAG, "Kamera izni reddedildi.")
                }
            )
        }
    }
}
