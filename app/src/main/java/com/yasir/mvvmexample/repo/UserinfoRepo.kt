package com.yasir.mvvmexample.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.yasir.mvvmexample.model.UserData
import com.yasir.mvvmexample.utils.BaseResponse
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yasir.mvvmexample.model.LoginResponseModel

object UserinfoRepo {

    var auth = Firebase.auth

    fun getUserData(id: String): MutableLiveData<BaseResponse<UserData>> {
        val mutableLiveData = MutableLiveData<BaseResponse<UserData>>()
        FirebaseFirestore.getInstance()
            .collection("Users")
            .document(id)
            .addSnapshotListener{ it, e->

                if (e != null) {
                    mutableLiveData.postValue(
                        BaseResponse.error(
                            e.message ?: "",
                            null
                        )
                    )
                    return@addSnapshotListener
                }

                if (it != null && it.exists()) {

                    val data = it.toObject(UserData::class.java)
                    mutableLiveData.postValue(BaseResponse.success(data))

                } else {
                    mutableLiveData.postValue(
                        BaseResponse.failed(
                            "data is null",
                            null
                        )
                    )
                }
            }

        return mutableLiveData
    }

    fun createAccount(name: String, email: String, password: String) : MutableLiveData<BaseResponse<LoginResponseModel>> {
        val authCheck = MutableLiveData<BaseResponse<LoginResponseModel>>()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val data = hashMapOf(
                        "fullName" to name,
                        "email" to email,
                        "password" to password
                    )
                    FirebaseFirestore.getInstance()
                        .collection("Users").document(user!!.uid)
                        .set(data)
                        .addOnCompleteListener {
                            authCheck.postValue(BaseResponse.success(LoginResponseModel(true)))
                        }
                        .addOnFailureListener {
                            authCheck.postValue(BaseResponse.error(it.message.toString(), null))
                        }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    authCheck.postValue(BaseResponse.error(task.exception?.message.toString(), null))

                }
            }
        return authCheck
    }

    fun loginAccount(name: String, email: String, password: String): MutableLiveData<BaseResponse<LoginResponseModel>> {
        val authCheck = MutableLiveData<BaseResponse<LoginResponseModel>>()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithEmail:success")
                    val user = auth.currentUser
                    authCheck.postValue(BaseResponse.success(LoginResponseModel(true)))

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    authCheck.postValue(BaseResponse.error(task.exception?.message.toString(), null))
                }
            }
        return authCheck
    }
}