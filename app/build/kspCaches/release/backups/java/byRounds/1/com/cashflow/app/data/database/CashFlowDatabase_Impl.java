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

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(4) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `accounts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `startingBalance` REAL NOT NULL, `currentBalance` REAL NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `income` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `amount` REAL NOT NULL, `recurrenceType` TEXT NOT NULL, `startDate` TEXT NOT NULL, `accountId` INTEGER NOT NULL, `isActive` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `income_overrides` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `incomeId` INTEGER NOT NULL, `date` TEXT NOT NULL, `amount` REAL NOT NULL, FOREIGN KEY(`incomeId`) REFERENCES `income`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bills` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `amount` REAL NOT NULL, `recurrenceType` TEXT NOT NULL, `startDate` TEXT NOT NULL, `endDate` TEXT, `accountId` INTEGER, `isActive` INTEGER NOT NULL, `reminderDaysBefore` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bill_overrides` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `billId` INTEGER NOT NULL, `date` TEXT NOT NULL, `amount` REAL NOT NULL, FOREIGN KEY(`billId`) REFERENCES `bills`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bill_payments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `billId` INTEGER NOT NULL, `accountId` INTEGER NOT NULL, `paymentDate` TEXT NOT NULL, `amount` REAL NOT NULL, `timestamp` TEXT NOT NULL, `transactionId` INTEGER, FOREIGN KEY(`billId`) REFERENCES `bills`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`accountId`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `transactions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `accountId` INTEGER NOT NULL, `type` TEXT NOT NULL, `amount` REAL NOT NULL, `date` TEXT NOT NULL, `timestamp` TEXT NOT NULL, `description` TEXT NOT NULL, `relatedBillId` INTEGER, `relatedIncomeId` INTEGER, FOREIGN KEY(`accountId`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '108bf684cde07262d4210258cb116768')");
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
        final HashMap<String, TableInfo.Column> _columnsTransactions = new HashMap<String, TableInfo.Column>(9);
        _columnsTransactions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("accountId", new TableInfo.Column("accountId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("timestamp", new TableInfo.Column("timestamp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("relatedBillId", new TableInfo.Column("relatedBillId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("relatedIncomeId", new TableInfo.Column("relatedIncomeId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTransactions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysTransactions.add(new TableInfo.ForeignKey("accounts", "CASCADE", "NO ACTION", Arrays.asList("accountId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesTransactions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTransactions = new TableInfo("transactions", _columnsTransactions, _foreignKeysTransactions, _indicesTransactions);
        final TableInfo _existingTransactions = TableInfo.read(db, "transactions");
        if (!_infoTransactions.equals(_existingTransactions)) {
          return new RoomOpenHelper.ValidationResult(false, "transactions(com.cashflow.app.data.entity.TransactionEntity).\n"
                  + " Expected:\n" + _infoTransactions + "\n"
                  + " Found:\n" + _existingTransactions);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "108bf684cde07262d4210258cb116768", "393e5f94dc4c232d04f1c40611c7a53f");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "accounts","income","income_overrides","bills","bill_overrides","bill_payments","transactions");
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
}
