package dev.yh.managers;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.yh.utils.ItemUtils;
import dev.yh.utils.BlockUtils;

import java.util.List;

public class WorldManager {

    private static final WorldManager instance = new WorldManager();
    public static WorldManager getInstance() { return instance; }

    private static final int MAX_ITEMS_PER_DROP = 3;
    private static final double SPREAD_RADIUS = 2.0;

    public void spawnBlockDrop(World world, double x, double z, Player player) {
        int groundY = getHighestBlockY(world, (int) x, (int) z);

        // Coordenadas enteras para setBlock
        int bx = (int) x;
        int by = groundY + 1;
        int bz = (int) z;

        if (player != null) {
            player.sendMessage(Message.raw("§e[HyDrops] §7Caja cayendo en: " + bx + "," + by + "," + bz));
        }

        world.execute(() -> {
            try {
                // 1. ELIMINAR CUALQUIER ENTIDAD FANTASMA PREVIA (Limpieza)
                // (Opcional, pero buena práctica si hay basura en esa coordenada)

                // 2. COLOCAR EL BLOQUE FÍSICO
                // Usamos nombres que sabemos que funcionan.
                // IMPORTANTE: Busca en tu reference_items.json el nombre exacto del cofre.
                // A veces es "Chest", "Chest_Wood", "Container_Chest".
                // Por ahora usaremos "Chest" que suele ser el estándar, o un bloque sólido visible.

                String blockId = "Furniture_Tavern_Chest_Small"; // Intenta con "Chest" o "hytale:Chest"

                // Si tienes dudas del nombre, usa "Dirt" para probar que la física funciona
                // String blockId = "Dirt";

                // Usamos la API directa de setBlock (Tu PrefabUtils logic)
                if (!blockId.contains(":") && !blockId.contains("_")) {
                    // Si es un nombre simple, a veces necesita hytale:
                    blockId = blockId;
                }

                // Forzamos la colocación del bloque
                world.setBlock(bx, by, bz, blockId);

                // DEBUG: Avisar si funcionó
                System.out.println("Bloque físico colocado: " + blockId);

            } catch (Exception e) {
                System.out.println("Error colocando bloque físico: " + e.getMessage());
            }
        });
    }

    // =========================================================
    // FASE 2: SPAWN DE ITEMS (LOOT)
    // =========================================================

    /**
     * Versión 1: Para comandos de admin (Calcula la altura automáticamente).
     */
    public void spawnPhysicalDrop(World world, double x, double z, List<String> itemStrings, Player playerContext) {
        int groundY = getHighestBlockY(world, (int) x, (int) z);
        // Creamos la posición y llamamos a la Versión 2
        Vector3d spawnPos = new Vector3d(x + 0.5, groundY + 2.0, z + 0.5);
        spawnPhysicalDrop(world, spawnPos, itemStrings, playerContext);
    }

    /**
     * Versión 2: Para el Listener (Usa una posición exacta).
     * AQUÍ ESTÁ LA LÓGICA DE DIVISIÓN DE STACKS.
     */
    public void spawnPhysicalDrop(World world, Vector3d spawnPos, List<String> itemStrings, Player playerContext) {
        world.execute(() -> {
            try {
                Store<EntityStore> store = playerContext.getReference().getStore();

                for (String entry : itemStrings) {
                    String[] parts = entry.split("x ");
                    if (parts.length < 2) continue;

                    int totalQuantity = Integer.parseInt(parts[0]);
                    String itemId = parts[1].trim();

                    // --- LÓGICA DE DIVISIÓN DE STACKS ---
                    while (totalQuantity > 0) {
                        int batchSize = Math.min(totalQuantity, MAX_ITEMS_PER_DROP);
                        totalQuantity -= batchSize;

                        ItemStack stack = new ItemStack(itemId, batchSize);

                        if (stack.isValid() && !stack.isEmpty()) {
                            // Usamos ItemUtils con la posición exacta y el radio
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

    // =========================================================
    // UTILIDADES
    // =========================================================
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