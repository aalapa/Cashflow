package com.cashflow.app.data.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.cashflow.app.data.dao.AccountDao;
import com.cashflow.app.data.dao.AccountDao_Impl;
import com.cashflow.app.data.dao.BillDao;
import com.cashflow.app.data.dao.BillDao_Impl;
import com.cashflow.app.data.dao.BillPaymentDao;
import com.cashflow.app.data.dao.BillPaymentDao_Impl;
import com.cashflow.app.data.dao.CategorizationRuleDao;
import com.cashflow.app.data.dao.CategorizationRuleDao_Impl;
import com.cashflow.app.data.dao.EnvelopeAllocationDao;
import com.cashflow.app.data.dao.EnvelopeAllocationDao_Impl;
import com.cashflow.app.data.dao.EnvelopeDao;
import com.cashflow.app.data.dao.EnvelopeDao_Impl;
import com.cashflow.app.data.dao.EnvelopeTransferDao;
import com.cashflow.app.data.dao.EnvelopeTransferDao_Impl;
import com.cashflow.app.data.dao.IncomeDao;
import com.cashflow.app.data.dao.IncomeDao_Impl;
import com.cashflow.app.data.dao.TransactionDao;
import com.cashflow.app.data.dao.TransactionDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CashFlowDatabase_Impl extends CashFlowDatabase {
  private volatile AccountDao _accountDao;

  private volatile IncomeDao _incomeDao;

  private volatile BillDao _billDao;

  private volatile BillPaymentDao _billPaymentDao;

  private volatile TransactionDao _transactionDao;

  private volatile EnvelopeDao _envelopeDao;

  private volatile EnvelopeAllocationDao _envelopeAllocationDao;

  private volatile EnvelopeTransferDao _envelopeTransferDao;

  private volatile CategorizationRuleDao _categorizationRuleDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(8) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `accounts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `startingBalance` REAL NOT NULL, `currentBalance` REAL NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `income` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `amount` REAL NOT NULL, `recurrenceType` TEXT NOT NULL, `startDate` TEXT NOT NULL, `accountId` INTEGER NOT NULL, `isActive` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `income_overrides` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `incomeId` INTEGER NOT NULL, `date` TEXT NOT NULL, `amount` REAL NOT NULL, FOREIGN KEY(`incomeId`) REFERENCES `income`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bills` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `amount` REAL NOT NULL, `recurrenceType` TEXT NOT NULL, `startDate` TEXT NOT NULL, `endDate` TEXT, `accountId` INTEGER, `isActive` INTEGER NOT NULL, `reminderDaysBefore` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bill_overrides` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `billId` INTEGER NOT NULL, `date` TEXT NOT NULL, `amount` REAL NOT NULL, FOREIGN KEY(`billId`) REFERENCES `bills`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bill_payments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `billId` INTEGER NOT NULL, `accountId` INTEGER NOT NULL, `paymentDate` TEXT NOT NULL, `amount` REAL NOT NULL, `timestamp` TEXT NOT NULL, `transactionId` INTEGER, FOREIGN KEY(`billId`) REFERENCES `bills`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`accountId`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `transactions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `accountId` INTEGER NOT NULL, `toAccountId` INTEGER, `type` TEXT NOT NULL, `amount` REAL NOT NULL, `date` TEXT NOT NULL, `timestamp` TEXT NOT NULL, `description` TEXT NOT NULL, `relatedBillId` INTEGER, `relatedIncomeId` INTEGER, `envelopeId` INTEGER, FOREIGN KEY(`accountId`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`toAccountId`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `envelopes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `color` TEXT NOT NULL, `icon` TEXT, `budgetedAmount` REAL NOT NULL, `periodType` TEXT NOT NULL, `accountId` INTEGER, `carryOverEnabled` INTEGER NOT NULL, `createdAt` TEXT NOT NULL, `isActive` INTEGER NOT NULL, FOREIGN KEY(`accountId`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `envelope_allocations` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `envelopeId` INTEGER NOT NULL, `amount` REAL NOT NULL, `periodStart` TEXT NOT NULL, `periodEnd` TEXT NOT NULL, `incomeId` INTEGER, `createdAt` TEXT NOT NULL, FOREIGN KEY(`envelopeId`) REFERENCES `envelopes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`incomeId`) REFERENCES `income`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_envelope_allocations_envelopeId_periodStart` ON `envelope_allocations` (`envelopeId`, `periodStart`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `envelope_transfers` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `fromEnvelopeId` INTEGER NOT NULL, `toEnvelopeId` INTEGER NOT NULL, `amount` REAL NOT NULL, `date` TEXT NOT NULL, `description` TEXT, `timestamp` TEXT NOT NULL, FOREIGN KEY(`fromEnvelopeId`) REFERENCES `envelopes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`toEnvelopeId`) REFERENCES `envelopes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `categorization_rules` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `envelopeId` INTEGER NOT NULL, `keyword` TEXT NOT NULL, `isActive` INTEGER NOT NULL, `createdAt` TEXT NOT NULL, FOREIGN KEY(`envelopeId`) REFERENCES `envelopes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '24d6084658d8027fca939e96ce7488a8')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `accounts`");
        db.execSQL("DROP TABLE IF EXISTS `income`");
        db.execSQL("DROP TABLE IF EXISTS `income_overrides`");
        db.execSQL("DROP TABLE IF EXISTS `bills`");
        db.execSQL("DROP TABLE IF EXISTS `bill_overrides`");
        db.execSQL("DROP TABLE IF EXISTS `bill_payments`");
        db.execSQL("DROP TABLE IF EXISTS `transactions`");
        db.execSQL("DROP TABLE IF EXISTS `envelopes`");
        db.execSQL("DROP TABLE IF EXISTS `envelope_allocations`");
        db.execSQL("DROP TABLE IF EXISTS `envelope_transfers`");
        db.execSQL("DROP TABLE IF EXISTS `categorization_rules`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsAccounts = new HashMap<String, TableInfo.Column>(5);
        _columnsAccounts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("startingBalance", new TableInfo.Column("startingBalance", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("currentBalance", new TableInfo.Column("currentBalance", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAccounts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAccounts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAccounts = new TableInfo("accounts", _columnsAccounts, _foreignKeysAccounts, _indicesAccounts);
        final TableInfo _existingAccounts = TableInfo.read(db, "accounts");
        if (!_infoAccounts.equals(_existingAccounts)) {
          return new RoomOpenHelper.ValidationResult(false, "accounts(com.cashflow.app.data.entity.AccountEntity).\n"
                  + " Expected:\n" + _infoAccounts + "\n"
                  + " Found:\n" + _existingAccounts);
        }
        final HashMap<String, TableInfo.Column> _columnsIncome = new HashMap<String, TableInfo.Column>(7);
        _columnsIncome.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncome.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncome.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncome.put("recurrenceType", new TableInfo.Column("recurrenceType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncome.put("startDate", new TableInfo.Column("startDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncome.put("accountId", new TableInfo.Column("accountId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncome.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIncome = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesIncome = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoIncome = new TableInfo("income", _columnsIncome, _foreignKeysIncome, _indicesIncome);
        final TableInfo _existingIncome = TableInfo.read(db, "income");
        if (!_infoIncome.equals(_existingIncome)) {
          return new RoomOpenHelper.ValidationResult(false, "income(com.cashflow.app.data.entity.IncomeEntity).\n"
                  + " Expected:\n" + _infoIncome + "\n"
                  + " Found:\n" + _existingIncome);
        }
        final HashMap<String, TableInfo.Column> _columnsIncomeOverrides = new HashMap<String, TableInfo.Column>(4);
        _columnsIncomeOverrides.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeOverrides.put("incomeId", new TableInfo.Column("incomeId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeOverrides.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeOverrides.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIncomeOverrides = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysIncomeOverrides.add(new TableInfo.ForeignKey("income", "CASCADE", "NO ACTION", Arrays.asList("incomeId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesIncomeOverrides = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoIncomeOverrides = new TableInfo("income_overrides", _columnsIncomeOverrides, _foreignKeysIncomeOverrides, _indicesIncomeOverrides);
        final TableInfo _existingIncomeOverrides = TableInfo.read(db, "income_overrides");
        if (!_infoIncomeOverrides.equals(_existingIncomeOverrides)) {
          return new RoomOpenHelper.ValidationResult(false, "income_overrides(com.cashflow.app.data.entity.IncomeOverrideEntity).\n"
                  + " Expected:\n" + _infoIncomeOverrides + "\n"
                  + " Found:\n" + _existingIncomeOverrides);
        }
        final HashMap<String, TableInfo.Column> _columnsBills = new HashMap<String, TableInfo.Column>(9);
        _columnsBills.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBills.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBills.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBills.put("recurrenceType", new TableInfo.Column("recurrenceType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBills.put("startDate", new TableInfo.Column("startDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBills.put("endDate", new TableInfo.Column("endDate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBills.put("accountId", new TableInfo.Column("accountId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBills.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBills.put("reminderDaysBefore", new TableInfo.Column("reminderDaysBefore", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBills = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBills = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBills = new TableInfo("bills", _columnsBills, _foreignKeysBills, _indicesBills);
        final TableInfo _existingBills = TableInfo.read(db, "bills");
        if (!_infoBills.equals(_existingBills)) {
          return new RoomOpenHelper.ValidationResult(false, "bills(com.cashflow.app.data.entity.BillEntity).\n"
                  + " Expected:\n" + _infoBills + "\n"
                  + " Found:\n" + _existingBills);
        }
        final HashMap<String, TableInfo.Column> _columnsBillOverrides = new HashMap<String, TableInfo.Column>(4);
        _columnsBillOverrides.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillOverrides.put("billId", new TableInfo.Column("billId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillOverrides.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillOverrides.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBillOverrides = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysBillOverrides.add(new TableInfo.ForeignKey("bills", "CASCADE", "NO ACTION", Arrays.asList("billId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesBillOverrides = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBillOverrides = new TableInfo("bill_overrides", _columnsBillOverrides, _foreignKeysBillOverrides, _indicesBillOverrides);
        final TableInfo _existingBillOverrides = TableInfo.read(db, "bill_overrides");
        if (!_infoBillOverrides.equals(_existingBillOverrides)) {
          return new RoomOpenHelper.ValidationResult(false, "bill_overrides(com.cashflow.app.data.entity.BillOverrideEntity).\n"
                  + " Expected:\n" + _infoBillOverrides + "\n"
                  + " Found:\n" + _existingBillOverrides);
        }
        final HashMap<String, TableInfo.Column> _columnsBillPayments = new HashMap<String, TableInfo.Column>(7);
        _columnsBillPayments.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillPayments.put("billId", new TableInfo.Column("billId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillPayments.put("accountId", new TableInfo.Column("accountId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillPayments.put("paymentDate", new TableInfo.Column("paymentDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillPayments.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillPayments.put("timestamp", new TableInfo.Column("timestamp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBillPayments.put("transactionId", new TableInfo.Column("transactionId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBillPayments = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysBillPayments.add(new TableInfo.ForeignKey("bills", "CASCADE", "NO ACTION", Arrays.asList("billId"), Arrays.asList("id")));
        _foreignKeysBillPayments.add(new TableInfo.ForeignKey("accounts", "CASCADE", "NO ACTION", Arrays.asList("accountId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesBillPayments = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBillPayments = new TableInfo("bill_payments", _columnsBillPayments, _foreignKeysBillPayments, _indicesBillPayments);
        final TableInfo _existingBillPayments = TableInfo.read(db, "bill_payments");
        if (!_infoBillPayments.equals(_existingBillPayments)) {
          return new RoomOpenHelper.ValidationResult(false, "bill_payments(com.cashflow.app.data.entity.BillPaymentEntity).\n"
                  + " Expected:\n" + _infoBillPayments + "\n"
                  + " Found:\n" + _existingBillPayments);
        }
        final HashMap<String, TableInfo.Column> _columnsTransactions = new HashMap<String, TableInfo.Column>(11);
        _columnsTransactions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("accountId", new TableInfo.Column("accountId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("toAccountId", new TableInfo.Column("toAccountId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("timestamp", new TableInfo.Column("timestamp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("relatedBillId", new TableInfo.Column("relatedBillId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("relatedIncomeId", new TableInfo.Column("relatedIncomeId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("envelopeId", new TableInfo.Column("envelopeId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTransactions = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysTransactions.add(new TableInfo.ForeignKey("accounts", "CASCADE", "NO ACTION", Arrays.asList("accountId"), Arrays.asList("id")));
        _foreignKeysTransactions.add(new TableInfo.ForeignKey("accounts", "CASCADE", "NO ACTION", Arrays.asList("toAccountId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesTransactions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTransactions = new TableInfo("transactions", _columnsTransactions, _foreignKeysTransactions, _indicesTransactions);
        final TableInfo _existingTransactions = TableInfo.read(db, "transactions");
        if (!_infoTransactions.equals(_existingTransactions)) {
          return new RoomOpenHelper.ValidationResult(false, "transactions(com.cashflow.app.data.entity.TransactionEntity).\n"
                  + " Expected:\n" + _infoTransactions + "\n"
                  + " Found:\n" + _existingTransactions);
        }
        final HashMap<String, TableInfo.Column> _columnsEnvelopes = new HashMap<String, TableInfo.Column>(10);
        _columnsEnvelopes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopes.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopes.put("color", new TableInfo.Column("color", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopes.put("icon", new TableInfo.Column("icon", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopes.put("budgetedAmount", new TableInfo.Column("budgetedAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopes.put("periodType", new TableInfo.Column("periodType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopes.put("accountId", new TableInfo.Column("accountId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopes.put("carryOverEnabled", new TableInfo.Column("carryOverEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopes.put("createdAt", new TableInfo.Column("createdAt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopes.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEnvelopes = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysEnvelopes.add(new TableInfo.ForeignKey("accounts", "SET NULL", "NO ACTION", Arrays.asList("accountId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesEnvelopes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEnvelopes = new TableInfo("envelopes", _columnsEnvelopes, _foreignKeysEnvelopes, _indicesEnvelopes);
        final TableInfo _existingEnvelopes = TableInfo.read(db, "envelopes");
        if (!_infoEnvelopes.equals(_existingEnvelopes)) {
          return new RoomOpenHelper.ValidationResult(false, "envelopes(com.cashflow.app.data.entity.EnvelopeEntity).\n"
                  + " Expected:\n" + _infoEnvelopes + "\n"
                  + " Found:\n" + _existingEnvelopes);
        }
        final HashMap<String, TableInfo.Column> _columnsEnvelopeAllocations = new HashMap<String, TableInfo.Column>(7);
        _columnsEnvelopeAllocations.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopeAllocations.put("envelopeId", new TableInfo.Column("envelopeId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopeAllocations.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopeAllocations.put("periodStart", new TableInfo.Column("periodStart", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopeAllocations.put("periodEnd", new TableInfo.Column("periodEnd", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopeAllocations.put("incomeId", new TableInfo.Column("incomeId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopeAllocations.put("createdAt", new TableInfo.Column("createdAt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEnvelopeAllocations = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysEnvelopeAllocations.add(new TableInfo.ForeignKey("envelopes", "CASCADE", "NO ACTION", Arrays.asList("envelopeId"), Arrays.asList("id")));
        _foreignKeysEnvelopeAllocations.add(new TableInfo.ForeignKey("income", "SET NULL", "NO ACTION", Arrays.asList("incomeId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesEnvelopeAllocations = new HashSet<TableInfo.Index>(1);
        _indicesEnvelopeAllocations.add(new TableInfo.Index("index_envelope_allocations_envelopeId_periodStart", true, Arrays.asList("envelopeId", "periodStart"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoEnvelopeAllocations = new TableInfo("envelope_allocations", _columnsEnvelopeAllocations, _foreignKeysEnvelopeAllocations, _indicesEnvelopeAllocations);
        final TableInfo _existingEnvelopeAllocations = TableInfo.read(db, "envelope_allocations");
        if (!_infoEnvelopeAllocations.equals(_existingEnvelopeAllocations)) {
          return new RoomOpenHelper.ValidationResult(false, "envelope_allocations(com.cashflow.app.data.entity.EnvelopeAllocationEntity).\n"
                  + " Expected:\n" + _infoEnvelopeAllocations + "\n"
                  + " Found:\n" + _existingEnvelopeAllocations);
        }
        final HashMap<String, TableInfo.Column> _columnsEnvelopeTransfers = new HashMap<String, TableInfo.Column>(7);
        _columnsEnvelopeTransfers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopeTransfers.put("fromEnvelopeId", new TableInfo.Column("fromEnvelopeId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopeTransfers.put("toEnvelopeId", new TableInfo.Column("toEnvelopeId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopeTransfers.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopeTransfers.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopeTransfers.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEnvelopeTransfers.put("timestamp", new TableInfo.Column("timestamp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEnvelopeTransfers = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysEnvelopeTransfers.add(new TableInfo.ForeignKey("envelopes", "CASCADE", "NO ACTION", Arrays.asList("fromEnvelopeId"), Arrays.asList("id")));
        _foreignKeysEnvelopeTransfers.add(new TableInfo.ForeignKey("envelopes", "CASCADE", "NO ACTION", Arrays.asList("toEnvelopeId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesEnvelopeTransfers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEnvelopeTransfers = new TableInfo("envelope_transfers", _columnsEnvelopeTransfers, _foreignKeysEnvelopeTransfers, _indicesEnvelopeTransfers);
        final TableInfo _existingEnvelopeTransfers = TableInfo.read(db, "envelope_transfers");
        if (!_infoEnvelopeTransfers.equals(_existingEnvelopeTransfers)) {
          return new RoomOpenHelper.ValidationResult(false, "envelope_transfers(com.cashflow.app.data.entity.EnvelopeTransferEntity).\n"
                  + " Expected:\n" + _infoEnvelopeTransfers + "\n"
                  + " Found:\n" + _existingEnvelopeTransfers);
        }
        final HashMap<String, TableInfo.Column> _columnsCategorizationRules = new HashMap<String, TableInfo.Column>(5);
        _columnsCategorizationRules.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategorizationRules.put("envelopeId", new TableInfo.Column("envelopeId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategorizationRules.put("keyword", new TableInfo.Column("keyword", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategorizationRules.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategorizationRules.put("createdAt", new TableInfo.Column("createdAt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCategorizationRules = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysCategorizationRules.add(new TableInfo.ForeignKey("envelopes", "CASCADE", "NO ACTION", Arrays.asList("envelopeId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesCategorizationRules = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCategorizationRules = new TableInfo("categorization_rules", _columnsCategorizationRules, _foreignKeysCategorizationRules, _indicesCategorizationRules);
        final TableInfo _existingCategorizationRules = TableInfo.read(db, "categorization_rules");
        if (!_infoCategorizationRules.equals(_existingCategorizationRules)) {
          return new RoomOpenHelper.ValidationResult(false, "categorization_rules(com.cashflow.app.data.entity.CategorizationRuleEntity).\n"
                  + " Expected:\n" + _infoCategorizationRules + "\n"
                  + " Found:\n" + _existingCategorizationRules);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "24d6084658d8027fca939e96ce7488a8", "14548e555fa44f7d09410d24729d994c");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "accounts","income","income_overrides","bills","bill_overrides","bill_payments","transactions","envelopes","envelope_allocations","envelope_transfers","categorization_rules");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `accounts`");
      _db.execSQL("DELETE FROM `income`");
      _db.execSQL("DELETE FROM `income_overrides`");
      _db.execSQL("DELETE FROM `bills`");
      _db.execSQL("DELETE FROM `bill_overrides`");
      _db.execSQL("DELETE FROM `bill_payments`");
      _db.execSQL("DELETE FROM `transactions`");
      _db.execSQL("DELETE FROM `envelopes`");
      _db.execSQL("DELETE FROM `envelope_allocations`");
      _db.execSQL("DELETE FROM `envelope_transfers`");
      _db.execSQL("DELETE FROM `categorization_rules`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(AccountDao.class, AccountDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IncomeDao.class, IncomeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BillDao.class, BillDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BillPaymentDao.class, BillPaymentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TransactionDao.class, TransactionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EnvelopeDao.class, EnvelopeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EnvelopeAllocationDao.class, EnvelopeAllocationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EnvelopeTransferDao.class, EnvelopeTransferDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CategorizationRuleDao.class, CategorizationRuleDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public AccountDao accountDao() {
    if (_accountDao != null) {
      return _accountDao;
    } else {
      synchronized(this) {
        if(_accountDao == null) {
          _accountDao = new AccountDao_Impl(this);
        }
        return _accountDao;
      }
    }
  }

  @Override
  public IncomeDao incomeDao() {
    if (_incomeDao != null) {
      return _incomeDao;
    } else {
      synchronized(this) {
        if(_incomeDao == null) {
          _incomeDao = new IncomeDao_Impl(this);
        }
        return _incomeDao;
      }
    }
  }

  @Override
  public BillDao billDao() {
    if (_billDao != null) {
      return _billDao;
    } else {
      synchronized(this) {
        if(_billDao == null) {
          _billDao = new BillDao_Impl(this);
        }
        return _billDao;
      }
    }
  }

  @Override
  public BillPaymentDao billPaymentDao() {
    if (_billPaymentDao != null) {
      return _billPaymentDao;
    } else {
      synchronized(this) {
        if(_billPaymentDao == null) {
          _billPaymentDao = new BillPaymentDao_Impl(this);
        }
        return _billPaymentDao;
      }
    }
  }

  @Override
  public TransactionDao transactionDao() {
    if (_transactionDao != null) {
      return _transactionDao;
    } else {
      synchronized(this) {
        if(_transactionDao == null) {
          _transactionDao = new TransactionDao_Impl(this);
        }
        return _transactionDao;
      }
    }
  }

  @Override
  public EnvelopeDao envelopeDao() {
    if (_envelopeDao != null) {
      return _envelopeDao;
    } else {
      synchronized(this) {
        if(_envelopeDao == null) {
          _envelopeDao = new EnvelopeDao_Impl(this);
        }
        return _envelopeDao;
      }
    }
  }

  @Override
  public EnvelopeAllocationDao envelopeAllocationDao() {
    if (_envelopeAllocationDao != null) {
      return _envelopeAllocationDao;
    } else {
      synchronized(this) {
        if(_envelopeAllocationDao == null) {
          _envelopeAllocationDao = new EnvelopeAllocationDao_Impl(this);
        }
        return _envelopeAllocationDao;
      }
    }
  }

  @Override
  public EnvelopeTransferDao envelopeTransferDao() {
    if (_envelopeTransferDao != null) {
      return _envelopeTransferDao;
    } else {
      synchronized(this) {
        if(_envelopeTransferDao == null) {
          _envelopeTransferDao = new EnvelopeTransferDao_Impl(this);
        }
        return _envelopeTransferDao;
      }
    }
  }

  @Override
  public CategorizationRuleDao categorizationRuleDao() {
    if (_categorizationRuleDao != null) {
      return _categorizationRuleDao;
    } else {
      synchronized(this) {
        if(_categorizationRuleDao == null) {
          _categorizationRuleDao = new CategorizationRuleDao_Impl(this);
        }
        return _categorizationRuleDao;
      }
    }
  }
}
