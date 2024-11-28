// app/src/main/java/com/bignerdranch/android/moodi/MainActivity.kt

package com.bignerdranch.android.moodi.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.os.Build
import android.content.pm.PackageManager
import androidx.navigation.ui.setupWithNavController
import com.bignerdranch.android.moodi.NotificationUtils
import com.bignerdranch.android.moodi.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Removing tint programmatically because we used AppCompat for everything instead of
        // Material Component (BottomNavigationView is part of it)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.itemIconTintList = null


        // Setup bottom navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .setupWithNavController(navController)

        // Keep the notification permission check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        // Setup notification channel
        NotificationUtils.createNotificationChannel(this)
    }
}
