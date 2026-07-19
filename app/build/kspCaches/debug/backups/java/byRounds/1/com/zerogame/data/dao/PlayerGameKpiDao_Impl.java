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
import com.zerogame.data.Converters;
import com.zerogame.data.model.GameType;
import com.zerogame.data.model.PlayerGameKpi;
import java.lang.Class;
import java.lang.Exception;
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
public final class PlayerGameKpiDao_Impl implements PlayerGameKpiDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PlayerGameKpi> __insertionAdapterOfPlayerGameKpi;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfDeleteByPlayerId;

  public PlayerGameKpiDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPlayerGameKpi = new EntityInsertionAdapter<PlayerGameKpi>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `player_game_kpis` (`playerId`,`gameType`,`totalGames`,`totalWins`,`winRate`,`currentWinStreak`,`maxWinStreak`,`totalPoints`,`averagePoints`,`totalZeros`,`triggerCount`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PlayerGameKpi entity) {
        statement.bindLong(1, entity.getPlayerId());
        final String _tmp = __converters.fromGameType(entity.getGameType());
        statement.bindString(2, _tmp);
        statement.bindLong(3, entity.getTotalGames());
        statement.bindLong(4, entity.getTotalWins());
        statement.bindDouble(5, entity.getWinRate());
        statement.bindLong(6, entity.getCurrentWinStreak());
        statement.bindLong(7, entity.getMaxWinStreak());
        statement.bindLong(8, entity.getTotalPoints());
        statement.bindDouble(9, entity.getAveragePoints());
        statement.bindLong(10, entity.getTotalZeros());
        statement.bindLong(11, entity.getTriggerCount());
      }
    };
    this.__preparedStmtOfDeleteByPlayerId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM player_game_kpis WHERE playerId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final PlayerGameKpi kpi, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPlayerGameKpi.insert(kpi);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertAll(final List<PlayerGameKpi> kpis,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPlayerGameKpi.insert(kpis);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByPlayerId(final long playerId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByPlayerId.acquire();
        int _argIndex = 1;
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
          __preparedStmtOfDeleteByPlayerId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PlayerGameKpi>> getKpisByPlayerId(final long playerId) {
    final String _sql = "SELECT * FROM player_game_kpis WHERE playerId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, playerId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"player_game_kpis"}, new Callable<List<PlayerGameKpi>>() {
      @Override
      @NonNull
      public List<PlayerGameKpi> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfGameType = CursorUtil.getColumnIndexOrThrow(_cursor, "gameType");
          final int _cursorIndexOfTotalGames = CursorUtil.getColumnIndexOrThrow(_cursor, "totalGames");
          final int _cursorIndexOfTotalWins = CursorUtil.getColumnIndexOrThrow(_cursor, "totalWins");
          final int _cursorIndexOfWinRate = CursorUtil.getColumnIndexOrThrow(_cursor, "winRate");
          final int _cursorIndexOfCurrentWinStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "currentWinStreak");
          final int _cursorIndexOfMaxWinStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "maxWinStreak");
          final int _cursorIndexOfTotalPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPoints");
          final int _cursorIndexOfAveragePoints = CursorUtil.getColumnIndexOrThrow(_cursor, "averagePoints");
          final int _cursorIndexOfTotalZeros = CursorUtil.getColumnIndexOrThrow(_cursor, "totalZeros");
          final int _cursorIndexOfTriggerCount = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerCount");
          final List<PlayerGameKpi> _result = new ArrayList<PlayerGameKpi>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PlayerGameKpi _item;
            final long _tmpPlayerId;
            _tmpPlayerId = _cursor.getLong(_cursorIndexOfPlayerId);
            final GameType _tmpGameType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfGameType);
            _tmpGameType = __converters.toGameType(_tmp);
            final int _tmpTotalGames;
            _tmpTotalGames = _cursor.getInt(_cursorIndexOfTotalGames);
            final int _tmpTotalWins;
            _tmpTotalWins = _cursor.getInt(_cursorIndexOfTotalWins);
            final float _tmpWinRate;
            _tmpWinRate = _cursor.getFloat(_cursorIndexOfWinRate);
            final int _tmpCurrentWinStreak;
            _tmpCurrentWinStreak = _cursor.getInt(_cursorIndexOfCurrentWinStreak);
            final int _tmpMaxWinStreak;
            _tmpMaxWinStreak = _cursor.getInt(_cursorIndexOfMaxWinStreak);
            final int _tmpTotalPoints;
            _tmpTotalPoints = _cursor.getInt(_cursorIndexOfTotalPoints);
            final float _tmpAveragePoints;
            _tmpAveragePoints = _cursor.getFloat(_cursorIndexOfAveragePoints);
            final int _tmpTotalZeros;
            _tmpTotalZeros = _cursor.getInt(_cursorIndexOfTotalZeros);
            final int _tmpTriggerCount;
            _tmpTriggerCount = _cursor.getInt(_cursorIndexOfTriggerCount);
            _item = new PlayerGameKpi(_tmpPlayerId,_tmpGameType,_tmpTotalGames,_tmpTotalWins,_tmpWinRate,_tmpCurrentWinStreak,_tmpMaxWinStreak,_tmpTotalPoints,_tmpAveragePoints,_tmpTotalZeros,_tmpTriggerCount);
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
  public Object getKpi(final long playerId, final GameType gameType,
      final Continuation<? super PlayerGameKpi> $completion) {
    final String _sql = "SELECT * FROM player_game_kpis WHERE playerId = ? AND gameType = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, playerId);
    _argIndex = 2;
    final String _tmp = __converters.fromGameType(gameType);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PlayerGameKpi>() {
      @Override
      @Nullable
      public PlayerGameKpi call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfGameType = CursorUtil.getColumnIndexOrThrow(_cursor, "gameType");
          final int _cursorIndexOfTotalGames = CursorUtil.getColumnIndexOrThrow(_cursor, "totalGames");
          final int _cursorIndexOfTotalWins = CursorUtil.getColumnIndexOrThrow(_cursor, "totalWins");
          final int _cursorIndexOfWinRate = CursorUtil.getColumnIndexOrThrow(_cursor, "winRate");
          final int _cursorIndexOfCurrentWinStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "currentWinStreak");
          final int _cursorIndexOfMaxWinStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "maxWinStreak");
          final int _cursorIndexOfTotalPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPoints");
          final int _cursorIndexOfAveragePoints = CursorUtil.getColumnIndexOrThrow(_cursor, "averagePoints");
          final int _cursorIndexOfTotalZeros = CursorUtil.getColumnIndexOrThrow(_cursor, "totalZeros");
          final int _cursorIndexOfTriggerCount = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerCount");
          final PlayerGameKpi _result;
          if (_cursor.moveToFirst()) {
            final long _tmpPlayerId;
            _tmpPlayerId = _cursor.getLong(_cursorIndexOfPlayerId);
            final GameType _tmpGameType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfGameType);
            _tmpGameType = __converters.toGameType(_tmp_1);
            final int _tmpTotalGames;
            _tmpTotalGames = _cursor.getInt(_cursorIndexOfTotalGames);
            final int _tmpTotalWins;
            _tmpTotalWins = _cursor.getInt(_cursorIndexOfTotalWins);
            final float _tmpWinRate;
            _tmpWinRate = _cursor.getFloat(_cursorIndexOfWinRate);
            final int _tmpCurrentWinStreak;
            _tmpCurrentWinStreak = _cursor.getInt(_cursorIndexOfCurrentWinStreak);
            final int _tmpMaxWinStreak;
            _tmpMaxWinStreak = _cursor.getInt(_cursorIndexOfMaxWinStreak);
            final int _tmpTotalPoints;
            _tmpTotalPoints = _cursor.getInt(_cursorIndexOfTotalPoints);
            final float _tmpAveragePoints;
            _tmpAveragePoints = _cursor.getFloat(_cursorIndexOfAveragePoints);
            final int _tmpTotalZeros;
            _tmpTotalZeros = _cursor.getInt(_cursorIndexOfTotalZeros);
            final int _tmpTriggerCount;
            _tmpTriggerCount = _cursor.getInt(_cursorIndexOfTriggerCount);
            _result = new PlayerGameKpi(_tmpPlayerId,_tmpGameType,_tmpTotalGames,_tmpTotalWins,_tmpWinRate,_tmpCurrentWinStreak,_tmpMaxWinStreak,_tmpTotalPoints,_tmpAveragePoints,_tmpTotalZeros,_tmpTriggerCount);
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
