package com.fauzimaulana.warungku.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fauzimaulana.warungku.R
import com.fauzimaulana.warungku.addupdate.AddUpdateActivity
import com.fauzimaulana.warungku.databinding.ActivityDetailBinding
import com.fauzimaulana.warungku.model.Store
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = Firebase.database

        val store = intent.getParcelableExtra<Store>(EXTRA_DATA)
        showDetailStory(store!!)

        binding.fabEdit.setOnClickListener {
            val intent = Intent(this, AddUpdateActivity::class.java)
            intent.putExtra(AddUpdateActivity.EXTRA_STORE, store)
            startActivity(intent)
        }
    }

    private fun showDetailStory(store: Store) {
        with(binding) {
            Glide.with(this@DetailActivity)
                .load(store.photoUrl)
                .into(imageShop)
            textViewNameBody.text = store.storeName
            textViewCoordinateBody.text = resources.getString(R.string.coordinate_placeholder, store.lat, store.lon)
            textViewAddressBody.text = store.address
        }
    }

    private fun deleteStore(store: Store) {
        val storeRef = db.reference
        val query = storeRef.child(STORE_CHILD).orderByChild("storeName").equalTo(store.storeName)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapshot in snapshot.children) {
                    itemSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DetailActivity", error.message)
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteStore -> {
                val store = intent.getParcelableExtra<Store>(EXTRA_DATA)
                deleteStore(store!!)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
        private const val STORE_CHILD = "store"
    }
}