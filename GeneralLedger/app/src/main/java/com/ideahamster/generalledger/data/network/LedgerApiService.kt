package com.ideahamster.generalledger.data.network

import com.ideahamster.generalledger.data.entity.Transaction
import retrofit2.http.GET

interface LedgerApiService {
    @GET("/invoices")
    suspend fun getTransactionList() : List<Transaction>
}