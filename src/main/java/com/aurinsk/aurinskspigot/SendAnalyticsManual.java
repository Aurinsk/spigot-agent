package com.aurinsk.aurinskspigot;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import com.sun.management.OperatingSystemMXBean;

public class SendAnalyticsManual implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String firstArg, String[] args) {
        String playercount = String.valueOf(Bukkit.getOnlinePlayers().size());
        String memoryusage = String.valueOf(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        String cpuLoad = getCpuLoad();

        if (cpuLoad == "0.0") {
            cpuLoad = getCpuLoad();
        }

        String ip = getPublicIP();

        sendRequest(playercount, memoryusage, cpuLoad, ip);

        Bukkit.broadcastMessage("POST sent");

        return true;
    }

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

    private void sendRequest(String playercount, String memoryusage, String cpuLoad, String ip) {
        URL url = null;
        try {
            url = new URL("http://192.168.1.249/test");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLConnection con = null;
        try {
            con = url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpURLConnection http = (HttpURLConnection)con;
        try {
            http.setRequestMethod("POST"); // PUT is another valid option
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        http.setDoOutput(true);

        Map<String,String> arguments = new HashMap<>();
        arguments.put("playercount", playercount);
        arguments.put("memoryusage", memoryusage);
        arguments.put("cpuload", cpuLoad);
        arguments.put("ip", ip);
        StringJoiner sj = new StringJoiner("&");
        for(Map.Entry<String,String> entry : arguments.entrySet()) {
            try {
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        try {
            http.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(OutputStream os = http.getOutputStream()) {
            os.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
