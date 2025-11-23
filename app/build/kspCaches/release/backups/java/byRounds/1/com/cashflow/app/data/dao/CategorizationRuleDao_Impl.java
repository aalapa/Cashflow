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
import com.cashflow.app.data.entity.CategorizationRuleEntity;
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
public final class CategorizationRuleDao_Impl implements CategorizationRuleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CategorizationRuleEntity> __insertionAdapterOfCategorizationRuleEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<CategorizationRuleEntity> __deletionAdapterOfCategorizationRuleEntity;

  private final EntityDeletionOrUpdateAdapter<CategorizationRuleEntity> __updateAdapterOfCategorizationRuleEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllRules;

  public CategorizationRuleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCategorizationRuleEntity = new EntityInsertionAdapter<CategorizationRuleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `categorization_rules` (`id`,`envelopeId`,`keyword`,`isActive`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CategorizationRuleEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getEnvelopeId());
        statement.bindString(3, entity.getKeyword());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final String _tmp_1 = __converters.fromLocalDateTime(entity.getCreatedAt());
        statement.bindString(5, _tmp_1);
      }
    };
    this.__deletionAdapterOfCategorizationRuleEntity = new EntityDeletionOrUpdateAdapter<CategorizationRuleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `categorization_rules` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CategorizationRuleEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfCategorizationRuleEntity = new EntityDeletionOrUpdateAdapter<CategorizationRuleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `categorization_rules` SET `id` = ?,`envelopeId` = ?,`keyword` = ?,`isActive` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CategorizationRuleEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getEnvelopeId());
        statement.bindString(3, entity.getKeyword());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final String _tmp_1 = __converters.fromLocalDateTime(entity.getCreatedAt());
        statement.bindString(5, _tmp_1);
        statement.bindLong(6, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllRules = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM categorization_rules";
        return _query;
      }
    };
  }

  @Override
  public Object insertRule(final CategorizationRuleEntity rule,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCategorizationRuleEntity.insertAndReturnId(rule);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteRule(final CategorizationRuleEntity rule,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCategorizationRuleEntity.handle(rule);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateRule(final CategorizationRuleEntity rule,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCategorizationRuleEntity.handle(rule);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllRules(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllRules.acquire();
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
          __preparedStmtOfDeleteAllRules.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CategorizationRuleEntity>> getAllActiveRules() {
    final String _sql = "SELECT * FROM categorization_rules WHERE isActive = 1 ORDER BY keyword";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"categorization_rules"}, new Callable<List<CategorizationRuleEntity>>() {
      @Override
      @NonNull
      public List<CategorizationRuleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "envelopeId");
          final int _cursorIndexOfKeyword = CursorUtil.getColumnIndexOrThrow(_cursor, "keyword");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<CategorizationRuleEntity> _result = new ArrayList<CategorizationRuleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CategorizationRuleEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpEnvelopeId;
            _tmpEnvelopeId = _cursor.getLong(_cursorIndexOfEnvelopeId);
            final String _tmpKeyword;
            _tmpKeyword = _cursor.getString(_cursorIndexOfKeyword);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final LocalDateTime _tmpCreatedAt;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfCreatedAt);
            _tmpCreatedAt = __converters.toLocalDateTime(_tmp_1);
            _item = new CategorizationRuleEntity(_tmpId,_tmpEnvelopeId,_tmpKeyword,_tmpIsActive,_tmpCreatedAt);
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
  public Flow<List<CategorizationRuleEntity>> getRulesForEnvelope(final long envelopeId) {
    final String _sql = "SELECT * FROM categorization_rules WHERE envelopeId = ? AND isActive = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, envelopeId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"categorization_rules"}, new Callable<List<CategorizationRuleEntity>>() {
      @Override
      @NonNull
      public List<CategorizationRuleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "envelopeId");
          final int _cursorIndexOfKeyword = CursorUtil.getColumnIndexOrThrow(_cursor, "keyword");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<CategorizationRuleEntity> _result = new ArrayList<CategorizationRuleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CategorizationRuleEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpEnvelopeId;
            _tmpEnvelopeId = _cursor.getLong(_cursorIndexOfEnvelopeId);
            final String _tmpKeyword;
            _tmpKeyword = _cursor.getString(_cursorIndexOfKeyword);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final LocalDateTime _tmpCreatedAt;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfCreatedAt);
            _tmpCreatedAt = __converters.toLocalDateTime(_tmp_1);
            _item = new CategorizationRuleEntity(_tmpId,_tmpEnvelopeId,_tmpKeyword,_tmpIsActive,_tmpCreatedAt);
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
  public Object getRuleById(final long id,
      final Continuation<? super CategorizationRuleEntity> $completion) {
    final String _sql = "SELECT * FROM categorization_rules WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CategorizationRuleEntity>() {
      @Override
      @Nullable
      public CategorizationRuleEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEnvelopeId = CursorUtil.getColumnIndexOrThrow(_cursor, "envelopeId");
          final int _cursorIndexOfKeyword = CursorUtil.getColumnIndexOrThrow(_cursor, "keyword");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final CategorizationRuleEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpEnvelopeId;
            _tmpEnvelopeId = _cursor.getLong(_cursorIndexOfEnvelopeId);
            final String _tmpKeyword;
            _tmpKeyword = _cursor.getString(_cursorIndexOfKeyword);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final LocalDateTime _tmpCreatedAt;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfCreatedAt);
            _tmpCreatedAt = __converters.toLocalDateTime(_tmp_1);
            _result = new CategorizationRuleEntity(_tmpId,_tmpEnvelopeId,_tmpKeyword,_tmpIsActive,_tmpCreatedAt);
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
