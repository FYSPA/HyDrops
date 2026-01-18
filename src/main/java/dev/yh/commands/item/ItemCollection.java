package dev.yh.commands.item;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import dev.yh.commands.item.sub.DumpItemsCmd;
import dev.yh.commands.item.sub.GetAllItems;
import dev.yh.managers.ItemManager;

public class ItemCollection extends AbstractCommandCollection {

    // Constructor recibe el Manager
    public ItemCollection(ItemManager itemManager) {
        super("items", "Items System");

        // Pasamos el manager hacia abajo (al subcomando)
        this.addSubCommand(new GetAllItems(itemManager));
        this.addSubCommand(new DumpItemsCmd());
    }

    @Override
    protected boolean canGeneratePermission() {
        return false; // Desactiva el candado automático. ¡Público para todos!
    }
}