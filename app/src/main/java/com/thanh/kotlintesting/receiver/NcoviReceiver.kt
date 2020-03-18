package com.thanh.kotlintesting.receiver

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.thanh.kotlintesting.NotificationHelper
import com.thanh.kotlintesting.models.ResponseModel
import com.thanh.kotlintesting.network.RestClientJava
import com.thanh.kotlintesting.network.service.NcoviService
import com.thanh.kotlintesting.service.ForegroundService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

@RequiresApi(Build.VERSION_CODES.O)
class NcoviReceiver : BroadcastReceiver() {
    var isUpdating = false
    private val URL:String="api/ncov-moh/index.php?type=vn"
    lateinit var dataReset : BehaviorSubject<ResponseModel>

    override fun onReceive(context: Context?, intent: Intent?) {

        var actionCode:String = intent?.extras?.getString("code")?:""
        Log.e("actionname",actionCode)
        when(actionCode){
            NotificationHelper.ACTION_CLOSE -> close(context)
            NotificationHelper.ACTION_UPDATE -> update(context)
            else -> update(context)
        }
        //
    }
    private fun update(context: Context?){
        if (!isUpdating){
            initObservable(context)
            initService(context, URL)
            isUpdating=true
        }
    }

    private fun close(context: Context?){
        context?.stopService(Intent(context,ForegroundService::class.java))
        if( context is Activity)
            context.finish()
    }

    private fun initService(context: Context?, url:String){
        var service: NcoviService = RestClientJava.createService(
            NcoviService::class.java)
        service.getData(url).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                dataReset.onNext(it)
                Toast.makeText(context,"Đã cập nhật", Toast.LENGTH_SHORT).show()
                isUpdating = false
            },{
                Log.e("error",it.message)
                isUpdating = false
            })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initObservable(context: Context?){
        dataReset = BehaviorSubject.create()
        dataReset.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {it->
                if(it.exitValue){
                    var case=it.data?.vietnam?.cases
                    var deaths=it.data?.vietnam?.deaths
                    var recovery=it.data?.vietnam?.recovered
                    var intent=Intent(context, ForegroundService::class.java)
                    intent.putExtra("case",case)
                    intent.putExtra("deaths",deaths)
                    intent.putExtra("recovery",recovery)
                    Log.e("start service","start->"+intent.extras?.get("case"))
                    context?.startForegroundService(intent)
                }
            }
    }
}