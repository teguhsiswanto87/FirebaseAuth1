package com.teguh.firebaseauth1.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import com.teguh.firebaseauth1.R
import com.teguh.firebaseauth1.uitls.logout
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(toolbar)

        val navController = Navigation.findNavController(this, R.id.fragment)
        NavigationUI.setupWithNavController(nav_view, navController)
        //when fragment change, title of action bar(toolbar) also change
        NavigationUI.setupActionBarWithNavController(this, navController, drawer_layout)

    }

    //to handle navigation drawer (hamburger icon)
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.fragment), drawer_layout
        )
    }//we need Fragment to display this, so you must create FragmentLayout Home and Profile

    //option menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == R.id.action_logout) {
            AlertDialog.Builder(this).apply {
                setTitle("Logout")
                setMessage("Are you sure to logout from this account ?")
                setIcon(R.drawable.ic_warning)
                setPositiveButton("Yes") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    logout() //this fun only intent from home activity to Login Activity
                }
                setNegativeButton("Cancel") { _, _ ->

                }
            }.create().show()
        } else {

        }

        return super.onOptionsItemSelected(item)
    }

}
