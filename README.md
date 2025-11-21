# CashFlow Android App

A Kotlin Android application for managing personal cash flow, helping you track income, bills, and account balances to ensure you can cover all your expenses.

## Features

- **Account Management**: Create and manage multiple accounts (Savings, Checking, Credit Cards)
- **Income Tracking**: Schedule bi-weekly or recurring income with manual overrides
- **Bill Management**: Schedule recurring bills with variable amounts and due dates
- **Timeline View**: Visualize cash flow over 30, 60, 90 days or custom periods
- **Cash Flow Analysis**: See when your balance goes negative (highlighted in red) or low (highlighted in amber)
- **Transaction History**: Track all transactions manually
- **Notifications**: Get reminders for upcoming bills

## Architecture

- **MVI (Model-View-Intent)**: Clean architecture pattern for state management
- **Jetpack Compose**: Modern declarative UI framework
- **Room Database**: Local data persistence
- **Kotlin Coroutines & Flow**: Asynchronous data handling

## Project Structure

```
app/src/main/java/com/cashflow/app/
├── data/
│   ├── dao/              # Room DAOs for database access
│   ├── database/         # Room database and converters
│   ├── entity/           # Room entities
│   ├── model/            # Data models (enums, types)
│   └── repository/       # Repository implementation
├── domain/
│   ├── model/            # Domain models
│   └── repository/       # Repository interface
├── ui/
│   ├── accounts/         # Accounts screen (MVI)
│   ├── bills/            # Bills screen (MVI)
│   ├── income/           # Income screen (MVI)
│   ├── timeline/         # Timeline screen (MVI)
│   ├── transactions/     # Transactions screen (MVI)
│   ├── navigation/       # Navigation setup
│   └── theme/            # Material 3 theme
├── di/                   # Dependency injection
└── notification/         # Notification workers
```

## Setup

1. Open the project in Android Studio
2. Sync Gradle files
3. Build and run on an Android device or emulator (API 26+)

## Usage

1. **Add Accounts**: Start by adding your accounts (Checking, Savings, Credit Cards) with starting balances
2. **Add Income**: Set up your bi-weekly salary or other recurring income
3. **Add Bills**: Add recurring bills with due dates
4. **View Timeline**: Check the timeline to see your projected cash flow
5. **Add Transactions**: Manually add transactions as needed
6. **Monitor**: Days with negative balance are highlighted in red, low balance days in amber

## Key Components

### Timeline View
The timeline shows your projected cash balance day by day, highlighting:
- **Red**: Negative balance (cash flow negative)
- **Amber**: Low balance (< $100)
- **Green/Default**: Positive balance

### Bi-weekly Income
Set up your salary to occur every 14 days starting from a specific date. You can override the amount for any specific payday.

### Recurring Bills
Bills can be scheduled as:
- Bi-weekly
- Weekly
- Monthly
- Custom (to be implemented)

Each bill can have variable amounts with manual overrides for specific dates.

## Technologies Used

- Kotlin
- Jetpack Compose
- Room Database
- Material 3 Design
- Kotlin Coroutines & Flow
- Work Manager (for notifications)

## License

This project is for personal use.

