package dev.yh.commands.drop.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import dev.yh.managers.LootConfigLoader;
import dev.yh.managers.LootManager;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.concurrent.CompletableFuture;

public class ReloadLootCmd extends AbstractCommand {
    private final LootManager lootManager;

    public ReloadLootCmd(LootManager lootManager) {
        super("reload", "Recarga la configuración del JSON sin reiniciar");
        this.lootManager = lootManager;
    }

    @Override
    protected boolean canGeneratePermission() {
        return false; // Desactiva el candado automático. ¡Público para todos!
    }

    @NullableDecl
    @Override
    protected CompletableFuture<Void> execute(CommandContext context) {
        // Volvemos a usar el loader para leer el disco
        LootConfigLoader loader = new LootConfigLoader();
        lootManager.reloadData(loader.load());

        context.sender().sendMessage(Message.raw("§a[HyDrops] ¡Configuración recargada con éxito!"));
        return CompletableFuture.completedFuture(null);
    }


}
