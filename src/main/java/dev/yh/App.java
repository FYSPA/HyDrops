package dev.yh;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import dev.yh.listeners.DropBreakSystem;
import dev.yh.listeners.DropFallingSystem;
import dev.yh.listeners.DropTimerSystem;
import dev.yh.managers.*;
import dev.yh.core.ModRegistry;
import dev.yh.model.FallingDropComponent;
import dev.yh.model.LootEntry;

public class App extends JavaPlugin {

    private ItemManager itemManager;
    private ZoneManager zoneManager;
    private LootManager lootManager;
    private ModRegistry modRegistry;
    private DropManager dropManager;
    private DropRegistry dropRegistry;

    public App(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        getLogger().at(Level.INFO).log("Iniciando HyDrops Setup...");

        // 1. INICIALIZAR MANAGERS (Los cerebros)
        LootConfigLoader loader = new LootConfigLoader();
        Map<String, List<LootEntry>> data = loader.load();

        this.lootManager = new LootManager(data);
        this.zoneManager = new ZoneManager();
        this.itemManager = new ItemManager();
        this.dropManager = new DropManager();
        this.dropRegistry = new DropRegistry();

        // 2. REGISTRAR COMPONENTE ECS (La etiqueta)
        var type = getEntityStoreRegistry().registerComponent(
                FallingDropComponent.class,
                "HyDrop:Falling",
                FallingDropComponent.CODEC
        );
        if (type != null) {
            FallingDropComponent.setComponentType(type);
        }

        // 3. REGISTRAR SISTEMAS ECS (Los motores)
        // El reloj nativo que decide cuándo cae un drop
        this.getEntityStoreRegistry().registerSystem(new DropTimerSystem(dropManager));

        // El sistema físico que baja los bloques
        this.getEntityStoreRegistry().registerSystem(new DropFallingSystem(dropRegistry));

        // El sistema que detecta cuando se rompe un cofre del mod
        this.getEntityStoreRegistry().registerSystem(new DropBreakSystem(lootManager, zoneManager, dropManager, dropRegistry));

        // 4. REGISTRAR COMANDOS
        this.modRegistry = new ModRegistry(lootManager, zoneManager, itemManager, dropManager);
        this.modRegistry.registerAllCommands(this.getCommandRegistry());

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