package com.example.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notification.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

//    Notif Untuk OS Tiramisu ke Atas
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if(isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notifications permission rejected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = getString(R.string.notification_title)
        val mesagge = getString(R.string.notification_message)

        if(Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        binding.btnSendNotification.setOnClickListener {
            sendNotification(title,mesagge)
        }


        binding.btnOpenDetail.setOnClickListener {
            val detailIntent = Intent(this@MainActivity, DetailActivity::class.java)
            detailIntent.putExtra(DetailActivity.EXTRA_TITLE, title)
            detailIntent.putExtra(DetailActivity.EXTRA_MESSAGE, mesagge)
            startActivity(detailIntent)
        }

    }

    private fun sendNotification(title:String, message:String) {

        val notifDetailIntent = Intent(this, DetailActivity::class.java)
        notifDetailIntent.putExtra(DetailActivity.EXTRA_TITLE, title)
        notifDetailIntent.putExtra(DetailActivity.EXTRA_MESSAGE, message)

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://dicoding.com"))
        val pendingIntent =TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(notifDetailIntent)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            } else {
                getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        // mengirim notifikasi sesuai dengan id yang kita berikan
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // menambahkan element apa saja yang akan di tampilkan
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.notification)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // ketika notification disentuh
            .setContentIntent(pendingIntent)
            .setSubText(getString(R.string.notification_subtext))

        // notif untuk os oreo ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "dicoding channel"
    }
}