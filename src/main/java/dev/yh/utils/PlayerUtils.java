package dev.yh.utils;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;

public class PlayerUtils {

    public static Vector3d getPos(Player player) {
        try {
            // 1. Validar que el jugador y su referencia existan
            var entityRef = player.getReference();
            if (entityRef == null || !entityRef.isValid()) {
                return null;
            }

            var store = entityRef.getStore();
            if (store == null) return null;

            // 2. Intentar obtener el componente de transformación
            TransformComponent transform = (TransformComponent) store.getComponent(
                    entityRef,
                    TransformComponent.getComponentType()
            );

            if (transform != null) {
                return transform.getPosition();
            }

            // 3. PLAN B: Algunas versiones de Hytale permiten esto si lo anterior falla
            // return player.getTransform().getPosition();

        } catch (Exception e) {
            System.err.println("[PlayerUtils] Error al obtener posición: " + e.getMessage());
        }
        return null;
    }
}