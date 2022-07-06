package com.fauzimaulana.warungku.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Store(
    val photoUrl: String? = null,
    val storeName: String? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val address: String? = null
) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}
