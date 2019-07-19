package com.teguh.firebaseauth1.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.FirebaseDatabase
import com.teguh.firebaseauth1.R
import com.teguh.firebaseauth1.model.Rate
import com.teguh.firebaseauth1.uitls.toast

class RateAdapter(val nCtx: Context, val layoutResId: Int, val rateList: List<Rate>) :
    ArrayAdapter<Rate>(nCtx, layoutResId, rateList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        //create custom layout
        //include layout
        val layoutInflater: LayoutInflater = LayoutInflater.from(nCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        //get textView
        val tvName = view.findViewById<TextView>(R.id.tv_rate_name);
        val tvUpdate = view.findViewById<TextView>(R.id.tv_rate_update);

        //get data rate
        val rate = rateList[position]

        tvName.text = rate.name
        tvUpdate.setOnClickListener {
            //create parameters to set value that existing when it click
            showUpdateDialog(rate)

        }

        return view

    }

    fun showUpdateDialog(rate: Rate) {
        val builder = AlertDialog.Builder(nCtx)
        builder.setTitle("Update Rate")

        //we need a layout inflater object to inflate this object
        val inflater = LayoutInflater.from(nCtx)
        val view = inflater.inflate(R.layout.layout_update_rate, null)

        //from this view, get data from edittext and rating bar
        val edtName = view.findViewById<EditText>(R.id.edt_urate_name)
        val rbRate = view.findViewById<RatingBar>(R.id.rb_urate_star)

        //use parameter rate to display existing values
        edtName.setText(rate.name)
        rbRate.rating = rate.rate!!.toFloat()

        //set this view to Builder
        builder.setView(view)

        builder.setPositiveButton("Update") { dialog, which ->//use lamda
            //update the value
            val dbRate = FirebaseDatabase.getInstance().getReference("rates")

            //get the new value
            val name = edtName.text.toString().trim()
            val rating = rbRate.rating.toInt()

            if (name.isEmpty()) {
                edtName.error = "Please Enter the value"
                edtName.requestFocus()
                return@setPositiveButton
            }

            if (rating == 0) {
                rbRate.requestFocus()
                nCtx.toast("Rating tidak boleh kosong")
                return@setPositiveButton
            }

            val rate = Rate(rate.id, name, rbRate.rating.toInt())

            dbRate.child(rate.id.toString()).setValue(rate).addOnCompleteListener {
                nCtx.toast("Rate from ${rate.id} Updated")
            }

//            nCtx.toast("Rate from ${rate.id} Updated")

        }


        builder.setNegativeButton("Cancel") { dialog, which -> }

        //show alert dialog
        val alert = builder.create()
        alert.show()

    }


}
