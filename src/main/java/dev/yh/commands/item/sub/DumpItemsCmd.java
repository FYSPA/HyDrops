package dev.yh.commands.item.sub;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
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

public class DumpItemsCmd extends AbstractCommand {

    private final Gson gson;

    public DumpItemsCmd() {
        super("json", "Exporta todos los IDs de items válidos a un archivo JSON");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        context.sender().sendMessage(Message.raw("§e[HyDrops] Iniciando exportación de items..."));

        // 1. Obtener el almacén de items del juego
        AssetStore<String, Item, ?> itemStore = AssetRegistry.getAssetStore(Item.class);

        if (itemStore == null) {
            context.sender().sendMessage(Message.raw("§cError: No se pudo acceder al AssetRegistry."));
            return CompletableFuture.completedFuture(null);
        }

        // 2. Extraer todos los IDs (Keys)
        // Obtenemos los nombres REALES (ej: Weapon_Sword_Iron)
        List<String> validIds = new ArrayList<>(itemStore.getAssetMap().getAssetMap().keySet());
        Collections.sort(validIds); // Los ordenamos alfabéticamente

        // 3. Guardar en archivo
        File folder = new File("mods/HyDrops");
        if (!folder.exists()) folder.mkdirs();

        File file = new File(folder, "reference_items.json");

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(validIds, writer);

            context.sender().sendMessage(Message.raw("§a[Éxito] Se guardaron " + validIds.size() + " items en:"));
            context.sender().sendMessage(Message.raw("§7" + file.getAbsolutePath()));
            context.sender().sendMessage(Message.raw("§bUsa estos IDs exactos en tu loot_config.json"));

        } catch (Exception e) {
            context.sender().sendMessage(Message.raw("§cError al guardar el archivo: " + e.getMessage()));
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(null);
    }
}