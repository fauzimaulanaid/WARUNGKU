package com.fauzimaulana.warungku.addupdate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fauzimaulana.warungku.R
import com.fauzimaulana.warungku.databinding.ActivityAddUpdateBinding

class AddUpdateActivity : AppCompatActivity() {

    private var _binding: ActivityAddUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}