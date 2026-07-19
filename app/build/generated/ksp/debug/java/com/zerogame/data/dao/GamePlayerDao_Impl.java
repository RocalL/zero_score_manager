package com.zerogame.data.dao;

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
import com.zerogame.data.model.GamePlayer;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class GamePlayerDao_Impl implements GamePlayerDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<GamePlayer> __insertionAdapterOfGamePlayer;

  private final EntityDeletionOrUpdateAdapter<GamePlayer> __updateAdapterOfGamePlayer;

  private final SharedSQLiteStatement __preparedStmtOfUpdateScore;

  public GamePlayerDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGamePlayer = new EntityInsertionAdapter<GamePlayer>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `game_players` (`id`,`gameId`,`playerId`,`totalScore`,`roundsPlayed`,`zerosAchieved`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GamePlayer entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getGameId());
        statement.bindLong(3, entity.getPlayerId());
        statement.bindLong(4, entity.getTotalScore());
        statement.bindLong(5, entity.getRoundsPlayed());
        statement.bindLong(6, entity.getZerosAchieved());
      }
    };
    this.__updateAdapterOfGamePlayer = new EntityDeletionOrUpdateAdapter<GamePlayer>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `game_players` SET `id` = ?,`gameId` = ?,`playerId` = ?,`totalScore` = ?,`roundsPlayed` = ?,`zerosAchieved` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GamePlayer entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getGameId());
        statement.bindLong(3, entity.getPlayerId());
        statement.bindLong(4, entity.getTotalScore());
        statement.bindLong(5, entity.getRoundsPlayed());
        statement.bindLong(6, entity.getZerosAchieved());
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateScore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE game_players SET totalScore = totalScore + ?, roundsPlayed = roundsPlayed + 1, zerosAchieved = zerosAchieved + CASE WHEN ? THEN 1 ELSE 0 END WHERE gameId = ? AND playerId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertGamePlayer(final GamePlayer gamePlayer,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfGamePlayer.insertAndReturnId(gamePlayer);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertGamePlayers(final List<GamePlayer> gamePlayers,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfGamePlayer.insert(gamePlayers);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateGamePlayer(final GamePlayer gamePlayer,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfGamePlayer.handle(gamePlayer);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateScore(final long gameId, final long playerId, final int score,
      final boolean achievedZero, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateScore.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, score);
        _argIndex = 2;
        final int _tmp = achievedZero ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, gameId);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, playerId);
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
          __preparedStmtOfUpdateScore.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<GamePlayer>> getGamePlayersByGameId(final long gameId) {
    final String _sql = "SELECT * FROM game_players WHERE gameId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, gameId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"game_players"}, new Callable<List<GamePlayer>>() {
      @Override
      @NonNull
      public List<GamePlayer> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameId = CursorUtil.getColumnIndexOrThrow(_cursor, "gameId");
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfTotalScore = CursorUtil.getColumnIndexOrThrow(_cursor, "totalScore");
          final int _cursorIndexOfRoundsPlayed = CursorUtil.getColumnIndexOrThrow(_cursor, "roundsPlayed");
          final int _cursorIndexOfZerosAchieved = CursorUtil.getColumnIndexOrThrow(_cursor, "zerosAchieved");
          final List<GamePlayer> _result = new ArrayList<GamePlayer>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GamePlayer _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpGameId;
            _tmpGameId = _cursor.getLong(_cursorIndexOfGameId);
            final long _tmpPlayerId;
            _tmpPlayerId = _cursor.getLong(_cursorIndexOfPlayerId);
            final int _tmpTotalScore;
            _tmpTotalScore = _cursor.getInt(_cursorIndexOfTotalScore);
            final int _tmpRoundsPlayed;
            _tmpRoundsPlayed = _cursor.getInt(_cursorIndexOfRoundsPlayed);
            final int _tmpZerosAchieved;
            _tmpZerosAchieved = _cursor.getInt(_cursorIndexOfZerosAchieved);
            _item = new GamePlayer(_tmpId,_tmpGameId,_tmpPlayerId,_tmpTotalScore,_tmpRoundsPlayed,_tmpZerosAchieved);
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
  public Object getGamePlayersByGameIdSync(final long gameId,
      final Continuation<? super List<GamePlayer>> $completion) {
    final String _sql = "SELECT * FROM game_players WHERE gameId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, gameId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<GamePlayer>>() {
      @Override
      @NonNull
      public List<GamePlayer> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameId = CursorUtil.getColumnIndexOrThrow(_cursor, "gameId");
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfTotalScore = CursorUtil.getColumnIndexOrThrow(_cursor, "totalScore");
          final int _cursorIndexOfRoundsPlayed = CursorUtil.getColumnIndexOrThrow(_cursor, "roundsPlayed");
          final int _cursorIndexOfZerosAchieved = CursorUtil.getColumnIndexOrThrow(_cursor, "zerosAchieved");
          final List<GamePlayer> _result = new ArrayList<GamePlayer>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GamePlayer _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpGameId;
            _tmpGameId = _cursor.getLong(_cursorIndexOfGameId);
            final long _tmpPlayerId;
            _tmpPlayerId = _cursor.getLong(_cursorIndexOfPlayerId);
            final int _tmpTotalScore;
            _tmpTotalScore = _cursor.getInt(_cursorIndexOfTotalScore);
            final int _tmpRoundsPlayed;
            _tmpRoundsPlayed = _cursor.getInt(_cursorIndexOfRoundsPlayed);
            final int _tmpZerosAchieved;
            _tmpZerosAchieved = _cursor.getInt(_cursorIndexOfZerosAchieved);
            _item = new GamePlayer(_tmpId,_tmpGameId,_tmpPlayerId,_tmpTotalScore,_tmpRoundsPlayed,_tmpZerosAchieved);
            _result.add(_item);
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
  public Object getGamePlayer(final long gameId, final long playerId,
      final Continuation<? super GamePlayer> $completion) {
    final String _sql = "SELECT * FROM game_players WHERE gameId = ? AND playerId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, gameId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, playerId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<GamePlayer>() {
      @Override
      @Nullable
      public GamePlayer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameId = CursorUtil.getColumnIndexOrThrow(_cursor, "gameId");
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfTotalScore = CursorUtil.getColumnIndexOrThrow(_cursor, "totalScore");
          final int _cursorIndexOfRoundsPlayed = CursorUtil.getColumnIndexOrThrow(_cursor, "roundsPlayed");
          final int _cursorIndexOfZerosAchieved = CursorUtil.getColumnIndexOrThrow(_cursor, "zerosAchieved");
          final GamePlayer _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpGameId;
            _tmpGameId = _cursor.getLong(_cursorIndexOfGameId);
            final long _tmpPlayerId;
            _tmpPlayerId = _cursor.getLong(_cursorIndexOfPlayerId);
            final int _tmpTotalScore;
            _tmpTotalScore = _cursor.getInt(_cursorIndexOfTotalScore);
            final int _tmpRoundsPlayed;
            _tmpRoundsPlayed = _cursor.getInt(_cursorIndexOfRoundsPlayed);
            final int _tmpZerosAchieved;
            _tmpZerosAchieved = _cursor.getInt(_cursorIndexOfZerosAchieved);
            _result = new GamePlayer(_tmpId,_tmpGameId,_tmpPlayerId,_tmpTotalScore,_tmpRoundsPlayed,_tmpZerosAchieved);
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
  public Object getWinner(final long gameId, final Continuation<? super GamePlayer> $completion) {
    final String _sql = "SELECT * FROM game_players WHERE gameId = ? ORDER BY totalScore ASC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, gameId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<GamePlayer>() {
      @Override
      @Nullable
      public GamePlayer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameId = CursorUtil.getColumnIndexOrThrow(_cursor, "gameId");
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfTotalScore = CursorUtil.getColumnIndexOrThrow(_cursor, "totalScore");
          final int _cursorIndexOfRoundsPlayed = CursorUtil.getColumnIndexOrThrow(_cursor, "roundsPlayed");
          final int _cursorIndexOfZerosAchieved = CursorUtil.getColumnIndexOrThrow(_cursor, "zerosAchieved");
          final GamePlayer _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpGameId;
            _tmpGameId = _cursor.getLong(_cursorIndexOfGameId);
            final long _tmpPlayerId;
            _tmpPlayerId = _cursor.getLong(_cursorIndexOfPlayerId);
            final int _tmpTotalScore;
            _tmpTotalScore = _cursor.getInt(_cursorIndexOfTotalScore);
            final int _tmpRoundsPlayed;
            _tmpRoundsPlayed = _cursor.getInt(_cursorIndexOfRoundsPlayed);
            final int _tmpZerosAchieved;
            _tmpZerosAchieved = _cursor.getInt(_cursorIndexOfZerosAchieved);
            _result = new GamePlayer(_tmpId,_tmpGameId,_tmpPlayerId,_tmpTotalScore,_tmpRoundsPlayed,_tmpZerosAchieved);
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
  public Flow<List<GamePlayer>> getGamePlayersByPlayerId(final long playerId) {
    final String _sql = "SELECT * FROM game_players WHERE playerId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, playerId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"game_players"}, new Callable<List<GamePlayer>>() {
      @Override
      @NonNull
      public List<GamePlayer> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameId = CursorUtil.getColumnIndexOrThrow(_cursor, "gameId");
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfTotalScore = CursorUtil.getColumnIndexOrThrow(_cursor, "totalScore");
          final int _cursorIndexOfRoundsPlayed = CursorUtil.getColumnIndexOrThrow(_cursor, "roundsPlayed");
          final int _cursorIndexOfZerosAchieved = CursorUtil.getColumnIndexOrThrow(_cursor, "zerosAchieved");
          final List<GamePlayer> _result = new ArrayList<GamePlayer>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GamePlayer _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpGameId;
            _tmpGameId = _cursor.getLong(_cursorIndexOfGameId);
            final long _tmpPlayerId;
            _tmpPlayerId = _cursor.getLong(_cursorIndexOfPlayerId);
            final int _tmpTotalScore;
            _tmpTotalScore = _cursor.getInt(_cursorIndexOfTotalScore);
            final int _tmpRoundsPlayed;
            _tmpRoundsPlayed = _cursor.getInt(_cursorIndexOfRoundsPlayed);
            final int _tmpZerosAchieved;
            _tmpZerosAchieved = _cursor.getInt(_cursorIndexOfZerosAchieved);
            _item = new GamePlayer(_tmpId,_tmpGameId,_tmpPlayerId,_tmpTotalScore,_tmpRoundsPlayed,_tmpZerosAchieved);
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
  public Object getGamePlayersByPlayerIdSync(final long playerId,
      final Continuation<? super List<GamePlayer>> $completion) {
    final String _sql = "SELECT * FROM game_players WHERE playerId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, playerId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<GamePlayer>>() {
      @Override
      @NonNull
      public List<GamePlayer> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameId = CursorUtil.getColumnIndexOrThrow(_cursor, "gameId");
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfTotalScore = CursorUtil.getColumnIndexOrThrow(_cursor, "totalScore");
          final int _cursorIndexOfRoundsPlayed = CursorUtil.getColumnIndexOrThrow(_cursor, "roundsPlayed");
          final int _cursorIndexOfZerosAchieved = CursorUtil.getColumnIndexOrThrow(_cursor, "zerosAchieved");
          final List<GamePlayer> _result = new ArrayList<GamePlayer>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GamePlayer _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpGameId;
            _tmpGameId = _cursor.getLong(_cursorIndexOfGameId);
            final long _tmpPlayerId;
            _tmpPlayerId = _cursor.getLong(_cursorIndexOfPlayerId);
            final int _tmpTotalScore;
            _tmpTotalScore = _cursor.getInt(_cursorIndexOfTotalScore);
            final int _tmpRoundsPlayed;
            _tmpRoundsPlayed = _cursor.getInt(_cursorIndexOfRoundsPlayed);
            final int _tmpZerosAchieved;
            _tmpZerosAchieved = _cursor.getInt(_cursorIndexOfZerosAchieved);
            _item = new GamePlayer(_tmpId,_tmpGameId,_tmpPlayerId,_tmpTotalScore,_tmpRoundsPlayed,_tmpZerosAchieved);
            _result.add(_item);
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
