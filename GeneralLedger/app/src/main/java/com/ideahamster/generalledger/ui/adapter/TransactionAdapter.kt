package com.ideahamster.generalledger.ui.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ideahamster.generalledger.R
import com.ideahamster.generalledger.data.Transaction
import com.ideahamster.generalledger.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TransactionAdapter @Inject constructor() :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var transactionList = mutableListOf<Transaction>()

    fun updateTransactionList(transactionList: List<Transaction>) {
        this.transactionList = transactionList.toMutableList()
        notifyItemRangeInserted(0, transactionList.size)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]

        holder.view.tvDescription.text =
            if (TextUtils.isEmpty(transaction.description)) "N/A" else transaction.description
        holder.view.tvAmount.text = holder.view.root.context.getString(
            R.string.currency_format,
            transaction.amount,
            transaction.currency
        )
        val amountTextColorId =
            if (transaction.isCredit) android.R.color.holo_green_dark else android.R.color.holo_red_light
        holder.view.tvAmount.setTextColor(
            ContextCompat.getColor(
                holder.view.root.context,
                amountTextColorId
            )
        )
        val dateText = getFormattedDate(transaction)
        holder.view.tvDate.text = dateText
    }

    private fun getFormattedDate(transaction: Transaction): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ", Locale.ENGLISH)
        val date: Date? = dateFormat.parse(transaction.createdAt)
        val displayFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        return if (date != null) displayFormat.format(date) else "N/A"
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    class TransactionViewHolder(val view: ItemTransactionBinding) : RecyclerView.ViewHolder(view.root)
}