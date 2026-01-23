package dev.yh.listeners;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.math.vector.Vector3d;
import dev.yh.managers.DropManager;
import dev.yh.utils.PlayerUtils;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class DropTimerSystem extends EntityTickingSystem<EntityStore> {

    private final DropManager dropManager;
    private final Random random = new Random();

    // Tiempos (En segundos)
    private float nextDropTimer = 20.0f; // Primer drop al minuto
    private final float INTERVALO = 40.0f; // 20 minutos

    // --- COMPUERTA DE TIEMPO SEGURA ---
    private long lastTimestamp = 0;

    public DropTimerSystem(DropManager dropManager) {
        this.dropManager = dropManager;
    }

    @Override
    public Query getQuery() {
        return Query.and(Player.getComponentType());
    }

    @Override
    public void tick(float dt, int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                     @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        long now = System.currentTimeMillis();
        if (now - lastTimestamp < 45) {
            return;
        }
        lastTimestamp = now;
        nextDropTimer -= dt;

        if (nextDropTimer <= 0) {
            triggerRandomDrop();
            nextDropTimer = INTERVALO;
        }
    }

    private void triggerRandomDrop() {
        try {
            List<PlayerRef> playerRefs = Universe.get().getPlayers();
            if (playerRefs == null || playerRefs.isEmpty()) return;

            PlayerRef ref = playerRefs.get(random.nextInt(playerRefs.size()));
            Player target = PlayerUtils.getPlayerFromRef(ref);

            if (target != null) {
                Vector3d pPos = PlayerUtils.getPos(target);
                if (pPos != null) {
                    double fx = pPos.x + (random.nextDouble() - 0.5) * 100;
                    double fz = pPos.z + (random.nextDouble() - 0.5) * 100;

                    PlayerUtils.broadcast("[HyDrop] ¡Suministros detectados cayendo en X:" + (int)fx + " Z:" + (int)fz + "!", "#0FF516");
                    dropManager.spawnFallingCrate(target.getWorld(), fx, fz, target);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}