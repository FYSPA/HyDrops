package dev.yh.commands.item.sub;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.particle.config.ParticleSystem; // Clase de tu doc
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DumpParticlesCmd extends AbstractCommand {

    private final Gson gson;

    public DumpParticlesCmd() {
        super("particles", "Exporta todos los IDs de partículas a un archivo JSON");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        context.sender().sendMessage(Message.raw("§b[HyDrops] §fBuscando sistemas de partículas..."));

        try {
            // 1. Obtener el almacén de sistemas de partículas
            AssetStore<String, ParticleSystem, ?> particleStore = AssetRegistry.getAssetStore(ParticleSystem.class);

            if (particleStore == null) {
                context.sender().sendMessage(Message.raw("§cError: No se pudo acceder al AssetStore de partículas."));
                return CompletableFuture.completedFuture(null);
            }

            // 2. Extraer y ordenar los IDs
            List<String> particleIds = new ArrayList<>(particleStore.getAssetMap().getAssetMap().keySet());
            Collections.sort(particleIds);

            // 3. Guardar en la carpeta del mod
            File folder = new File("mods/HyDrops");
            if (!folder.exists()) folder.mkdirs();

            File file = new File(folder, "reference_particles.json");

            try (Writer writer = new FileWriter(file)) {
                gson.toJson(particleIds, writer);

                context.sender().sendMessage(Message.raw("§a[Éxito] Se exportaron " + particleIds.size() + " partículas."));
                context.sender().sendMessage(Message.raw("§7Archivo: " + file.getName()));
                context.sender().sendMessage(Message.raw("§eRevisa el archivo para ver los IDs reales."));

            }
        } catch (Exception e) {
            context.sender().sendMessage(Message.raw("§cError crítico: " + e.getMessage()));
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(null);
    }
}