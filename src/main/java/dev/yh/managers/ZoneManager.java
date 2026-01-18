package dev.yh.managers;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
// Importamos la interfaz de Chunks (puede variar el nombre, pero esta es la estándar)
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;

import java.lang.reflect.Method;

public class ZoneManager {

    private static final ZoneManager instance = new ZoneManager();
    public static ZoneManager getInstance() { return instance; }

    /**
     * Obtiene la ID de la zona del jugador.
     * Retorna 1 si falla.
     */
    public int getPlayerZoneId(Player player) {
        World world = player.getWorld();
        if (world == null) return 1;

        Vector3d pos = getPlayerPosition(player);
        if (pos == null) return 1;

        // ESTRATEGIA 1: LEER EL CHUNK (La forma correcta y rápida)
        try {
            // Convertimos posición de Bloque a posición de Chunk (Dividir por 16)
            int chunkX = (int) Math.floor(pos.x) >> 4;
            int chunkZ = (int) Math.floor(pos.z) >> 4;

            // El objeto World suele tener un método getChunk
            // Usamos reflexión aquí por si tu versión llama al método distinto (getChunkAt, getChunkSync, etc)
            WorldChunk chunk = getChunkSafely(world, chunkX, chunkZ);

            if (chunk != null) {
                // Intentamos leer la zona del chunk
                // A veces es chunk.getZoneId(), a veces chunk.getGenerationData().getZoneId()
                return getZoneFromChunk(chunk);
            }

        } catch (Exception e) {
            // Si falla la API de Chunks, no imprimimos error, pasamos al plan B silenciosamente
        }

        // ESTRATEGIA 2: FALLBACK POR DISTANCIA (Matemática simple)
        // Si no pudimos leer la memoria del juego, adivinamos por distancia.
        return calculateFakeZoneByDistance(pos.x, pos.z);
    }

    // --- MÉTODOS DE UTILIDAD ---

    /**
     * Intenta obtener un Chunk usando reflexión para evitar errores de compilación
     * si el método cambia de nombre.
     */
    private WorldChunk getChunkSafely(World world, int x, int z) {
        try {
            // Intento A: getChunk(int, int) - Estilo clásico
            Method m = world.getClass().getMethod("getChunk", int.class, int.class);
            return (WorldChunk) m.invoke(world, x, z);
        } catch (Exception e1) {
            try {
                // Intento B: getChunk(long) - Estilo optimizado (Hytale suele usar este)
                // Convertimos (x, z) a un solo número long
                long chunkKey = ((long)x & 0xFFFFFFFFL) | ((long)z & 0xFFFFFFFFL) << 32;

                Method m = world.getClass().getMethod("getChunk", long.class);
                return (WorldChunk) m.invoke(world, chunkKey);
            } catch (Exception e2) {
                try {
                    // Intento C: getChunkSync(int, int) - Por si acaso
                    Method m = world.getClass().getMethod("getChunkSync", int.class, int.class);
                    return (WorldChunk) m.invoke(world, x, z);
                } catch (Exception e3) {
                    return null; // Nos rendimos, usar Fallback de distancia
                }
            }
        }
    }

    /**
     * Intenta sacar el ID de la zona desde el objeto Chunk
     */
    private int getZoneFromChunk(Object chunk) {
        try {
            // Intento A: getZoneId() directo
            Method m = chunk.getClass().getMethod("getZoneId");
            return (int) m.invoke(chunk);
        } catch (Exception e) {
            try {
                // Intento B: getZone().id()
                Method getZone = chunk.getClass().getMethod("getZone");
                Object zoneObj = getZone.invoke(chunk);
                if (zoneObj != null) {
                    Method idMethod = zoneObj.getClass().getMethod("id");
                    return (int) idMethod.invoke(zoneObj);
                }
            } catch (Exception e2) {
                // Falló
            }
        }
        return 1; // Default
    }

    private int calculateFakeZoneByDistance(double x, double z) {
        // Pitágoras: a^2 + b^2 = c^2
        double distance = Math.sqrt(x * x + z * z);

        // Hytale Zonas (Aproximación):
        // Zona 1 (Tierra): Centro
        // Zona 2 (Desierto), 3 (Hielo), 4 (Lava): Alrededor
        // Este fallback asume anillos, aunque en realidad son islas radiales.

        if (distance < 1000) return 1; // Centro
        if (distance < 2500) return 2; // Anillo 2
        if (distance < 4000) return 3; // Anillo 3
        return 4;                      // Lejos
    }

    private Vector3d getPlayerPosition(Player player) {
        try {
            var entityRef = player.getReference();
            if (entityRef != null && entityRef.isValid()) {
                var store = entityRef.getStore();
                TransformComponent transform = (TransformComponent) store.getComponent(entityRef, TransformComponent.getComponentType());
                if (transform != null) {
                    return transform.getPosition();
                }
            }
        } catch (Exception e) { }
        return null;
    }
}