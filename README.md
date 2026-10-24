# TrustBank — Agentic A2UI Banking Demo

Android banking demo that renders agent-generated screens with the official Jetpack Compose A2UI libraries. The native app owns login, dashboard navigation, and payment authorization. A generic A2UI host renders workflow surfaces from protocol messages.

This is a demonstration. All balances, transfers, loans, and disputes are mock data. No real money moves.

## Architecture

```
Login (native) → Dashboard (native) → Agent workflow (generic A2UI surface)
```

| Layer | Role |
| --- | --- |
| `TrustBankApp` | Navigation 3 shell. Authentication and dashboard stay in the app. |
| `A2uiSessionViewModel` | Processes A2UI messages, collects outbound events, talks to the agent backend. |
| `DynamicA2uiScreen` | Hosts `androidx.compose.material3.a2ui.A2uiSurface`. |
| `createBankingCatalog` | Official Material 3 Basic Catalog plus five banking extensions. |
| `A2uiActionDispatcher` | Allowlists registered actions before they reach the backend. |
| `MockAgentBackend` | Deterministic A2UI responses for transfer, loan, credit-limit, and fraud. |
| `BankingRepositoryImpl` | Trusted mock bank: risk, EMI eligibility, disputes, idempotency. |

## Dependencies

- `androidx.a2ui:a2ui-engine:1.0.0-alpha02`
- `androidx.a2ui:a2ui-model:1.0.0-alpha02`
- `androidx.a2ui.compose:compose-runtime:1.0.0-alpha02`
- `androidx.a2ui.compose:compose-ui:1.0.0-alpha02`
- `androidx.compose.material3:material3-a2ui:1.0.0-alpha02`

Protocol version: `v0.9`. Catalog ID: `https://piappstudio.com/a2ui/catalogs/trustbank-banking/v1`.

Supported components are listed in `app/src/main/assets/a2ui/catalog/supported_components.md`.

## Run

1. Open the project in Android Studio.
2. Sync Gradle.
3. Run the `app` configuration on an API 26+ emulator or device.

Demo login:

- Username: any non-empty value
- Password: `demo`
- Or tap **Continue as demo customer**

## Workflows

From the dashboard:

- **Transfer money** — low-risk confirm, extra mock verification for larger amounts, manual review for high-risk international wires.
- **Apply for a loan** — amount/tenure sliders, live illustrative EMI, document picker, demo eligibility.
- **Increase credit limit** — same lending surface with credit-limit demo rules.
- **Report fraud** — transaction investigation, verified location card, explicit dispute confirmation.
- **Banking assistant** — generic A2UI intro surface.

## Connecting a real agent later

1. Replace `MockAgentBackend` with a REST (then streaming) implementation of `AgentBackend`.
2. Send `A2uiClientEventMessage` payloads to the agent. Keep `A2uiActionDispatcher` as the allowlist.
3. Parse inbound JSON with `androidx.a2ui.compose.runtime.A2uiMessageParser()` and call `processor.processInput`.
4. Give the agent `skills/banking-a2ui-screen-generator/SKILL.md` and the catalog document.
5. Keep transfer execution, eligibility, and dispute creation in the trusted backend. The agent must not authorize money movement.

Example fixtures live in `app/src/main/assets/a2ui/fixtures/`.

## Tests

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
```
