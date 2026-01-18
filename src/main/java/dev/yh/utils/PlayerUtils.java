package dev.yh.utils;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;

public class PlayerUtils {

    /**
     * Extrae la posición (Vector3d) de un jugador usando el sistema de componentes.
     */
    public static Vector3d getPos(Player player) {
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
            // Loguear error opcionalmente
        }
        return null;
    }
}