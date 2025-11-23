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
import com.cashflow.app.data.entity.TransactionEntity;
import com.cashflow.app.data.model.TransactionType;
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
public final class TransactionDao_Impl implements TransactionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TransactionEntity> __insertionAdapterOfTransactionEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<TransactionEntity> __deletionAdapterOfTransactionEntity;

  private final EntityDeletionOrUpdateAdapter<TransactionEntity> __updateAdapterOfTransactionEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllTransactions;

  public TransactionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTransactionEntity = new EntityInsertionAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `transactions` (`id`,`accountId`,`toAccountId`,`type`,`amount`,`date`,`timestamp`,`description`,`relatedBillId`,`relatedIncomeId`,`envelopeId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAccountId());
        if (entity.getToAccountId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getToAccountId());
        }
        final String _tmp = __converters.fromTransactionType(entity.getType());
        statement.bindString(4, _tmp);
        statement.bindDouble(5, entity.getAmount());
        final String _tmp_1 = __converters.fromLocalDate(entity.getDate());
        statement.bindString(6, _tmp_1);
        final String _tmp_2 = __converters.fromLocalDateTime(entity.getTimestamp());
        statement.bindString(7, _tmp_2);
        statement.bindString(8, entity.getDescription());
        if (entity.getRelatedBillId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getRelatedBillId());
        }
        if (entity.getRelatedIncomeId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getRelatedIncomeId());
        }
        if (entity.getEnvelopeId() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getEnvelopeId());
        }
      }
    };
    this.__deletionAdapterOfTransactionEntity = new EntityDeletionOrUpdateAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `transactions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfTransactionEntity = new EntityDeletionOrUpdateAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `transactions` SET `id` = ?,`accountId` = ?,`toAccountId` = ?,`type` = ?,`amount` = ?,`date` = ?,`timestamp` = ?,`description` = ?,`relatedBillId` = ?,`relatedIncomeId` = ?,`envelopeId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAccountId());
        if (entity.getToAccountId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getToAccountId());
        }
        final String _tmp = __converters.fromTransactionType(entity.getType());
        statement.bindString(4, _tmp);
        statement.bindDouble(5, entity.getAmount());
        final String _tmp_1 = __converters.fromLocalDate(entity.getDate());
        statement.bindString(6, _tmp_1);
        final String _tmp_2 = __converters.fromLocalDateTime(entity.getTimestamp());
        statement.bindString(7, _tmp_2);
        statement.bindString(8, entity.getDescription());
        if (entity.getRelatedBillId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getRelatedBillId());
        }
        if (entity.getRelatedIncomeId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getRelatedIncomeId());
        }
        if (entity.getEnvelopeId() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getEnvelopeId());
        }
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllTransactions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM transactions";
        return _query;
      }
    };
  }

  @Override
  public Object insertTransaction(final TransactionEntity transaction,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTransactionEntity.insertAndReturnId(transaction);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTransaction(final TransactionEntity transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTransactionEntity.handle(transaction);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTransaction(final TransactionEntity transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTransactionEntity.handle(transaction);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllTransactions(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllTransactions.acquire();
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
          __preparedStmtOfDeleteAllTransactions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TransactionEntity>> getAllTransactions() {
    final String _sql = "SELECT * FROM transactions ORDER BY date DESC, timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "accountId");
          final int _cursorIndexOfToAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "toAccountId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfRelatedBillId = CursorUtil.getColumnIndexOrThrow(_cursor, "relatedBillId");
          final int _cursorIndexOfRelatedIncomeId = CursorUtil.getColumnIndexOrThrow(_cursor, "relatedIncomeId");
          final int _cursorIndexOfEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "envelopeId");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final Long _tmpToAccountId;
            if (_cursor.isNull(_cursorIndexOfToAccountId)) {
              _tmpToAccountId = null;
            } else {
              _tmpToAccountId = _cursor.getLong(_cursorIndexOfToAccountId);
            }
            final TransactionType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toTransactionType(_tmp);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final LocalDate _tmpDate;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfDate);
            _tmpDate = __converters.toLocalDate(_tmp_1);
            final LocalDateTime _tmpTimestamp;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfTimestamp);
            _tmpTimestamp = __converters.toLocalDateTime(_tmp_2);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final Long _tmpRelatedBillId;
            if (_cursor.isNull(_cursorIndexOfRelatedBillId)) {
              _tmpRelatedBillId = null;
            } else {
              _tmpRelatedBillId = _cursor.getLong(_cursorIndexOfRelatedBillId);
            }
            final Long _tmpRelatedIncomeId;
            if (_cursor.isNull(_cursorIndexOfRelatedIncomeId)) {
              _tmpRelatedIncomeId = null;
            } else {
              _tmpRelatedIncomeId = _cursor.getLong(_cursorIndexOfRelatedIncomeId);
            }
            final Long _tmpEnvelopeId;
            if (_cursor.isNull(_cursorIndexOfEnvelopeId)) {
              _tmpEnvelopeId = null;
            } else {
              _tmpEnvelopeId = _cursor.getLong(_cursorIndexOfEnvelopeId);
            }
            _item = new TransactionEntity(_tmpId,_tmpAccountId,_tmpToAccountId,_tmpType,_tmpAmount,_tmpDate,_tmpTimestamp,_tmpDescription,_tmpRelatedBillId,_tmpRelatedIncomeId,_tmpEnvelopeId);
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
  public Flow<List<TransactionEntity>> getTransactionsForAccount(final long accountId) {
    final String _sql = "SELECT * FROM transactions WHERE accountId = ? ORDER BY date DESC, timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, accountId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "accountId");
          final int _cursorIndexOfToAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "toAccountId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfRelatedBillId = CursorUtil.getColumnIndexOrThrow(_cursor, "relatedBillId");
          final int _cursorIndexOfRelatedIncomeId = CursorUtil.getColumnIndexOrThrow(_cursor, "relatedIncomeId");
          final int _cursorIndexOfEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "envelopeId");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final Long _tmpToAccountId;
            if (_cursor.isNull(_cursorIndexOfToAccountId)) {
              _tmpToAccountId = null;
            } else {
              _tmpToAccountId = _cursor.getLong(_cursorIndexOfToAccountId);
            }
            final TransactionType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toTransactionType(_tmp);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final LocalDate _tmpDate;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfDate);
            _tmpDate = __converters.toLocalDate(_tmp_1);
            final LocalDateTime _tmpTimestamp;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfTimestamp);
            _tmpTimestamp = __converters.toLocalDateTime(_tmp_2);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final Long _tmpRelatedBillId;
            if (_cursor.isNull(_cursorIndexOfRelatedBillId)) {
              _tmpRelatedBillId = null;
            } else {
              _tmpRelatedBillId = _cursor.getLong(_cursorIndexOfRelatedBillId);
            }
            final Long _tmpRelatedIncomeId;
            if (_cursor.isNull(_cursorIndexOfRelatedIncomeId)) {
              _tmpRelatedIncomeId = null;
            } else {
              _tmpRelatedIncomeId = _cursor.getLong(_cursorIndexOfRelatedIncomeId);
            }
            final Long _tmpEnvelopeId;
            if (_cursor.isNull(_cursorIndexOfEnvelopeId)) {
              _tmpEnvelopeId = null;
            } else {
              _tmpEnvelopeId = _cursor.getLong(_cursorIndexOfEnvelopeId);
            }
            _item = new TransactionEntity(_tmpId,_tmpAccountId,_tmpToAccountId,_tmpType,_tmpAmount,_tmpDate,_tmpTimestamp,_tmpDescription,_tmpRelatedBillId,_tmpRelatedIncomeId,_tmpEnvelopeId);
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
  public Flow<List<TransactionEntity>> getTransactionsBetween(final LocalDate startDate,
      final LocalDate endDate) {
    final String _sql = "SELECT * FROM transactions WHERE date BETWEEN ? AND ? ORDER BY date DESC, timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final String _tmp = __converters.fromLocalDate(startDate);
    _statement.bindString(_argIndex, _tmp);
    _argIndex = 2;
    final String _tmp_1 = __converters.fromLocalDate(endDate);
    _statement.bindString(_argIndex, _tmp_1);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "accountId");
          final int _cursorIndexOfToAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "toAccountId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfRelatedBillId = CursorUtil.getColumnIndexOrThrow(_cursor, "relatedBillId");
          final int _cursorIndexOfRelatedIncomeId = CursorUtil.getColumnIndexOrThrow(_cursor, "relatedIncomeId");
          final int _cursorIndexOfEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "envelopeId");
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final Long _tmpToAccountId;
            if (_cursor.isNull(_cursorIndexOfToAccountId)) {
              _tmpToAccountId = null;
            } else {
              _tmpToAccountId = _cursor.getLong(_cursorIndexOfToAccountId);
            }
            final TransactionType _tmpType;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toTransactionType(_tmp_2);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final LocalDate _tmpDate;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfDate);
            _tmpDate = __converters.toLocalDate(_tmp_3);
            final LocalDateTime _tmpTimestamp;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfTimestamp);
            _tmpTimestamp = __converters.toLocalDateTime(_tmp_4);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final Long _tmpRelatedBillId;
            if (_cursor.isNull(_cursorIndexOfRelatedBillId)) {
              _tmpRelatedBillId = null;
            } else {
              _tmpRelatedBillId = _cursor.getLong(_cursorIndexOfRelatedBillId);
            }
            final Long _tmpRelatedIncomeId;
            if (_cursor.isNull(_cursorIndexOfRelatedIncomeId)) {
              _tmpRelatedIncomeId = null;
            } else {
              _tmpRelatedIncomeId = _cursor.getLong(_cursorIndexOfRelatedIncomeId);
            }
            final Long _tmpEnvelopeId;
            if (_cursor.isNull(_cursorIndexOfEnvelopeId)) {
              _tmpEnvelopeId = null;
            } else {
              _tmpEnvelopeId = _cursor.getLong(_cursorIndexOfEnvelopeId);
            }
            _item = new TransactionEntity(_tmpId,_tmpAccountId,_tmpToAccountId,_tmpType,_tmpAmount,_tmpDate,_tmpTimestamp,_tmpDescription,_tmpRelatedBillId,_tmpRelatedIncomeId,_tmpEnvelopeId);
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
  public Object getTransactionById(final long id,
      final Continuation<? super TransactionEntity> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TransactionEntity>() {
      @Override
      @Nullable
      public TransactionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "accountId");
          final int _cursorIndexOfToAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "toAccountId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfRelatedBillId = CursorUtil.getColumnIndexOrThrow(_cursor, "relatedBillId");
          final int _cursorIndexOfRelatedIncomeId = CursorUtil.getColumnIndexOrThrow(_cursor, "relatedIncomeId");
          final int _cursorIndexOfEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "envelopeId");
          final TransactionEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final Long _tmpToAccountId;
            if (_cursor.isNull(_cursorIndexOfToAccountId)) {
              _tmpToAccountId = null;
            } else {
              _tmpToAccountId = _cursor.getLong(_cursorIndexOfToAccountId);
            }
            final TransactionType _tmpType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfType);
            _tmpType = __converters.toTransactionType(_tmp);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final LocalDate _tmpDate;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfDate);
            _tmpDate = __converters.toLocalDate(_tmp_1);
            final LocalDateTime _tmpTimestamp;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfTimestamp);
            _tmpTimestamp = __converters.toLocalDateTime(_tmp_2);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final Long _tmpRelatedBillId;
            if (_cursor.isNull(_cursorIndexOfRelatedBillId)) {
              _tmpRelatedBillId = null;
            } else {
              _tmpRelatedBillId = _cursor.getLong(_cursorIndexOfRelatedBillId);
            }
            final Long _tmpRelatedIncomeId;
            if (_cursor.isNull(_cursorIndexOfRelatedIncomeId)) {
              _tmpRelatedIncomeId = null;
            } else {
              _tmpRelatedIncomeId = _cursor.getLong(_cursorIndexOfRelatedIncomeId);
            }
            final Long _tmpEnvelopeId;
            if (_cursor.isNull(_cursorIndexOfEnvelopeId)) {
              _tmpEnvelopeId = null;
            } else {
              _tmpEnvelopeId = _cursor.getLong(_cursorIndexOfEnvelopeId);
            }
            _result = new TransactionEntity(_tmpId,_tmpAccountId,_tmpToAccountId,_tmpType,_tmpAmount,_tmpDate,_tmpTimestamp,_tmpDescription,_tmpRelatedBillId,_tmpRelatedIncomeId,_tmpEnvelopeId);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
