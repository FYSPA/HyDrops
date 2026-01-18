package dev.yh.managers;

import dev.yh.model.LootEntry;
import java.util.*;

public class LootManager {

    private final Map<String, List<LootEntry>> lootTables;
    private final Random random = new Random();

    // Recibe los datos ya cargados por el Loader
    public LootManager(Map<String, List<LootEntry>> lootTables) {
        this.lootTables = lootTables;
    }

    public Map<String, List<LootEntry>> getLootTables() {
        return this.lootTables;
    }

    public List<String> generateLootForZone(int zoneId, int rolls) {
        String key = "zone_" + zoneId;
        List<LootEntry> table = lootTables.getOrDefault(key, new ArrayList<>());
        List<String> result = new ArrayList<>();

        if (table.isEmpty()) return result;

        int totalWeight = table.stream().mapToInt(LootEntry::getWeight).sum();

        for (int i = 0; i < rolls; i++) {
            LootEntry selected = pickRandom(table, totalWeight);
            int qty = random.nextInt((selected.getMax() - selected.getMin()) + 1) + selected.getMin();
            result.add(qty + "x " + selected.getId());
        }
        return result;
    }

    private LootEntry pickRandom(List<LootEntry> table, int totalWeight) {
        int value = random.nextInt(totalWeight);
        for (LootEntry entry : table) {
            value -= entry.getWeight();
            if (value < 0) return entry;
        }
        return table.get(0);
    }

    public void reloadData(Map<String, List<LootEntry>> newData) {
        this.lootTables.clear();
        this.lootTables.putAll(newData);
    }

}