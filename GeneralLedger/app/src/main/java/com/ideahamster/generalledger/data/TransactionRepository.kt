package com.ideahamster.generalledger.data

import com.ideahamster.generalledger.network.LedgerApiService
import com.ideahamster.generalledger.network.NetworkResult
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TransactionRepository @Inject constructor(private val ledgerApiService: LedgerApiService) {

    suspend fun getTransactionList() = flow {
        emit(NetworkResult.Loading(true))
        val response = ledgerApiService.getTransactionList()
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }
}