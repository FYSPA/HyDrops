package dev.yh.commands.drop.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.math.vector.Vector3d;

import dev.yh.managers.LootManager;
import dev.yh.managers.ZoneManager;
import dev.yh.managers.DropManager;
import dev.yh.utils.PlayerUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SpawnLootCmd extends AbstractCommand {

    private final LootManager lootManager;
    private final ZoneManager zoneManager;
    private final DropManager dropManager;

    private final OptionalArg<Integer> zoneArg;
    private final OptionalArg<Integer> amountArg;

    public SpawnLootCmd(LootManager loot, ZoneManager zone, DropManager world) {
        super("loot", "Spawnea los ITEMS directamente (Lluvia de loot)");
        this.lootManager = loot;
        this.zoneManager = zone;
        this.dropManager = world;

        this.zoneArg = withOptionalArg("zone", "ID de zona", ArgTypes.INTEGER);
        this.amountArg = withOptionalArg("amount", "Cantidad", ArgTypes.INTEGER);
    }

    @Override
    protected boolean canGeneratePermission() {
        return false; // Desactiva el candado automático. ¡Público para todos!
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!(context.sender() instanceof Player)) return CompletableFuture.completedFuture(null);

        Player player = (Player) context.sender();
        World world = player.getWorld();

        world.execute(() -> {
            Vector3d pos = PlayerUtils.getPos(player);
            if (pos == null) return;

            // 1. Calcular Zona y Cantidad
            int zoneId = zoneArg.provided(context) ? zoneArg.get(context) : zoneManager.getPlayerZoneId(player);
            int amount = amountArg.provided(context) ? amountArg.get(context) : 3;

            // 2. Generar Lista
            List<String> items = lootManager.generateLootForZone(zoneId, amount);

            if (items.isEmpty()) {
                player.sendMessage(Message.raw("§cError: No hay loot para la zona " + zoneId));
                return;
            }

            // 3. CORRECCIÓN: Usar el nuevo nombre y pasar el Vector3d pos
            dropManager.spawnLootBurst(world, pos, items, player);

            player.sendMessage(Message.raw("§b[HyDrops] §a¡Lluvia de items generada!"));
        });

        return CompletableFuture.completedFuture(null);
    }
}