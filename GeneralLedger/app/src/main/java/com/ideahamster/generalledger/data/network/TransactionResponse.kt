package com.ideahamster.generalledger.network

import com.ideahamster.generalledger.data.Transaction

data class TransactionResponse(val items: List<Transaction>, val errorMessage: String)
