package fr.nocsy.mcpets.data.sql;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Databases {

    @Getter
    @Setter
    public static MySQLDB mySQL;

    private static String table = GlobalConfig.getInstance().getMySQL_Prefix() + "mcpets_player_data";
    private static String activeTable = GlobalConfig.getInstance().getMySQL_Prefix() + "mcpets_active_pet";
    private static ConcurrentHashMap<UUID, Object> playerLocks = new ConcurrentHashMap<>();

    // -------------------------------------------------------------------------
    // アクティブペットレコード: プレイヤーがログアウト時に使用していたペットを記憶し、
    // Velocityクロスサーバー切り替え後に目的サーバーで復元するための
    // 専用テーブル mcpets_active_pet を使用する。
    // -------------------------------------------------------------------------

    private static final String PET_ID_DELIMITER = ",";

    private static final String SKIN_DELIMITER = ":";

    public static class ActivePetRecord {
        private final List<String> petIds;
        private final Map<String, String> skinUuids;
        private final long updatedAt;

        public ActivePetRecord(List<String> petIds, Map<String, String> skinUuids, long updatedAt) {
            this.petIds = petIds;
            this.skinUuids = skinUuids;
            this.updatedAt = updatedAt;
        }

        /** このレコードに保存されているアクティブペットIDの一覧（複数可）。 */
        public List<String> getPetIds()  { return petIds; }
        /** 指定ペットIDのスキンUUID。スキン未設定の場合はnull。 */
        public String getSkinUuid(String petId) { return skinUuids.get(petId); }
        public long   getUpdatedAt() { return updatedAt; }
    }

    public static boolean init() {
        // MySQL明示的に無効化 — YAMLへフォールバック（ログなし）
        if (GlobalConfig.getInstance().isDisableMySQL()) {
            return false;
        }

        String host = GlobalConfig.getInstance().getMySQL_HOST();
        String user = GlobalConfig.getInstance().getMySQL_USER();
        String pass = GlobalConfig.getInstance().getMySQL_PASSWORD();
        String port = GlobalConfig.getInstance().getMySQL_PORT();
        String db   = GlobalConfig.getInstance().getMySQL_DB();

        // MySQL認証情報が未設定 — YAMLへフォールバック（ログなし）
        if (isBlankOrNull(host) || isBlankOrNull(user) || isBlankOrNull(db)) {
            return false;
        }

        Databases.setMySQL(new MySQLDB(user, pass, host, port, db));
        if (!Databases.getMySQL().init()) {
            MCPets.getInstance().getLogger().warning("[MCPets] MySQLへの接続に失敗しました — YAMLストレージにフォールバックします。");
            GlobalConfig.getInstance().setDatabaseSupport(false);
            return false;
        }
        GlobalConfig.getInstance().setDatabaseSupport(true);
        createSQLTables();
        return true;
    }

    private static boolean isBlankOrNull(String s) {
        return s == null || s.isBlank();
    }

    public static void createSQLTables() {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;
        getMySQL().query("CREATE TABLE IF NOT EXISTS " + table + " (id INT NOT NULL AUTO_INCREMENT, uuid TEXT, names TEXT, inventories LONGTEXT, data LONGTEXT, lastActivePet TEXT, primary key (id));");
        getMySQL().query("ALTER TABLE " + table + " MODIFY inventories LONGTEXT, MODIFY data LONGTEXT;");
        ResultSet rs = getMySQL().preparedQuery("SELECT count(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = 'lastActivePet'",
                GlobalConfig.getInstance().getMySQL_DB(), table);
        try {
            if (rs != null && rs.next() && rs.getInt(1) == 0) {
                 getMySQL().query("ALTER TABLE " + table + " ADD lastActivePet TEXT;");
            }
        } catch (SQLException e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "lastActivePetカラムの確認に失敗しました", e);
        }
        createActivePetTable();
    }

    private static void createActivePetTable() {
        getMySQL().query("CREATE TABLE IF NOT EXISTS " + activeTable
                + " (uuid VARCHAR(36) NOT NULL, pet_id VARCHAR(255) NOT NULL,"
                + " updated_at BIGINT NOT NULL, PRIMARY KEY (uuid))");
    }

    public static boolean loadData() {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return false;

        ResultSet playerData = getMySQL().query("SELECT * FROM " + table + ";");

        if (playerData == null)
            return true;

        try {
            while (playerData.next()) {
                String uuidStr = playerData.getString("uuid");
                UUID uuid = UUID.fromString(uuidStr);

                synchronized (getLockForPlayer(uuid)) {
                    PlayerData pd = PlayerData.getEmpty(uuid);

                    // まずペットスタットをデシリアライズ（インベントリに影響するため）
                    PetStats.remove(uuid);

                    for (String seria : playerData.getString("data").split(";;;")) {
                        PetStats stats = PetStats.unzerialize(seria);
                        if (stats == null)
                            continue;
                        stats.launchTimers();
                        PetStats.register(stats);
                    }

                    // ペット名をデシリアライズ（レガシーの__active__キーはサイレントに削除）
                    ConcurrentHashMap<String, String> names = unserializeData(playerData, "names");
                    names.remove("__active__");
                    pd.setMapOfRegisteredNames(names);

                    // ペットインベントリをデシリアライズ
                    pd.setMapOfRegisteredInventories(unserializeData(playerData, "inventories"));
                    for (String petId : pd.getMapOfRegisteredInventories().keySet()) {
                        String seriaInv = pd.getMapOfRegisteredInventories().get(petId);
                        PetInventory.unserialize(petId + ";" + seriaInv, pd.getUuid());
                    }

                    try {
                        pd.setLastActivePet(playerData.getString("lastActivePet"));
                    } catch (SQLException e) {
                        // カラムが未作成の場合
                    }
                    PlayerData.getRegisteredData().put(uuid, pd);
                }
            }
        }
        catch (SQLException e1) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "データベースからのプレイヤーデータ読み込みに失敗しました", e1);
            return false;
        }

        return true;
    }

    private static Object getLockForPlayer(UUID playerUUID) {
        return playerLocks.computeIfAbsent(playerUUID, k -> new Object());
    }

    public static boolean loadData(UUID playerUUID) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return false;

        // 指定されたUUIDのプレイヤーデータを取得するSQLクエリを実行
        ResultSet playerData = getMySQL().preparedQuery("SELECT * FROM " + table + " WHERE uuid=?", playerUUID.toString());

        if (playerData == null)
            return true;

        try {
            while (playerData.next()) {
                String uuidStr = playerData.getString("uuid");
                UUID uuid = UUID.fromString(uuidStr);

                synchronized (getLockForPlayer(uuid)) {
                    PlayerData pd = PlayerData.getEmpty(uuid);

                    // まずペットスタットをデシリアライズ（インベントリに影響するため）
                    PetStats.remove(uuid);

                    for (String seria : playerData.getString("data").split(";;;")) {
                        PetStats stats = PetStats.unzerialize(seria);
                        if (stats == null)
                            continue;
                        stats.launchTimers();
                        PetStats.register(stats);
                    }

                    // ペット名をデシリアライズ（レガシーの__active__キーはサイレントに削除）
                    ConcurrentHashMap<String, String> names = unserializeData(playerData, "names");
                    names.remove("__active__");
                    pd.setMapOfRegisteredNames(names);

                    // ペットインベントリをデシリアライズ
                    pd.setMapOfRegisteredInventories(unserializeData(playerData, "inventories"));
                    for (String petId : pd.getMapOfRegisteredInventories().keySet()) {
                        String seriaInv = pd.getMapOfRegisteredInventories().get(petId);
                        PetInventory.unserialize(petId + ";" + seriaInv, pd.getUuid());
                    }

                    try {
                        pd.setLastActivePet(playerData.getString("lastActivePet"));
                    } catch (SQLException e) {
                        // カラムが未作成の場合
                    }
                    PlayerData.getRegisteredData().put(uuid, pd);
                }
            }
        }
        catch (SQLException e1) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "プレイヤーデータの読み込みに失敗しました: " + playerUUID, e1);
            return false;
        }
        return true;
    }

    public static void saveData() {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;

        getMySQL().query("TRUNCATE " + table);

        for (UUID uuid : PlayerData.getRegisteredData().keySet()) {
            synchronized (getLockForPlayer(uuid)) {
                PlayerData pd = PlayerData.getRegisteredData().get(uuid);

                String names = buildStringSerialized(pd.getMapOfRegisteredNames());
                String inventories = buildStringSerialized(pd.getMapOfRegisteredInventories());
                String lastActivePet = pd.getLastActivePet();
                if (lastActivePet == null) lastActivePet = "";

                StringBuilder data = new StringBuilder();

                for (PetStats stats : PetStats.getPetStats(uuid)) {
                    data.append(stats.serialize()).append(";;;");
                }
                if (data.length() > 0)
                    data = new StringBuilder(data.substring(0, data.length() - 3));

                getMySQL().preparedQuery("INSERT INTO " + table + " (uuid, names, inventories, data, lastActivePet) VALUES (?, ?, ?, ?, ?)",
                        uuid.toString(), names, inventories, data.toString(), lastActivePet);
            }
        }
    }

    public static void savePlayerData(UUID playerUUID) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;
        if (!PlayerData.isRegistered(playerUUID))
            return;

        synchronized (getLockForPlayer(playerUUID)) {
            PlayerData pd = PlayerData.getRegisteredData().get(playerUUID);

            String names = buildStringSerialized(pd.getMapOfRegisteredNames());
            String inventories = buildStringSerialized(pd.getMapOfRegisteredInventories());
            String lastActivePet = pd.getLastActivePet();
            if (lastActivePet == null) lastActivePet = "";

            StringBuilder data = new StringBuilder();

            for (PetStats stats : PetStats.getPetStats(playerUUID)) {
                data.append(stats.serialize()).append(";;;");
            }
            if (data.length() > 0)
                data = new StringBuilder(data.substring(0, data.length() - 3));

            // まず既存のプレイヤーデータを削除
            getMySQL().preparedQuery("DELETE FROM " + table + " WHERE uuid=?", playerUUID.toString());

            // 新しいプレイヤーデータを挿入
            getMySQL().preparedQuery("INSERT INTO " + table + " (uuid, names, inventories, data, lastActivePet) VALUES (?, ?, ?, ?, ?)",
                    playerUUID.toString(), names, inventories, data.toString(), lastActivePet);
        }
    }

    // -------------------------------------------------------------------------
    // Velocityクロスサーバー切り替え用アクティブペット永続化
    // -------------------------------------------------------------------------

    /**
     * プレイヤーのアクティブペットを専用テーブルにアップサートする。
     * 各エントリは "petId:skinUuid"（スキンなしの場合は "petId"）形式で、
     * pet_idカラムにカンマ区切りで保存される。
     */
    public static void saveActivePet(UUID uuid, List<String> petIds, Map<String, String> skinUuids) {
        if (!GlobalConfig.getInstance().isDatabaseSupport()) return;
        final List<String> entries = new ArrayList<>();
        for (String petId : petIds) {
            String skinUuid = skinUuids != null ? skinUuids.get(petId) : null;
            entries.add(skinUuid != null ? petId + SKIN_DELIMITER + skinUuid : petId);
        }
        String joined = String.join(PET_ID_DELIMITER, entries);
        long now = System.currentTimeMillis();
        getMySQL().preparedQuery(
                "INSERT INTO " + activeTable + " (uuid, pet_id, updated_at) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE pet_id=?, updated_at=?",
                uuid.toString(), joined, now, joined, now);
    }

    /**
     * プレイヤーのアクティブペットレコードを読み込む。存在しない場合はnullを返す。
     * クロスサーバー切り替えで到着したかどうかをログイン時に確認するために呼び出す。
     */
    public static ActivePetRecord loadActivePet(UUID uuid) {
        if (!GlobalConfig.getInstance().isDatabaseSupport()) return null;
        ResultSet rs = getMySQL().preparedQuery(
                "SELECT pet_id, updated_at FROM " + activeTable + " WHERE uuid=?",
                uuid.toString());
        if (rs == null) return null;
        try {
            if (rs.next()) {
                String raw = rs.getString("pet_id");
                if (raw == null) return null;
                List<String> ids = new ArrayList<>();
                Map<String, String> skinUuids = new HashMap<>();
                for (String entry : raw.split(PET_ID_DELIMITER)) {
                    entry = entry.trim();
                    if (entry.isEmpty()) continue;
                    if (entry.contains(SKIN_DELIMITER)) {
                        String[] parts = entry.split(SKIN_DELIMITER, 2);
                        ids.add(parts[0]);
                        skinUuids.put(parts[0], parts[1]);
                    } else {
                        ids.add(entry);
                    }
                }
                return new ActivePetRecord(ids, skinUuids, rs.getLong("updated_at"));
            }
        } catch (SQLException e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "アクティブペットレコードの読み込みに失敗しました: " + uuid, e);
        }
        return null;
    }

    /**
     * プレイヤーのアクティブペットレコードを削除する。
     * ペットが目的地でスポーン済み、または不要になった場合に呼び出す。
     */
    public static void clearActivePet(UUID uuid) {
        if (!GlobalConfig.getInstance().isDatabaseSupport()) return;
        getMySQL().preparedQuery("DELETE FROM " + activeTable + " WHERE uuid=?", uuid.toString());
    }

    public static void closeConnection() {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;

        mySQL.close();
    }

    private static String buildStringSerialized(Map<String,String> map) {
        String builder = "";
        for (String id : map.keySet()) {
            String seria = map.get(id);
            String seriaId = id + ";;" + seria;
            if (builder.isBlank())
                builder = seriaId;
            else
                builder = builder + ";;;" + seriaId;
        }
        return builder;
    }

    public static ConcurrentHashMap<String, String> unserializeData(ResultSet resultSet, String targetedColumn) throws SQLException {
        String targetedResults = resultSet.getString(targetedColumn);
        ConcurrentHashMap<String, String> outputMap = new ConcurrentHashMap<>();

        String[] seriaTable = targetedResults.split(";;;");

        for (String seriaContents : seriaTable) {
            // 入力が空または不正な形式の場合の処理
            if (seriaContents == null || !seriaContents.contains(";;"))
                continue;

            String[] seriaData = seriaContents.split(";;");
            try {
                String pet_id = seriaData[0];
                String content = seriaData[1];
                outputMap.put(pet_id, content);
            }
            catch (IndexOutOfBoundsException ex) {
                MCPets.getInstance().getLogger().log(Level.SEVERE, "[データベース] デシリアライズ中にインデックス超過: " + seriaContents, ex);
            }
        }

        return outputMap;
    }
}
