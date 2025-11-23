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
import com.cashflow.app.data.entity.EnvelopeEntity;
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
import kotlinx.datetime.LocalDateTime;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class EnvelopeDao_Impl implements EnvelopeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EnvelopeEntity> __insertionAdapterOfEnvelopeEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<EnvelopeEntity> __deletionAdapterOfEnvelopeEntity;

  private final EntityDeletionOrUpdateAdapter<EnvelopeEntity> __updateAdapterOfEnvelopeEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllEnvelopes;

  public EnvelopeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEnvelopeEntity = new EntityInsertionAdapter<EnvelopeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `envelopes` (`id`,`name`,`color`,`icon`,`budgetedAmount`,`periodType`,`accountId`,`carryOverEnabled`,`createdAt`,`isActive`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EnvelopeEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getColor());
        if (entity.getIcon() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getIcon());
        }
        statement.bindDouble(5, entity.getBudgetedAmount());
        final String _tmp = __converters.fromRecurrenceType(entity.getPeriodType());
        statement.bindString(6, _tmp);
        if (entity.getAccountId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getAccountId());
        }
        final int _tmp_1 = entity.getCarryOverEnabled() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        final String _tmp_2 = __converters.fromLocalDateTime(entity.getCreatedAt());
        statement.bindString(9, _tmp_2);
        final int _tmp_3 = entity.isActive() ? 1 : 0;
        statement.bindLong(10, _tmp_3);
      }
    };
    this.__deletionAdapterOfEnvelopeEntity = new EntityDeletionOrUpdateAdapter<EnvelopeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `envelopes` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EnvelopeEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfEnvelopeEntity = new EntityDeletionOrUpdateAdapter<EnvelopeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `envelopes` SET `id` = ?,`name` = ?,`color` = ?,`icon` = ?,`budgetedAmount` = ?,`periodType` = ?,`accountId` = ?,`carryOverEnabled` = ?,`createdAt` = ?,`isActive` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EnvelopeEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getColor());
        if (entity.getIcon() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getIcon());
        }
        statement.bindDouble(5, entity.getBudgetedAmount());
        final String _tmp = __converters.fromRecurrenceType(entity.getPeriodType());
        statement.bindString(6, _tmp);
        if (entity.getAccountId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getAccountId());
        }
        final int _tmp_1 = entity.getCarryOverEnabled() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        final String _tmp_2 = __converters.fromLocalDateTime(entity.getCreatedAt());
        statement.bindString(9, _tmp_2);
        final int _tmp_3 = entity.isActive() ? 1 : 0;
        statement.bindLong(10, _tmp_3);
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllEnvelopes = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM envelopes";
        return _query;
      }
    };
  }

  @Override
  public Object insertEnvelope(final EnvelopeEntity envelope,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfEnvelopeEntity.insertAndReturnId(envelope);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteEnvelope(final EnvelopeEntity envelope,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfEnvelopeEntity.handle(envelope);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateEnvelope(final EnvelopeEntity envelope,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfEnvelopeEntity.handle(envelope);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllEnvelopes(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllEnvelopes.acquire();
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
          __preparedStmtOfDeleteAllEnvelopes.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EnvelopeEntity>> getAllActiveEnvelopes() {
    final String _sql = "SELECT * FROM envelopes WHERE isActive = 1 ORDER BY name";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"envelopes"}, new Callable<List<EnvelopeEntity>>() {
      @Override
      @NonNull
      public List<EnvelopeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "icon");
          final int _cursorIndexOfBudgetedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "budgetedAmount");
          final int _cursorIndexOfPeriodType = CursorUtil.getColumnIndexOrThrow(_cursor, "periodType");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "accountId");
          final int _cursorIndexOfCarryOverEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "carryOverEnabled");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<EnvelopeEntity> _result = new ArrayList<EnvelopeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EnvelopeEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpColor;
            _tmpColor = _cursor.getString(_cursorIndexOfColor);
            final String _tmpIcon;
            if (_cursor.isNull(_cursorIndexOfIcon)) {
              _tmpIcon = null;
            } else {
              _tmpIcon = _cursor.getString(_cursorIndexOfIcon);
            }
            final double _tmpBudgetedAmount;
            _tmpBudgetedAmount = _cursor.getDouble(_cursorIndexOfBudgetedAmount);
            final RecurrenceType _tmpPeriodType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPeriodType);
            _tmpPeriodType = __converters.toRecurrenceType(_tmp);
            final Long _tmpAccountId;
            if (_cursor.isNull(_cursorIndexOfAccountId)) {
              _tmpAccountId = null;
            } else {
              _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            }
            final boolean _tmpCarryOverEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfCarryOverEnabled);
            _tmpCarryOverEnabled = _tmp_1 != 0;
            final LocalDateTime _tmpCreatedAt;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfCreatedAt);
            _tmpCreatedAt = __converters.toLocalDateTime(_tmp_2);
            final boolean _tmpIsActive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_3 != 0;
            _item = new EnvelopeEntity(_tmpId,_tmpName,_tmpColor,_tmpIcon,_tmpBudgetedAmount,_tmpPeriodType,_tmpAccountId,_tmpCarryOverEnabled,_tmpCreatedAt,_tmpIsActive);
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
  public Flow<List<EnvelopeEntity>> getAllEnvelopes() {
    final String _sql = "SELECT * FROM envelopes ORDER BY name";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"envelopes"}, new Callable<List<EnvelopeEntity>>() {
      @Override
      @NonNull
      public List<EnvelopeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "icon");
          final int _cursorIndexOfBudgetedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "budgetedAmount");
          final int _cursorIndexOfPeriodType = CursorUtil.getColumnIndexOrThrow(_cursor, "periodType");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "accountId");
          final int _cursorIndexOfCarryOverEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "carryOverEnabled");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<EnvelopeEntity> _result = new ArrayList<EnvelopeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EnvelopeEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpColor;
            _tmpColor = _cursor.getString(_cursorIndexOfColor);
            final String _tmpIcon;
            if (_cursor.isNull(_cursorIndexOfIcon)) {
              _tmpIcon = null;
            } else {
              _tmpIcon = _cursor.getString(_cursorIndexOfIcon);
            }
            final double _tmpBudgetedAmount;
            _tmpBudgetedAmount = _cursor.getDouble(_cursorIndexOfBudgetedAmount);
            final RecurrenceType _tmpPeriodType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPeriodType);
            _tmpPeriodType = __converters.toRecurrenceType(_tmp);
            final Long _tmpAccountId;
            if (_cursor.isNull(_cursorIndexOfAccountId)) {
              _tmpAccountId = null;
            } else {
              _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            }
            final boolean _tmpCarryOverEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfCarryOverEnabled);
            _tmpCarryOverEnabled = _tmp_1 != 0;
            final LocalDateTime _tmpCreatedAt;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfCreatedAt);
            _tmpCreatedAt = __converters.toLocalDateTime(_tmp_2);
            final boolean _tmpIsActive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_3 != 0;
            _item = new EnvelopeEntity(_tmpId,_tmpName,_tmpColor,_tmpIcon,_tmpBudgetedAmount,_tmpPeriodType,_tmpAccountId,_tmpCarryOverEnabled,_tmpCreatedAt,_tmpIsActive);
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
  public Object getEnvelopeById(final long id,
      final Continuation<? super EnvelopeEntity> $completion) {
    final String _sql = "SELECT * FROM envelopes WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EnvelopeEntity>() {
      @Override
      @Nullable
      public EnvelopeEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "icon");
          final int _cursorIndexOfBudgetedAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "budgetedAmount");
          final int _cursorIndexOfPeriodType = CursorUtil.getColumnIndexOrThrow(_cursor, "periodType");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "accountId");
          final int _cursorIndexOfCarryOverEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "carryOverEnabled");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final EnvelopeEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpColor;
            _tmpColor = _cursor.getString(_cursorIndexOfColor);
            final String _tmpIcon;
            if (_cursor.isNull(_cursorIndexOfIcon)) {
              _tmpIcon = null;
            } else {
              _tmpIcon = _cursor.getString(_cursorIndexOfIcon);
            }
            final double _tmpBudgetedAmount;
            _tmpBudgetedAmount = _cursor.getDouble(_cursorIndexOfBudgetedAmount);
            final RecurrenceType _tmpPeriodType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPeriodType);
            _tmpPeriodType = __converters.toRecurrenceType(_tmp);
            final Long _tmpAccountId;
            if (_cursor.isNull(_cursorIndexOfAccountId)) {
              _tmpAccountId = null;
            } else {
              _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            }
            final boolean _tmpCarryOverEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfCarryOverEnabled);
            _tmpCarryOverEnabled = _tmp_1 != 0;
            final LocalDateTime _tmpCreatedAt;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfCreatedAt);
            _tmpCreatedAt = __converters.toLocalDateTime(_tmp_2);
            final boolean _tmpIsActive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_3 != 0;
            _result = new EnvelopeEntity(_tmpId,_tmpName,_tmpColor,_tmpIcon,_tmpBudgetedAmount,_tmpPeriodType,_tmpAccountId,_tmpCarryOverEnabled,_tmpCreatedAt,_tmpIsActive);
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
