package com.ideahamster.generalledger.data

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity
data class Transaction(
    val amount: Double,
    val currency: String,
    val description: String,
    @SerializedName("is_credit") val isCredit: Boolean,
    @SerializedName("created_at") val createdAt: String
)