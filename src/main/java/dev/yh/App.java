package dev.yh;

import java.util.logging.Level;
import javax.annotation.Nonnull;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import dev.yh.managers.ItemManager; // Importamos el manager
import dev.yh.managers.LootManager;
import dev.yh.managers.WorldManager;
import dev.yh.managers.ZoneManager;
import dev.yh.core.ModRegistry;

public class App extends JavaPlugin {

    // Definimos el manager como propiedad de la clase para que viva mientras el plugin viva
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

        this.lootManager = new LootManager();
        this.zoneManager = new ZoneManager();
        this.itemManager = new ItemManager();
        this.worldManager = new WorldManager();

        // 2. INICIALIZAR EL REGISTRADOR
        this.modRegistry = new ModRegistry(lootManager, zoneManager, itemManager, worldManager);

        // 3. REGISTRO AUTOMATIZADO
        // Le pasamos el registro de Hytale y él hace todo el trabajo sucio
        modRegistry.registerAllCommands(this.getCommandRegistry());
    }

    @Override
    protected void start(){
        getLogger().at(Level.INFO).log("Plugin iniciado correctamente.");
    }

    @Override
    protected void shutdown(){
        getLogger().at(Level.INFO).log("Apagando plugin...");
        this.itemManager = null; // Limpieza (opcional, Java lo hace solo, pero es buena práctica en plugins grandes)
    }
}