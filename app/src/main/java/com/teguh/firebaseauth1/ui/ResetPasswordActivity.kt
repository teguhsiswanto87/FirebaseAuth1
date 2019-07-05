package com.teguh.firebaseauth1.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.teguh.firebaseauth1.R
import com.teguh.firebaseauth1.uitls.toast
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        btn_reset_password.setOnClickListener {
            val email = edt_reset_email.text.toString().trim()

            if (email.isEmpty()) {
                edt_reset_email.error = "Email Required"
                edt_reset_email.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edt_reset_email.error = "Invalid Email"
                edt_reset_email.requestFocus()
                return@setOnClickListener
            }

            progressbar.visibility = View.VISIBLE

            FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    progressbar.visibility = View.GONE
                    if (task.isSuccessful) {
                        this.toast("Check Your Email $email")
                    }else{
                        this.toast(task.exception?.message!!)
                    }
                }

        }

    }

}
