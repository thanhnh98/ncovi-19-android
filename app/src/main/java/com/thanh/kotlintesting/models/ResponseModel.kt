package com.thanh.kotlintesting.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.thanh.kotlintesting.models.ScopeModel


data class ResponseModel(
    @SerializedName("success")
    @Expose
    var exitValue:Boolean,
    @SerializedName("data")
    @Expose
    var data: ScopeModel? = null
)