package dev.yh;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import dev.yh.listeners.DropBreakSystem;
import dev.yh.managers.*;
import dev.yh.core.ModRegistry;
import dev.yh.model.LootEntry;

public class App extends JavaPlugin {

    private ItemManager itemManager;
    private ZoneManager zoneManager;
    private LootManager lootManager;
    private ModRegistry modRegistry;
    private WorldManager worldManager;

    public App(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        getLogger().at(Level.INFO).log("Starting SETUP phase for %s...", getManifest().getName());
        LootConfigLoader loader = new LootConfigLoader();
        Map<String, List<LootEntry>> data = loader.load();

        this.lootManager = new LootManager(data);
        this.zoneManager = new ZoneManager();
        this.itemManager = new ItemManager();
        this.worldManager = new WorldManager();

        this.modRegistry = new ModRegistry(lootManager, zoneManager, itemManager, worldManager);
        modRegistry.registerAllCommands(this.getCommandRegistry());

        DropBreakSystem dropSystem = new DropBreakSystem(lootManager, zoneManager, worldManager);
        this.getEntityStoreRegistry().registerSystem(dropSystem);

    }

    @Override
    protected void start(){
        getLogger().at(Level.INFO).log("Plugin iniciado correctamente.");
    }

    @Override
    protected void shutdown(){
        getLogger().at(Level.INFO).log("Apagando plugin...");
        this.itemManager = null;
    }
}