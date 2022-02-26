package com.example.android.examenadolfo.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity

object NetworkConnection {

    @SuppressLint("MissingPermission", "NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun isConnected(activity: Activity?): Boolean {
        activity?.let {
            val manager = it.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            val activeNetwork = manager?.activeNetwork ?: return false
            val networkCapabilities = manager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun hasInternetConnection(activity: FragmentActivity?): Boolean {
        activity?.let {
            return if (isConnected(it)) {
                true
            } else {
                MyCustomDialog().show(activity.supportFragmentManager, "MyCustomFragment")
                return false
            }
        }
        return false
    }
}