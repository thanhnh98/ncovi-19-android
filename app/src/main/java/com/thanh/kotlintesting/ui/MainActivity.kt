package com.thanh.kotlintesting.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.thanh.kotlintesting.Constants
import com.thanh.kotlintesting.R
import com.thanh.kotlintesting.models.ResponseModel
import com.thanh.kotlintesting.network.RestClientJava
import com.thanh.kotlintesting.network.service.NcoviService
import com.thanh.kotlintesting.service.ForegroundService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main.*

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    lateinit var data:BehaviorSubject<ResponseModel>
    private val requestUrl:String= Constants.API_URL
    lateinit var processBar:ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        processBar = ProgressDialog(this)
        initBehaviorData()
        initService(requestUrl)
    }

    private fun initBehaviorData() {
        data= BehaviorSubject.create()
        data.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {it->
                if(it.exitValue){
                    Log.e("data resonse",it.toString())
                    if(processBar.isShowing) processBar.dismiss()
                    var case=it.data?.vietnam?.cases
                    var deaths=it.data?.vietnam?.deaths
                    var recovery=it.data?.vietnam?.recovered
                    tvSoCaNhiem.text= "Hiện có $case ca nhiễm"
                    tvTuVong.text="Đã có $deaths trường hợp tử vong"
                    tvRecovery.text="Đã hồi phục $recovery"
                    var intent=Intent(this,ForegroundService::class.java)
                    intent.putExtra("case",case)
                    intent.putExtra("deaths",deaths)
                    intent.putExtra("recovery",recovery)
                    Log.e("start service","start->"+intent.extras?.get("case"))
                    startForegroundService(intent)
                }
            }

    }

    fun initService(url:String){
        if (!processBar.isShowing) {
            processBar.setTitle("Đang cập nhật...")
            processBar.show()
        }
        var service: NcoviService = RestClientJava.createService(
            NcoviService::class.java)
        service.getData(url).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                data.onNext(it)
            },{
                Log.e("error",it.message)
            })
    }
}
