package com.cashflow.app.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.cashflow.app.data.database.Converters;
import com.cashflow.app.data.entity.IncomeEntity;
import com.cashflow.app.data.entity.IncomeOverrideEntity;
import com.cashflow.app.data.model.RecurrenceType;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;
import kotlinx.datetime.LocalDate;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IncomeDao_Impl implements IncomeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<IncomeEntity> __insertionAdapterOfIncomeEntity;

  private final Converters __converters = new Converters();

  private final EntityInsertionAdapter<IncomeOverrideEntity> __insertionAdapterOfIncomeOverrideEntity;

  private final EntityDeletionOrUpdateAdapter<IncomeEntity> __deletionAdapterOfIncomeEntity;

  private final EntityDeletionOrUpdateAdapter<IncomeOverrideEntity> __deletionAdapterOfIncomeOverrideEntity;

  private final EntityDeletionOrUpdateAdapter<IncomeEntity> __updateAdapterOfIncomeEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllIncome;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllOverrides;

  public IncomeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfIncomeEntity = new EntityInsertionAdapter<IncomeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `income` (`id`,`name`,`amount`,`recurrenceType`,`startDate`,`accountId`,`isActive`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getAmount());
        final String _tmp = __converters.fromRecurrenceType(entity.getRecurrenceType());
        statement.bindString(4, _tmp);
        final String _tmp_1 = __converters.fromLocalDate(entity.getStartDate());
        statement.bindString(5, _tmp_1);
        statement.bindLong(6, entity.getAccountId());
        final int _tmp_2 = entity.isActive() ? 1 : 0;
        statement.bindLong(7, _tmp_2);
      }
    };
    this.__insertionAdapterOfIncomeOverrideEntity = new EntityInsertionAdapter<IncomeOverrideEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `income_overrides` (`id`,`incomeId`,`date`,`amount`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeOverrideEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getIncomeId());
        final String _tmp = __converters.fromLocalDate(entity.getDate());
        statement.bindString(3, _tmp);
        statement.bindDouble(4, entity.getAmount());
      }
    };
    this.__deletionAdapterOfIncomeEntity = new EntityDeletionOrUpdateAdapter<IncomeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `income` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfIncomeOverrideEntity = new EntityDeletionOrUpdateAdapter<IncomeOverrideEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `income_overrides` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeOverrideEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfIncomeEntity = new EntityDeletionOrUpdateAdapter<IncomeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `income` SET `id` = ?,`name` = ?,`amount` = ?,`recurrenceType` = ?,`startDate` = ?,`accountId` = ?,`isActive` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getAmount());
        final String _tmp = __converters.fromRecurrenceType(entity.getRecurrenceType());
        statement.bindString(4, _tmp);
        final String _tmp_1 = __converters.fromLocalDate(entity.getStartDate());
        statement.bindString(5, _tmp_1);
        statement.bindLong(6, entity.getAccountId());
        final int _tmp_2 = entity.isActive() ? 1 : 0;
        statement.bindLong(7, _tmp_2);
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllIncome = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM income";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllOverrides = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM income_overrides";
        return _query;
      }
    };
  }

  @Override
  public Object insertIncome(final IncomeEntity income,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfIncomeEntity.insertAndReturnId(income);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertOverride(final IncomeOverrideEntity override,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfIncomeOverrideEntity.insert(override);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteIncome(final IncomeEntity income,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfIncomeEntity.handle(income);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOverride(final IncomeOverrideEntity override,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfIncomeOverrideEntity.handle(override);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateIncome(final IncomeEntity income,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfIncomeEntity.handle(income);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllIncome(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllIncome.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllIncome.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllOverrides(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllOverrides.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllOverrides.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<IncomeEntity>> getAllActiveIncome() {
    final String _sql = "SELECT * FROM income WHERE isActive = 1 ORDER BY startDate";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"income"}, new Callable<List<IncomeEntity>>() {
      @Override
      @NonNull
      public List<IncomeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfRecurrenceType = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrenceType");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "accountId");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<IncomeEntity> _result = new ArrayList<IncomeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncomeEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final RecurrenceType _tmpRecurrenceType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfRecurrenceType);
            _tmpRecurrenceType = __converters.toRecurrenceType(_tmp);
            final LocalDate _tmpStartDate;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStartDate);
            _tmpStartDate = __converters.toLocalDate(_tmp_1);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final boolean _tmpIsActive;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_2 != 0;
            _item = new IncomeEntity(_tmpId,_tmpName,_tmpAmount,_tmpRecurrenceType,_tmpStartDate,_tmpAccountId,_tmpIsActive);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<IncomeEntity>> getAllIncome() {
    final String _sql = "SELECT * FROM income ORDER BY startDate";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"income"}, new Callable<List<IncomeEntity>>() {
      @Override
      @NonNull
      public List<IncomeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfRecurrenceType = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrenceType");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "accountId");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<IncomeEntity> _result = new ArrayList<IncomeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncomeEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final RecurrenceType _tmpRecurrenceType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfRecurrenceType);
            _tmpRecurrenceType = __converters.toRecurrenceType(_tmp);
            final LocalDate _tmpStartDate;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStartDate);
            _tmpStartDate = __converters.toLocalDate(_tmp_1);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final boolean _tmpIsActive;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_2 != 0;
            _item = new IncomeEntity(_tmpId,_tmpName,_tmpAmount,_tmpRecurrenceType,_tmpStartDate,_tmpAccountId,_tmpIsActive);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getIncomeById(final long id, final Continuation<? super IncomeEntity> $completion) {
    final String _sql = "SELECT * FROM income WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<IncomeEntity>() {
      @Override
      @Nullable
      public IncomeEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfRecurrenceType = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrenceType");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "accountId");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final IncomeEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final RecurrenceType _tmpRecurrenceType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfRecurrenceType);
            _tmpRecurrenceType = __converters.toRecurrenceType(_tmp);
            final LocalDate _tmpStartDate;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStartDate);
            _tmpStartDate = __converters.toLocalDate(_tmp_1);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final boolean _tmpIsActive;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_2 != 0;
            _result = new IncomeEntity(_tmpId,_tmpName,_tmpAmount,_tmpRecurrenceType,_tmpStartDate,_tmpAccountId,_tmpIsActive);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getOverride(final long incomeId, final LocalDate date,
      final Continuation<? super IncomeOverrideEntity> $completion) {
    final String _sql = "SELECT * FROM income_overrides WHERE incomeId = ? AND date = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, incomeId);
    _argIndex = 2;
    final String _tmp = __converters.fromLocalDate(date);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<IncomeOverrideEntity>() {
      @Override
      @Nullable
      public IncomeOverrideEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIncomeId = CursorUtil.getColumnIndexOrThrow(_cursor, "incomeId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final IncomeOverrideEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpIncomeId;
            _tmpIncomeId = _cursor.getLong(_cursorIndexOfIncomeId);
            final LocalDate _tmpDate;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfDate);
            _tmpDate = __converters.toLocalDate(_tmp_1);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            _result = new IncomeOverrideEntity(_tmpId,_tmpIncomeId,_tmpDate,_tmpAmount);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<IncomeOverrideEntity>> getOverridesForIncome(final long incomeId) {
    final String _sql = "SELECT * FROM income_overrides WHERE incomeId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, incomeId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"income_overrides"}, new Callable<List<IncomeOverrideEntity>>() {
      @Override
      @NonNull
      public List<IncomeOverrideEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIncomeId = CursorUtil.getColumnIndexOrThrow(_cursor, "incomeId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final List<IncomeOverrideEntity> _result = new ArrayList<IncomeOverrideEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncomeOverrideEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpIncomeId;
            _tmpIncomeId = _cursor.getLong(_cursorIndexOfIncomeId);
            final LocalDate _tmpDate;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDate);
            _tmpDate = __converters.toLocalDate(_tmp);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            _item = new IncomeOverrideEntity(_tmpId,_tmpIncomeId,_tmpDate,_tmpAmount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
