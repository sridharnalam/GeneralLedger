package com.ideahamster.generalledger.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ideahamster.generalledger.LedgerApplication
import com.ideahamster.generalledger.R
import com.ideahamster.generalledger.data.entity.Transaction
import com.ideahamster.generalledger.data.repository.TransactionRepository
import com.ideahamster.generalledger.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val mainRepository: TransactionRepository, application: Application
) : AndroidViewModel(application) {

    companion object {
        const val TAG = "TransactionViewModel"
    }

    private var _networkResponse = MutableLiveData<NetworkResult<List<Transaction>>>()
    val networkResponse: LiveData<NetworkResult<List<Transaction>>> = _networkResponse

    private val _transactionsFlow = MutableStateFlow<List<Transaction>>(emptyList())
    val transactionsFlow: StateFlow<List<Transaction>> = _transactionsFlow

    private val _balanceFlow = MutableStateFlow<List<Map.Entry<String, Double>>>(emptyList())
    val balanceFlow: StateFlow<List<Map.Entry<String, Double>>> = _balanceFlow

    fun markTransactionsPulled() {
        val application = getApplication<LedgerApplication>()
        val sharedPref = application.getSharedPreferences(
            application.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        with(sharedPref.edit()) {
            putBoolean(application.getString(R.string.transactions_sync_key), true)
            apply()
        }
    }

    fun areTransactionsPulled(): Boolean {
        val application = getApplication<LedgerApplication>()
        val sharedPref = application.getSharedPreferences(
            application.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        return sharedPref.getBoolean(application.getString(R.string.transactions_sync_key), false)
    }

    fun insertTransactionDetails(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.insertTransaction(transaction)
        }
    }

    fun getTransactionList() {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.getTransactionList()
                .catch { cause: Throwable ->
                    cause.message?.let { Log.e(TAG, it) }
                }
                .collect { transitions ->
                    _transactionsFlow.value = transitions
                    calculateCurrencyBalance(transitions)
                }
        }
    }

    private fun calculateCurrencyBalance(transactionList: List<Transaction>) {
        viewModelScope.launch {
            flow<List<Map.Entry<String, Double>>> {
                val balanceMap = HashMap<String, Double>()
                for (transaction in transactionList) {
                    val amount =
                        if (transaction.isCredit == true) transaction.amount!! else transaction.amount!! * (-1)
                    if (balanceMap[transaction.currency!!] != null) {
                        val balance = balanceMap[transaction.currency]!! + amount
                        balanceMap[transaction.currency] = balance
                    } else {
                        balanceMap[transaction.currency] = amount
                    }
                }
                emit(balanceMap.entries.toList())
            }
                .collect {
                    _balanceFlow.value = it
                }
        }
    }

    fun pullRemoteTransactionList() {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.pullRemoteTransactions().collect {
                when (it) {
                    is NetworkResult.Success -> {
                        val transactionList = it.data
                        mainRepository.insertTransactionList(transactionList)
                    }
                    else -> {}
                }
                _networkResponse.postValue(it)
            }
        }
    }

    fun exportCSV(transactionList: MutableList<Transaction>): Flow<String> {
        return flow {
            val filename = getApplication<LedgerApplication>().getString(R.string.exported_csv_file)
            getApplication<LedgerApplication>().openFileOutput(filename, Context.MODE_PRIVATE).use {

                it.write("""id, created_at, description, is_credit, currency, amount""".toByteArray())
                it.write("\n".toByteArray())

                transactionList.forEach { transaction: Transaction ->
                    it.write("${transaction.id}, ${transaction.createdAt}, ${transaction.description}, ${transaction.isCredit}, ${transaction.currency}, ${transaction.amount}".toByteArray())
                    it.write("\n".toByteArray())
                }
                it.flush()
                emit(filename)
            }
        }
    }
}