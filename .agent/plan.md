# Project: Agentic Banking App — Dynamic A2UI Renderer

## 1. Role and Objective

Act as a Senior Android Architect, Kotlin developer, Jetpack Compose expert, and AI-agent integration engineer.

Build a modern Android banking application that demonstrates how an AI agent can dynamically generate interactive banking screens using the A2UI (Agent-to-UI) protocol.

The core objective is to build a **generic, reusable A2UI rendering host** that accepts A2UI messages from a backend agent and renders them using the default components provided by the official Android Jetpack Compose A2UI library.

Do not hardcode each agent-generated screen as a separate Compose screen. The renderer must be generic, driven by validated A2UI messages, data models, and a registered component catalog.

Use native Android components and Jetpack Compose. Prefer the official AndroidX A2UI implementation over third-party implementations.

## 2. Technology Stack

- Language: Kotlin
- UI: Jetpack Compose
- Design system: Material 3
- Architecture: MVVM with clean separation of concerns
- Asynchronous processing: Kotlin Coroutines and Flow
- Dependency injection: Hilt or an existing project-standard DI framework
- Networking: Retrofit/OkHttp or the project's existing networking stack
- Serialization: The serialization mechanism supported by the selected A2UI version
- Navigation: Navigation Compose
- State management: ViewModel and StateFlow
- Testing: JUnit, Kotlin coroutine testing, and Compose UI tests

Before writing code:

1. Inspect the current project structure, Gradle configuration, Kotlin version, Compose version, and Android SDK configuration.
2. Read the official A2UI documentation and current Android API reference.
3. Verify the exact artifact versions, catalog APIs, parser APIs, processor APIs, and rendering APIs.
4. Use a mutually compatible dependency set from Google Maven.
5. Do not invent API names or use deprecated or incompatible sample code.
6. If no suitable stable version exists, use the compatible alpha version and isolate it behind a repository/renderer abstraction.

## 3. Application Navigation Flow

Implement the following navigation hierarchy:

**Banking Application → Banking Dashboard → Banking Agent → Dynamic A2UI Surface**

### Banking application entry

Create a professional banking application entry screen with:
- Banking application branding.
- Secure login or a mock authenticated session for the demo.
- A clear dashboard entry point.
- Loading, error, and authentication states.

For the demo, provide a mock authenticated session so that the dynamic UI flows can be tested without connecting to a real bank.

### Banking dashboard

Create a native Jetpack Compose dashboard containing:
- Account summary and masked account number.
- Available balance using clearly labeled demo data.
- Recent transactions.
- Quick actions: Transfer Money, Apply for Loan, Increase Credit Limit, and Report Fraud.
- A Banking Assistant entry point.
- A section for pending approvals and alerts.

Dashboard navigation and authentication must remain under application control, not under arbitrary agent instructions.

When a user selects a banking action, open the agent conversation or workflow and render the backend's A2UI response in a reusable dynamic content surface.

## 4. Generic A2UI Rendering Engine

Create a reusable component named `DynamicA2uiScreen` or an equivalent host component.

Its responsibility is to render any supported A2UI surface without knowing whether it represents a transfer, loan, credit card, or fraud workflow.

Use the official A2UI message model, parser, processor, runtime, and Basic Material Catalog APIs appropriate to the selected library version.

The renderer must support:

- Parsing and validating A2UI messages.
- Rendering surfaces from the supported component catalog.
- Reactive data binding.
- Incremental message updates.
- Server-provided component properties and data models.
- User interactions and action dispatch.
- Loading, empty, and error states.
- Lifecycle-aware state collection.
- Accessible native Compose controls.
- Safe handling of unsupported components and malformed messages.

Use the library's default Basic Catalog components wherever possible. Do not recreate standard text, button, card, layout, text-field, selection, or slider components when the library already provides them.

Create a custom catalog only when a required banking-specific component cannot be represented adequately using the default catalog.

Suggested architecture:

- `A2uiMessageRepository`: Obtains messages from the agent backend or mock fixtures.
- `A2uiSessionViewModel`: Maintains the active workflow and message-processing state.
- `A2uiMessageProcessor`: Wraps the official message parser and processor.
- `A2uiCatalogProvider`: Exposes the selected supported component catalog.
- `DynamicA2uiScreen`: Hosts and displays the active A2UI surface.
- `A2uiActionDispatcher`: Validates and routes supported UI actions.
- `BankingNavigationController`: Owns application navigation and protected transitions.

Use actual official library APIs wherever possible. These names describe suggested application-level abstractions, not necessarily existing AndroidX class names.

## 5. Backend Agent Integration

Define a clear interface between the Android app and the agent backend.

The backend agent should return A2UI protocol messages, not arbitrary HTML, Kotlin source code, or executable UI definitions.

The integration must support:
- Starting a banking workflow.
- Sending a user message or UI action.
- Receiving A2UI messages.
- Applying incremental surface and data-model updates.
- Handling agent errors and timeouts.
- Cancelling a workflow.
- Preserving session and workflow identifiers.
- Resuming a workflow when supported.

Keep transport independent from rendering. Use REST initially, and design an interface that can support streaming or WebSocket transport later.

