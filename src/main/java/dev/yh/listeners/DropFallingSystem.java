package dev.yh.listeners;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
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
import java.lang.reflect.Method;

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

        falling.ticksExisted++;

        int ix = (int) Math.floor(pos.x);
        int iy = (int) Math.floor(pos.y - 0.5);
        int iz = (int) Math.floor(pos.z);

        Object blockObj = world.getBlock(ix, iy, iz);
        boolean isFloor = isSolidFloor(blockObj);
        if (falling.ticksExisted < 10) {
            isFloor = false;
        }

        // 3. LÓGICA DE MOVIMIENTO
        if (!isFloor && pos.y > 0) {
            double newY = pos.y - 0.5; // Velocidad de caída

            TransformComponent updatedTransform = new TransformComponent(
                    new Vector3d(pos.x, newY, pos.z),
                    transform.getRotation()
            );

            commandBuffer.putComponent(archetypeChunk.getReferenceTo(i), TransformComponent.getComponentType(), updatedTransform);
            commandBuffer.putComponent(archetypeChunk.getReferenceTo(i), FallingDropComponent.getComponentType(), falling);
        }
        else {
            String finalBlock = (blockObj != null) ? blockObj.toString() : "NULL";

            landing(world, ix, iy + 1, iz, archetypeChunk.getReferenceTo(i), commandBuffer, finalBlock);
        }
    }

    private boolean isSolidFloor(Object block) {
        if (block == null) return false;

        try {
            Method isAir = block.getClass().getMethod("isAir");
            if ((boolean) isAir.invoke(block)) return false;
        } catch (Exception ignored) {}

        String name = block.toString().toLowerCase();

        boolean isNotSolid = name.contains("air") ||
                name.contains("void") ||
                name.contains("cloud") ||
                name.contains("null") ||
                name.contains("env") ||
                name.contains("atmosphere") ||
                name.equals("0");

        return !isNotSolid;
    }

    private void landing(World world, int x, int y, int z, Ref<EntityStore> ref, CommandBuffer<EntityStore> cb, String blockName) {
        try {
            world.setBlock(x, y, z, "Furniture_Tavern_Chest_Small");
            registry.registerDrop(x, y, z);
            cb.removeEntity(ref, RemoveReason.REMOVE);

            Universe.get().getPlayers().forEach(p ->
                    p.sendMessage(Message.raw("[HyDrops] ¡Suministro aterrizado sobre" + "  " + x + "  " + y + "  " + z).color("#0F35F5"))
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}