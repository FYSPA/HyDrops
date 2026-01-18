package dev.yh.commands.drop.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import dev.yh.managers.LootManager;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TestLootCmd extends AbstractCommand {

    private final LootManager lootManager;
    private final RequiredArg<Integer> zoneArg;
    private final RequiredArg<Integer> rollsArg;

    public TestLootCmd(LootManager lootManager) {
        super("test", "Prueba la aleatorización de una zona");
        this.lootManager = lootManager;

        // Aquí los hacemos obligatorios para el test: /drop test <zona> <tiros>
        this.zoneArg = withRequiredArg("zone", "Zona a probar", ArgTypes.INTEGER);
        this.rollsArg = withRequiredArg("rolls", "Cantidad de tiradas", ArgTypes.INTEGER);
    }

    @Override
    protected boolean canGeneratePermission() {
        return false; // Desactiva el candado automático. ¡Público para todos!
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        int zoneId = zoneArg.get(context);
        int rolls = rollsArg.get(context);

        context.sender().sendMessage(Message.raw("§e[Simulación] Tirando dados " + rolls + " veces para Zona " + zoneId + "..."));

        // Generamos el loot (solo la lista de strings)
        List<String> results = lootManager.generateLootForZone(zoneId, rolls);

        if (results.isEmpty()) {
            context.sender().sendMessage(Message.raw("§cNo hay configuración para la Zona " + zoneId));
            return CompletableFuture.completedFuture(null);
        }

        // Imprimimos el resultado de la aleatorización
        for (String item : results) {
            context.sender().sendMessage(Message.raw("§7- Probabilidad ganadora: §a" + item));
        }

        context.sender().sendMessage(Message.raw("§e[Simulación] Finalizada."));
        return CompletableFuture.completedFuture(null);
    }
}