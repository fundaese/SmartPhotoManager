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

    fun loadPhotos() {
        Log.d(TAG, "loadPhotos: Fotoğraflar yüklenmeye başlıyor.")
        viewModelScope.launch {
            val photoList = repository.getPhotosFromGallery()
            if (photoList.isNotEmpty()) {
                Log.d(TAG, "loadPhotos: ${photoList.size} fotoğraf bulundu.")
            } else {
                Log.d(TAG, "loadPhotos: Fotoğraf bulunamadı.")
            }
            _photos.value = photoList
        }
    }
}
