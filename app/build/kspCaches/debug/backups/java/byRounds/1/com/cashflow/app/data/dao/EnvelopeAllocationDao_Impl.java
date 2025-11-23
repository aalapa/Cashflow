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
import com.cashflow.app.data.entity.EnvelopeAllocationEntity;
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
public final class EnvelopeAllocationDao_Impl implements EnvelopeAllocationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EnvelopeAllocationEntity> __insertionAdapterOfEnvelopeAllocationEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<EnvelopeAllocationEntity> __deletionAdapterOfEnvelopeAllocationEntity;

  private final EntityDeletionOrUpdateAdapter<EnvelopeAllocationEntity> __updateAdapterOfEnvelopeAllocationEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllocationsForEnvelope;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllAllocations;

  public EnvelopeAllocationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEnvelopeAllocationEntity = new EntityInsertionAdapter<EnvelopeAllocationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `envelope_allocations` (`id`,`envelopeId`,`amount`,`periodStart`,`periodEnd`,`incomeId`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EnvelopeAllocationEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getEnvelopeId());
        statement.bindDouble(3, entity.getAmount());
        final String _tmp = __converters.fromLocalDate(entity.getPeriodStart());
        statement.bindString(4, _tmp);
        final String _tmp_1 = __converters.fromLocalDate(entity.getPeriodEnd());
        statement.bindString(5, _tmp_1);
        if (entity.getIncomeId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getIncomeId());
        }
        final String _tmp_2 = __converters.fromLocalDateTime(entity.getCreatedAt());
        statement.bindString(7, _tmp_2);
      }
    };
    this.__deletionAdapterOfEnvelopeAllocationEntity = new EntityDeletionOrUpdateAdapter<EnvelopeAllocationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `envelope_allocations` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EnvelopeAllocationEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfEnvelopeAllocationEntity = new EntityDeletionOrUpdateAdapter<EnvelopeAllocationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `envelope_allocations` SET `id` = ?,`envelopeId` = ?,`amount` = ?,`periodStart` = ?,`periodEnd` = ?,`incomeId` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EnvelopeAllocationEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getEnvelopeId());
        statement.bindDouble(3, entity.getAmount());
        final String _tmp = __converters.fromLocalDate(entity.getPeriodStart());
        statement.bindString(4, _tmp);
        final String _tmp_1 = __converters.fromLocalDate(entity.getPeriodEnd());
        statement.bindString(5, _tmp_1);
        if (entity.getIncomeId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getIncomeId());
        }
        final String _tmp_2 = __converters.fromLocalDateTime(entity.getCreatedAt());
        statement.bindString(7, _tmp_2);
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllocationsForEnvelope = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM envelope_allocations WHERE envelopeId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllAllocations = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM envelope_allocations";
        return _query;
      }
    };
  }

  @Override
  public Object insertAllocation(final EnvelopeAllocationEntity allocation,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfEnvelopeAllocationEntity.insertAndReturnId(allocation);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllocation(final EnvelopeAllocationEntity allocation,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfEnvelopeAllocationEntity.handle(allocation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAllocation(final EnvelopeAllocationEntity allocation,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfEnvelopeAllocationEntity.handle(allocation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllocationsForEnvelope(final long envelopeId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllocationsForEnvelope.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, envelopeId);
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
          __preparedStmtOfDeleteAllocationsForEnvelope.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllAllocations(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllAllocations.acquire();
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
          __preparedStmtOfDeleteAllAllocations.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EnvelopeAllocationEntity>> getAllocationsForEnvelope(final long envelopeId) {
    final String _sql = "SELECT * FROM envelope_allocations WHERE envelopeId = ? ORDER BY periodStart DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, envelopeId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"envelope_allocations"}, new Callable<List<EnvelopeAllocationEntity>>() {
      @Override
      @NonNull
      public List<EnvelopeAllocationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "envelopeId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPeriodStart = CursorUtil.getColumnIndexOrThrow(_cursor, "periodStart");
          final int _cursorIndexOfPeriodEnd = CursorUtil.getColumnIndexOrThrow(_cursor, "periodEnd");
          final int _cursorIndexOfIncomeId = CursorUtil.getColumnIndexOrThrow(_cursor, "incomeId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<EnvelopeAllocationEntity> _result = new ArrayList<EnvelopeAllocationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EnvelopeAllocationEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpEnvelopeId;
            _tmpEnvelopeId = _cursor.getLong(_cursorIndexOfEnvelopeId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final LocalDate _tmpPeriodStart;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPeriodStart);
            _tmpPeriodStart = __converters.toLocalDate(_tmp);
            final LocalDate _tmpPeriodEnd;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfPeriodEnd);
            _tmpPeriodEnd = __converters.toLocalDate(_tmp_1);
            final Long _tmpIncomeId;
            if (_cursor.isNull(_cursorIndexOfIncomeId)) {
              _tmpIncomeId = null;
            } else {
              _tmpIncomeId = _cursor.getLong(_cursorIndexOfIncomeId);
            }
            final LocalDateTime _tmpCreatedAt;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfCreatedAt);
            _tmpCreatedAt = __converters.toLocalDateTime(_tmp_2);
            _item = new EnvelopeAllocationEntity(_tmpId,_tmpEnvelopeId,_tmpAmount,_tmpPeriodStart,_tmpPeriodEnd,_tmpIncomeId,_tmpCreatedAt);
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
  public Object getAllocationForEnvelopeAndDate(final long envelopeId, final LocalDate date,
      final Continuation<? super EnvelopeAllocationEntity> $completion) {
    final String _sql = "SELECT * FROM envelope_allocations WHERE envelopeId = ? AND periodStart <= ? AND periodEnd >= ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, envelopeId);
    _argIndex = 2;
    final String _tmp = __converters.fromLocalDate(date);
    _statement.bindString(_argIndex, _tmp);
    _argIndex = 3;
    final String _tmp_1 = __converters.fromLocalDate(date);
    _statement.bindString(_argIndex, _tmp_1);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EnvelopeAllocationEntity>() {
      @Override
      @Nullable
      public EnvelopeAllocationEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "envelopeId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPeriodStart = CursorUtil.getColumnIndexOrThrow(_cursor, "periodStart");
          final int _cursorIndexOfPeriodEnd = CursorUtil.getColumnIndexOrThrow(_cursor, "periodEnd");
          final int _cursorIndexOfIncomeId = CursorUtil.getColumnIndexOrThrow(_cursor, "incomeId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final EnvelopeAllocationEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpEnvelopeId;
            _tmpEnvelopeId = _cursor.getLong(_cursorIndexOfEnvelopeId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final LocalDate _tmpPeriodStart;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPeriodStart);
            _tmpPeriodStart = __converters.toLocalDate(_tmp_2);
            final LocalDate _tmpPeriodEnd;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfPeriodEnd);
            _tmpPeriodEnd = __converters.toLocalDate(_tmp_3);
            final Long _tmpIncomeId;
            if (_cursor.isNull(_cursorIndexOfIncomeId)) {
              _tmpIncomeId = null;
            } else {
              _tmpIncomeId = _cursor.getLong(_cursorIndexOfIncomeId);
            }
            final LocalDateTime _tmpCreatedAt;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfCreatedAt);
            _tmpCreatedAt = __converters.toLocalDateTime(_tmp_4);
            _result = new EnvelopeAllocationEntity(_tmpId,_tmpEnvelopeId,_tmpAmount,_tmpPeriodStart,_tmpPeriodEnd,_tmpIncomeId,_tmpCreatedAt);
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
  public Object getAllocationForPeriod(final long envelopeId, final LocalDate date,
      final Continuation<? super EnvelopeAllocationEntity> $completion) {
    final String _sql = "SELECT * FROM envelope_allocations WHERE envelopeId = ? AND periodStart <= ? AND periodEnd >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, envelopeId);
    _argIndex = 2;
    final String _tmp = __converters.fromLocalDate(date);
    _statement.bindString(_argIndex, _tmp);
    _argIndex = 3;
    final String _tmp_1 = __converters.fromLocalDate(date);
    _statement.bindString(_argIndex, _tmp_1);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EnvelopeAllocationEntity>() {
      @Override
      @Nullable
      public EnvelopeAllocationEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "envelopeId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPeriodStart = CursorUtil.getColumnIndexOrThrow(_cursor, "periodStart");
          final int _cursorIndexOfPeriodEnd = CursorUtil.getColumnIndexOrThrow(_cursor, "periodEnd");
          final int _cursorIndexOfIncomeId = CursorUtil.getColumnIndexOrThrow(_cursor, "incomeId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final EnvelopeAllocationEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpEnvelopeId;
            _tmpEnvelopeId = _cursor.getLong(_cursorIndexOfEnvelopeId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final LocalDate _tmpPeriodStart;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPeriodStart);
            _tmpPeriodStart = __converters.toLocalDate(_tmp_2);
            final LocalDate _tmpPeriodEnd;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfPeriodEnd);
            _tmpPeriodEnd = __converters.toLocalDate(_tmp_3);
            final Long _tmpIncomeId;
            if (_cursor.isNull(_cursorIndexOfIncomeId)) {
              _tmpIncomeId = null;
            } else {
              _tmpIncomeId = _cursor.getLong(_cursorIndexOfIncomeId);
            }
            final LocalDateTime _tmpCreatedAt;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfCreatedAt);
            _tmpCreatedAt = __converters.toLocalDateTime(_tmp_4);
            _result = new EnvelopeAllocationEntity(_tmpId,_tmpEnvelopeId,_tmpAmount,_tmpPeriodStart,_tmpPeriodEnd,_tmpIncomeId,_tmpCreatedAt);
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
  public Object getAllocationById(final long id,
      final Continuation<? super EnvelopeAllocationEntity> $completion) {
    final String _sql = "SELECT * FROM envelope_allocations WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EnvelopeAllocationEntity>() {
      @Override
      @Nullable
      public EnvelopeAllocationEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "envelopeId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPeriodStart = CursorUtil.getColumnIndexOrThrow(_cursor, "periodStart");
          final int _cursorIndexOfPeriodEnd = CursorUtil.getColumnIndexOrThrow(_cursor, "periodEnd");
          final int _cursorIndexOfIncomeId = CursorUtil.getColumnIndexOrThrow(_cursor, "incomeId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final EnvelopeAllocationEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpEnvelopeId;
            _tmpEnvelopeId = _cursor.getLong(_cursorIndexOfEnvelopeId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final LocalDate _tmpPeriodStart;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPeriodStart);
            _tmpPeriodStart = __converters.toLocalDate(_tmp);
            final LocalDate _tmpPeriodEnd;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfPeriodEnd);
            _tmpPeriodEnd = __converters.toLocalDate(_tmp_1);
            final Long _tmpIncomeId;
            if (_cursor.isNull(_cursorIndexOfIncomeId)) {
              _tmpIncomeId = null;
            } else {
              _tmpIncomeId = _cursor.getLong(_cursorIndexOfIncomeId);
            }
            final LocalDateTime _tmpCreatedAt;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfCreatedAt);
            _tmpCreatedAt = __converters.toLocalDateTime(_tmp_2);
            _result = new EnvelopeAllocationEntity(_tmpId,_tmpEnvelopeId,_tmpAmount,_tmpPeriodStart,_tmpPeriodEnd,_tmpIncomeId,_tmpCreatedAt);
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
  public Flow<List<EnvelopeAllocationEntity>> getAllocationsInRange(final long envelopeId,
      final LocalDate startDate, final LocalDate endDate) {
    final String _sql = "SELECT * FROM envelope_allocations WHERE envelopeId = ? AND periodStart >= ? AND periodEnd <= ? ORDER BY periodStart";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, envelopeId);
    _argIndex = 2;
    final String _tmp = __converters.fromLocalDate(startDate);
    _statement.bindString(_argIndex, _tmp);
    _argIndex = 3;
    final String _tmp_1 = __converters.fromLocalDate(endDate);
    _statement.bindString(_argIndex, _tmp_1);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"envelope_allocations"}, new Callable<List<EnvelopeAllocationEntity>>() {
      @Override
      @NonNull
      public List<EnvelopeAllocationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "envelopeId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPeriodStart = CursorUtil.getColumnIndexOrThrow(_cursor, "periodStart");
          final int _cursorIndexOfPeriodEnd = CursorUtil.getColumnIndexOrThrow(_cursor, "periodEnd");
          final int _cursorIndexOfIncomeId = CursorUtil.getColumnIndexOrThrow(_cursor, "incomeId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<EnvelopeAllocationEntity> _result = new ArrayList<EnvelopeAllocationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EnvelopeAllocationEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpEnvelopeId;
            _tmpEnvelopeId = _cursor.getLong(_cursorIndexOfEnvelopeId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final LocalDate _tmpPeriodStart;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPeriodStart);
            _tmpPeriodStart = __converters.toLocalDate(_tmp_2);
            final LocalDate _tmpPeriodEnd;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfPeriodEnd);
            _tmpPeriodEnd = __converters.toLocalDate(_tmp_3);
            final Long _tmpIncomeId;
            if (_cursor.isNull(_cursorIndexOfIncomeId)) {
              _tmpIncomeId = null;
            } else {
              _tmpIncomeId = _cursor.getLong(_cursorIndexOfIncomeId);
            }
            final LocalDateTime _tmpCreatedAt;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfCreatedAt);
            _tmpCreatedAt = __converters.toLocalDateTime(_tmp_4);
            _item = new EnvelopeAllocationEntity(_tmpId,_tmpEnvelopeId,_tmpAmount,_tmpPeriodStart,_tmpPeriodEnd,_tmpIncomeId,_tmpCreatedAt);
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
