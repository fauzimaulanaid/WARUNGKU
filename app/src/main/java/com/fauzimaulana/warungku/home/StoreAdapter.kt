package com.fauzimaulana.warungku.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fauzimaulana.warungku.R
import com.fauzimaulana.warungku.databinding.ItemListBinding
import com.fauzimaulana.warungku.model.Store
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class StoreAdapter(
    options: FirebaseRecyclerOptions<Store>,
): FirebaseRecyclerAdapter<Store, StoreAdapter.StoreViewHolder>(options) {

    inner class StoreViewHolder(private val binding: ItemListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(store: Store) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(store.photoUrl)
                    .into(imageShop)
                textViewName.text = store.storeName
                textViewCoordinate.text = itemView.context.resources.getString(R.string.coordinate_placeholder, store.lat, store.lon)
                textViewAddress.text = store.address
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_list, parent, false)
        val binding = ItemListBinding.bind(view)
        return StoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int, model: Store) {
        holder.bind(model)
    }
}