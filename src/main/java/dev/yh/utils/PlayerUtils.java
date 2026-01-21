package dev.yh.utils;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;

import java.util.List;
import java.util.UUID;

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


    public static UUID getUuid(Player player) {
        try {
            var entityRef = player.getReference();
            var store = entityRef.getStore();
            UUIDComponent uuidComp = (UUIDComponent) store.getComponent(entityRef, UUIDComponent.getComponentType());
            return (uuidComp != null) ? uuidComp.getUuid() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static Player getPlayerFromRef(PlayerRef ref) {
        try {
            var entityRef = ref.getReference();
            if (entityRef != null && entityRef.isValid()) {
                return entityRef.getStore().getComponent(entityRef, Player.getComponentType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void broadcast(String text) {
        // Obtenemos el Universo
        Universe universe = Universe.get();

        // Buscamos el mundo principal (usualmente llamado "world" u "orbis")
        // Si no tienes el nombre, podemos usar el mundo de la primera referencia que encontremos
        var worlds = universe.getWorlds();
        if (worlds.isEmpty()) return;

        World mainWorld = worlds.values().iterator().next();

        // FORZAMOS la ejecución dentro del hilo del mundo
        mainWorld.execute(() -> {
            Message message = Message.raw(text);

            // Ahora que estamos "dentro" del mundo, el Store está desbloqueado
            for (PlayerRef ref : universe.getPlayers()) {
                Player p = getPlayerFromRef(ref); // Ahora esto NO dará null
                if (p != null) {
                    p.sendMessage(message);
                }
            }
        });
    }

    public static void sendMessageTo(String playerName, String text) {
        // En lugar de bucles for, usamos el buscador oficial que es más rápido
        PlayerRef ref = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT_IGNORE_CASE);
        if (ref != null && ref.isValid()) {
            Player p = getPlayerFromRef(ref);
            if (p != null) {
                p.sendMessage(Message.raw(text));
            }
        }
    }
}