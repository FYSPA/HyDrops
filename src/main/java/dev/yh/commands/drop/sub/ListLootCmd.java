package dev.yh.commands.drop.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import dev.yh.managers.LootManager;
import dev.yh.model.LootEntry;
import dev.yh.utils.PlayerUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ListLootCmd extends AbstractCommand {

    private final LootManager lootManager;
    private final OptionalArg<Integer> zoneArg;

    public ListLootCmd(LootManager lootManager) {
        super("list", "Muestra el contenido de la configuración de drops");
        this.lootManager = lootManager;

        // Opcional: /drop list --zone 1 (para ver solo una zona)
        this.zoneArg = withOptionalArg("zone", "Filtrar por zona", ArgTypes.INTEGER);
    }

    @Override
    protected boolean canGeneratePermission() {
        return false; // Desactiva el candado automático. ¡Público para todos!
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        Map<String, List<LootEntry>> allTables = lootManager.getLootTables();

        if (allTables.isEmpty()) {
            PlayerUtils.broadcast( "El sistema de loot está vacío o no se ha cargado.", "#F5DA0F");
            return CompletableFuture.completedFuture(null);
        }

        context.sender().sendMessage(Message.raw("--- Configuración de HyDrops ---").color("#EDF50F"));

        if (zoneArg.provided(context)) {
            String targetKey = "zone_" + zoneArg.get(context);
            if (allTables.containsKey(targetKey)) {
                printZone(context, targetKey, allTables.get(targetKey));
            } else {
                context.sender().sendMessage(Message.raw("La zona " + targetKey + " no existe en el JSON."). color("#F5DA0F"));
            }
        } else {
            allTables.forEach((zone, items) -> printZone(context, zone, items));
        }

        return CompletableFuture.completedFuture(null);
    }

    private void printZone(CommandContext context, String zoneName, List<LootEntry> items) {
        context.sender().sendMessage(Message.raw("ID: " + zoneName + " (" + items.size() + " items)"));
        for (LootEntry item : items) {
            context.sender().sendMessage(Message.raw(
                    " §8- §b" + item.getId() +
                            "(Cant: " + item.getMin() + "-" + item.getMax() + ") " +
                            "[Peso: " + item.getWeight() + "]"
            ));
        }
    }
}