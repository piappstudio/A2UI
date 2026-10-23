package com.piappstudio.a2ui

import com.piappstudio.a2ui.data.model.AccountType
import com.piappstudio.a2ui.data.model.TransferRequest
import com.piappstudio.a2ui.data.repository.BankingRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BankingRepositoryTest {

    @Test
    fun `initial sample accounts and transactions are loaded correctly`() = runTest {
        val repo = BankingRepositoryImpl()
        val accounts = repo.getAccounts().first()
        val transactions = repo.getTransactions().first()
        assertTrue(accounts.isNotEmpty())
        assertTrue(transactions.isNotEmpty())
    }

    @Test
    fun `executeTransfer deducts funds and adds transaction`() = runTest {
        val repo = BankingRepositoryImpl()
        val initialAccounts = repo.getAccounts().first()
        val checking = initialAccounts.first { it.type == AccountType.CHECKING }
        val initialBalance = checking.balance

        val request = TransferRequest(
            recipientName = "Acme Corp",
            accountNumber = "123456789",
            amount = 500.0,
            isInternational = false
        )
        val success = repo.executeTransfer(request)
        assertTrue(success)

        val updatedAccounts = repo.getAccounts().first()
        val updatedChecking = updatedAccounts.first { it.id == checking.id }
        assertEquals(initialBalance - 500.0, updatedChecking.balance, 0.01)

        val transactions = repo.getTransactions().first()
        assertEquals("Transfer to Acme Corp", transactions.first().merchantName)
    }

    @Test
    fun `executeTransfer fails when insufficient funds`() = runTest {
        val repo = BankingRepositoryImpl()
        val request = TransferRequest(
            recipientName = "Luxury Yacht",
            accountNumber = "999999999",
            amount = 999999.0,
            isInternational = true
        )
        val success = repo.executeTransfer(request)
        assertFalse(success)
    }
}
