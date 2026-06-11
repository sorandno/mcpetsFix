package fr.nocsy.mcpets.data.sql;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.logging.Level;

public class MySQLDB {

    private Connection sqlCon;
    private String user;
    private String pass;
    private String ip;
    private String port;
    private String db;

    /** 最後の接続検証が成功したタイムスタンプ */
    private long lastValidationTime = 0;
    /** 接続検証の最小間隔（ミリ秒） */
    private static final long VALIDATION_INTERVAL_MS = 5000;

    public MySQLDB(String user, String pass, String ip, String port, String db) {
        this.user = user;
        this.pass = pass;
        this.ip = ip;
        this.port = port;
        this.db = db;
    }

    public boolean init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = urlBuilder();
            this.sqlCon = DriverManager.getConnection(url, this.user, this.pass);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public void close() {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;
        try {
            this.sqlCon.close();
        }
        catch (Exception e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "SQL接続のクローズに失敗しました", e);
        }
    }

    public String urlBuilder() {
        return "jdbc:mysql://" + this.ip + ":" + this.port + "/" + this.db;
    }

    private void ensureConnection() throws SQLException {
        long now = System.currentTimeMillis();
        if (now - lastValidationTime < VALIDATION_INTERVAL_MS) {
            return;
        }
        if (!this.sqlCon.isValid(1)) {
            this.sqlCon.close();
            this.init();
        }
        lastValidationTime = now;
    }

    public ResultSet query(String s) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return null;
        try {
            ensureConnection();
        } catch (SQLException e1) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "SQL接続の検証に失敗しました", e1);
        }
        ResultSet set = null;
        try {
            Statement stat = this.sqlCon.createStatement();
            if (s.toLowerCase().startsWith("select")) {
                set = stat.executeQuery(s);
                closeStat(stat);
            } else {
                stat.executeUpdate(s);
                stat.close();
            }

        } catch (SQLException e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "SQLクエリが失敗しました: " + s, e);
        }
        return set;
    }

    public ResultSet preparedQuery(String sql, Object... params) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return null;
        try {
            ensureConnection();
        } catch (SQLException e1) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "SQL接続の検証に失敗しました", e1);
        }
        ResultSet set = null;
        try {
            PreparedStatement pstmt = this.sqlCon.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            if (sql.trim().toLowerCase().startsWith("select")) {
                set = pstmt.executeQuery();
                closeStat(pstmt);
            } else {
                pstmt.executeUpdate();
                pstmt.close();
            }
        } catch (SQLException e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "SQLプリペアドクエリが失敗しました: " + sql, e);
        }
        return set;
    }

    private void closeStat(final Statement stat) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    stat.close();
                } catch (SQLException e) {
                    MCPets.getInstance().getLogger().log(Level.SEVERE, "SQLステートメントのクローズに失敗しました", e);
                }
            }
        }.runTaskLater(MCPets.getInstance(), 5L);

    }
}
