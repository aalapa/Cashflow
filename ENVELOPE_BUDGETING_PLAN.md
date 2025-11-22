# Envelope Budgeting App - Implementation Plan

## Overview
This document outlines the plan to convert the existing Cash Flow app into a full-fledged Envelope Budgeting app.

## Current Foundation ✅

### What We Already Have (Strong Foundation)
- ✅ **Accounts Management** (Checking, Savings, Credit Cards)
- ✅ **Income Tracking** with scheduling (bi-weekly, monthly, etc.)
- ✅ **Expense/Bill Tracking** with recurring bills
- ✅ **Transaction History** with full logging
- ✅ **Cash Flow Timeline** with visual calendar
- ✅ **Account Detail Views** showing transaction history
- ✅ **Data Persistence** (Room Database)
- ✅ **Clean MVI Architecture** (Model-View-Intent)
- ✅ **Modern UI** (Jetpack Compose, Material 3)
- ✅ **Export/Import** functionality
- ✅ **Dark Theme** support

## Envelope Budgeting Features to Add

### Core Features

#### 1. **Envelopes/Categories**
- Create custom envelopes (Groceries, Entertainment, Gas, Dining, etc.)
- Each envelope has:
  - Name
  - Color (for visual distinction)
  - Icon (optional)
  - Budgeted amount per period (monthly/bi-weekly)
  - Account association (optional - can be tied to specific account or global)

#### 2. **Budget Allocation**
- Allocate income to envelopes
- Auto-allocation rules (e.g., "30% of income goes to Groceries")
- Manual allocation screen
- Percentage-based or fixed-amount allocation
- Support for multiple income sources

#### 3. **Envelope Balances**
- Track remaining balance per envelope
- Show spent vs. budgeted
- Visual progress indicators (progress bars, circular progress)
- Color coding:
  - Green = healthy (plenty left)
  - Yellow/Amber = warning (getting low)
  - Red = over budget or empty

#### 4. **Transaction-to-Envelope Mapping**
- Assign transactions to envelopes
- Optional: Auto-categorization rules (e.g., "Starbucks" → "Dining Out")
- View transactions filtered by envelope
- Support for split transactions (one transaction, multiple envelopes)

#### 5. **Envelope Spending Dashboard**
- List all envelopes with current balances
- Quick view of available funds
- Visual indicators (cards, progress bars)
- Sort/filter options
- Total budgeted vs. total spent overview

#### 6. **Over-Budget Protection/Warnings**
- Alerts when envelope is empty or low
- Option to block spending (or just warn)
- Show negative balances clearly
- Notification system for budget violations

#### 7. **Period Management**
- Monthly/bi-weekly reset functionality
- Carry-over options (rollover unused funds to next period)
- Budget history tracking
- Period comparison views

#### 8. **Envelope Transfers**
- Move money between envelopes
- Transfer history
- Reason/note for transfers

## UI Additions Needed

### New Screens
1. **Envelopes Tab/Screen**
   - List of all envelopes
   - Create new envelope button
   - Visual cards showing balance and status
   - Quick actions (edit, delete, view details)

2. **Envelope Detail Screen**
   - Current balance and budget
   - Transaction history for this envelope
   - Spending trends/charts
   - Edit envelope settings

3. **Budget Allocation Screen**
   - Income source selection
   - Envelope allocation interface
   - Auto-allocation rules setup
   - Allocation history

4. **Envelope Picker Component**
   - Dropdown/selector in transaction dialogs
   - Quick category selection
   - Recent envelopes

5. **Envelope Dashboard**
   - Overview of all envelopes
   - Visual progress indicators
   - Quick stats (total budgeted, total spent, remaining)

### UI Components
- Envelope card component
- Progress bar/circular progress for envelope status
- Envelope color picker
- Allocation slider/input
- Envelope transaction list item

## Database Schema Changes

### New Entities

#### `EnvelopeEntity`
```kotlin
@Entity(tableName = "envelopes")
data class EnvelopeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val color: String, // Hex color code
    val icon: String? = null, // Icon name/resource
    val budgetedAmount: Double,
    val periodType: PeriodType, // MONTHLY, BI_WEEKLY, etc.
    val accountId: Long? = null, // Optional: tie to specific account
    val createdAt: LocalDateTime,
    val isActive: Boolean = true
)
```

#### `EnvelopeAllocationEntity`
```kotlin
@Entity(tableName = "envelope_allocations")
data class EnvelopeAllocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val envelopeId: Long,
    val amount: Double,
    val periodStart: LocalDate,
    val periodEnd: LocalDate,
    val incomeId: Long? = null, // Which income source funded this
    val createdAt: LocalDateTime
)
```

