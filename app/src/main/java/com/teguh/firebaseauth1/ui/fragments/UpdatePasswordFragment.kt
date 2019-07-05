package com.teguh.firebaseauth1.ui.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

import com.teguh.firebaseauth1.R
import com.teguh.firebaseauth1.uitls.toast
import kotlinx.android.synthetic.main.fragment_update_password.layoutPassword
import kotlinx.android.synthetic.main.fragment_update_password.layoutUpdatePassword
import kotlinx.android.synthetic.main.fragment_update_password.*

class UpdatePasswordFragment : Fragment() {

    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_password, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutPassword.visibility = View.VISIBLE
        layoutUpdatePassword.visibility = View.GONE

        btn_updatepassword_authenticate.setOnClickListener {

            layoutPassword.visibility = View.VISIBLE
            layoutUpdatePassword.visibility = View.GONE

            val password = edt_updatepassword_password.text.toString().trim()

            if (password.isEmpty()) {
                edt_updatepassword_password.error = "password required"
                edt_updatepassword_password.requestFocus()
                return@setOnClickListener
            }

            currentUser?.let { user ->
                val credential = EmailAuthProvider.getCredential(user.email!!, password)

                progressbar_password.visibility = View.VISIBLE

                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        progressbar_password.visibility = View.GONE
                        when {
                            task.isSuccessful -> {
                                layoutPassword.visibility = View.GONE
                                layoutUpdatePassword.visibility = View.VISIBLE
                            }
                            task.exception is FirebaseAuthInvalidCredentialsException -> {
                                edt_updatepassword_password.error = "Invalid Password"
                                edt_updatepassword_password.requestFocus()
                            }
                            else -> context?.toast(task.exception?.message!!)
                        }
                    }

            }

        }

        //now attach, button update listener
        btn_updatepassword_update.setOnClickListener {

            val password = edt_updatepassword_newpassword.text.toString().trim()
            val confirmPassword = edt_updatepassword_newpasswordconfirmation.text.toString().trim()

            if (password.isEmpty() || password.length < 6) {
                edt_updatepassword_newpassword.error = "password required, at least 6 characters"
                edt_updatepassword_newpassword.requestFocus()
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                edt_updatepassword_newpasswordconfirmation.error = "confirm password required"
                edt_updatepassword_newpasswordconfirmation.requestFocus()
                return@setOnClickListener
            }
            if (!password.equals(confirmPassword)) {
                edt_updatepassword_newpasswordconfirmation.error = "confirm password did not match"
                edt_updatepassword_newpasswordconfirmation.requestFocus()
                return@setOnClickListener
            }

            //if validation success, we will change the password
            currentUser?.let { user ->
                //show progress bar while updating password
                progressbar_password.visibility = View.VISIBLE

                user.updatePassword(password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            progressbar_password.visibility = View.VISIBLE
                            val action = UpdatePasswordFragmentDirections.actionPasswordUpdated()
                            Navigation.findNavController(it).navigate(action)
                            context?.toast("Password Updated")
                            // if the user forget password. (make this feature)
                        } else {
                            context?.toast(task.exception?.message!!)
                        }
                    }
            }

        }

    }

}
