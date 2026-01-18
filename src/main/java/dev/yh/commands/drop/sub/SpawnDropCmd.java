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
import dev.yh.managers.WorldManager;
import dev.yh.utils.PlayerUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SpawnDropCmd extends AbstractCommand {

    private final LootManager lootManager;
    private final ZoneManager zoneManager;
    private final WorldManager worldManager;

    private final OptionalArg<Integer> zoneArg;
    private final OptionalArg<Integer> amountArg;

    public SpawnDropCmd(LootManager loot, ZoneManager zone, WorldManager world) {
        super("spawn", "Suelta items en tu posición actual según la zona");
        this.lootManager = loot;
        this.zoneManager = zone;
        this.worldManager = world;

        this.zoneArg = withOptionalArg("zone", "ID de zona manual", ArgTypes.INTEGER);
        this.amountArg = withOptionalArg("amount", "Cantidad de items", ArgTypes.INTEGER);
    }

    @Override
    protected boolean canGeneratePermission() {
        return false; // Desactiva el candado automático. ¡Público para todos!
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!(context.sender() instanceof Player)) {
            context.sender().sendMessage(Message.raw("§cSolo jugadores!"));
            return CompletableFuture.completedFuture(null);
        }

        Player player = (Player) context.sender();
        final World world = player.getWorld();

        // Ejecutamos dentro del hilo del mundo para mayor seguridad
        player.getWorld().execute(() -> {
            Vector3d pos = PlayerUtils.getPos(player);

            if (pos == null) {
                player.sendMessage(Message.raw("§cError al detectar tu posición física. Intentelo de nuevo."));
                return;
            }

            // --- Toda la lógica del comando se mueve aquí adentro ---
            int targetZone = zoneArg.provided(context) ? zoneArg.get(context) : zoneManager.getPlayerZoneId(player);
            int amount = amountArg.provided(context) ? amountArg.get(context) : 3;

            List<String> items = lootManager.generateLootForZone(targetZone, amount);

            if (items.isEmpty()) {
                player.sendMessage(Message.raw("§cNo hay loot para la zona " + targetZone));
                return;
            }

            // Soltamos los items
            worldManager.spawnPhysicalDrop(world, pos.x, pos.z, items, player);
            player.sendMessage(Message.raw("Cordenadas:"  + pos.x + "   " + pos.y + "   " + pos.z));

            player.sendMessage(Message.raw("§e[Drop Ark] §a¡Loot spawneado!"));
        });

        return CompletableFuture.completedFuture(null);
    }
}