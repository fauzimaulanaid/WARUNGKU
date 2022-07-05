package com.fauzimaulana.warungku.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fauzimaulana.warungku.R
import com.fauzimaulana.warungku.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}