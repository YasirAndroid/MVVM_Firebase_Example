package com.yasir.mvvmexample.ui

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.shashank.sony.fancytoastlib.FancyToast
import com.yasir.mvvmexample.R
import com.yasir.mvvmexample.repo.UserinfoRepo
import com.yasir.mvvmexample.utils.Status
import com.yasir.mvvmexample.viewmodel.UserInfoViewModel

class LoginActivity : AppCompatActivity() {

    var fbDataStore: FirebaseFirestore? = null
    var fbAuth: FirebaseAuth? = null

    private lateinit var viewModel: UserInfoViewModel
    private lateinit var progressDialogLayout: ProgressDialog
    private lateinit var btnSignup: Button
    private lateinit var btnLogin: Button
    private lateinit var etFullname: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    var repo = UserinfoRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initUi()
        if (fbAuth?.currentUser!=null) {
            startActivity(Intent(this, MainPageActivity::class.java))
            finish()
        }

        try {
            viewModel = ViewModelProvider(this)[UserInfoViewModel::class.java]

            viewModel.authCheck.observe(this, {
                when (it.status) {
                    Status.SUCCESS -> {
                        progressDialogLayout.hide()
                        startActivity(Intent(this, MainPageActivity::class.java))
                    }
                    Status.ERROR -> {
                        progressDialogLayout.hide()
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        progressDialogLayout.show()
                    }
                    Status.FAILED -> {
                        progressDialogLayout.hide()
                    }
                }
            })
            viewModel.getUserData(fbAuth!!.currentUser!!.uid)

        } catch (e: Exception) {

        }

        btnSignup.setOnClickListener {

            if (etEmail.text.isNotEmpty() && etFullname.text.isNotEmpty() && etPassword.text.isNotEmpty()) {
                val email = etEmail.text.toString()
                val name = etFullname.text.toString()
                val password = etPassword.text.toString()
                viewModel.signUp(name, email, password)
            } else {
                FancyToast.makeText(
                    this,
                    "Please Fill All Fields",
                    FancyToast.LENGTH_LONG,
                    FancyToast.ERROR, false
                ).show()
            }
        }

        btnLogin.setOnClickListener {

            if (etEmail.text.isNotEmpty() && etFullname.text.isNotEmpty() && etPassword.text.isNotEmpty()) {
                val email = etEmail.text.toString()
                val name = etFullname.text.toString()
                val password = etPassword.text.toString()
                viewModel.login(name, email, password)
            } else {
                FancyToast.makeText(
                    this,
                    "Please Fill All Fields",
                    FancyToast.LENGTH_LONG,
                    FancyToast.ERROR, false
                ).show()
            }
        }
    }

    fun initUi() {
        fbAuth = FirebaseAuth.getInstance()
        fbDataStore = FirebaseFirestore.getInstance()
        progressDialogLayout = ProgressDialog(this)
        btnLogin = findViewById(R.id.btn_login)
        btnSignup = findViewById(R.id.btn_signup)
        etFullname = findViewById(R.id.et_fullname)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
    }
}