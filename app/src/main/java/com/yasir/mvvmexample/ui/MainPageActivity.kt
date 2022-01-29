package com.yasir.mvvmexample.ui

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yasir.mvvmexample.R
import com.yasir.mvvmexample.utils.Status
import com.yasir.mvvmexample.viewmodel.UserInfoViewModel

class MainPageActivity : AppCompatActivity() {

    var fbDataStore: FirebaseFirestore? = null
    var fbAuth: FirebaseAuth? = null

    private lateinit var viewModel: UserInfoViewModel
    private lateinit var progressDialogLayout: ProgressDialog
    private lateinit var email: TextView
    private lateinit var name: TextView
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            initUi()
            viewModel = ViewModelProvider(this)[UserInfoViewModel::class.java]

            viewModel.userData.observe(this, {
                when (it.status) {
                    Status.SUCCESS -> {
                        progressDialogLayout.hide()

                        //Show Details
                        email.append(it.data?.email.toString())
                        name.append(it.data?.fullName.toString())
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

        btnLogout.setOnClickListener {
            fbAuth?.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun initUi() {
        fbAuth = FirebaseAuth.getInstance()
        fbDataStore = FirebaseFirestore.getInstance()
        progressDialogLayout = ProgressDialog(this)
        email = findViewById(R.id.tv_email)
        name = findViewById(R.id.tv_fullname)
        btnLogout = findViewById(R.id.btn_logout)
    }
}