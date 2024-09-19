package com.funda.smartphotomanager.ui.fragment.fullscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.funda.smartphotomanager.databinding.FragmentFullScreenImageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FullScreenImageFragment : Fragment() {

    private lateinit var binding: FragmentFullScreenImageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullScreenImageBinding.inflate(inflater, container, false)

        val imageUri = arguments?.getString("imageUri")

        imageUri?.let {
            Glide.with(this)
                .load(it)
                .into(binding.fullscreenImageView)
        }

        return binding.root
    }
}