Create a mock agent implementation so that the app works without a live AI service.

For demo purposes, use deterministic responses based on the selected banking action. Do not require a paid LLM API to run the initial application.

## 6. Banking Workflow A: Secure Fund Transfer

### Scenario

A customer initiates a high-value domestic wire transfer or an international payment.

The agent evaluates the transaction context and requests additional verification based on a risk decision returned by the trusted backend.

### Expected dynamic UI

Render A2UI screens containing supported catalog components for:

- Beneficiary and recipient details.
- Source account selection.
- Transfer amount and currency.
- International exchange-rate breakdown.
- Transfer fees and estimated delivery time.
- Risk warning and localized fraud-alert text.
- Additional verification instructions.
- Transfer review and confirmation.

Provide different mock agent responses for:
- Low-risk transfer.
- Transfer requiring additional verification.
- High-risk transfer requiring manual review or rejection.

The backend must make the authoritative risk decision. The agent may explain the decision and propose the UI but must not independently authorize a transfer.

Use native biometric authentication APIs only when configured and available. Never simulate successful biometric verification merely by pressing a button.

For the demo, use a clearly labeled mock verification flow.

### Security requirements

- Show a confirmation screen before submitting a transfer.
- Display the recipient, amount, currency, and applicable fees before confirmation.
- Require a server-authorized transaction state before final execution.
- Treat the transfer as pending until the backend confirms its result.
- Prevent duplicate submissions with idempotency keys.
- Never let an A2UI message bypass authentication or transaction authorization.

## 7. Banking Workflow B: Loan and Credit Card Applications

### Scenario

A conversational loan officer guides the customer through a personal loan application or credit-limit increase.

The agent dynamically presents the next step based on backend eligibility results and the information provided by the customer.

### Expected dynamic UI

Render supported A2UI components for:

- Loan or credit-limit selection.
- Loan amount and repayment tenure.
- Income and employment information.
- Interactive EMI calculator with amount, interest rate, and tenure.
- Document upload instructions and upload controls.
- Application review and consent.
- Eligibility results.
- Submission status and next steps.

Use backend-provided, clearly labeled demo eligibility rules.

Calculate illustrative EMI values correctly, using the standard amortizing-loan formula when the interest rate is positive, and handle zero-interest and invalid-input cases.

The displayed EMI must update when the customer changes the supported calculator inputs.

Clearly distinguish estimated EMI from an actual bank offer. Interest rates, eligibility decisions, and final terms must come from a trusted backend in a production integration.

For document upload, use the Android system document picker or an equivalent secure mechanism. Do not invent a document-upload capability in the A2UI protocol if the selected catalog does not support it; use an approved application action or a narrowly scoped custom component.

## 8. Banking Workflow C: Fraud Dispute and Transaction Investigation

### Scenario

A customer selects a suspicious transaction from the dashboard and asks the banking agent to investigate it.

The agent receives an authorized transaction reference and generates a transaction-specific investigation interface.

### Expected dynamic UI

Render supported A2UI components for:

- Transaction details, merchant, amount, date, and status.
- Merchant information and available contact options.
- A transaction location summary, when verified location information is available.
- A map or location card through an approved, explicitly registered custom component if the default catalog does not provide one.
- A suspicious-transaction questionnaire.
- Dispute reason selection.
- A one-tap dispute submission action.
- Confirmation and dispute reference number.

Do not assume that a transaction's merchant location is the customer's physical location. Use only verified and authorized location data.

Require explicit customer confirmation before creating a dispute. The backend must verify that the selected transaction belongs to the authenticated customer and that it is eligible for a dispute.

Support mock outcomes for dispute submitted, additional information required, and transaction not eligible.

## 9. Component Catalog and Extensibility

Use the official A2UI Basic Material Catalog for standard UI elements.

The application must not assume that every banking feature exists as a built-in component. Check the selected catalog and protocol version before using a component.

Build an extension mechanism for approved banking-specific components, such as:

- Exchange-rate summary.
- Banking verification status.
- Transaction location card.
- Secure document attachment.
- EMI summary.

Only implement a custom component when required. Document its property schema, data binding, validation, accessibility behavior, and action contract.

The backend and Android client must agree on the component catalog and protocol version. Unsupported component types must produce a safe fallback rather than crashing the application.

## 10. Agent Skill

Create a reusable agent skill named `banking-a2ui-screen-generator`.

Its purpose is to instruct a backend AI agent to generate valid, safe, and accessible A2UI interfaces for the banking application.

The skill should contain:

### Skill metadata
- Name: `banking-a2ui-screen-generator`
- Description: Generates validated A2UI interfaces for secure banking workflows using only the component catalog supported by the client.

### Instructions

The agent must:

