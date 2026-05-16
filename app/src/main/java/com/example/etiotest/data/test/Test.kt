package com.example.etiotest.data.test

// data/model/Test.kt
data class Test(
    val id: String,
    val name: String,
    val price: Double
)

// data/model/CartItem.kt
data class CartItem(
    val test: Test,
    var quantity: Int = 1
)
