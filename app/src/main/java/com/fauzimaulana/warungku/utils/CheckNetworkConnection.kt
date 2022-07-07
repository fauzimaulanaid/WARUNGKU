package com.fauzimaulana.warungku.utils

import android.content.Context
import android.net.ConnectivityManager

@Suppress("DEPRECATION")
class CheckNetworkConnection {
    fun networkCheck(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}