package dev.yh;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import dev.yh.listeners.DropBreakSystem;
import dev.yh.listeners.DropFallingSystem;
import dev.yh.managers.*;
import dev.yh.core.ModRegistry;
import dev.yh.model.FallingDropComponent;
import dev.yh.model.LootEntry;

public class App extends JavaPlugin {

    private ItemManager itemManager;
    private ZoneManager zoneManager;
    private LootManager lootManager;
    private ModRegistry modRegistry;
    private WorldManager worldManager;
    private DropRegistry dropRegistry;

    public App(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        getLogger().at(Level.INFO).log("Starting SETUP phase for %s...", getManifest().getName());

        // 1. Inicializar Managers
        LootConfigLoader loader = new LootConfigLoader();
        Map<String, List<LootEntry>> data = loader.load();
        this.lootManager = new LootManager(data);
        this.zoneManager = new ZoneManager();
        this.itemManager = new ItemManager();
        this.worldManager = new WorldManager();
        this.dropRegistry = new DropRegistry(); // <--- Registro de seguridad

        // 2. Comandos
        this.modRegistry = new ModRegistry(lootManager, zoneManager, itemManager, worldManager);
        modRegistry.registerAllCommands(this.getCommandRegistry());

        // 3. Registro de Componentes ECS
        // Registramos esto ANTES que los sistemas para que no sea null
        // En tu setup() de App.java
        var type = getEntityStoreRegistry().registerComponent(
                FallingDropComponent.class,
                "HyDrop:Falling",
                FallingDropComponent.CODEC
        );

        if (type == null) {
            getLogger().at(java.util.logging.Level.SEVERE).log("No se pudo registrar el componente");
        } else {
            FallingDropComponent.setComponentType(type);
            getLogger().at(java.util.logging.Level.INFO).log("Componente registrado con ID: " + type.getIndex());
        }

        // 4. Registro de Sistemas ECS
        // El sistema que hace que caigan los bloques
        this.getEntityStoreRegistry().registerSystem(new DropFallingSystem(dropRegistry));

        // El sistema que detecta cuando se rompe un bloque
        DropBreakSystem breakSystem = new DropBreakSystem(lootManager, zoneManager, worldManager, dropRegistry);
        this.getEntityStoreRegistry().registerSystem(breakSystem);

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