package dev.yh.commands.drop;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import dev.yh.commands.drop.sub.ListLootCmd;
import dev.yh.commands.drop.sub.ReloadLootCmd;
import dev.yh.commands.drop.sub.SpawnDropCmd;
import dev.yh.commands.drop.sub.TestLootCmd;
import dev.yh.managers.LootManager;
import dev.yh.managers.ZoneManager;
import dev.yh.managers.WorldManager;

public class DropCollection extends AbstractCommandCollection {

    // CONSTRUCTOR: Pide los dos managers
    public DropCollection(
            LootManager loot,
            ZoneManager zone,
            WorldManager worldM

            ) {
        super("drop", "Comandos del sistema de Ark Drops");

        // Creamos el subcomando pasándole todo lo que necesita
        this.addSubCommand(new SpawnDropCmd(loot, zone, worldM));
        this.addSubCommand(new TestLootCmd(loot));
        this.addSubCommand(new ListLootCmd(loot));
        this.addSubCommand(new ReloadLootCmd(loot));
    }
}