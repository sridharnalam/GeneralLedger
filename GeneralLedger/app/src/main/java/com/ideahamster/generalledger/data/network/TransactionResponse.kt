package com.ideahamster.generalledger.network

import com.ideahamster.generalledger.data.entity.Transaction

data class TransactionResponse(val items: List<Transaction>, val errorMessage: String)
