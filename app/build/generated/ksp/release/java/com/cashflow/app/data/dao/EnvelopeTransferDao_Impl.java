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
import com.cashflow.app.data.entity.EnvelopeTransferEntity;
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
public final class EnvelopeTransferDao_Impl implements EnvelopeTransferDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EnvelopeTransferEntity> __insertionAdapterOfEnvelopeTransferEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<EnvelopeTransferEntity> __deletionAdapterOfEnvelopeTransferEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllTransfers;

  public EnvelopeTransferDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEnvelopeTransferEntity = new EntityInsertionAdapter<EnvelopeTransferEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `envelope_transfers` (`id`,`fromEnvelopeId`,`toEnvelopeId`,`amount`,`date`,`description`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EnvelopeTransferEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getFromEnvelopeId());
        statement.bindLong(3, entity.getToEnvelopeId());
        statement.bindDouble(4, entity.getAmount());
        final String _tmp = __converters.fromLocalDate(entity.getDate());
        statement.bindString(5, _tmp);
        if (entity.getDescription() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getDescription());
        }
        final String _tmp_1 = __converters.fromLocalDateTime(entity.getTimestamp());
        statement.bindString(7, _tmp_1);
      }
    };
    this.__deletionAdapterOfEnvelopeTransferEntity = new EntityDeletionOrUpdateAdapter<EnvelopeTransferEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `envelope_transfers` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EnvelopeTransferEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllTransfers = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM envelope_transfers";
        return _query;
      }
    };
  }

  @Override
  public Object insertTransfer(final EnvelopeTransferEntity transfer,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfEnvelopeTransferEntity.insertAndReturnId(transfer);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTransfer(final EnvelopeTransferEntity transfer,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfEnvelopeTransferEntity.handle(transfer);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllTransfers(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllTransfers.acquire();
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
          __preparedStmtOfDeleteAllTransfers.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EnvelopeTransferEntity>> getTransfersForEnvelope(final long envelopeId) {
    final String _sql = "SELECT * FROM envelope_transfers WHERE fromEnvelopeId = ? OR toEnvelopeId = ? ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, envelopeId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, envelopeId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"envelope_transfers"}, new Callable<List<EnvelopeTransferEntity>>() {
      @Override
      @NonNull
      public List<EnvelopeTransferEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFromEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "fromEnvelopeId");
          final int _cursorIndexOfToEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "toEnvelopeId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<EnvelopeTransferEntity> _result = new ArrayList<EnvelopeTransferEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EnvelopeTransferEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFromEnvelopeId;
            _tmpFromEnvelopeId = _cursor.getLong(_cursorIndexOfFromEnvelopeId);
            final long _tmpToEnvelopeId;
            _tmpToEnvelopeId = _cursor.getLong(_cursorIndexOfToEnvelopeId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final LocalDate _tmpDate;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDate);
            _tmpDate = __converters.toLocalDate(_tmp);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final LocalDateTime _tmpTimestamp;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTimestamp);
            _tmpTimestamp = __converters.toLocalDateTime(_tmp_1);
            _item = new EnvelopeTransferEntity(_tmpId,_tmpFromEnvelopeId,_tmpToEnvelopeId,_tmpAmount,_tmpDate,_tmpDescription,_tmpTimestamp);
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
  public Flow<List<EnvelopeTransferEntity>> getAllTransfers() {
    final String _sql = "SELECT * FROM envelope_transfers ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"envelope_transfers"}, new Callable<List<EnvelopeTransferEntity>>() {
      @Override
      @NonNull
      public List<EnvelopeTransferEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFromEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "fromEnvelopeId");
          final int _cursorIndexOfToEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "toEnvelopeId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<EnvelopeTransferEntity> _result = new ArrayList<EnvelopeTransferEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EnvelopeTransferEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFromEnvelopeId;
            _tmpFromEnvelopeId = _cursor.getLong(_cursorIndexOfFromEnvelopeId);
            final long _tmpToEnvelopeId;
            _tmpToEnvelopeId = _cursor.getLong(_cursorIndexOfToEnvelopeId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final LocalDate _tmpDate;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDate);
            _tmpDate = __converters.toLocalDate(_tmp);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final LocalDateTime _tmpTimestamp;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTimestamp);
            _tmpTimestamp = __converters.toLocalDateTime(_tmp_1);
            _item = new EnvelopeTransferEntity(_tmpId,_tmpFromEnvelopeId,_tmpToEnvelopeId,_tmpAmount,_tmpDate,_tmpDescription,_tmpTimestamp);
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
  public Object getTransferById(final long id,
      final Continuation<? super EnvelopeTransferEntity> $completion) {
    final String _sql = "SELECT * FROM envelope_transfers WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EnvelopeTransferEntity>() {
      @Override
      @Nullable
      public EnvelopeTransferEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFromEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "fromEnvelopeId");
          final int _cursorIndexOfToEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "toEnvelopeId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final EnvelopeTransferEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFromEnvelopeId;
            _tmpFromEnvelopeId = _cursor.getLong(_cursorIndexOfFromEnvelopeId);
            final long _tmpToEnvelopeId;
            _tmpToEnvelopeId = _cursor.getLong(_cursorIndexOfToEnvelopeId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final LocalDate _tmpDate;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfDate);
            _tmpDate = __converters.toLocalDate(_tmp);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final LocalDateTime _tmpTimestamp;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTimestamp);
            _tmpTimestamp = __converters.toLocalDateTime(_tmp_1);
            _result = new EnvelopeTransferEntity(_tmpId,_tmpFromEnvelopeId,_tmpToEnvelopeId,_tmpAmount,_tmpDate,_tmpDescription,_tmpTimestamp);
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
