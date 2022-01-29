package com.yasir.mvvmexample.model

data class UserData(
    val fullName: String = "",
    val email: String = "",
    val password: String = "") {

    constructor():this("","","")

}
