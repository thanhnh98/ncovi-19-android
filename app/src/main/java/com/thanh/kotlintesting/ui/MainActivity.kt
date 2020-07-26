package com.thanh.kotlintesting.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var data:BehaviorSubject<ResponseModel>
    private val requestUrl:String= Constants.API_URL
    lateinit var processBar:ProgressDialog
    private var isShowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        processBar = ProgressDialog(this)
        initBehaviorData()
        initService(requestUrl)
        hideGlobal()
        tv_area_global.setOnClickListener(this)
        img_show.setOnClickListener(this)
    }

    private fun initBehaviorData() {
        data= BehaviorSubject.create()
        data.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {it->
                if(it.exitValue){
                    if(processBar.isShowing) processBar.dismiss()
                    var case= it.data?.vietnam?.cases
                    var deaths=it.data?.vietnam?.deaths
                    var recovery=it.data?.vietnam?.recovered
                    tvSoCaNhiem.text= getString(R.string.text_sick).replace("%s",case?:"---" , false)
                    tvTuVong.text= getString(R.string.text_death).replace("%s",deaths?:"---" , false)
                    tvRecovery.text=getString(R.string.text_recovery).replace("%s", recovery?:"---", false)
                    var intent=Intent(this,ForegroundService::class.java)
                    intent.putExtra(Constants.BUNDLE_KEY_CASE,case)
                    intent.putExtra(Constants.BUNDLE_KEY_DEATH,deaths)
                    intent.putExtra(Constants.BUNDLE_KEY_RECOVERED,recovery)
                    startForegroundService(intent)

                    buildDataForGlobal(it)
                }
            }

    }

    private fun buildDataForGlobal(it: ResponseModel?) {
        var caseGlobal= numberFormat(it?.data?.global?.cases!!.toInt())
        var deathsGlobal=numberFormat(it?.data?.global?.deaths!!.toInt())
        var recoveryGlobal=numberFormat(it?.data?.global?.recovered!!.toInt())
        tvSoCaNhiemGlobal.text= getString(R.string.text_sick).replace("%s",caseGlobal?:"---" , false)
        tvTuVongGlobal.text= getString(R.string.text_death).replace("%s",deathsGlobal?:"---" , false)
        tvRecoveryGlobal.text=getString(R.string.text_recovery).replace("%s", recoveryGlobal?:"---", false)

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

    private fun hideGlobal() {
        img_show.setImageResource(R.drawable.ic_arrow_drop_down)
        ctl_global.visibility = GONE
    }
    private fun showGlobal(){
        img_show.setImageResource(R.drawable.ic_arrow_drop_up)
        ctl_global.visibility = VISIBLE
    }

    private fun updateStatusGlobalShowing(){
        if (isShowing) {
            hideGlobal()
        }else
            showGlobal()
        isShowing = !isShowing
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tv_area_global, R.id.img_show -> updateStatusGlobalShowing()
        }
    }

    private fun numberFormat(count:Int):String{
            val formatter =
                NumberFormat.getInstance(Locale.US) as DecimalFormat
            val symbols = formatter.decimalFormatSymbols
            symbols.groupingSeparator = '.'
            formatter.decimalFormatSymbols = symbols
            return formatter.format(count.toLong())
    }

}
