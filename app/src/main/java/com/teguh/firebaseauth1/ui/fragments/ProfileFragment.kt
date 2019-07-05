package com.teguh.firebaseauth1.ui.fragments


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage

import com.teguh.firebaseauth1.R
import com.teguh.firebaseauth1.uitls.toast
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ProfileFragment : Fragment() {

    //default image when user not upload image
    private val DEFAULT_IMAGE_URL = "https://www.aflac.com/_Global-Assets/dagger/home/img/icon_life.png"

    private lateinit var imageUri: Uri

    private val REQUEST_IMAGE_CAPTURE = 100

    //current login and user from firebase authentification
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser?.let { user ->
            Glide.with(this)
                .load(user.photoUrl)
                .into(img_profile)

            edt_profile_name.setText(user.displayName)
            txt_profile_email.text = user.email
            //to display email is verify or not
            if (user.isEmailVerified) {
                txt_profile_not_verified.visibility = View.INVISIBLE
                txt_profile_verified.visibility = View.VISIBLE
            } else {
                txt_profile_not_verified.visibility = View.VISIBLE
            }

            //set phone number, but sebelumnya belum upload phone number, jadi cek apakah phone number ada atau tidak
            txt_profile_phone.text = if (user.phoneNumber.isNullOrEmpty()) "Add Number" else user.phoneNumber


        }

        img_profile.setOnClickListener {
            takePictureIntent()
        }

        btn_profile_save.setOnClickListener {
            //check the photo (uploaded by the user or not)
            val photo = when {
                ::imageUri.isInitialized -> imageUri
                currentUser?.photoUrl == null -> Uri.parse(DEFAULT_IMAGE_URL)
                else -> currentUser?.photoUrl
            }

            //display name on the editText
            val name = edt_profile_name.text.toString().trim()

            //add validation to username
            if (name.isEmpty()) {
                edt_profile_name.error = "name required"
                edt_profile_name.requestFocus()
                return@setOnClickListener
            }

            //when everythis is fine, now to update & create user profile change request
            val updates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photo)
                .build()

            // show progress updating
            progressbar.visibility = View.VISIBLE
            //update profile
            currentUser?.updateProfile(updates)?.addOnCompleteListener { task ->
                progressbar.visibility = View.INVISIBLE
                if (task.isSuccessful) {
                    context?.toast("Profile Updated")
                } else {
                    context?.toast(task.exception?.message!!)
                }
            }

        }

        txt_profile_not_verified.setOnClickListener {
            //send verification to teh user. When is done, next check on your inbox
            //To see change label of email verification, logout first then login again, so you must to create fun logout
            currentUser?.sendEmailVerification()?.addOnCompleteListener {
                if (it.isSuccessful) {
                    context?.toast("Email Verification sent")
                } else {
                    context?.toast(it.exception?.message!!)
                }
            }
        }

        txt_profile_email.setOnClickListener {
            val action = ProfileFragmentDirections.actionUpdateEmail()
            Navigation.findNavController(it).navigate(action)
        }

        txt_profile_phone.setOnClickListener {
            val action = ProfileFragmentDirections.actionVerifyPhone()
            Navigation.findNavController(it).navigate(action)
        }

        txt_profile_password.setOnClickListener {view ->
            val action = ProfileFragmentDirections.actionUpdatePassword()
            Navigation.findNavController(view).navigate(action)
        }

    }

    // to take picture by camera
    private fun takePictureIntent() {//need constant to callback, so create it
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePicture ->
            takePicture.resolveActivity(activity?.packageManager!!)?.also {
                startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            uploadImageAndSaveUri(imageBitmap)
        }
    }

    private fun uploadImageAndSaveUri(imageBitmap: Bitmap) {//buat var image uri dulu
        val baos = ByteArrayOutputStream()
        val storageref = FirebaseStorage.getInstance()
            .reference
            .child("pics/${FirebaseAuth.getInstance().currentUser?.uid}")
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        val image = baos.toByteArray()

        val upload = storageref.putBytes(image)

        //show progressBar loading
        progressbar_pic.visibility = View.VISIBLE
        upload.addOnCompleteListener { uploadTask ->
            progressbar_pic.visibility = View.INVISIBLE

            if (uploadTask.isSuccessful) {
                storageref.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let {
                        imageUri = it
                        activity?.toast(imageUri.toString())

                        //if upload is Succesfull, so set img_profile
                        img_profile.setImageBitmap(imageBitmap)
                    }
                }
            } else {
                uploadTask.exception?.let {
                    activity?.toast(it.message!!)
                }
            }
        }
    }

}
