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
import com.cashflow.app.data.entity.BillPaymentEntity;
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
import kotlinx.datetime.LocalDateTime;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BillPaymentDao_Impl implements BillPaymentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BillPaymentEntity> __insertionAdapterOfBillPaymentEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<BillPaymentEntity> __deletionAdapterOfBillPaymentEntity;

  public BillPaymentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBillPaymentEntity = new EntityInsertionAdapter<BillPaymentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `bill_payments` (`id`,`billId`,`accountId`,`paymentDate`,`amount`,`timestamp`,`transactionId`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BillPaymentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBillId());
        statement.bindLong(3, entity.getAccountId());
        final String _tmp = __converters.fromLocalDate(entity.getPaymentDate());
        statement.bindString(4, _tmp);
        statement.bindDouble(5, entity.getAmount());
        final String _tmp_1 = __converters.fromLocalDateTime(entity.getTimestamp());
        statement.bindString(6, _tmp_1);
        if (entity.getTransactionId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getTransactionId());
        }
      }
    };
    this.__deletionAdapterOfBillPaymentEntity = new EntityDeletionOrUpdateAdapter<BillPaymentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `bill_payments` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BillPaymentEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public Object insertPayment(final BillPaymentEntity payment,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBillPaymentEntity.insertAndReturnId(payment);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePayment(final BillPaymentEntity payment,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfBillPaymentEntity.handle(payment);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPayment(final long billId, final LocalDate date,
      final Continuation<? super BillPaymentEntity> $completion) {
    final String _sql = "SELECT * FROM bill_payments WHERE billId = ? AND paymentDate = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, billId);
    _argIndex = 2;
    final String _tmp = __converters.fromLocalDate(date);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BillPaymentEntity>() {
      @Override
      @Nullable
      public BillPaymentEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBillId = CursorUtil.getColumnIndexOrThrow(_cursor, "billId");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "accountId");
          final int _cursorIndexOfPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentDate");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionId");
          final BillPaymentEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBillId;
            _tmpBillId = _cursor.getLong(_cursorIndexOfBillId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final LocalDate _tmpPaymentDate;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfPaymentDate);
            _tmpPaymentDate = __converters.toLocalDate(_tmp_1);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final LocalDateTime _tmpTimestamp;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfTimestamp);
            _tmpTimestamp = __converters.toLocalDateTime(_tmp_2);
            final Long _tmpTransactionId;
            if (_cursor.isNull(_cursorIndexOfTransactionId)) {
              _tmpTransactionId = null;
            } else {
              _tmpTransactionId = _cursor.getLong(_cursorIndexOfTransactionId);
            }
            _result = new BillPaymentEntity(_tmpId,_tmpBillId,_tmpAccountId,_tmpPaymentDate,_tmpAmount,_tmpTimestamp,_tmpTransactionId);
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
  public Flow<List<BillPaymentEntity>> getPaymentsForBill(final long billId) {
    final String _sql = "SELECT * FROM bill_payments WHERE billId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, billId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bill_payments"}, new Callable<List<BillPaymentEntity>>() {
      @Override
      @NonNull
      public List<BillPaymentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBillId = CursorUtil.getColumnIndexOrThrow(_cursor, "billId");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "accountId");
          final int _cursorIndexOfPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentDate");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionId");
          final List<BillPaymentEntity> _result = new ArrayList<BillPaymentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BillPaymentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBillId;
            _tmpBillId = _cursor.getLong(_cursorIndexOfBillId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final LocalDate _tmpPaymentDate;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPaymentDate);
            _tmpPaymentDate = __converters.toLocalDate(_tmp);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final LocalDateTime _tmpTimestamp;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTimestamp);
            _tmpTimestamp = __converters.toLocalDateTime(_tmp_1);
            final Long _tmpTransactionId;
            if (_cursor.isNull(_cursorIndexOfTransactionId)) {
              _tmpTransactionId = null;
            } else {
              _tmpTransactionId = _cursor.getLong(_cursorIndexOfTransactionId);
            }
            _item = new BillPaymentEntity(_tmpId,_tmpBillId,_tmpAccountId,_tmpPaymentDate,_tmpAmount,_tmpTimestamp,_tmpTransactionId);
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
  public Flow<List<BillPaymentEntity>> getPaymentsForDate(final LocalDate date) {
    final String _sql = "SELECT * FROM bill_payments WHERE paymentDate = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromLocalDate(date);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bill_payments"}, new Callable<List<BillPaymentEntity>>() {
      @Override
      @NonNull
      public List<BillPaymentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBillId = CursorUtil.getColumnIndexOrThrow(_cursor, "billId");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "accountId");
          final int _cursorIndexOfPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentDate");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionId");
          final List<BillPaymentEntity> _result = new ArrayList<BillPaymentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BillPaymentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBillId;
            _tmpBillId = _cursor.getLong(_cursorIndexOfBillId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final LocalDate _tmpPaymentDate;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfPaymentDate);
            _tmpPaymentDate = __converters.toLocalDate(_tmp_1);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final LocalDateTime _tmpTimestamp;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfTimestamp);
            _tmpTimestamp = __converters.toLocalDateTime(_tmp_2);
            final Long _tmpTransactionId;
            if (_cursor.isNull(_cursorIndexOfTransactionId)) {
              _tmpTransactionId = null;
            } else {
              _tmpTransactionId = _cursor.getLong(_cursorIndexOfTransactionId);
            }
            _item = new BillPaymentEntity(_tmpId,_tmpBillId,_tmpAccountId,_tmpPaymentDate,_tmpAmount,_tmpTimestamp,_tmpTransactionId);
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
