package com.kith.core.model.data

data class Transaction(
    val id: String,
    val title: String,
    val timestamp: String,
    val xpAmount: Int
)