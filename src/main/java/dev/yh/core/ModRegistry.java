package dev.yh.core;

import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import dev.yh.commands.drop.DropCollection;
import dev.yh.commands.item.ItemCollection;
import dev.yh.managers.ItemManager;
import dev.yh.managers.LootManager;
import dev.yh.managers.WorldManager;
import dev.yh.managers.ZoneManager;

public class ModRegistry {

    private final LootManager lootManager;
    private final ZoneManager zoneManager;
    private final ItemManager itemManager;
    private final WorldManager worldManager;

    // Recibimos los managers una sola vez
    public ModRegistry(LootManager loot, ZoneManager zone, ItemManager item, WorldManager worldManager) {
        this.lootManager = loot;
        this.zoneManager = zone;
        this.itemManager = item;
        this.worldManager = worldManager;
    }

    /**
     * Aquí registramos TODO. Si agregas 100 comandos, los pones aquí.
     * Tu App.java nunca cambiará.
     */
    public void registerAllCommands(CommandRegistry registry) {
        // 1. Drop Collection
        registry.registerCommand(new DropCollection(lootManager, zoneManager, worldManager));

        // 2. Item Collection
        registry.registerCommand(new ItemCollection(itemManager));

        // 3. Futuros comandos...
        // registry.registerCommand(new EconomyCollection(economyManager));

        System.out.println("[ModRegistry] Todos los comandos han sido registrados.");
    }
}