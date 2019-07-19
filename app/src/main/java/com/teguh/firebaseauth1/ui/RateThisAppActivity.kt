package com.teguh.firebaseauth1.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.teguh.firebaseauth1.R
import com.teguh.firebaseauth1.model.Rate
import com.teguh.firebaseauth1.uitls.toast
import kotlinx.android.synthetic.main.activity_rate_this_app.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.teguh.firebaseauth1.adapter.RateAdapter


class RateThisAppActivity : AppCompatActivity() {

    private val ref = FirebaseDatabase.getInstance().getReference("rates")
    lateinit var rateList: MutableList<Rate>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate_this_app)

        rateList = mutableListOf()
        btn_rate_save.setOnClickListener {
            saveRate()
        }


    }

    //    write data
    private fun saveRate() {
        val name = edt_rate_name.text.toString().trim()

        if (name.isEmpty()) {
            edt_rate_name.error = "Name Required"
            edt_rate_name.requestFocus()
            return
        }

        if (rb_rate_star.rating.toInt() == 0) {
            toast("Rating tidak Boleh kosong")
            rb_rate_star.requestFocus()
            return
        }

        val rateId = ref.push().key.toString()
        val rate = Rate(rateId, name, rb_rate_star.rating.toInt())//1. create Rate Object

        ref.child(rateId).setValue(rate).addOnCompleteListener {
            if (it.isSuccessful) {
                //to clear filled input
                rb_rate_star.rating = 0F
                edt_rate_name.setText("")

                this.toast("Successfull save data to realtime database on firebase")
            } else {
                this.toast("Failed save data to firebase $rateId $name ${rb_rate_star.numStars}")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //    read data
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                toast("Failed to read data from firebase")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    rateList.clear()
                    for (r in p0.children) {
                        val rate = r.getValue(Rate::class.java)
                        rateList.add(rate!!)
                    }

                    val adapter = RateAdapter(this@RateThisAppActivity, R.layout.item_rates, rateList)
                    lv_rate.adapter = adapter
                    // when we reading value of database/firebase
                    // we need to define an empty CONSTRUCTOR MODEL CLASS
                }
            }

        })
    }


}

