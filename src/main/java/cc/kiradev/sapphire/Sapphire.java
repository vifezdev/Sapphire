package cc.kiradev.sapphire;

import cc.kiradev.sapphire.loader.PluginLoader;
import cc.kiradev.sapphire.util.CC;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class Sapphire extends JavaPlugin {

    @Override
    public void onEnable() {
        loadConfig();
        loadPlugins();
    }

    private void loadConfig() {
        saveResource("loader.yml", false);
    }

    private void loadPlugins() {
        Yaml yaml = new Yaml();
        try {
            URL configUrl = new URL("https://loader.kiradev.cc/loader.yml");
            Map<String, List<String>> config = (Map<String, List<String>>) yaml.load(configUrl.openStream());
            List<String> productsToLoad = config != null ? config.get("Loader") : null;
            PluginLoader pluginLoader = new PluginLoader();
            if (productsToLoad != null) {
                for (String product : productsToLoad) {
                    try {
                        loadPlugin(product, pluginLoader);
                    } catch (IOException e) {
                        getLogger().warning(CC.translate("&7[&cSapphire&7] &fSapphire is unable to locate the required jar to load, Please create a ticket in the discord: &7https://discord.gg/kiradev"));
                        e.printStackTrace();
                    }
                }
            } else {
                getLogger().warning(CC.translate("&7[&cSapphire&7] &cPlease enter product names into loader.yml"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPlugin(String productName, PluginLoader pluginLoader) throws IOException {
        URL pluginUrl = new URL("https://loader.kiradev.cc/products/" + productName);
        byte[] pluginBytes = downloadPluginBytes(pluginUrl);
        pluginLoader.loadPluginFromMemory(pluginBytes);
    }

    private byte[] downloadPluginBytes(URL pluginUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) pluginUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        try (InputStream inputStream = connection.getInputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        }
    }
}