package com.yasir.mvvmexample.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yasir.mvvmexample.model.LoginResponseModel
import com.yasir.mvvmexample.model.UserData
import com.yasir.mvvmexample.repo.UserinfoRepo
import com.yasir.mvvmexample.utils.BaseResponse

class UserInfoViewModel : ViewModel() {

    var repo = UserinfoRepo
    var userData = MutableLiveData<BaseResponse<UserData>>()
    var authCheck = MutableLiveData<BaseResponse<LoginResponseModel>>()

    fun getUserData(id: String) {
        userData.postValue(BaseResponse.loading(null))
        repo.getUserData(id).observeForever {
            userData.postValue(it)
        }
    }

    fun login(name: String, email: String, password: String) {

        authCheck.postValue(BaseResponse.loading())

        repo.loginAccount(name, email, password).observeForever {
            authCheck.postValue(it)
        }
    }

    fun signUp(name: String, email: String, password: String) {

        authCheck.postValue(BaseResponse.loading())

        repo.createAccount(name, email, password).observeForever {
            authCheck.postValue(it)
        }
    }
}