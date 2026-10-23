package com.piappstudio.a2ui.data.repository

import com.piappstudio.a2ui.data.model.Account
import com.piappstudio.a2ui.data.model.Transaction
import com.piappstudio.a2ui.data.model.TransferRequest
import kotlinx.coroutines.flow.Flow

interface BankingRepository {
    fun getAccounts(): Flow<List<Account>>
    fun getTransactions(): Flow<List<Transaction>>
    fun addTransaction(transaction: Transaction)
    fun executeTransfer(request: TransferRequest): Boolean
    suspend fun getAccountById(id: String): Account?
}
