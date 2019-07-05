package com.teguh.firebaseauth1.ui.fragments


import android.os.Bundle
import android.util.Patterns
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
import kotlinx.android.synthetic.main.fragment_update_email.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class UpdateEmailFragment : Fragment() {

    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutUpdateEmail.visibility = View.GONE
        layoutPassword.visibility = View.VISIBLE

        btn_updateemail_authenticate.setOnClickListener {

            val password = edt_updateemail_password.text.toString().trim()

            if (password.isEmpty()) {
                edt_updateemail_password.error = "Password required"
                edt_updateemail_password.requestFocus()
                return@setOnClickListener
            }

            //check password is correct or not
            //need to credential instance & put email from user
            //make sure that current user is not null
            currentUser?.let { user ->
                //now make credential object
                val credential = EmailAuthProvider.getCredential(user.email!!, password)

                progressbar.visibility = View.VISIBLE

                //user credential object to re-auth the user
                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        progressbar.visibility = View.GONE
                        when {
                            task.isSuccessful -> {
                                layoutPassword.visibility = View.GONE
                                layoutUpdateEmail.visibility = View.VISIBLE
                            }
                            task.exception is FirebaseAuthInvalidCredentialsException -> {//if invalid credential
                                edt_updateemail_password.error = "Invalid Password"
                                edt_updateemail_password.requestFocus()
                            }
                            else -> //if the task is not successfull,
                                context?.toast(task.exception?.message!!)
                        }
                    }

            }


        }

        //if user auth is sucess, we will see the update email layout & email updated
        btn_updateemail_update.setOnClickListener {
            val email = edt_updateemail_email.text.toString().trim()

            if (email.isEmpty()) {
                edt_updateemail_email.error = "Email Required"
                edt_updateemail_email.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edt_updateemail_email.error = "Valid Email Required"
                edt_updateemail_email.requestFocus()
                return@setOnClickListener
            }

            progressbar.visibility = View.VISIBLE

            currentUser?.let { user ->

                user.updateEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            progressbar.visibility = View.GONE
                            //we will start ProfileActivity Again
                            val action = UpdateEmailFragmentDirections.actionEmailUpdated()
                            Navigation.findNavController(it).navigate(action)
                        } else {
                            context?.toast(task.exception?.message!!)
                        }
                    }

            }

        }

    }


}
