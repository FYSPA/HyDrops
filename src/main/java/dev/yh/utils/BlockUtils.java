package dev.yh.utils;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.entity.entities.BlockEntity; // Importante
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class BlockUtils {

    /**
     * Spawnea un bloque usando el sistema ECS (Igual que el comando nativo /spawnblock).
     */
    public static void spawnBlockEntity(World world, Vector3d pos, String blockId) {
        if (world == null) return;

        // Aseguramos el prefijo
        if (!blockId.contains(":")) blockId = blockId;

        try {
            // 1. OBTENER RECURSO DE TIEMPO (Requisito del motor)
            // Extraído de: world.getEntityStore().getStore().getResource(...)
            TimeResource timeResource = (TimeResource) world.getEntityStore().getStore()
                    .getResource(TimeResource.getResourceType());

            // 2. ENSAMBLAR LA ENTIDAD DEL BLOQUE
            // Esto crea el "Holder" con los componentes base
            Holder<EntityStore> blockEntityHolder = BlockEntity.assembleDefaultBlockEntity(
                    timeResource,
                    blockId,
                    pos
            );

            // 3. CONFIGURAR POSICIÓN Y ROTACIÓN
            TransformComponent transform = (TransformComponent) blockEntityHolder.ensureAndGetComponent(TransformComponent.getComponentType());
            transform.setPosition(pos);
            transform.setRotation(Vector3f.ZERO); // Rotación estándar

            // 4. AÑADIR AL MUNDO (SPAWN REAL)
            world.getEntityStore().getStore().addEntity(blockEntityHolder, AddReason.SPAWN);

            System.out.println("[BlockUtils] BlockEntity spawneada: " + blockId + " en " + pos);

        } catch (Exception e) {
            System.err.println("[BlockUtils] Error crítico spawneando BlockEntity: " + e.getMessage());
            e.printStackTrace();
        }
    }
}