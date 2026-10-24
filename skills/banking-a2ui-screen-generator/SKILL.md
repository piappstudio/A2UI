---
name: banking-a2ui-screen-generator
description: Generates validated A2UI interfaces for secure banking workflows using only the component catalog supported by the TrustBank Android client.
---

# Banking A2UI Screen Generator

Generate A2UI protocol messages for TrustBank. Output protocol messages only. Never output Kotlin, JavaScript, HTML, or executable code.

## Catalog contract

- Protocol version: `v0.9`
- Catalog ID: `https://piappstudio.com/a2ui/catalogs/trustbank-banking/v1`
- Use Basic Catalog components whenever they are sufficient: `Text`, `Row`, `Column`, `Card`, `Button`, `TextField`, `CheckBox`, `ChoicePicker`, `Slider`, `Divider`, `List`.
- Use banking extensions only when needed: `EmiSummary`, `ExchangeRateSummary`, `VerificationStatus`, `TransactionLocationCard`, `DocumentAttachment`.
- Root component id must be `root`.
- Children are component ids, never inline trees.

## Rules

1. Identify the current workflow: transfer, loan, credit-limit increase, or fraud dispute.
2. Request only the information needed for the current step.
3. Keep dynamic values in the data model and bind with `{ "path": "/pointer" }`.
4. Prefer incremental `updateDataModel` when only values change.
5. Every `Button` needs a `child` Text id and an `action.event.name` from the registered list.
6. Sensitive actions need an explicit confirmation step.
7. Never invent balances, FX rates, eligibility, or fraud findings. Those come from the trusted backend.
8. Never collect passwords, PINs, CVVs, or full card numbers.
9. Never claim biometric success. Demo verification must be labeled as mock verification.
10. Unsupported component types must not be invented.

## Registered events

`reviewTransfer`, `confirmTransfer`, `cancelWorkflow`, `startMockVerification`, `completeMockVerification`, `submitLoan`, `submitCreditLimit`, `attachDocument`, `submitDispute`, `closeWorkflow`, `retryWorkflow`

## Message envelope

```json
{
  "version": "v0.9",
  "createSurface": {
    "surfaceId": "wf_transfer",
    "catalogId": "https://piappstudio.com/a2ui/catalogs/trustbank-banking/v1",
    "sendDataModel": true
  }
}
```

## Workflow templates

### Transfer
1. Form: source account, beneficiary, amount, currency, international flag.
2. Review: recipient, amount, currency, fees, FX summary, risk explanation.
3. Low risk: confirm. Verification required: labeled mock verification checkbox. High risk: pending review, do not execute.

### Loan / credit limit
1. Amount and tenure sliders, income, employment, `EmiSummary`, document attach, consent.
2. Submit to backend eligibility. Distinguish illustrative EMI from an offer.

### Fraud dispute
1. Transaction details from an authorized transaction id.
2. Merchant contact and `TransactionLocationCard` only when location is verified.
3. Reason, details, explicit confirmation, then `submitDispute`.
