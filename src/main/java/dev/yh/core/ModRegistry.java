package dev.yh.core;

import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import dev.yh.commands.drop.DropCollection;
import dev.yh.commands.item.ItemCollection;
import dev.yh.managers.ItemManager;
import dev.yh.managers.LootManager;
import dev.yh.managers.DropManager;
import dev.yh.managers.ZoneManager;

public class ModRegistry {

    private final LootManager lootManager;
    private final ZoneManager zoneManager;
    private final ItemManager itemManager;
    private final DropManager dropManager;


    public ModRegistry(LootManager loot, ZoneManager zone, ItemManager item, DropManager worldManager) {
        this.lootManager = loot;
        this.zoneManager = zone;
        this.itemManager = item;
        this.dropManager = worldManager;
    }

    public void registerAllCommands(CommandRegistry registry) {
        registry.registerCommand(new DropCollection(lootManager, zoneManager, dropManager));
        registry.registerCommand(new ItemCollection(itemManager));
        System.out.println("[ModRegistry] Todos los comandos han sido registrados.");
    }


}