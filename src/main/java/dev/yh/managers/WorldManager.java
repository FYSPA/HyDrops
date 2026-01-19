package dev.yh.managers;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.BlockEntity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.yh.model.FallingDropComponent;
import dev.yh.utils.ItemUtils;
import dev.yh.utils.BlockUtils;

import java.util.List;

public class WorldManager {

    private static final WorldManager instance = new WorldManager();
    public static WorldManager getInstance() { return instance; }

    private static final int MAX_ITEMS_PER_DROP = 3;
    private static final double SPREAD_RADIUS = 2.0;

    public void spawnBlockDrop(World world, double x, double z, Player player) {
        // 1. Buscamos el suelo REAL (empezando desde 120 hacia abajo para evitar nubes/techo)
        int groundY = getHighestBlockY(world, (int) x, (int) z);

        // 2. Altura de SPAWN (Cielo)
        Vector3d skyPos = new Vector3d(x + 0.5, 150.0, z + 0.5);

        world.execute(() -> {
            try {
                var store = world.getEntityStore().getStore();
                var time = (com.hypixel.hytale.server.core.modules.time.TimeResource)
                        store.getResource(TimeResource.getResourceType());

                var holder = BlockEntity.assembleDefaultBlockEntity(time, "Furniture_Tavern_Chest_Small", skyPos);

                var type = FallingDropComponent.getComponentType();
                if (type != null) {
                    // Seteamos el targetY REAL (el suelo que encontramos)
                    holder.addComponent(type, new FallingDropComponent((double) groundY));

                    if (player != null) {
                        player.sendMessage(Message.raw("§6[HyDrops] §eSuministro creado a Y:150. Debe bajar hasta Y:" + groundY));
                    }
                }
                store.addEntity(holder, AddReason.SPAWN);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public void spawnPhysicalDrop(World world, double x, double z, List<String> itemStrings, Player playerContext) {
        int groundY = getHighestBlockY(world, (int) x, (int) z);
        // Creamos la posición y llamamos a la Versión 2
        Vector3d spawnPos = new Vector3d(x + 0.5, groundY + 2.0, z + 0.5);
        spawnPhysicalDrop(world, spawnPos, itemStrings, playerContext);
    }

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
                playerContext.sendMessage(Message.raw("[WorldManager] Error: " + e.getMessage()));
                e.printStackTrace();
            }
        });
    }

    private int getHighestBlockY(World world, int x, int z) {
        // Escaneamos desde 120 hacia abajo para no detectar cosas en el cielo
        for (int y = 120; y > 0; y--) {
            Object block = world.getBlock(x, y, z);
            if (block != null) {
                String name = block.toString().toLowerCase();
                // SOLO aceptamos bloques que NO sean aire, nulos o nubes
                if (!name.contains("air") && !name.contains("null") && !name.contains("cloud") && !name.contains("void")) {
                    return y;
                }
            }
        }
        return 60; // Altura por defecto si es un mundo vacío
    }
}