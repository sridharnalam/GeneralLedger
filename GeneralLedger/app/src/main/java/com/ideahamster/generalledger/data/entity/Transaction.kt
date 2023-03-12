package com.ideahamster.generalledger.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ideahamster.generalledger.data.util.DateUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "transaction_table")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null,
    val amount: Double?,
    val currency: String?,
    val description: String?,
    @SerializedName("is_credit") val isCredit: Boolean?,
    @SerializedName("created_at") val createdAt: String?
): Parcelable {
    val formattedCreatedAt : String get() {
        return DateUtil.changeDateFormat(createdAt)
    }
}