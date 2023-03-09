package com.ideahamster.generalledger.ui.adapter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ideahamster.generalledger.data.Transaction
import com.ideahamster.generalledger.data.TransactionRepository
import com.ideahamster.generalledger.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val mainRepository: TransactionRepository
) : ViewModel() {

    private var _transactionResponse = MutableLiveData<NetworkResult<List<Transaction>>>()
    val transactionResponse: LiveData<NetworkResult<List<Transaction>>> = _transactionResponse

    init {
        fetchTransactionList()
    }

    private fun fetchTransactionList() {
        viewModelScope.launch {
            mainRepository.getTransactionList().collect {
                _transactionResponse.postValue(it)
            }
        }
    }
}