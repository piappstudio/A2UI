package com.piappstudio.a2ui.data.repository

import com.piappstudio.a2ui.data.model.Account
import com.piappstudio.a2ui.data.model.Transaction
import com.piappstudio.a2ui.data.model.TransferRequest
import com.piappstudio.a2ui.data.sample.SampleBankingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BankingRepositoryImpl : BankingRepository {

    private val _accounts = MutableStateFlow(SampleBankingData.accounts)
    private val _transactions = MutableStateFlow(SampleBankingData.transactions)

    override fun getAccounts(): Flow<List<Account>> = _accounts.asStateFlow()

    override fun getTransactions(): Flow<List<Transaction>> = _transactions.asStateFlow()

    override fun addTransaction(transaction: Transaction) {
        val current = _transactions.value.toMutableList()
        current.add(0, transaction)
        _transactions.value = current
    }

    override fun executeTransfer(request: TransferRequest): Boolean {
        val accounts = _accounts.value
        val primary = accounts.firstOrNull { it.type == com.piappstudio.a2ui.data.model.AccountType.CHECKING } ?: accounts.firstOrNull()
        if (primary == null || primary.balance < request.amount) {
            return false
        }
        val updatedAccounts = accounts.map { acc ->
            if (acc.id == primary.id) {
                acc.copy(balance = acc.balance - request.amount)
            } else {
                acc
            }
        }
        _accounts.value = updatedAccounts

        val newTx = Transaction(
            id = "tx_${System.currentTimeMillis()}",
            merchantName = "Transfer to ${request.recipientName}",
            amount = -request.amount,
            date = "Just now",
            category = if (request.isInternational) "Int'l Wire" else "Transfer",
            isHighRisk = request.isInternational || request.amount >= 1000.0,
            accountId = primary.id
        )
        addTransaction(newTx)
        return true
    }

    override suspend fun getAccountById(id: String): Account? {
        return _accounts.value.find { it.id == id }
    }
}
