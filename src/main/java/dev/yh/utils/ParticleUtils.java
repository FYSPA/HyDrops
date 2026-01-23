package dev.yh.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectList;

public class ParticleUtils {

    /**
     * Spawnea un efecto de partículas para todos los jugadores en un radio de 100 bloques.
     */
    public static void spawnGlobalEffect(String effectId, Vector3d position, Store<EntityStore> store) {
        try {
            // 1. Obtener el recurso espacial para buscar jugadores
            SpatialResource<Ref<EntityStore>, EntityStore> playerSpatial =
                    (SpatialResource) store.getResource(EntityModule.get().getPlayerSpatialResourceType());

            // 2. Obtener la lista temporal de referencias (ThreadLocal para rendimiento)
            ObjectList<Ref<EntityStore>> playersInRange = SpatialResource.getThreadLocalReferenceList();

            // 3. Buscar jugadores en un radio de 100 bloques
            playerSpatial.getSpatialStructure().collect(position, 100.0, playersInRange);

            // 4. Enviar el efecto a esos jugadores
            // Usamos rotación ZERO ya que es un faro vertical
            ParticleUtil.spawnParticleEffect(effectId, position, Vector3f.ZERO, playersInRange, store);

        } catch (Exception e) {
            // System.err.println("Error al spawnear particulas: " + e.getMessage());
        }
    }
}