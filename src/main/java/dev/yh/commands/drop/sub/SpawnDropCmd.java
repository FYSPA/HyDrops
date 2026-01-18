package dev.yh.commands.drop.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.math.vector.Vector3d;

import dev.yh.managers.LootManager;
import dev.yh.managers.ZoneManager;
import dev.yh.managers.WorldManager;

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
    @Nonnull
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {

        // 1. VALIDACIÓN: Solo permitimos que lo usen jugadores
        if (!(context.sender() instanceof Player)) {
            context.sender().sendMessage(Message.raw("§cEste comando solo puede ser ejecutado por un jugador dentro del mundo."));
            return CompletableFuture.completedFuture(null);
        }

        Player player = (Player) context.sender();
        World world = player.getWorld();

        // 2. OBTENER POSICIÓN (Lógica ECS de tu HomeManager)
        Vector3d pos = null;
        try {
            var entityRef = player.getReference();
            var store = entityRef.getStore();
            TransformComponent transform = (TransformComponent) store.getComponent(entityRef, TransformComponent.getComponentType());
            if (transform != null) {
                pos = transform.getPosition();
            }
        } catch (Exception e) {
            player.sendMessage(Message.raw("§cError al detectar tu posición física."));
            return CompletableFuture.completedFuture(null);
        }

        if (pos == null) return CompletableFuture.completedFuture(null);

        // 3. DETERMINAR ZONA
        int targetZone;
        if (zoneArg.provided(context)) {
            targetZone = zoneArg.get(context);
        } else {
            // Detección automática con tu ZoneManager
            targetZone = zoneManager.getPlayerZoneId(player);
            player.sendMessage(Message.raw("§7(Sistema) Zona detectada: §f" + targetZone));
        }

        // 4. GENERAR LOOT
        int amount = amountArg.provided(context) ? amountArg.get(context) : 3;
        List<String> items = lootManager.generateLootForZone(targetZone, amount);

        if (items.isEmpty()) {
            player.sendMessage(Message.raw("§cError: No hay loot para la Zona " + targetZone));
            return CompletableFuture.completedFuture(null);
        }

        // 5. SPAWN FÍSICO (LA LLUVIA DE OBJETOS)
        // Usamos tu WorldManager que busca el suelo y suelta los ItemStacks
        worldManager.spawnPhysicalDrop(world, pos.x, pos.z, items);

        player.sendMessage(Message.raw("§e[Drop Ark] §a¡Han caído " + amount + " objetos!"));

        return CompletableFuture.completedFuture(null);
    }
}