package com.thanh.kotlintesting.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NCoviModel(
    @SerializedName("cases")
    @Expose
    var cases: String? = null,

    @SerializedName("deaths")
    @Expose
    var deaths: String? = null,

    @SerializedName("recovered")
    @Expose
    var recovered: String? = null
)