1. Identify the current banking workflow and user intent.
2. Request only the information necessary for the current step.
3. Select components from the registered A2UI catalog.
4. Generate protocol-compliant A2UI messages using the exact selected protocol version.
5. Keep dynamic content in the A2UI data model where appropriate.
6. Use incremental updates when supported and appropriate.
7. Return clear validation errors or clarification steps when needed.
8. Generate accessible labels and actionable instructions.
9. Use explicit confirmation steps for sensitive actions.
10. Never generate executable code, arbitrary native class names, or unregistered components.
11. Never invent account balances, exchange rates, transaction statuses, credit decisions, or fraud findings.
12. Treat backend authorization and business rules as authoritative.
13. Avoid collecting passwords, PINs, CVVs, full card numbers, or authentication secrets in conversational UI.
14. Return user-facing messages and valid A2UI protocol output according to the backend integration contract.

Create separate example prompts or workflow templates for transfers, loan applications, and fraud disputes.

If the agent framework supports skills as Markdown instructions, place the skill in a `skills/banking-a2ui-screen-generator/SKILL.md` file. Keep the skill independent from Android rendering code.

## 11. Security and Trust Boundaries

Treat every A2UI response as untrusted input, even if it comes from the configured backend.

- Validate the protocol schema and message sequence.
- Render only registered catalog components.
- Permit only explicitly registered actions.
- Keep authentication, navigation, transaction authorization, and final submission under trusted application/backend control.
- Validate action arguments and workflow state.
- Never execute generated Kotlin, JavaScript, HTML, shell commands, or arbitrary code.
- Do not allow agent-generated URLs to launch arbitrary external destinations.
- Redact sensitive data from logs, analytics, and crash reports.
- Use TLS and secure session handling.
- Avoid persisting sensitive financial information unless required and appropriately protected.
- Prevent duplicate and out-of-order actions from corrupting workflow state.
- Include clear user-visible errors and recovery paths.

## 12. UI and User Experience

Use a clean, professional banking interface with Material 3.

The native application shell should provide:
- Consistent app branding and navigation.
- A scrollable dashboard.
- A clear workflow header and back-navigation behavior.
- A loading indicator while waiting for the agent.
- A safe error and retry experience.
- Accessible typography and touch targets.
- Light and dark theme support.
- Responsive layouts for different screen sizes.

The A2UI content should inherit the app's visual design system where supported by the selected catalog. Do not rely on the agent to define unrestricted styling.

## 13. Mock Data and Demonstration

Provide local fixtures for the three banking workflows.

Include:
- A demo banking dashboard.
- At least one initial A2UI surface for each workflow.
- A low-risk transfer and a transfer requiring additional verification.
- A loan form with a working illustrative EMI calculator.
- A credit-limit increase workflow.
- A fraud investigation form with a dispute confirmation.
- A mock agent response sequence showing at least one incremental update if supported by the selected protocol version.
- Invalid message and unsupported component examples for error handling.

The application must be demonstrable without live banking credentials, real money movement, a real credit decision, or a production AI API.

Clearly label all mock financial data and mock outcomes.

## 14. Testing Requirements

Write tests for:

- A2UI message parsing and schema validation.
- Surface creation and incremental updates.
- Data binding and UI state updates.
- Action dispatch and argument validation.
- Unsupported component handling.
- Workflow navigation.
- EMI calculations and boundary conditions.
- Transfer confirmation and duplicate-submit prevention.
- Fraud dispute validation.
- Loading, error, cancellation, and retry states.
- Compose rendering and accessibility semantics.

Use protocol fixtures compatible with the exact selected A2UI version.

## 15. Deliverables

Produce a working Android project with:

1. A runnable banking application.
2. A native dashboard.
3. A reusable generic A2UI screen renderer.
4. A2UI parser, processor, and component catalog integration.
5. A mock agent backend implementation.
6. Three working banking workflow demonstrations.
7. A reusable `banking-a2ui-screen-generator` agent skill.
8. Unit and UI tests.
9. A README with architecture, setup, dependencies, and execution instructions.
10. Example A2UI request/response fixtures and the supported component catalog.
11. A clear explanation of how to connect a real agent backend later.

## 16. Implementation Plan

Work incrementally:

Phase 1: Inspect the project and verify official A2UI dependencies and APIs.

Phase 2: Build the Android app shell, dashboard, and navigation.

Phase 3: Integrate the A2UI parser, processor, runtime, and default component catalog.

Phase 4: Build the generic dynamic renderer and mock agent repository.

Phase 5: Implement and demonstrate the three banking workflows.

Phase 6: Implement the agent skill and protocol fixtures.

Phase 7: Add security validation, tests, and documentation.

Phase 8: Build and run the app, resolve compilation errors, and verify each workflow.

Implementation status (2026-10-23): Phases 1–8 implemented in the Android project. Native login and dashboard, generic `DynamicA2uiScreen` using `material3-a2ui` 1.0.0-alpha02, mock agent for transfer/loan/credit-limit/fraud, banking catalog extensions, agent skill, fixtures, unit tests, and `assembleDebug` are in place.

Do not stop after generating sample snippets. Implement the project, run available build and test tasks, fix errors, and report what was actually verified.

## Definition of Done

The project is complete when the banking dashboard opens, a user can launch each of the three workflows, the mock agent returns A2UI messages, and a single generic renderer displays the appropriate native Compose UI. User interactions must update the UI or invoke validated actions, and sensitive banking actions must remain protected by trusted application and backend logic.