package com.teguh.firebaseauth1.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.teguh.firebaseauth1.R
import com.teguh.firebaseauth1.uitls.login
import com.teguh.firebaseauth1.uitls.toast
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        button_register.setOnClickListener {
            val email = edt_updateemail_email.text.toString().trim()
            val password = edt_updateemail_password.text.toString().trim()

            if (email.isEmpty()) {
                edt_updateemail_email.error = "Email Required"
                edt_updateemail_email.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edt_updateemail_email.error = "Invalid Email Address"
                edt_updateemail_email.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 6) {
                edt_updateemail_password.error = "6 Char Password Required"
                edt_updateemail_password.requestFocus()
                return@setOnClickListener
            }

            registerUser(email, password)

        }

        text_view_login.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }

    }

    private fun registerUser(email: String, password: String) {
        progressbar.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressbar.visibility = View.GONE
                if (task.isSuccessful) {
                    //registration Success -> user Login in Helper
                    login()
                } else {
                    task.exception?.message?.let {
                        toast(it)
                    }

                }
            }
    }

}
