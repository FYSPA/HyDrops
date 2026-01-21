package dev.yh.listeners;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i; // Importante

import dev.yh.managers.DropRegistry;
import dev.yh.managers.LootManager;
import dev.yh.managers.ZoneManager;
import dev.yh.managers.DropManager;
import dev.yh.utils.PlayerUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.List;

public class DropBreakSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {

    private final LootManager lootManager;
    private final ZoneManager zoneManager;
    private final DropManager dropManager;
    private final DropRegistry dropRegistry;

    public DropBreakSystem(LootManager loot, ZoneManager zone, DropManager world, DropRegistry registry) {
        super(BreakBlockEvent.class);
        this.lootManager = loot;
        this.zoneManager = zone;
        this.dropManager = world;
        this.dropRegistry = registry;
    }

    @Override
    public Query getQuery() {
        return Query.and(Player.getComponentType());
    }

    @Override
    public void handle(int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                       @Nonnull Store<EntityStore> store,
                       @Nonnull CommandBuffer<EntityStore> commandBuffer,
                       @Nonnull BreakBlockEvent event) {

        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        try {
            Field posField = event.getClass().getDeclaredField("targetBlock");
            posField.setAccessible(true);
            Vector3i blockPos = (Vector3i) posField.get(event);

            // 2. FILTRO DE SEGURIDAD
            if (!dropRegistry.isModDrop(blockPos.x, blockPos.y, blockPos.z)) {
                return;
            }


            Field typeField = event.getClass().getDeclaredField("blockType");
            typeField.setAccessible(true);
            Object blockTypeObj = typeField.get(event);
            Field idField = blockTypeObj.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            String blockId = idField.get(blockTypeObj).toString();

            if (blockId.toLowerCase().contains("chest") || blockId.toLowerCase().contains("crate")) {

                player.sendMessage(Message.raw("[HyDrops] ¡Suministro abierto!").color("#13F50F"));

                int zoneId = zoneManager.getPlayerZoneId(player);
                List<String> loot = lootManager.generateLootForZone(zoneId, 5);

                if (!loot.isEmpty()) {
                    World world = player.getWorld();
                    Vector3d spawnPos = new Vector3d(blockPos.x + 0.5, blockPos.y + 0.5, blockPos.z + 0.5);
                    dropManager.spawnLootBurst(world, spawnPos, loot, player);
                    dropRegistry.unregisterDrop(blockPos.x, blockPos.y, blockPos.z);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}