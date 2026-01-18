package dev.yh.commands.drop.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.math.vector.Vector3d;

import dev.yh.managers.WorldManager;
import dev.yh.utils.PlayerUtils;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class SpawnBlockCmd extends AbstractCommand {

    private final WorldManager worldManager;
    private final OptionalArg<Double> xArg;
    private final OptionalArg<Double> zArg;

    public SpawnBlockCmd(WorldManager world) {
        super("block", "Spawnea la CAJA del drop (el bloque físico)");
        this.worldManager = world;
        this.xArg = withOptionalArg("x", "Coordenada X", ArgTypes.DOUBLE);
        this.zArg = withOptionalArg("z", "Coordenada Z", ArgTypes.DOUBLE);
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

            double tx = xArg.provided(context) ? xArg.get(context) : pos.x;
            double tz = zArg.provided(context) ? zArg.get(context) : pos.z;

            // LLAMADA AL MANAGER DE BLOQUE
            worldManager.spawnBlockDrop(world, tx, tz, player);

            player.sendMessage(Message.raw("§e[HyDrops] §a¡Caja spawneada!"));
        });

        return CompletableFuture.completedFuture(null);
    }
}