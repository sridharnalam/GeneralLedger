package com.ideahamster.generalledger.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ideahamster.generalledger.R
import com.ideahamster.generalledger.databinding.ItemCurrencyBalanceBinding
import javax.inject.Inject

class BalanceAdapter @Inject constructor() :
    RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Map.Entry<String, Double>>() {
        override fun areItemsTheSame(
            oldItem: Map.Entry<String, Double>, newItem: Map.Entry<String, Double>
        ): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(
            oldItem: Map.Entry<String, Double>, newItem: Map.Entry<String, Double>
        ): Boolean {
            return oldItem.key == newItem.key && oldItem.value == newItem.value
        }
    }
    private val differ = AsyncListDiffer(this, differCallback)

    fun updateBalanceList(balanceList: List<Map.Entry<String, Double>>) {
        differ.submitList(balanceList.toMutableList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalanceViewHolder {
        val binding =
            ItemCurrencyBalanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BalanceViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BalanceViewHolder, position: Int) {
        val balanceCurrency = differ.currentList[position]
        holder.view.tvCurrencyCode.text = balanceCurrency.key
        holder.view.tvBalanceAmount.text = holder.view.root.context.getString(
            R.string.balance_format, balanceCurrency.value
        )
        val balanceTextColorId =
            if (balanceCurrency.value < 0) android.R.color.holo_red_light else android.R.color.holo_green_dark
        holder.view.tvBalanceAmount.setTextColor(
            ContextCompat.getColor(
                holder.view.root.context, balanceTextColorId
            )
        )
    }

    class BalanceViewHolder(val view: ItemCurrencyBalanceBinding) :
        RecyclerView.ViewHolder(view.root)

}