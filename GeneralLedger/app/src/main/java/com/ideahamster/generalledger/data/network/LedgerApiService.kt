package com.ideahamster.generalledger.network

import com.ideahamster.generalledger.data.Transaction
import retrofit2.http.GET

interface LedgerApiService {
    @GET("/invoices")
    suspend fun getTransactionList() : List<Transaction>
}