package com.piappstudio.a2ui.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.piappstudio.a2ui.a2ui.agent.AgentWorkflow
import com.piappstudio.a2ui.data.model.Account
import com.piappstudio.a2ui.data.model.BankingAlert
import com.piappstudio.a2ui.data.model.PendingApproval
import com.piappstudio.a2ui.data.model.Transaction
import com.piappstudio.a2ui.domain.MoneyFormat
import com.piappstudio.a2ui.ui.viewmodel.DashboardUiState

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    onOpenWorkflow: (AgentWorkflow, String?) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text("Good afternoon, ${state.customer.displayName}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("DEMO DATA · No live bank connection", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
        }
        item {
            Text("Accounts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.accounts, key = { it.id }) { account ->
                    AccountCard(account)
                }
            }
        }
        item {
            Text("Quick actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionRow("Transfer money", "Domestic or international demo wire", Icons.AutoMirrored.Rounded.Send) {
                    onOpenWorkflow(AgentWorkflow.TRANSFER, null)
                }
                ActionRow("Apply for a loan", "Personal loan with illustrative EMI", Icons.Outlined.Savings) {
                    onOpenWorkflow(AgentWorkflow.LOAN, null)
                }
                ActionRow("Increase credit limit", "Demo eligibility check", Icons.Outlined.CreditCard) {
                    onOpenWorkflow(AgentWorkflow.CREDIT_LIMIT, null)
                }
                ActionRow("Report fraud", "Investigate a flagged transaction", Icons.Outlined.Report) {
                    val txId = state.transactions.firstOrNull { it.isHighRisk }?.id
                    onOpenWorkflow(AgentWorkflow.FRAUD, txId)
                }
                ActionRow("Banking assistant", "Ask the agent for the next step", Icons.Outlined.SupportAgent) {
                    onOpenWorkflow(AgentWorkflow.ASSISTANT, null)
                }
            }
        }
        if (state.alerts.isNotEmpty()) {
            item {
                Text("Alerts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            items(state.alerts, key = { it.id }) { alert ->
                AlertCard(alert)
            }
        }
        if (state.approvals.isNotEmpty()) {
            item {
                Text("Pending approvals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            items(state.approvals, key = { it.id }) { approval ->
                ApprovalCard(approval)
            }
        }
        item {
            Text("Recent transactions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        items(state.transactions, key = { it.id }) { tx ->
            TransactionRow(tx) {
                onOpenWorkflow(AgentWorkflow.FRAUD, tx.id)
            }
        }
    }
}

@Composable
private fun AccountCard(account: Account) {
    Card(
        modifier = Modifier.width(250.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(account.name, fontWeight = FontWeight.SemiBold)
                Icon(Icons.Rounded.AccountBalance, contentDescription = null)
            }
            Text(account.maskedNumber, style = MaterialTheme.typography.bodySmall)
            Text(MoneyFormat.currency(account.balance, account.currency), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ActionRow(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun AlertCard(alert: BankingAlert) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
        Column(Modifier.padding(16.dp)) {
            Text(alert.title, fontWeight = FontWeight.Bold)
            Text(alert.body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ApprovalCard(approval: PendingApproval) {
    Card {
        Column(Modifier.padding(16.dp)) {
            Text(approval.title, fontWeight = FontWeight.Bold)
            Text(approval.body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun TransactionRow(tx: Transaction, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (tx.isHighRisk) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Icon(
                        imageVector = if (tx.isHighRisk) Icons.Rounded.Warning else Icons.Rounded.AccountBalance,
                        contentDescription = if (tx.isHighRisk) "Flagged transaction" else "Transaction",
                        modifier = Modifier.padding(10.dp).size(20.dp),
                    )
                }
                Column {
                    Text(tx.merchant.name, fontWeight = FontWeight.Bold)
                    Text("${tx.dateLabel} · ${tx.merchant.category}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Text(
                MoneyFormat.currency(tx.amount, tx.currency),
                fontWeight = FontWeight.Bold,
                color = if (tx.amount < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            )
        }
    }
}
