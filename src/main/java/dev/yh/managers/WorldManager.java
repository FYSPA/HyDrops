package dev.yh.managers;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.World;

import java.lang.reflect.Method;
import java.util.List;

public class WorldManager {

    public void spawnPhysicalDrop(World world, double x, double z, List<String> itemStrings) {
        if (world == null || itemStrings.isEmpty()) return;

        // Buscamos la altura (Y) + 3 bloques para que caigan
        double y = getHighestBlockY(world, (int) x, (int) z) + 3.0;
        Vector3d position = new Vector3d(x + 0.5, y, z + 0.5);

        for (String entry : itemStrings) {
            try {
                String[] parts = entry.split("x ");
                if (parts.length < 2) continue;

                int quantity = Integer.parseInt(parts[0]);
                String itemId = parts[1].trim();

                // Crear el ItemStack
                ItemStack stack = new ItemStack(itemId, quantity);

                if (stack.isValid() && !stack.isEmpty()) {
                    // LLAMADA DINÁMICA: Intentamos spawnear el item
                    dropItemStackSafely(world, position, stack);
                }
            } catch (Exception e) {
                System.out.println("[WorldManager] Error en item: " + entry);
            }
        }
    }

    /**
     * Intenta encontrar el método para soltar items en el mundo.
     * Hytale puede llamarlo 'spawnItem', 'dropItem' o 'dropItemStack'.
     */
    private void dropItemStackSafely(World world, Vector3d pos, ItemStack stack) {
        try {
            // Intento 1: spawnItem(Vector3d, ItemStack)
            try {
                Method m = world.getClass().getMethod("spawnItem", Vector3d.class, ItemStack.class);
                m.invoke(world, pos, stack);
                return;
            } catch (NoSuchMethodException e) { /* Sigue al siguiente */ }

            // Intento 2: dropItem(Vector3d, ItemStack)
            try {
                Method m = world.getClass().getMethod("dropItem", Vector3d.class, ItemStack.class);
                m.invoke(world, pos, stack);
                return;
            } catch (NoSuchMethodException e) { /* Sigue al siguiente */ }

            // Intento 3: Si nada funciona, imprimimos qué métodos tiene el mundo para investigar
            System.out.println("[WorldManager] No se encontró método de drop. Revisa la consola para ver métodos disponibles.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getHighestBlockY(World world, int x, int z) {
        for (int y = 150; y > 0; y--) {
            // Tu arreglo: Convertimos el bloque a String para comparar
            String blockId = String.valueOf(world.getBlock(x, y, z));

            // Hytale a veces devuelve "hytale:air", "air" o "Block{hytale:air}"
            if (blockId != null && !blockId.contains("air")) {
                return y;
            }
        }
        return 70;
    }
}