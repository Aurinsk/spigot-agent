package com.aurinsk.aurinskspigot;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

public class AurinskSpigot extends JavaPlugin {
    String uuid;
    public static AurinskSpigot plugin;

    @Override
    public void onEnable() {
        plugin = this;

        File pluginDir = new File(this.getDataFolder() + "/");
        Bukkit.broadcastMessage(this.getDataFolder() + "/");
        if (pluginDir.exists()) {
            File uuidFile = new File(this.getDataFolder() + "/uuid");
            try {
                Scanner reader = new Scanner(uuidFile);
                uuid = reader.nextLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            pluginDir.mkdir();
            File uuidFile = new File(this.getDataFolder() + "/uuid");
            try {
                uuidFile.createNewFile();
                FileWriter writer = new FileWriter(this.getDataFolder() + "/uuid");

                UUID ranUuid = UUID.randomUUID();
                uuid = String.valueOf(ranUuid);

                writer.write(String.valueOf(ranUuid));
                writer.close();

                FileWriter writer2 = new FileWriter(this.getDataFolder() + "/README.txt");
                writer2.write("Do not edit the UUID file unless support instructs you to. Editing of it will break the sending of analytics.");
                writer2.close();

            } catch (IOException e) {
                e.printStackTrace();
            }



        }

        this.getCommand("sendanalyticsmanual").setExecutor(new SendAnalyticsManual());

        new BukkitRunnable() {
            public void run() {
                SendAnalytics postAnalytics = new SendAnalytics(uuid);
            }
        }.runTaskTimer(this, 0, 1200);
    }

    @Override
    public void onDisable()
    {
        plugin = null;
    }

    public static AurinskSpigot get()
    {
        return plugin;
    }
}