#### `EnvelopeTransactionEntity` (Optional - for detailed tracking)
```kotlin
@Entity(tableName = "envelope_transactions")
data class EnvelopeTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionId: Long, // FK to TransactionEntity
    val envelopeId: Long, // FK to EnvelopeEntity
    val amount: Double, // Can be partial if split transaction
    val createdAt: LocalDateTime
)
```

### Modified Entities

#### `TransactionEntity` (Add envelope reference)
```kotlin
// Add to existing TransactionEntity:
val envelopeId: Long? = null // Optional: which envelope this transaction belongs to
```

### New DAOs
- `EnvelopeDao` - CRUD operations for envelopes
- `EnvelopeAllocationDao` - Manage allocations
- `EnvelopeTransactionDao` - Link transactions to envelopes

## Architecture Integration

### MVI Pattern
- **EnvelopeState** - State for envelope screens
- **EnvelopeIntent** - User actions (Create, Edit, Delete, Allocate, etc.)
- **EnvelopeViewModel** - Business logic and state management

### Repository Updates
- Add envelope-related methods to `CashFlowRepository`
- Implement in `CashFlowRepositoryImpl`
- Methods needed:
  - `getAllEnvelopes(): Flow<List<Envelope>>`
  - `createEnvelope(envelope: Envelope): suspend Long`
  - `updateEnvelope(envelope: Envelope): suspend Unit`
  - `deleteEnvelope(envelopeId: Long): suspend Unit`
  - `allocateToEnvelope(envelopeId: Long, amount: Double, period: Period): suspend Unit`
  - `getEnvelopeBalance(envelopeId: Long, date: LocalDate): Flow<Double>`
  - `getEnvelopeTransactions(envelopeId: Long): Flow<List<Transaction>>`
  - `assignTransactionToEnvelope(transactionId: Long, envelopeId: Long): suspend Unit`

## Migration Path

### Phase 1: Foundation
1. Create database entities and DAOs
2. Add envelope domain models
3. Update repository interface and implementation
4. Database migration (version bump)

### Phase 2: Basic CRUD
1. Create EnvelopeState/Intent/ViewModel
2. Build Envelope list screen
3. Create/Edit/Delete envelope dialogs
4. Test basic envelope management

### Phase 3: Budget Allocation
1. Build allocation screen
2. Implement allocation logic
3. Link income to envelope allocation
4. Show allocation history

### Phase 4: Transaction Integration
1. Add envelope picker to transaction dialogs
2. Update transaction entity with envelopeId
3. Link transactions to envelopes
4. Filter transactions by envelope

### Phase 5: Dashboard & Balances
1. Build envelope dashboard
2. Calculate and display balances
3. Visual progress indicators
4. Over-budget warnings

### Phase 6: Period Management
1. Implement period reset functionality
2. Carry-over logic
3. Period comparison views
4. Budget history tracking

### Phase 7: Advanced Features
1. Auto-categorization rules
2. Split transactions
3. Envelope transfers
4. Notifications for budget violations
5. Analytics and reports

## Design Considerations

### User Experience
- **Progressive Disclosure**: Start simple, add complexity gradually
- **Visual Feedback**: Color coding, progress bars, clear status indicators
- **Quick Actions**: Easy envelope selection, fast allocation
- **Flexibility**: Support both strict and flexible budgeting styles

### Data Integrity
- Ensure envelope balances are always accurate
- Handle edge cases (negative balances, period transitions)
- Maintain transaction history integrity
- Support data export/import with envelopes

### Performance
- Efficient balance calculations
- Optimize queries for envelope transactions
- Cache envelope balances when possible
- Lazy load transaction lists

## Benefits of This Approach

1. **Natural Evolution**: Builds on existing solid foundation
2. **Coexistence**: Envelope system can work alongside cash flow features
3. **Flexible**: Users can use envelopes or traditional tracking, or both
4. **Scalable**: Architecture supports future enhancements
5. **User-Friendly**: Leverages existing UI patterns and design language

## Questions to Consider

1. **Strict vs. Flexible**: Should envelopes block spending when empty, or just warn?
2. **Account Association**: Should envelopes be global or tied to specific accounts?
3. **Period Default**: Monthly, bi-weekly, or user-configurable?
4. **Carry-Over**: Always, never, or user choice?
5. **Split Transactions**: Support splitting one transaction across multiple envelopes?
6. **Auto-Categorization**: How sophisticated should the rules engine be?

## Next Steps

When ready to implement:
1. Review and refine this plan
2. Start with Phase 1 (Database foundation)
3. Iterate through phases
4. Test thoroughly at each phase
5. Gather user feedback

---

**Created**: 2024
**Status**: Planning Phase
**Priority**: Future Enhancement

