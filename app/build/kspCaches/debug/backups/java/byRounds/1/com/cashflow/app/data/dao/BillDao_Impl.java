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
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.cashflow.app.data.database.Converters;
import com.cashflow.app.data.entity.BillEntity;
import com.cashflow.app.data.entity.BillOverrideEntity;
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
public final class BillDao_Impl implements BillDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BillEntity> __insertionAdapterOfBillEntity;

  private final Converters __converters = new Converters();

  private final EntityInsertionAdapter<BillOverrideEntity> __insertionAdapterOfBillOverrideEntity;

  private final EntityDeletionOrUpdateAdapter<BillEntity> __deletionAdapterOfBillEntity;

  private final EntityDeletionOrUpdateAdapter<BillOverrideEntity> __deletionAdapterOfBillOverrideEntity;

  private final EntityDeletionOrUpdateAdapter<BillEntity> __updateAdapterOfBillEntity;

  public BillDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBillEntity = new EntityInsertionAdapter<BillEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `bills` (`id`,`name`,`amount`,`recurrenceType`,`startDate`,`endDate`,`accountId`,`isActive`,`reminderDaysBefore`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BillEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getAmount());
        final String _tmp = __converters.fromRecurrenceType(entity.getRecurrenceType());
        statement.bindString(4, _tmp);
        final String _tmp_1 = __converters.fromLocalDate(entity.getStartDate());
        statement.bindString(5, _tmp_1);
        final String _tmp_2;
        if (entity.getEndDate() == null) {
          _tmp_2 = null;
        } else {
          _tmp_2 = __converters.fromLocalDate(entity.getEndDate());
        }
        if (_tmp_2 == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp_2);
        }
        if (entity.getAccountId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getAccountId());
        }
        final int _tmp_3 = entity.isActive() ? 1 : 0;
        statement.bindLong(8, _tmp_3);
        statement.bindLong(9, entity.getReminderDaysBefore());
      }
    };
    this.__insertionAdapterOfBillOverrideEntity = new EntityInsertionAdapter<BillOverrideEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `bill_overrides` (`id`,`billId`,`date`,`amount`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BillOverrideEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBillId());
        final String _tmp = __converters.fromLocalDate(entity.getDate());
        statement.bindString(3, _tmp);
        statement.bindDouble(4, entity.getAmount());
      }
    };
    this.__deletionAdapterOfBillEntity = new EntityDeletionOrUpdateAdapter<BillEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `bills` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BillEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfBillOverrideEntity = new EntityDeletionOrUpdateAdapter<BillOverrideEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `bill_overrides` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BillOverrideEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfBillEntity = new EntityDeletionOrUpdateAdapter<BillEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `bills` SET `id` = ?,`name` = ?,`amount` = ?,`recurrenceType` = ?,`startDate` = ?,`endDate` = ?,`accountId` = ?,`isActive` = ?,`reminderDaysBefore` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BillEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getAmount());
        final String _tmp = __converters.fromRecurrenceType(entity.getRecurrenceType());
        statement.bindString(4, _tmp);
        final String _tmp_1 = __converters.fromLocalDate(entity.getStartDate());
        statement.bindString(5, _tmp_1);
        final String _tmp_2;
        if (entity.getEndDate() == null) {
          _tmp_2 = null;
        } else {
          _tmp_2 = __converters.fromLocalDate(entity.getEndDate());
        }
        if (_tmp_2 == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp_2);
        }
        if (entity.getAccountId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getAccountId());
        }
        final int _tmp_3 = entity.isActive() ? 1 : 0;
        statement.bindLong(8, _tmp_3);
        statement.bindLong(9, entity.getReminderDaysBefore());
        statement.bindLong(10, entity.getId());
      }
    };
  }

  @Override
  public Object insertBill(final BillEntity bill, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBillEntity.insertAndReturnId(bill);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertOverride(final BillOverrideEntity override,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBillOverrideEntity.insert(override);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBill(final BillEntity bill, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfBillEntity.handle(bill);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOverride(final BillOverrideEntity override,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfBillOverrideEntity.handle(override);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateBill(final BillEntity bill, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfBillEntity.handle(bill);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BillEntity>> getAllActiveBills() {
    final String _sql = "SELECT * FROM bills WHERE isActive = 1 ORDER BY startDate";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bills"}, new Callable<List<BillEntity>>() {
      @Override
      @NonNull
      public List<BillEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfRecurrenceType = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrenceType");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "accountId");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfReminderDaysBefore = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderDaysBefore");
          final List<BillEntity> _result = new ArrayList<BillEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BillEntity _item;
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
            final LocalDate _tmpEndDate;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfEndDate);
            }
            if (_tmp_2 == null) {
              _tmpEndDate = null;
            } else {
              _tmpEndDate = __converters.toLocalDate(_tmp_2);
            }
            final Long _tmpAccountId;
            if (_cursor.isNull(_cursorIndexOfAccountId)) {
              _tmpAccountId = null;
            } else {
              _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            }
            final boolean _tmpIsActive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_3 != 0;
            final int _tmpReminderDaysBefore;
            _tmpReminderDaysBefore = _cursor.getInt(_cursorIndexOfReminderDaysBefore);
            _item = new BillEntity(_tmpId,_tmpName,_tmpAmount,_tmpRecurrenceType,_tmpStartDate,_tmpEndDate,_tmpAccountId,_tmpIsActive,_tmpReminderDaysBefore);
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
  public Flow<List<BillEntity>> getAllBills() {
    final String _sql = "SELECT * FROM bills ORDER BY startDate";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bills"}, new Callable<List<BillEntity>>() {
      @Override
      @NonNull
      public List<BillEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfRecurrenceType = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrenceType");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "accountId");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfReminderDaysBefore = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderDaysBefore");
          final List<BillEntity> _result = new ArrayList<BillEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BillEntity _item;
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
            final LocalDate _tmpEndDate;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfEndDate);
            }
            if (_tmp_2 == null) {
              _tmpEndDate = null;
            } else {
              _tmpEndDate = __converters.toLocalDate(_tmp_2);
            }
            final Long _tmpAccountId;
            if (_cursor.isNull(_cursorIndexOfAccountId)) {
              _tmpAccountId = null;
            } else {
              _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            }
            final boolean _tmpIsActive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_3 != 0;
            final int _tmpReminderDaysBefore;
            _tmpReminderDaysBefore = _cursor.getInt(_cursorIndexOfReminderDaysBefore);
            _item = new BillEntity(_tmpId,_tmpName,_tmpAmount,_tmpRecurrenceType,_tmpStartDate,_tmpEndDate,_tmpAccountId,_tmpIsActive,_tmpReminderDaysBefore);
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
  public Object getBillById(final long id, final Continuation<? super BillEntity> $completion) {
    final String _sql = "SELECT * FROM bills WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BillEntity>() {
      @Override
      @Nullable
      public BillEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfRecurrenceType = CursorUtil.getColumnIndexOrThrow(_cursor, "recurrenceType");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "accountId");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfReminderDaysBefore = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderDaysBefore");
          final BillEntity _result;
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
            final LocalDate _tmpEndDate;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfEndDate);
            }
            if (_tmp_2 == null) {
              _tmpEndDate = null;
            } else {
              _tmpEndDate = __converters.toLocalDate(_tmp_2);
            }
            final Long _tmpAccountId;
            if (_cursor.isNull(_cursorIndexOfAccountId)) {
              _tmpAccountId = null;
            } else {
              _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            }
            final boolean _tmpIsActive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_3 != 0;
            final int _tmpReminderDaysBefore;
            _tmpReminderDaysBefore = _cursor.getInt(_cursorIndexOfReminderDaysBefore);
            _result = new BillEntity(_tmpId,_tmpName,_tmpAmount,_tmpRecurrenceType,_tmpStartDate,_tmpEndDate,_tmpAccountId,_tmpIsActive,_tmpReminderDaysBefore);
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
  public Object getOverride(final long billId, final LocalDate date,
      final Continuation<? super BillOverrideEntity> $completion) {
    final String _sql = "SELECT * FROM bill_overrides WHERE billId = ? AND date = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, billId);
    _argIndex = 2;
    final String _tmp = __converters.fromLocalDate(date);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BillOverrideEntity>() {
      @Override
      @Nullable
      public BillOverrideEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBillId = CursorUtil.getColumnIndexOrThrow(_cursor, "billId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final BillOverrideEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBillId;
            _tmpBillId = _cursor.getLong(_cursorIndexOfBillId);
            final LocalDate _tmpDate;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfDate);
            _tmpDate = __converters.toLocalDate(_tmp_1);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            _result = new BillOverrideEntity(_tmpId,_tmpBillId,_tmpDate,_tmpAmount);
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
  public Flow<List<BillOverrideEntity>> getOverridesForBill(final long billId) {
    final String _sql = "SELECT * FROM bill_overrides WHERE billId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, billId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bill_overrides"}, new Callable<List<BillOverrideEntity>>() {
      @Override
      @NonNull
      public List<BillOverrideEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBillId = CursorUtil.getColumnIndexOrThrow(_cursor, "billId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final List<BillOverrideEntity> _result = new ArrayList<BillOverrideEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BillOverrideEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBillId;
            _tmpBillId = _cursor.getLong(_cursorIndexOfBillId);
            final LocalDate _tmpDate;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDate);
            _tmpDate = __converters.toLocalDate(_tmp);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            _item = new BillOverrideEntity(_tmpId,_tmpBillId,_tmpDate,_tmpAmount);
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
