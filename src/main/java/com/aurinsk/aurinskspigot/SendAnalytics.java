package com.aurinsk.aurinskspigot;

import org.bukkit.Bukkit;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import net.minecraft.server.MinecraftServer;

import com.sun.management.OperatingSystemMXBean;
import org.bukkit.plugin.PluginDescriptionFile;

public class SendAnalytics {

    private String getPublicIP() {
        try {
            URL ip = new URL("https://checkip.amazonaws.com/");
            BufferedReader in = new BufferedReader(new InputStreamReader(ip.openStream()));
            String publicIP = in.readLine();
            return publicIP;
        } catch (IOException e) {
            return String.valueOf(e);
        }

    }

    private String getCpuLoad() {
        OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        String cpuLoad = String.valueOf(bean.getProcessCpuLoad());
        if (cpuLoad != "0.0" && cpuLoad.length() > 4) {
            cpuLoad = cpuLoad.substring(0, 4);

        }
        return cpuLoad;
    }

    SendAnalytics(String uuid) {
        AurinskSpigot plugin = AurinskSpigot.get();

        String playercount = String.valueOf(Bukkit.getOnlinePlayers().size());
        String memoryusage = String.valueOf(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        String cpuLoad = getCpuLoad();
        String tps = String.valueOf(MinecraftServer.TPS);
        String ip = getPublicIP();
        String minecraftVersion = Bukkit.getBukkitVersion();
        String pluginVersion = plugin.getDescription().getVersion();

        URL url = null;
        try {
            url = new URL("http://192.168.1.251:3000/api/report");
        } catch (MalformedURLException e) {
            plugin.getLogger().warning("Error creating connection to API, skipping analytics send");
            return;
        }
        URLConnection con = null;
        try {
            con = url.openConnection();
        } catch (IOException e) {
            plugin.getLogger().warning("Error opening connection to API, skipping analytics send");
            return;
        }
        HttpURLConnection http = (HttpURLConnection) con;
        try {
            http.setRequestMethod("POST");
        } catch (ProtocolException e) {
            plugin.getLogger().warning("Error setting request method to API, skipping analytics send");
            return;
        }
        http.setDoOutput(true);

        Map<String,String> arguments = new HashMap<>();
        arguments.put("playercount", playercount);
        arguments.put("memory", memoryusage);
        arguments.put("cpu", cpuLoad);
        arguments.put("ip", ip);
        arguments.put("tps", tps);
        arguments.put("minecraftVersion", minecraftVersion);
        arguments.put("pluginVersion", pluginVersion);
        arguments.put("uuid", uuid);
        StringJoiner sj = new StringJoiner("&");
        for(Map.Entry<String,String> entry : arguments.entrySet()) {
            try {
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                plugin.getLogger().warning("Error encoding metrics, skipping analytics send");
                return;
            }
        }
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        try {
            http.connect();
        } catch (IOException e) {
            plugin.getLogger().severe("Error connecting to API, skipping analytics send");
            return;
        }
        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        } catch (IOException e) {
            plugin.getLogger().warning("Error writing output stream, skipping analytics send");
        }
    }
}
