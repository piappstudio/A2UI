# TrustBank A2UI catalog

Catalog ID: `https://piappstudio.com/a2ui/catalogs/trustbank-banking/v1`

Protocol version: `v0.9`

This catalog is a hybrid of the official A2UI Basic Catalog V1 (Material 3) plus five banking-specific components.

## Basic Catalog components

Text, Image, Icon, Video, AudioPlayer, Row, Column, List, Card, Tabs, Modal, Divider, Button, TextField, CheckBox, ChoicePicker, Slider, DateTimeInput

## Banking extensions

| Component | Purpose |
| --- | --- |
| `EmiSummary` | Illustrative EMI from amount, rate, and tenure. Not a bank offer. |
| `ExchangeRateSummary` | Backend FX quote, fee, and delivery estimate. |
| `VerificationStatus` | Trusted-backend verification state. Never claims biometric success. |
| `TransactionLocationCard` | Verified merchant location only. |
| `DocumentAttachment` | Dispatches `attachDocument` so the app can open the system picker. |

## Registered actions

`reviewTransfer`, `confirmTransfer`, `cancelWorkflow`, `startMockVerification`, `completeMockVerification`, `recalculateEmi`, `submitLoan`, `submitCreditLimit`, `attachDocument`, `submitDispute`, `closeWorkflow`, `retryWorkflow`
