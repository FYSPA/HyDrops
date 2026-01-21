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
import dev.yh.utils.PlayerUtils;
import dev.yh.utils.WorldUtils;

import java.util.List;

public class DropManager {

    private static final int MAX_ITEMS_PER_DROP = 3;
    private static final double SPREAD_RADIUS = 2.0;

    /**
     * Crea la entidad de la caja que caerá del cielo.
     */
    public void spawnFallingCrate(World world, double x, double z, Player player) {
        // Usamos la utilidad para la altura
        int groundY = WorldUtils.getHighestBlockY(world, (int) x, (int) z);
        Vector3d skyPos = new Vector3d(x + 0.5, 150.0, z + 0.5);

        world.execute(() -> {
            try {
                Store<EntityStore> store = world.getEntityStore().getStore();
                TimeResource time = (TimeResource) store.getResource(TimeResource.getResourceType());

                Holder<EntityStore> holder = BlockEntity.assembleDefaultBlockEntity(time, "Furniture_Tavern_Chest_Small", skyPos);

                var type = FallingDropComponent.getComponentType();
                if (type != null) {
                    holder.addComponent(type, new FallingDropComponent(0.0)); // 0.0 porque ahora detecta suelo solo
                    if (player != null) {
                        PlayerUtils.broadcast( "[HyDrops] ¡Suministro detectado en el cielo!", "#0FF52E");
                    }
                }
                store.addEntity(holder, AddReason.SPAWN);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    /**
     * Genera la explosión de ítems físicos.
     */
    public void spawnLootBurst(World world, Vector3d pos, List<String> itemStrings, Player playerContext) {
        world.execute(() -> {
            try {
                Store<EntityStore> store = playerContext.getReference().getStore();

                for (String entry : itemStrings) {
                    String[] parts = entry.split("x ");
                    if (parts.length < 2) continue;

                    int totalQuantity = Integer.parseInt(parts[0]);
                    String itemId = parts[1].trim();

                    while (totalQuantity > 0) {
                        int batchSize = Math.min(totalQuantity, MAX_ITEMS_PER_DROP);
                        totalQuantity -= batchSize;

                        ItemStack stack = new ItemStack(itemId, batchSize);
                        if (stack.isValid() && !stack.isEmpty()) {
                            ItemUtils.dropItem(store, stack, pos, SPREAD_RADIUS);
                        }
                    }
                }
            } catch (Exception e) {
                if (playerContext != null) PlayerUtils.broadcast("[DropManager] Error: " + e.getMessage(), "#F50F0F");
            }
        });
    }
}