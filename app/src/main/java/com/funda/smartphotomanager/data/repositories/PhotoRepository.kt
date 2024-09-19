package com.funda.smartphotomanager.data.repositories

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.funda.smartphotomanager.data.model.PhotoModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "PhotoRepository"

    suspend fun getPhotosFromGallery(): List<PhotoModel> {
        Log.d(TAG, "getPhotosFromGallery: Galeriden fotoğraflar çekiliyor.")
        return withContext(Dispatchers.IO) {
            val photoList = mutableListOf<PhotoModel>()

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
            )

            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

            val query = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
                        .appendPath(id.toString()).build()

                    Log.d(TAG, "Photo found: $contentUri")
                    val photo = PhotoModel(id, contentUri.toString())
                    photoList.add(photo)
                }
            }

            if (photoList.isNotEmpty()) {
                Log.d(TAG, "getPhotosFromGallery: ${photoList.size} fotoğraf bulundu.")
            } else {
                Log.d(TAG, "getPhotosFromGallery: Fotoğraf bulunamadı.")
            }

            photoList
        }
    }
}
