package dev.yh.commands.drop.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.math.vector.Vector3d;

import dev.yh.managers.DropManager;
import dev.yh.utils.PlayerUtils;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class SpawnBlockCmd extends AbstractCommand {

    private final DropManager dropManager;
    private final OptionalArg<Double> xArg;
    private final OptionalArg<Double> zArg;

    public SpawnBlockCmd(DropManager world) {
        super("block", "Spawnea la CAJA del drop instantáneamente");
        this.dropManager = world;
        this.xArg = withOptionalArg("x", "Coordenada X", ArgTypes.DOUBLE);
        this.zArg = withOptionalArg("z", "Coordenada Z", ArgTypes.DOUBLE);
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
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

            // LLAMADA INSTANTÁNEA AL DROP
            dropManager.spawnFallingCrate(world, tx, tz, player);

            PlayerUtils.broadcast("[HyDrops] ¡Caja enviada desde el cielo!", "#0FF516");
        });

        return CompletableFuture.completedFuture(null);
    }
}