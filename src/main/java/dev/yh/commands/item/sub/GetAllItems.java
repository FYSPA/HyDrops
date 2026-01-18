package dev.yh.commands.item.sub;

import javax.annotation.Nonnull;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import dev.yh.managers.ItemManager; // Importamos el manager

public class GetAllItems extends CommandBase {

    private final ItemManager itemManager;

    // INYECCIÓN DE DEPENDENCIA: Pedimos el manager en el constructor
    public GetAllItems(ItemManager itemManager){
        super("all", "Lista todos los items");
        this.itemManager = itemManager;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        context.sendMessage(Message.raw("Te voa imprimir items!"));

        // Usamos el manager inyectado. ¡Mucho más limpio!
        var items = itemManager.getLoadedItems();

        if (items.isEmpty()) {
            context.sendMessage(Message.raw("No se encontraron items cargados."));
            return;
        }

        items.forEach((id, item) -> {
            // id = "hytale:iron_sword"
            String nombre = item.getTranslationKey(); // Ojo: a veces esto devuelve null si no tiene traducción
            context.sendMessage(Message.raw(id + ": " + (nombre != null ? nombre : "Sin nombre")));
        });
    }
}