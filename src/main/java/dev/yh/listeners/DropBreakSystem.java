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

import dev.yh.managers.LootManager;
import dev.yh.managers.ZoneManager;
import dev.yh.managers.WorldManager;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.List;

public class DropBreakSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {

    private final LootManager lootManager;
    private final ZoneManager zoneManager;
    private final WorldManager worldManager;

    public DropBreakSystem(LootManager loot, ZoneManager zone, WorldManager world) {
        super(BreakBlockEvent.class);
        this.lootManager = loot;
        this.zoneManager = zone;
        this.worldManager = world;
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

        // 1. OBTENER COORDENADAS POR REFLEXIÓN (A prueba de fallos)
        Vector3d pos = getEventCoordinates(event);

        if (pos == null) {
            // Si falla, imprimimos los campos para investigar (SOLO DEBUG)
            System.out.println("--- CAMPOS DEL EVENTO ---");
            for(Field f : event.getClass().getDeclaredFields()) {
                System.out.println(f.getName() + " -> " + f.getType().getSimpleName());
            }
            return;
        }

        // 2. VERIFICAR BLOQUE USANDO EL MUNDO
        // No le preguntamos al evento, le preguntamos al mundo qué hay en esa posición
        World world = player.getWorld();
        Object blockObj = world.getBlock((int)pos.x, (int)pos.y, (int)pos.z);
        String blockId = (blockObj != null) ? blockObj.toString().toLowerCase() : "aire";

        // 3. LOGICA DEL DROP
        // Verifica si es un cofre (o el bloque que hayas puesto)
        if (blockId.contains("chest")) {

            player.sendMessage(Message.raw("§6[HyDrops] §a¡Suministro abierto!"));

            // Calcular y Soltar Loot
            int zoneId = zoneManager.getPlayerZoneId(player);
            List<String> loot = lootManager.generateLootForZone(zoneId, 5);

            if (!loot.isEmpty()) {
                Vector3d centerPos = new Vector3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
                worldManager.spawnPhysicalDrop(world, centerPos, loot, player);
            }
        }
    }

    /**
     * Este método busca dentro del evento cualquier variable que parezca una coordenada.
     */
    private Vector3d getEventCoordinates(Object event) {
        try {
            Class<?> clazz = event.getClass();
            double x = 0, y = 0, z = 0;
            boolean found = false;

            // Buscamos campos públicos llamados x, y, z o pos/position
            for (Field f : clazz.getFields()) { // Campos públicos
                String name = f.getName().toLowerCase();

                // Si encontramos un objeto de posición directo (Vector3i o Vector3d)
                if (name.contains("pos") && !f.getType().isPrimitive()) {
                    Object vector = f.get(event);
                    // Aquí asumimos que el vector tiene métodos getX/Y/Z o campos x/y/z
                    // Simplificación: usaremos el toString para debuggear si lo encuentra
                    System.out.println("[Debug] Encontrado campo posición: " + name + " -> " + vector);
                    // Si puedes, intenta castear esto. Por ahora, sigamos buscando x,y,z sueltos.
                }

                // Buscamos coordenadas sueltas (int o double)
                if (f.getType() == int.class || f.getType() == double.class) {
                    if (name.equals("x")) { x = ((Number)f.get(event)).doubleValue(); found = true; }
                    if (name.equals("y")) { y = ((Number)f.get(event)).doubleValue(); }
                    if (name.equals("z")) { z = ((Number)f.get(event)).doubleValue(); }
                }
            }

            if (found) return new Vector3d(x, y, z);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}