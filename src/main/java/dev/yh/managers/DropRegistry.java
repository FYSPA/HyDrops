package dev.yh.managers;

import com.hypixel.hytale.math.vector.Vector3i;
import java.util.HashSet;
import java.util.Set;

public class DropRegistry {
    // Guardamos un Set de posiciones para búsqueda rápida
    private final Set<Vector3i> activeDrops = new HashSet<>();

    public void registerDrop(int x, int y, int z) {
        activeDrops.add(new Vector3i(x, y, z));
    }

    public boolean isModDrop(int x, int y, int z) {
        return activeDrops.contains(new Vector3i(x, y, z));
    }

    public void unregisterDrop(int x, int y, int z) {
        activeDrops.remove(new Vector3i(x, y, z));
    }
}