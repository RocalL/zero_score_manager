package com.zerogame.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.zerogame.data.dao.GameDao;
import com.zerogame.data.dao.GameDao_Impl;
import com.zerogame.data.dao.GamePlayerDao;
import com.zerogame.data.dao.GamePlayerDao_Impl;
import com.zerogame.data.dao.PlayerDao;
import com.zerogame.data.dao.PlayerDao_Impl;
import com.zerogame.data.dao.PlayerGameKpiDao;
import com.zerogame.data.dao.PlayerGameKpiDao_Impl;
import com.zerogame.data.dao.RoundScoreDao;
import com.zerogame.data.dao.RoundScoreDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile PlayerDao _playerDao;

  private volatile GameDao _gameDao;

  private volatile GamePlayerDao _gamePlayerDao;

  private volatile RoundScoreDao _roundScoreDao;

  private volatile PlayerGameKpiDao _playerGameKpiDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `players` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `games` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `gameType` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `numberOfRounds` INTEGER NOT NULL, `isFinished` INTEGER NOT NULL, `config` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `game_players` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `gameId` INTEGER NOT NULL, `playerId` INTEGER NOT NULL, `totalScore` INTEGER NOT NULL, `roundsPlayed` INTEGER NOT NULL, `extras` TEXT NOT NULL, FOREIGN KEY(`gameId`) REFERENCES `games`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`playerId`) REFERENCES `players`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `round_scores` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `gameId` INTEGER NOT NULL, `playerId` INTEGER NOT NULL, `roundNumber` INTEGER NOT NULL, `score` INTEGER NOT NULL, `extras` TEXT NOT NULL, FOREIGN KEY(`gameId`) REFERENCES `games`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`playerId`) REFERENCES `players`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `player_game_kpis` (`playerId` INTEGER NOT NULL, `gameType` TEXT NOT NULL, `totalGames` INTEGER NOT NULL, `totalWins` INTEGER NOT NULL, `winRate` REAL NOT NULL, `currentWinStreak` INTEGER NOT NULL, `maxWinStreak` INTEGER NOT NULL, `totalPoints` INTEGER NOT NULL, `averagePoints` REAL NOT NULL, `totalZeros` INTEGER NOT NULL, `triggerCount` INTEGER NOT NULL, PRIMARY KEY(`playerId`, `gameType`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'd4691aa2c74e8548cd02a5c99181ea7b')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `players`");
        db.execSQL("DROP TABLE IF EXISTS `games`");
        db.execSQL("DROP TABLE IF EXISTS `game_players`");
        db.execSQL("DROP TABLE IF EXISTS `round_scores`");
        db.execSQL("DROP TABLE IF EXISTS `player_game_kpis`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsPlayers = new HashMap<String, TableInfo.Column>(3);
        _columnsPlayers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayers.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayers.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPlayers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPlayers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPlayers = new TableInfo("players", _columnsPlayers, _foreignKeysPlayers, _indicesPlayers);
        final TableInfo _existingPlayers = TableInfo.read(db, "players");
        if (!_infoPlayers.equals(_existingPlayers)) {
          return new RoomOpenHelper.ValidationResult(false, "players(com.zerogame.data.model.Player).\n"
                  + " Expected:\n" + _infoPlayers + "\n"
                  + " Found:\n" + _existingPlayers);
        }
        final HashMap<String, TableInfo.Column> _columnsGames = new HashMap<String, TableInfo.Column>(6);
        _columnsGames.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGames.put("gameType", new TableInfo.Column("gameType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGames.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGames.put("numberOfRounds", new TableInfo.Column("numberOfRounds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGames.put("isFinished", new TableInfo.Column("isFinished", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGames.put("config", new TableInfo.Column("config", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGames = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesGames = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGames = new TableInfo("games", _columnsGames, _foreignKeysGames, _indicesGames);
        final TableInfo _existingGames = TableInfo.read(db, "games");
        if (!_infoGames.equals(_existingGames)) {
          return new RoomOpenHelper.ValidationResult(false, "games(com.zerogame.data.model.Game).\n"
                  + " Expected:\n" + _infoGames + "\n"
                  + " Found:\n" + _existingGames);
        }
        final HashMap<String, TableInfo.Column> _columnsGamePlayers = new HashMap<String, TableInfo.Column>(6);
        _columnsGamePlayers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGamePlayers.put("gameId", new TableInfo.Column("gameId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGamePlayers.put("playerId", new TableInfo.Column("playerId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGamePlayers.put("totalScore", new TableInfo.Column("totalScore", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGamePlayers.put("roundsPlayed", new TableInfo.Column("roundsPlayed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGamePlayers.put("extras", new TableInfo.Column("extras", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGamePlayers = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysGamePlayers.add(new TableInfo.ForeignKey("games", "CASCADE", "NO ACTION", Arrays.asList("gameId"), Arrays.asList("id")));
        _foreignKeysGamePlayers.add(new TableInfo.ForeignKey("players", "CASCADE", "NO ACTION", Arrays.asList("playerId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesGamePlayers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGamePlayers = new TableInfo("game_players", _columnsGamePlayers, _foreignKeysGamePlayers, _indicesGamePlayers);
        final TableInfo _existingGamePlayers = TableInfo.read(db, "game_players");
        if (!_infoGamePlayers.equals(_existingGamePlayers)) {
          return new RoomOpenHelper.ValidationResult(false, "game_players(com.zerogame.data.model.GamePlayer).\n"
                  + " Expected:\n" + _infoGamePlayers + "\n"
                  + " Found:\n" + _existingGamePlayers);
        }
        final HashMap<String, TableInfo.Column> _columnsRoundScores = new HashMap<String, TableInfo.Column>(6);
        _columnsRoundScores.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoundScores.put("gameId", new TableInfo.Column("gameId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoundScores.put("playerId", new TableInfo.Column("playerId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoundScores.put("roundNumber", new TableInfo.Column("roundNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoundScores.put("score", new TableInfo.Column("score", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoundScores.put("extras", new TableInfo.Column("extras", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRoundScores = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysRoundScores.add(new TableInfo.ForeignKey("games", "CASCADE", "NO ACTION", Arrays.asList("gameId"), Arrays.asList("id")));
        _foreignKeysRoundScores.add(new TableInfo.ForeignKey("players", "CASCADE", "NO ACTION", Arrays.asList("playerId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesRoundScores = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRoundScores = new TableInfo("round_scores", _columnsRoundScores, _foreignKeysRoundScores, _indicesRoundScores);
        final TableInfo _existingRoundScores = TableInfo.read(db, "round_scores");
        if (!_infoRoundScores.equals(_existingRoundScores)) {
          return new RoomOpenHelper.ValidationResult(false, "round_scores(com.zerogame.data.model.RoundScore).\n"
                  + " Expected:\n" + _infoRoundScores + "\n"
                  + " Found:\n" + _existingRoundScores);
        }
        final HashMap<String, TableInfo.Column> _columnsPlayerGameKpis = new HashMap<String, TableInfo.Column>(11);
        _columnsPlayerGameKpis.put("playerId", new TableInfo.Column("playerId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayerGameKpis.put("gameType", new TableInfo.Column("gameType", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayerGameKpis.put("totalGames", new TableInfo.Column("totalGames", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayerGameKpis.put("totalWins", new TableInfo.Column("totalWins", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayerGameKpis.put("winRate", new TableInfo.Column("winRate", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayerGameKpis.put("currentWinStreak", new TableInfo.Column("currentWinStreak", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayerGameKpis.put("maxWinStreak", new TableInfo.Column("maxWinStreak", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayerGameKpis.put("totalPoints", new TableInfo.Column("totalPoints", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayerGameKpis.put("averagePoints", new TableInfo.Column("averagePoints", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayerGameKpis.put("totalZeros", new TableInfo.Column("totalZeros", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayerGameKpis.put("triggerCount", new TableInfo.Column("triggerCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPlayerGameKpis = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPlayerGameKpis = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPlayerGameKpis = new TableInfo("player_game_kpis", _columnsPlayerGameKpis, _foreignKeysPlayerGameKpis, _indicesPlayerGameKpis);
        final TableInfo _existingPlayerGameKpis = TableInfo.read(db, "player_game_kpis");
        if (!_infoPlayerGameKpis.equals(_existingPlayerGameKpis)) {
          return new RoomOpenHelper.ValidationResult(false, "player_game_kpis(com.zerogame.data.model.PlayerGameKpi).\n"
                  + " Expected:\n" + _infoPlayerGameKpis + "\n"
                  + " Found:\n" + _existingPlayerGameKpis);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "d4691aa2c74e8548cd02a5c99181ea7b", "7387a2e3f8f7fd0be3711c3b84a75325");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "players","games","game_players","round_scores","player_game_kpis");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `players`");
      _db.execSQL("DELETE FROM `games`");
      _db.execSQL("DELETE FROM `game_players`");
      _db.execSQL("DELETE FROM `round_scores`");
      _db.execSQL("DELETE FROM `player_game_kpis`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(PlayerDao.class, PlayerDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(GameDao.class, GameDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(GamePlayerDao.class, GamePlayerDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RoundScoreDao.class, RoundScoreDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PlayerGameKpiDao.class, PlayerGameKpiDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public PlayerDao playerDao() {
    if (_playerDao != null) {
      return _playerDao;
    } else {
      synchronized(this) {
        if(_playerDao == null) {
          _playerDao = new PlayerDao_Impl(this);
        }
        return _playerDao;
      }
    }
  }

  @Override
  public GameDao gameDao() {
    if (_gameDao != null) {
      return _gameDao;
    } else {
      synchronized(this) {
        if(_gameDao == null) {
          _gameDao = new GameDao_Impl(this);
        }
        return _gameDao;
      }
    }
  }

  @Override
  public GamePlayerDao gamePlayerDao() {
    if (_gamePlayerDao != null) {
      return _gamePlayerDao;
    } else {
      synchronized(this) {
        if(_gamePlayerDao == null) {
          _gamePlayerDao = new GamePlayerDao_Impl(this);
        }
        return _gamePlayerDao;
      }
    }
  }

  @Override
  public RoundScoreDao roundScoreDao() {
    if (_roundScoreDao != null) {
      return _roundScoreDao;
    } else {
      synchronized(this) {
        if(_roundScoreDao == null) {
          _roundScoreDao = new RoundScoreDao_Impl(this);
        }
        return _roundScoreDao;
      }
    }
  }

  @Override
  public PlayerGameKpiDao playerGameKpiDao() {
    if (_playerGameKpiDao != null) {
      return _playerGameKpiDao;
    } else {
      synchronized(this) {
        if(_playerGameKpiDao == null) {
          _playerGameKpiDao = new PlayerGameKpiDao_Impl(this);
        }
        return _playerGameKpiDao;
      }
    }
  }
}
