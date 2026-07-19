package com.zerogame.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.zerogame.data.model.RoundScore;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RoundScoreDao_Impl implements RoundScoreDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RoundScore> __insertionAdapterOfRoundScore;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRoundScoresByGameId;

  public RoundScoreDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRoundScore = new EntityInsertionAdapter<RoundScore>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `round_scores` (`id`,`gameId`,`playerId`,`roundNumber`,`score`,`achievedZero`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RoundScore entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getGameId());
        statement.bindLong(3, entity.getPlayerId());
        statement.bindLong(4, entity.getRoundNumber());
        statement.bindLong(5, entity.getScore());
        final int _tmp = entity.getAchievedZero() ? 1 : 0;
        statement.bindLong(6, _tmp);
      }
    };
    this.__preparedStmtOfDeleteRoundScoresByGameId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM round_scores WHERE gameId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertRoundScore(final RoundScore roundScore,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRoundScore.insertAndReturnId(roundScore);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertRoundScores(final List<RoundScore> roundScores,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRoundScore.insert(roundScores);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteRoundScoresByGameId(final long gameId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteRoundScoresByGameId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, gameId);
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
          __preparedStmtOfDeleteRoundScoresByGameId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RoundScore>> getRoundScoresByGameId(final long gameId) {
    final String _sql = "SELECT * FROM round_scores WHERE gameId = ? ORDER BY roundNumber ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, gameId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"round_scores"}, new Callable<List<RoundScore>>() {
      @Override
      @NonNull
      public List<RoundScore> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameId = CursorUtil.getColumnIndexOrThrow(_cursor, "gameId");
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "roundNumber");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfAchievedZero = CursorUtil.getColumnIndexOrThrow(_cursor, "achievedZero");
          final List<RoundScore> _result = new ArrayList<RoundScore>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoundScore _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpGameId;
            _tmpGameId = _cursor.getLong(_cursorIndexOfGameId);
            final long _tmpPlayerId;
            _tmpPlayerId = _cursor.getLong(_cursorIndexOfPlayerId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final boolean _tmpAchievedZero;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAchievedZero);
            _tmpAchievedZero = _tmp != 0;
            _item = new RoundScore(_tmpId,_tmpGameId,_tmpPlayerId,_tmpRoundNumber,_tmpScore,_tmpAchievedZero);
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
  public Flow<List<RoundScore>> getRoundScoresByGameAndPlayer(final long gameId,
      final long playerId) {
    final String _sql = "SELECT * FROM round_scores WHERE gameId = ? AND playerId = ? ORDER BY roundNumber ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, gameId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, playerId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"round_scores"}, new Callable<List<RoundScore>>() {
      @Override
      @NonNull
      public List<RoundScore> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameId = CursorUtil.getColumnIndexOrThrow(_cursor, "gameId");
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfRoundNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "roundNumber");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfAchievedZero = CursorUtil.getColumnIndexOrThrow(_cursor, "achievedZero");
          final List<RoundScore> _result = new ArrayList<RoundScore>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoundScore _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpGameId;
            _tmpGameId = _cursor.getLong(_cursorIndexOfGameId);
            final long _tmpPlayerId;
            _tmpPlayerId = _cursor.getLong(_cursorIndexOfPlayerId);
            final int _tmpRoundNumber;
            _tmpRoundNumber = _cursor.getInt(_cursorIndexOfRoundNumber);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final boolean _tmpAchievedZero;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAchievedZero);
            _tmpAchievedZero = _tmp != 0;
            _item = new RoundScore(_tmpId,_tmpGameId,_tmpPlayerId,_tmpRoundNumber,_tmpScore,_tmpAchievedZero);
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
  public Object getMaxRound(final long gameId, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT MAX(roundNumber) FROM round_scores WHERE gameId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, gameId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
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
