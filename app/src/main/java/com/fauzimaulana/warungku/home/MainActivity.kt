package com.fauzimaulana.warungku.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.fauzimaulana.warungku.R
import com.fauzimaulana.warungku.addupdate.AddUpdateActivity
import com.fauzimaulana.warungku.databinding.ActivityMainBinding
import com.fauzimaulana.warungku.login.LoginActivity
import com.fauzimaulana.warungku.model.Store
import com.fauzimaulana.warungku.utils.CheckNetworkConnection
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: StoreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.database
        val storeRef = db.reference.child(STORE_CHILD)

        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        manager.reverseLayout = true
        binding.rvStore.layoutManager = manager

        val options = FirebaseRecyclerOptions.Builder<Store>()
            .setQuery(storeRef, Store::class.java)
            .build()

        adapter = StoreAdapter(options)
        binding.rvStore.adapter = adapter
        adapter.startListening()
        val isConnected: Boolean = CheckNetworkConnection().networkCheck(this)
        if (isConnected) {
            binding.viewNoInternet.root.visibility = View.GONE
        } else {
            binding.viewNoInternet.root.visibility = View.VISIBLE
        }

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddUpdateActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        val user = auth.currentUser
        if (user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
    }

    private fun showAlertLogout() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        with(alertDialogBuilder) {
            setTitle(resources.getString(R.string.alert))
            setMessage(resources.getString(R.string.logout_confirmation))
            setCancelable(false)
            setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                auth.signOut()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            setNegativeButton(resources.getString(R.string.no)) { dialog, _ -> dialog.cancel() }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                showAlertLogout()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
        _binding = null
    }

    companion object {
        private const val STORE_CHILD = "store"
    }
}