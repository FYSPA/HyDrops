package dev.yh.utlis;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;

public class PlayerUtils {

    /**
     * Obtiene la posición actual de un jugador de forma segura.
     */
    public static Vector3d getPlayerPosition(Player player) {
        try {
            var entityRef = player.getReference();
            if (entityRef != null && entityRef.isValid()) {
                var store = entityRef.getStore();
                TransformComponent transform = (TransformComponent) store.getComponent(
                        entityRef,
                        TransformComponent.getComponentType()
                );

                if (transform != null) {
                    return transform.getPosition();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}