package org.animey.auth;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public final class Auth extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Encryptor encryptor = new Encryptor(getSalt());
        SqlStorage storage;
        try {
            storage = new SqlStorage("plugins/Auth/authmc.db", encryptor);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        Commands commands = new Commands(storage);
        Listeners listeners = new Listeners(storage);
        this.getServer().getPluginManager().registerEvents(listeners, this);
        this.getCommand("register").setExecutor(commands);
        this.getCommand("login").setExecutor(commands);
        this.getCommand("changepw").setExecutor(commands);
        this.getCommand("reset").setExecutor(commands); // Console exclusive command
        System.out.println("Auth on");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Auth off");
    }

    private String getSalt(){
        new File("plugins/Auth").mkdirs();
        File saltFile = new File("plugins/Auth/server.salt");
        String salt = "salt";
        if(!saltFile.exists() || saltFile.isDirectory()){
            salt = Encryptor.generateSalt(); // Initial run
            try {
                if(saltFile.createNewFile()){
                    FileWriter myWriter = new FileWriter("plugins/Auth/server.salt");
                    myWriter.write(salt);
                    myWriter.close();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            File myObj = new File("plugins/Auth/server.salt");
            try {
                Scanner reader = new Scanner(myObj);
                if(reader.hasNext())
                    salt = reader.nextLine();

            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }

        return salt;
    }
}
