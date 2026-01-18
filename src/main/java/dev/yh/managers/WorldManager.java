package dev.yh.managers;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.yh.utils.ItemUtils;

import java.util.List;

public class WorldManager {

    private static final WorldManager instance = new WorldManager();
    public static WorldManager getInstance() { return instance; }

    // Configuración: ¿Cuántos items máximo por montoncito visual?
    private static final int MAX_ITEMS_PER_DROP = 3;
    // Configuración: ¿Qué tan esparcidos? (en bloques)
    private static final double SPREAD_RADIUS = 2.0;

    public void spawnPhysicalDrop(World world, double x, double z, List<String> itemStrings, Player playerContext) {
        int groundY = getHighestBlockY(world, (int) x, (int) z);
        // Elevamos un poco más la altura (Y+2) para que caigan mejor
        Vector3d spawnPos = new Vector3d(x + 0.5, groundY + 2.0, z + 0.5);

        world.execute(() -> {
            try {
                Store<EntityStore> store = playerContext.getReference().getStore();

                for (String entry : itemStrings) {
                    String[] parts = entry.split("x ");
                    if (parts.length < 2) continue;

                    int totalQuantity = Integer.parseInt(parts[0]);
                    String itemId = parts[1].trim();

                    // --- LÓGICA DE DIVISIÓN DE STACKS ---
                    // Mientras nos queden items por soltar...
                    while (totalQuantity > 0) {
                        // Decidimos cuánto soltar en este montoncito
                        // Tomamos el mínimo entre lo que queda y el máximo permitido (ej: 3)
                        int batchSize = Math.min(totalQuantity, MAX_ITEMS_PER_DROP);

                        // Restamos al total
                        totalQuantity -= batchSize;

                        // Creamos el stack pequeño
                        ItemStack stack = new ItemStack(itemId, batchSize);

                        if (stack.isValid() && !stack.isEmpty()) {
                            // Llamamos a la utilidad con Dispersión
                            ItemUtils.dropItem(store, stack, spawnPos, SPREAD_RADIUS);
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("[WorldManager] Error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private int getHighestBlockY(World world, int x, int z) {
        for (int y = 150; y > 0; y--) {
            try {
                Object block = world.getBlock(x, y, z);
                if (block != null) {
                    String name = block.toString().toLowerCase();
                    if (!name.contains("air") && !name.contains("void") && !name.contains("null")) {
                        return y;
                    }
                }
            } catch (Exception e) {}
        }
        return 80;
    }
}