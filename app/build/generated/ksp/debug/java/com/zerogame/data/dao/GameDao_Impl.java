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
import com.zerogame.data.Converters;
import com.zerogame.data.model.Game;
import com.zerogame.data.model.GameType;
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
import java.util.Map;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class GameDao_Impl implements GameDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Game> __insertionAdapterOfGame;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<Game> __deletionAdapterOfGame;

  private final EntityDeletionOrUpdateAdapter<Game> __updateAdapterOfGame;

  private final SharedSQLiteStatement __preparedStmtOfFinishGame;

  public GameDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGame = new EntityInsertionAdapter<Game>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `games` (`id`,`gameType`,`createdAt`,`numberOfRounds`,`isFinished`,`config`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Game entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromGameType(entity.getGameType());
        statement.bindString(2, _tmp);
        statement.bindLong(3, entity.getCreatedAt());
        statement.bindLong(4, entity.getNumberOfRounds());
        final int _tmp_1 = entity.isFinished() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        final String _tmp_2 = __converters.fromExtrasMap(entity.getConfig());
        statement.bindString(6, _tmp_2);
      }
    };
    this.__deletionAdapterOfGame = new EntityDeletionOrUpdateAdapter<Game>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `games` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Game entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfGame = new EntityDeletionOrUpdateAdapter<Game>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `games` SET `id` = ?,`gameType` = ?,`createdAt` = ?,`numberOfRounds` = ?,`isFinished` = ?,`config` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Game entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromGameType(entity.getGameType());
        statement.bindString(2, _tmp);
        statement.bindLong(3, entity.getCreatedAt());
        statement.bindLong(4, entity.getNumberOfRounds());
        final int _tmp_1 = entity.isFinished() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        final String _tmp_2 = __converters.fromExtrasMap(entity.getConfig());
        statement.bindString(6, _tmp_2);
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfFinishGame = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE games SET isFinished = 1, numberOfRounds = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertGame(final Game game, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfGame.insertAndReturnId(game);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteGame(final Game game, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfGame.handle(game);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateGame(final Game game, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfGame.handle(game);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object finishGame(final long gameId, final int rounds,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfFinishGame.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, rounds);
        _argIndex = 2;
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
          __preparedStmtOfFinishGame.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Game>> getAllGames() {
    final String _sql = "SELECT * FROM games ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"games"}, new Callable<List<Game>>() {
      @Override
      @NonNull
      public List<Game> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameType = CursorUtil.getColumnIndexOrThrow(_cursor, "gameType");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfNumberOfRounds = CursorUtil.getColumnIndexOrThrow(_cursor, "numberOfRounds");
          final int _cursorIndexOfIsFinished = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinished");
          final int _cursorIndexOfConfig = CursorUtil.getColumnIndexOrThrow(_cursor, "config");
          final List<Game> _result = new ArrayList<Game>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Game _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final GameType _tmpGameType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfGameType);
            _tmpGameType = __converters.toGameType(_tmp);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpNumberOfRounds;
            _tmpNumberOfRounds = _cursor.getInt(_cursorIndexOfNumberOfRounds);
            final boolean _tmpIsFinished;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFinished);
            _tmpIsFinished = _tmp_1 != 0;
            final Map<String, String> _tmpConfig;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfConfig);
            _tmpConfig = __converters.toExtrasMap(_tmp_2);
            _item = new Game(_tmpId,_tmpGameType,_tmpCreatedAt,_tmpNumberOfRounds,_tmpIsFinished,_tmpConfig);
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
  public Flow<List<Game>> getGamesByType(final GameType gameType) {
    final String _sql = "SELECT * FROM games WHERE gameType = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromGameType(gameType);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"games"}, new Callable<List<Game>>() {
      @Override
      @NonNull
      public List<Game> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameType = CursorUtil.getColumnIndexOrThrow(_cursor, "gameType");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfNumberOfRounds = CursorUtil.getColumnIndexOrThrow(_cursor, "numberOfRounds");
          final int _cursorIndexOfIsFinished = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinished");
          final int _cursorIndexOfConfig = CursorUtil.getColumnIndexOrThrow(_cursor, "config");
          final List<Game> _result = new ArrayList<Game>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Game _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final GameType _tmpGameType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfGameType);
            _tmpGameType = __converters.toGameType(_tmp_1);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpNumberOfRounds;
            _tmpNumberOfRounds = _cursor.getInt(_cursorIndexOfNumberOfRounds);
            final boolean _tmpIsFinished;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsFinished);
            _tmpIsFinished = _tmp_2 != 0;
            final Map<String, String> _tmpConfig;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfConfig);
            _tmpConfig = __converters.toExtrasMap(_tmp_3);
            _item = new Game(_tmpId,_tmpGameType,_tmpCreatedAt,_tmpNumberOfRounds,_tmpIsFinished,_tmpConfig);
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
  public Object getGamesByTypeSync(final GameType gameType,
      final Continuation<? super List<Game>> $completion) {
    final String _sql = "SELECT * FROM games WHERE gameType = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromGameType(gameType);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Game>>() {
      @Override
      @NonNull
      public List<Game> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameType = CursorUtil.getColumnIndexOrThrow(_cursor, "gameType");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfNumberOfRounds = CursorUtil.getColumnIndexOrThrow(_cursor, "numberOfRounds");
          final int _cursorIndexOfIsFinished = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinished");
          final int _cursorIndexOfConfig = CursorUtil.getColumnIndexOrThrow(_cursor, "config");
          final List<Game> _result = new ArrayList<Game>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Game _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final GameType _tmpGameType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfGameType);
            _tmpGameType = __converters.toGameType(_tmp_1);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpNumberOfRounds;
            _tmpNumberOfRounds = _cursor.getInt(_cursorIndexOfNumberOfRounds);
            final boolean _tmpIsFinished;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsFinished);
            _tmpIsFinished = _tmp_2 != 0;
            final Map<String, String> _tmpConfig;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfConfig);
            _tmpConfig = __converters.toExtrasMap(_tmp_3);
            _item = new Game(_tmpId,_tmpGameType,_tmpCreatedAt,_tmpNumberOfRounds,_tmpIsFinished,_tmpConfig);
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
  public Object getGameById(final long gameId, final Continuation<? super Game> $completion) {
    final String _sql = "SELECT * FROM games WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, gameId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Game>() {
      @Override
      @Nullable
      public Game call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameType = CursorUtil.getColumnIndexOrThrow(_cursor, "gameType");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfNumberOfRounds = CursorUtil.getColumnIndexOrThrow(_cursor, "numberOfRounds");
          final int _cursorIndexOfIsFinished = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinished");
          final int _cursorIndexOfConfig = CursorUtil.getColumnIndexOrThrow(_cursor, "config");
          final Game _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final GameType _tmpGameType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfGameType);
            _tmpGameType = __converters.toGameType(_tmp);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpNumberOfRounds;
            _tmpNumberOfRounds = _cursor.getInt(_cursorIndexOfNumberOfRounds);
            final boolean _tmpIsFinished;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFinished);
            _tmpIsFinished = _tmp_1 != 0;
            final Map<String, String> _tmpConfig;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfConfig);
            _tmpConfig = __converters.toExtrasMap(_tmp_2);
            _result = new Game(_tmpId,_tmpGameType,_tmpCreatedAt,_tmpNumberOfRounds,_tmpIsFinished,_tmpConfig);
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
  public Flow<Game> getGameByIdFlow(final long gameId) {
    final String _sql = "SELECT * FROM games WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, gameId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"games"}, new Callable<Game>() {
      @Override
      @Nullable
      public Game call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameType = CursorUtil.getColumnIndexOrThrow(_cursor, "gameType");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfNumberOfRounds = CursorUtil.getColumnIndexOrThrow(_cursor, "numberOfRounds");
          final int _cursorIndexOfIsFinished = CursorUtil.getColumnIndexOrThrow(_cursor, "isFinished");
          final int _cursorIndexOfConfig = CursorUtil.getColumnIndexOrThrow(_cursor, "config");
          final Game _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final GameType _tmpGameType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfGameType);
            _tmpGameType = __converters.toGameType(_tmp);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpNumberOfRounds;
            _tmpNumberOfRounds = _cursor.getInt(_cursorIndexOfNumberOfRounds);
            final boolean _tmpIsFinished;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFinished);
            _tmpIsFinished = _tmp_1 != 0;
            final Map<String, String> _tmpConfig;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfConfig);
            _tmpConfig = __converters.toExtrasMap(_tmp_2);
            _result = new Game(_tmpId,_tmpGameType,_tmpCreatedAt,_tmpNumberOfRounds,_tmpIsFinished,_tmpConfig);
          } else {
            _result = null;
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
