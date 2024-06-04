package org.animey.auth;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqlStorage { // SQLite implementation at the moment
    private final String connection;
    private final Encryptor encryptor;
    private final ComponentLogger log;
    List<Player> frozen = new ArrayList<>();
    protected SqlStorage(String connDB, Encryptor encryptor) throws SQLException {
        this.encryptor = encryptor;
        connection = connDB;
        log = JavaPlugin.getPlugin(Auth.class).getComponentLogger();
        try (Connection conn = openConnection()) {
            String createTable = "CREATE TABLE IF NOT EXISTS login_users (name STRING PRIMARY KEY, password STRING)";
            Statement statement = conn.createStatement();
            statement.execute(createTable);
            statement.close();
        } // Confirm it works
    }
    private Connection openConnection(){
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:"+ connection);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean setPassword(@NotNull Player player, @NotNull String password){
        if(!getPassword(player).isEmpty()) return false;
        String encrypted = encryptor.encrypt(password);
        try(Connection conn = openConnection()){
            String call = "INSERT INTO login_users (name, password) VALUES ('"+ player.getName() + "', '"+ encrypted +"')";
            Statement statement = conn.createStatement();
            statement.execute(call);
            return true;
        } catch (SQLException e) {
            log.error(Component.text(e.getMessage(), Style.style(TextColor.color(200, 0, 0))));
            return false;
        }
    }
    public boolean changePassword(@NotNull Player player, @NotNull String oldPass, @NotNull String newPass) {
        if(!verifyPassword(player, oldPass))
            return false;
        String encrypted = encryptor.encrypt(newPass);
        try (Connection conn = openConnection()) {
            String call = "UPDATE login_users SET password = '"+ encrypted +"' WHERE name = '"+ player.getName()+ "'";
            Statement statement = conn.createStatement();
            statement.execute(call);
            return true;
        } catch (SQLException e) {
            log.error(Component.text(e.getMessage(), Style.style(TextColor.color(200, 0, 0))));
            return false;
        }
    }

    protected boolean deletePassword(@NotNull String playerName){
        try (Connection conn = openConnection()){
            String call = "DELETE FROM login_users WHERE name = '"+ playerName+ "'"; // TODO: Manage SQL injection attacks
            Statement statement = conn.createStatement();
            return statement.execute(call);
        } catch (SQLException e) {
            log.error(Component.text(e.getMessage(), Style.style(TextColor.color(200, 0, 0))));
            return false;
        }
    }


    // Verify if exists, otherwise create.
    public boolean verifyPassword(@NotNull Player player, @NotNull String password) {
        String retrieved = getPassword(player);
        String encrypted = encryptor.encrypt(password);
        return encrypted.equals(retrieved);
    }
    public String getPassword(@NotNull Player player){
        try (Connection conn = openConnection()){
            String call = "SELECT password FROM login_users WHERE name = '"+ player.getName() +"'"; // TODO: Manage SQL injection attacks
            Statement statement = conn.createStatement();
            statement.execute(call);
            String password = statement.getResultSet().getString(1);
            if(password != null) return password;
        } catch (SQLException e) {
            log.error(Component.text(e.getMessage(), Style.style(TextColor.color(200, 0, 0))));
            return "";
        }
        return "";
    }
}
