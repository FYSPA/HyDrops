package dev.yh.commands.drop;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import dev.yh.commands.drop.sub.SpawnDropCmd;
import dev.yh.managers.LootManager;
import dev.yh.managers.ZoneManager;
import dev.yh.managers.WorldManager;

public class DropCollection extends AbstractCommandCollection {

    // CONSTRUCTOR: Pide los dos managers
    public DropCollection(LootManager lootManager, ZoneManager zoneManager, WorldManager worldManager ) {
        super("drop", "Comandos del sistema de Ark Drops");

        // Creamos el subcomando pasándole todo lo que necesita
        this.addSubCommand(new SpawnDropCmd(lootManager, zoneManager, worldManager));
    }
}