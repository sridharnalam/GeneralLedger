package com.ideahamster.generalledger.data.repository

import com.ideahamster.generalledger.data.entity.Transaction
import com.ideahamster.generalledger.data.local.TransactionDao
import com.ideahamster.generalledger.data.network.LedgerApiService
import com.ideahamster.generalledger.data.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TransactionRepository @Inject constructor(private val ledgerApiService: LedgerApiService, private val transactionDao: TransactionDao) {

    suspend fun pullRemoteTransactions() = flow {
        emit(NetworkResult.Loading(true))
        val response = ledgerApiService.getTransactionList()
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun insertTransactionList(transactionList: List<Transaction>): List<Long> {
        return transactionDao.insertTransactionList(transactionList)
    }

    fun getTransactionList(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions()
    }

}