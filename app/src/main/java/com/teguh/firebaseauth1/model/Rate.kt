package com.teguh.firebaseauth1.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Rate(
    val id: String? = "",
    val name: String? = "",
    val rate: Int? = 0
)
// bisa juga dengan cara membuat constructor dengan value parameternya kosong (blank constructor)
