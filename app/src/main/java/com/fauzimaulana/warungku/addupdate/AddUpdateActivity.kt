package com.fauzimaulana.warungku.addupdate

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.fauzimaulana.warungku.R
import com.fauzimaulana.warungku.databinding.ActivityAddUpdateBinding
import com.fauzimaulana.warungku.home.MainActivity
import com.fauzimaulana.warungku.model.Store
import com.fauzimaulana.warungku.utils.Utils
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileInputStream
import java.util.jar.Attributes

class AddUpdateActivity : AppCompatActivity() {

    private var _binding: ActivityAddUpdateBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = Firebase.storage

        db = Firebase.database

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRES_PERMISSION, REQUEST_CODE_PERMISSION)
        }

        binding.buttonCamera.setOnClickListener {
            startTakePhoto()
        }
        binding.buttonGallery.setOnClickListener {
            startGallery()
        }
        binding.buttonSubmit.setOnClickListener {
            val storeName = binding.nameEditText.text.toString()
            val coordinate = binding.coordinateEditText.text.toString()
            val address = binding.addressEditText.text.toString()
            when {
                storeName.isEmpty() -> {
                    binding.nameEditTextLayout.error = resources.getString(R.string.name_empty)
                }
                coordinate.isEmpty() -> {
                    binding.coordinateEditTextLayout.error = resources.getString(R.string.coordinate_empty)
                }
                address.isEmpty() -> {
                    binding.addressEditTextLayout.error = resources.getString(R.string.address_empty)
                }
                else -> {
                    binding.contentAddUpdate.visibility = View.GONE
                    binding.viewLoading.root.visibility = View.VISIBLE
                    val latLngCoordinate = coordinate.split(",")
                    val lat: Double = latLngCoordinate[0].toDouble()
                    val lon: Double = latLngCoordinate[1].toDouble()
                    addStore(storeName, lat, lon, address)
                }
            }
        }
    }

    private fun allPermissionGranted() = REQUIRES_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionGranted()) {
                Toast.makeText(this, "Did not get permission to access camera", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun addStore(storeName: String, lat: Double, lon: Double, address: String) {
        val storageRef = storage.reference
        val uploadRef = storageRef.child("$storeName.jpg")

        val stream = FileInputStream(getFile)
        val uploadTask = uploadRef.putStream(stream)
        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "Upload success", Toast.LENGTH_SHORT).show()
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener { photoUrl ->
                val storeRef = db.reference.child(STORE_CHILD)
                val newStore = Store(
                    photoUrl.toString(),
                    storeName,
                    lat,
                    lon,
                    address
                )
                storeRef.push().setValue(newStore).addOnSuccessListener {
                    updateUI()
                }.addOnFailureListener {
                    binding.viewLoading.root.visibility = View.GONE
                    binding.contentAddUpdate.visibility = View.VISIBLE
                    Toast.makeText(this, "Failed to created a new store", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            binding.viewLoading.root.visibility = View.GONE
            binding.contentAddUpdate.visibility = View.VISIBLE
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        binding.viewLoading.root.visibility = View.GONE
        binding.viewSuccess.root.visibility = View.VISIBLE
        val screenTime = 3000L
        Handler(mainLooper).postDelayed({
            val intent = Intent(this@AddUpdateActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }, screenTime)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        Utils.createCustomTemptFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddUpdateActivity,
                "com.fauzimaulana.warungku",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            val result = BitmapFactory.decodeFile(myFile.path)
            getFile = myFile
            binding.previewImageShop.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFIle = Utils.uriToFile(selectedImg, this@AddUpdateActivity)
            getFile = myFIle
            binding.previewImageShop.setImageURI(selectedImg)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private val REQUIRES_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
        private const val STORE_CHILD = "store"
    }
}