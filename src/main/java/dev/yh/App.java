package dev.yh;

import java.util.logging.Level;

import javax.annotation.Nonnull;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;


/**
 * Clase principal del Plugin (Punto de entrada).
 * Hereda de JavaPlugin, que a su vez implementa el ciclo de vida de PluginBase.
 */
public class App extends JavaPlugin {

    /**
     * Constructor del Plugin.
     */
    public App(@Nonnull JavaPluginInit init) {
        super(init);
    }


    /**
     * FASE 1: SETUP
     * Se ejecuta durante el arranque del servidor, antes de que el mundo (Universe) esté listo.
     * USO RECOMENDADO:
     * - Cargar archivos de configuración (JSON).
     * - Inicializar conexiones a bases de datos.
     * - Registrar nuevos tipos de Assets o Codecs.
     * - El acceso al mundo aquí es limitado porque aún se está cargando.
     */
    @Override
    protected void setup() {
        getLogger().at(Level.INFO).log("Starting SETUP phase for %s...", getManifest().getName());
        // void
    }

    /**
     * FASE 2: START (Activación)
     * Se ejecuta cuando el servidor está listo para recibir jugadores y el mundo ya existe.
     *
     * USO RECOMENDADO:
     * - Registrar comandos en el CommandRegistry.
     * - Registrar escuchadores de eventos (EventHandlers).
     * - Iniciar tareas programadas (Tasks).
     */
    @Override
    protected void start(){
        getLogger().at(Level.INFO).log("Beginning START phase. Registering active systems...");

    }

    /**
     * FASE 3: SHUTDOWN (Apagado)
     * Se ejecuta cuando el servidor se detiene o el plugin es desactivado.
     *
     * USO RECOMENDADO:
     * - Guardar datos pendientes en disco.
     * - Cerrar conexiones activas.
     * - El servidor hace limpieza automática de comandos y eventos después de esto.
     */
    @Override
    protected void shutdown(){
        getLogger().at(Level.INFO).log("Performing SHUTDOWN cleanup. Saving state...");
    }

}
