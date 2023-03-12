package com.ideahamster.generalledger.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ideahamster.generalledger.data.entity.Transaction

@Database(entities = [Transaction::class],  version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getTransactionDao(): TransactionDao
}