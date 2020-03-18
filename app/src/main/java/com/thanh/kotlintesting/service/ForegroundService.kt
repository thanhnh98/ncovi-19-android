package com.thanh.kotlintesting.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.thanh.kotlintesting.NotificationHelper
import com.thanh.kotlintesting.models.NCoviModel
import com.thanh.kotlintesting.network.service.NcoviService
import com.thanh.kotlintesting.receiver.NcoviReceiver
import java.math.BigInteger

@RequiresApi(Build.VERSION_CODES.O)
class ForegroundService : Service() {
    lateinit var notificationHelper:NotificationHelper
    private lateinit var receiver: NcoviReceiver

    override fun onCreate() {
        super.onCreate()
        receiver  = NcoviReceiver()
        registerReceiver(receiver, IntentFilter("NCOVI_RECEIVER"))
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("OK","In Foreground")
        var case:String=intent?.extras?.get("case").toString()
        var recovery:String=intent?.extras?.get("recovery").toString()
        var  deaths:String=intent?.extras?.get("deaths").toString()
        var data=NCoviModel(case,deaths,recovery)
        notificationHelper= NotificationHelper(this,data)
        startForeground(1001,notificationHelper.builder())
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (receiver!=null) {
            unregisterReceiver(receiver)
        }
    }

}