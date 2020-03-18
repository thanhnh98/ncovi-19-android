package com.thanh.kotlintesting.models

import com.thanh.kotlintesting.models.NCoviModel

data class ScopeModel(
    var global: NCoviModel,
    var vietnam: NCoviModel
)