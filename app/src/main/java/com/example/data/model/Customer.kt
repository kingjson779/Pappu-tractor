package com.example.data.model

data class Customer(
    val id: String,
    val name: String,
    val fatherName: String = "",
    val mobileNumber: String,
    val village: String,
    val fullAddress: String = "",
    val registeredDate: String = "आज"
)
