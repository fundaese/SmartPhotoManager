package com.funda.smartphotomanager.utils

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionManager {
    fun checkAndRequestPermission(
        fragment: Fragment,
        permission: String,
        requestCode: Int,
        grantedAction: () -> Unit
    ) {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            grantedAction()
        } else {
            ActivityCompat.requestPermissions(
                fragment.requireActivity(),
                arrayOf(permission),
                requestCode
            )
        }
    }

    fun handlePermissionResult(
        grantResults: IntArray,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }
}
