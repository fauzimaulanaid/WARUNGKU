package com.fauzimaulana.warungku.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Store(
    val photoUrl: String? = null,
    val storeName: String? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val address: String? = null
):Parcelable {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}
