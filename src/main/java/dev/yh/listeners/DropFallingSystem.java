package dev.yh.listeners;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.world.World;

import dev.yh.managers.DropRegistry;
import dev.yh.model.FallingDropComponent;

import javax.annotation.Nonnull;

public class DropFallingSystem extends EntityTickingSystem<EntityStore> {
    private final DropRegistry registry;

    public DropFallingSystem(DropRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Query getQuery() {
        return Query.and(
                TransformComponent.getComponentType(),
                FallingDropComponent.getComponentType()
        );
    }

    @Override
    public void tick(float dt, int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                     @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        TransformComponent transform = archetypeChunk.getComponent(i, TransformComponent.getComponentType());
        FallingDropComponent falling = archetypeChunk.getComponent(i, FallingDropComponent.getComponentType());

        if (transform == null || falling == null) return;

        Vector3d pos = transform.getPosition();
        World world = store.getExternalData().getWorld();

        // LOG DE DEBUG (Para ver en la consola si el sistema está encontrando la entidad)
        // System.out.println("Procesando caída de entidad en Y: " + pos.y);

        // 1. Lógica de caída
        if (pos.y > falling.targetY + 0.1) {
            double newY = pos.y - 0.5; // Velocidad de caída

            // --- EL CAMBIO CLAVE ---
            // Creamos un NUEVO TransformComponent con la nueva posición
            // Conservamos la rotación y escala originales
            TransformComponent updatedTransform = new TransformComponent(
                    new Vector3d(pos.x, newY, pos.z), // Posición
                    transform.getRotation()           // Rotación
            );

            // Usamos el commandBuffer para "empujar" el cambio al motor del juego
            commandBuffer.putComponent(archetypeChunk.getReferenceTo(i), TransformComponent.getComponentType(), updatedTransform);

        }
        // 2. ATERRIZAJE
        else {
            try {
                int ix = (int)Math.floor(pos.x);
                int iy = (int)falling.targetY;
                int iz = (int)Math.floor(pos.z);

                world.setBlock(ix, iy, iz, "Furniture_Tavern_Chest_Small");
                registry.registerDrop(ix, iy, iz);

                commandBuffer.removeEntity(archetypeChunk.getReferenceTo(i), RemoveReason.REMOVE);

                Universe.get().getPlayers().forEach(p ->
                        p.sendMessage(Message.raw("§6§l[HyDrops] §a¡Suministro aterrizado en X:" + ix + " Z:" + iz + "!"))
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}