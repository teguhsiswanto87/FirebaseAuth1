package com.teguh.firebaseauth1.ui.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.teguh.firebaseauth1.R
import kotlinx.android.synthetic.main.fragment_verify_phone.*


class VerifyPhoneFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verify_phone, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        layoutPhone.visibility = View.VISIBLE
        layoutVerification.visibility = View.INVISIBLE

        btn_verifyphone_send_verification.setOnClickListener {
            layoutPhone.visibility = View.GONE
            layoutVerification.visibility = View.VISIBLE
        }

        super.onViewCreated(view, savedInstanceState)
    }

}
