package dev.yh.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.yh.model.LootEntry;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class LootConfigLoader {

    private final File folder = new File("mods/HyDrops");
    private final File file = new File(folder, "loot_config.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Map<String, List<LootEntry>> load() {
        if (!folder.exists()) folder.mkdirs();
        if (!file.exists()) extractFromJar();

        try (Reader reader = new FileReader(file)) {
            var type = new TypeToken<Map<String, List<LootEntry>>>(){}.getType();
            return gson.fromJson(reader, type);
        } catch (Exception e) {
            System.err.println("[LootConfigLoader] Error al leer el JSON.");
            return new HashMap<>();
        }
    }

    private void extractFromJar() {
        try (InputStream in = getClass().getResourceAsStream("/loot_config.json")) {
            if (in != null) Files.copy(in, file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}