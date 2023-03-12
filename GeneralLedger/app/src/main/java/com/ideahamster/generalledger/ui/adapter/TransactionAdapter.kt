package com.ideahamster.generalledger.ui.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ideahamster.generalledger.R
import com.ideahamster.generalledger.data.entity.Transaction
import com.ideahamster.generalledger.databinding.ItemTransactionBinding
import javax.inject.Inject

class TransactionAdapter @Inject constructor() :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    fun updateTransactionList(transactionList: List<Transaction>) {
        differ.submitList(transactionList.toMutableList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding =
            ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = differ.currentList[position]

        holder.view.tvDescription.text =
            if (TextUtils.isEmpty(transaction.description)) "N/A" else transaction.description
        holder.view.tvAmount.text = holder.view.root.context.getString(
            R.string.currency_format,
            transaction.amount,
            transaction.currency
        )
        val amountTextColorId =
            if (transaction.isCredit == true) android.R.color.holo_green_dark else android.R.color.holo_red_light
        holder.view.tvAmount.setTextColor(
            ContextCompat.getColor(
                holder.view.root.context,
                amountTextColorId
            )
        )
        val dateText = transaction.formattedCreatedAt
        holder.view.tvDate.text = dateText
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class TransactionViewHolder(val view: ItemTransactionBinding) :
        RecyclerView.ViewHolder(view.root)
}