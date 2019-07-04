package com.teguh.firebaseauth1.ui.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

import com.teguh.firebaseauth1.R
import com.teguh.firebaseauth1.uitls.toast
import kotlinx.android.synthetic.main.fragment_verify_phone.*
import java.util.concurrent.TimeUnit


class VerifyPhoneFragment : Fragment() {

    private var verificationId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verify_phone, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //show or hide layout
        layoutPhone.visibility = View.VISIBLE
        layoutVerification.visibility = View.INVISIBLE

        btn_verifyphone_send_verification.setOnClickListener {

            val phone = edt_verifyphone_phone.text.toString().trim()
            if (phone.isEmpty() || phone.length != 10) {
                edt_verifyphone_phone.error = "Enter a valid number"
                edt_verifyphone_phone.requestFocus()
                return@setOnClickListener
            }
            //if phone number is OK,then create a country code from selected item
            val phoneNumber = '+' + ccp_countrycode.selectedCountryCode + phone

            PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, requireActivity(), phoneAuthCallbacks)


            //show or hide layout
            layoutPhone.visibility = View.GONE
            layoutVerification.visibility = View.VISIBLE
        }

        // when user enter verifiationCode manual, so create button listener
        btn_verifyphone_verify.setOnClickListener {
            val code = edt_verifyphone_code.text.toString().trim()

            if (code.isEmpty()) {
                edt_verifyphone_code.error = "Code Required"
                edt_verifyphone_code.requestFocus()
                return@setOnClickListener //stop the further execution
            }

            //if we have a code, we need to generate phone credential object
            //fisrt, check verifiactionId is not null, easy do that, use let.
            // only run this code when varificationId is not null
            verificationId?.let {
                //it -> verificationId & code -> code that enter by user
                val credential = PhoneAuthProvider.getCredential(it, code)
                addPhoneNumber(credential)
            }

        }

    }

    private val phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential?) {
            //check phoneCredential null or not null
            phoneAuthCredential?.let {
                addPhoneNumber(it)
            }
        }

        override fun onVerificationFailed(exception: FirebaseException?) {
            context?.toast(exception?.message!!)
        }

        //when verification code not able automatically, so create global var to save code
        override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken?) {
            super.onCodeSent(verificationId, token)
            this@VerifyPhoneFragment.verificationId = verificationId
        }

    }

    private fun addPhoneNumber(phoneAuthCredential: PhoneAuthCredential) {
        FirebaseAuth.getInstance()
            .currentUser?.updatePhoneNumber(phoneAuthCredential)
            ?.addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    context?.toast("Phone Added")
                    //if phone number added, then direct to ProfileFragment (see the direction off fragment from nav_graph)
                    val action = VerifyPhoneFragmentDirections.actionPhoneVerified()
                    Navigation.findNavController(btn_verifyphone_verify).navigate(action)
                } else {
                    context?.toast(it.exception?.message!!)
                }
            }
    }

}
