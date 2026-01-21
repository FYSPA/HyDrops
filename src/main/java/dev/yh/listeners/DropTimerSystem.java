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

    // Ajustes de tiempo
    private float nextDropTimer = 60.0f; // Primer drop al minuto de iniciar
    private final float INTERVALO = 1200.0f; // 20 minutos entre drops

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

        // Solo ejecutamos la lógica una vez por tick global (i == 0)
        if (i != 0) return;

        nextDropTimer -= dt;

        if (nextDropTimer <= 0) {
            triggerRandomDrop();
            nextDropTimer = INTERVALO;
        }
    }

    private void triggerRandomDrop() {
        List<PlayerRef> playerRefs = Universe.get().getPlayers();
        if (playerRefs == null || playerRefs.isEmpty()) return;

        PlayerRef ref = playerRefs.get(random.nextInt(playerRefs.size()));
        Player target = PlayerUtils.getPlayerFromRef(ref);

        if (target != null) {
            Vector3d pPos = PlayerUtils.getPos(target);
            if (pPos != null) {
                double fx = pPos.x + (random.nextDouble() - 0.5) * 80;
                double fz = pPos.z + (random.nextDouble() - 0.5) * 80;

                // --- MENSAJE CON COORDENADAS ---
                // Redondeamos a (int) para que no salgan mil decimales en el chat
                String msg = String.format(
                        "§6§l[HyDrops] §e¡Suministros detectados cerca de §f%s §een §bX:%d Z:%d§e!",
                        target.getDisplayName(), (int)fx, (int)fz
                );

                PlayerUtils.broadcast(msg);

                // Lanzamos el drop
                dropManager.spawnFallingCrate(target.getWorld(), fx, fz, target);
            }
        }
    }
}