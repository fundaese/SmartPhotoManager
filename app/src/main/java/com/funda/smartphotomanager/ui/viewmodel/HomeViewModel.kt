package com.funda.smartphotomanager.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.funda.smartphotomanager.data.model.PhotoModel
import com.funda.smartphotomanager.data.repositories.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PhotoRepository
) : ViewModel() {

    private val _photos = MutableLiveData<List<PhotoModel>>()
    val photos: LiveData<List<PhotoModel>> get() = _photos

    private val TAG = "HomeViewModel"

    private var allPhotos = listOf<PhotoModel>()

    // Load Photo
    fun loadPhotos() {
        Log.d(TAG, "loadPhotos: Fotoğraflar yüklenmeye başlıyor.")

        viewModelScope.launch {
            val photoList = repository.getPhotosFromGallery()
            allPhotos = photoList
            _photos.value = photoList
        }
    }

    // Filter by name
    fun filterPhotosByName(query: String) {
        val filteredPhotos = allPhotos.filter { it.name.contains(query, ignoreCase = true) }
        _photos.value = filteredPhotos
    }

    // Filter by date
    fun sortPhotosByDate() {
        val sortedPhotos = allPhotos.sortedByDescending { it.date }
        _photos.value = sortedPhotos
    }

    // Filter & Sort by name
    fun sortPhotosByName() {
        val sortedPhotos = allPhotos.sortedBy { it.name }
        _photos.value = sortedPhotos
    }
}
