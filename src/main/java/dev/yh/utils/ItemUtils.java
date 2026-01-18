package dev.yh.utils;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;

import java.util.concurrent.ThreadLocalRandom;

public class ItemUtils {

    /**
     * Spawnea un item con dispersión física.
     * @param spreadRadius Qué tan lejos del centro pueden aparecer (ej: 1.5 bloques)
     */
    public static void dropItem(Store<EntityStore> store, ItemStack stack, Vector3d centerPos, double spreadRadius) {
        if (store == null || stack == null || !stack.isValid() || stack.isEmpty()) return;

        ThreadLocalRandom rng = ThreadLocalRandom.current();

        // 1. DISPERSIÓN DE POSICIÓN (Para que no nazcan todos pegados)
        // Calculamos un offset aleatorio alrededor del centro
        double offsetX = (rng.nextDouble() - 0.5) * spreadRadius * 2; // -R a +R
        double offsetZ = (rng.nextDouble() - 0.5) * spreadRadius * 2;

        // La posición final de nacimiento
        Vector3d finalPos = new Vector3d(
                centerPos.x + offsetX,
                centerPos.y,
                centerPos.z + offsetZ
        );

        // 2. DISPERSIÓN DE VELOCIDAD (Para que salten hacia afuera)
        // Hacemos que la velocidad empuje lejos del centro
        float velX = (float) (offsetX * 0.2); // Empuja en dirección del offset
        float velY = 0.3f + (rng.nextFloat() * 0.2f); // Siempre saltito arriba
        float velZ = (float) (offsetZ * 0.2);

        // 3. GENERAR ENTIDAD
        Holder<EntityStore> itemHolder = ItemComponent.generateItemDrop(
                store,
                stack,
                finalPos,
                Vector3f.ZERO,
                velX, velY, velZ
        );

        // 4. CONFIGURAR DELAY
        ItemComponent itemComp = (ItemComponent) itemHolder.getComponent(ItemComponent.getComponentType());
        if (itemComp != null) {
            // ANTES: 20 ticks (1 segundo).
            // AHORA: 5 ticks (0.25 seg) -> Casi instantáneo pero evita que lo recojas antes de verlo caer
            itemComp.setPickupDelay(5);
        }

        store.addEntity(itemHolder, AddReason.SPAWN);
    }
}