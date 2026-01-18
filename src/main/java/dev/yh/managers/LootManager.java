package dev.yh.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.yh.model.LootEntry; // Asegúrate que este import sea correcto

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class LootManager {

    // Map: "zone_1" -> Lista de items
    private Map<String, List<LootEntry>> lootTables;
    private final Random random;
    private final Gson gson;

    // DEFINIMOS LA RUTA EXACTA: carpeta_del_server/mods/ArkDrops/loot_config.json
    private final File DATA_FOLDER = new File("mods/HyDrops");
    private final File CONFIG_FILE = new File(DATA_FOLDER, "loot_config.json");

    public LootManager() {
        this.lootTables = new HashMap<>();
        this.random = new Random();
        this.gson = new GsonBuilder().setPrettyPrinting().create(); // Pretty para que el JSON se vea bonito

        loadConfig();
    }

    private void loadConfig() {
        // 1. Crear la carpeta si no existe
        if (!DATA_FOLDER.exists()) {
            DATA_FOLDER.mkdirs();
        }

        // 2. Si el archivo NO existe, creamos uno por defecto
        if (!CONFIG_FILE.exists()) {
            createDefaultConfig();
        }

        // 3. Leer el archivo
        try (Reader reader = new FileReader(CONFIG_FILE)) {
            Type type = new TypeToken<Map<String, List<LootEntry>>>(){}.getType();
            this.lootTables = gson.fromJson(reader, type);

            System.out.println("[LootManager] Configuración cargada desde: " + CONFIG_FILE.getAbsolutePath());
            System.out.println("[LootManager] Zonas cargadas: " + lootTables.keySet());

        } catch (Exception e) {
            System.err.println("[LootManager] Error leyendo JSON: " + e.getMessage());
            e.printStackTrace();
            this.lootTables = new HashMap<>();
        }
    }

    private void createDefaultConfig() {
        try (Writer writer = new FileWriter(CONFIG_FILE)) {
            // Creamos datos de ejemplo para que el archivo no esté vacío
            Map<String, List<LootEntry>> defaultData = new HashMap<>();

            // Ejemplo Zona 1
            List<String> items = new ArrayList<>();
            List<LootEntry> zone1 = new ArrayList<>();
            // Fíjate que aquí uso setters o constructor si lo tienes en tu modelo LootEntry
            // Si LootEntry no tiene constructor, usa setters. Aquí asumo un constructor o setters.
            // Adaptar esto a tu clase LootEntry:
            zone1.add(createEntry("Wood_Birch_Trunk_Full", 5, 10, 100));
            zone1.add(createEntry("Weapon_Staff_Bo_Bamboo", 1, 1, 30));

            defaultData.put("zone_1", zone1);

            // Guardamos
            gson.toJson(defaultData, writer);
            System.out.println("[LootManager] Archivo por defecto creado en: " + CONFIG_FILE.getPath());

        } catch (IOException e) {
            System.err.println("[LootManager] No se pudo crear el archivo por defecto.");
        }
    }

    // Helper para crear entradas rápido (Añádelo si te sirve)
    private LootEntry createEntry(String id, int min, int max, int weight) {
        LootEntry entry = new LootEntry();
        // Asumiendo que tienes setters en LootEntry.java, si son private usa reflexión o cambia a public
        entry.setId(id);
        entry.setMin(min);
        entry.setMax(max);
        entry.setWeight(weight);
        return entry;
    }

    public List<String> generateLootForZone(int zoneId, int rolls) {
        String zoneKey = "zone_" + zoneId;
        List<LootEntry> table = lootTables.get(zoneKey);
        List<String> result = new ArrayList<>();

        if (table == null || table.isEmpty()) {
            System.out.println("[Debug] No se encontró la key: " + zoneKey);
            System.out.println("[Debug] Keys disponibles: " + lootTables.keySet());
            return result;
        }

        int totalWeight = table.stream().mapToInt(LootEntry::getWeight).sum();

        for (int i = 0; i < rolls; i++) {
            LootEntry selected = pickRandomEntry(table, totalWeight);
            if (selected != null) {
                int quantity = random.nextInt((selected.getMax() - selected.getMin()) + 1) + selected.getMin();
                result.add(quantity + "x " + selected.getId());
            }
        }
        return result;
    }

    private LootEntry pickRandomEntry(List<LootEntry> table, int totalWeight) {
        if (totalWeight <= 0) return null;
        int value = random.nextInt(totalWeight);
        for (LootEntry entry : table) {
            value -= entry.getWeight();
            if (value < 0) return entry;
        }
        return table.get(0);
    }
}