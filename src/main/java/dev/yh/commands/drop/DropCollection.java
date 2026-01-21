package dev.yh.commands.drop;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import dev.yh.commands.drop.sub.*;
import dev.yh.managers.LootManager;
import dev.yh.managers.ZoneManager;
import dev.yh.managers.DropManager;

public class DropCollection extends AbstractCommandCollection {

    // CONSTRUCTOR: Pide los dos managers
    public DropCollection(
            LootManager loot,
            ZoneManager zone,
            DropManager dropM

            ) {
        super("drop", "Comandos del sistema de Ark Drops");

        // Creamos el subcomando pasándole todo lo que necesita
        this.addSubCommand(new SpawnLootCmd(loot, zone, dropM));
        this.addSubCommand(new TestLootCmd(loot));
        this.addSubCommand(new ListLootCmd(loot));
        this.addSubCommand(new ReloadLootCmd(loot));
        this.addSubCommand(new SpawnBlockCmd(dropM));
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }
}