package dev.yh.managers;

import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import java.util.Collections;
import java.util.Map;

public class ItemManager {

    // Método que encapsula la lógica compleja de obtener items
    public Map<String, Item> getLoadedItems() {
        AssetStore<String, Item, ?> itemStore = AssetRegistry.getAssetStore(Item.class);

        if (itemStore != null) {
            AssetMap<String, Item> assetMap = itemStore.getAssetMap();
            return assetMap.getAssetMap();
        }

        // Retornamos un mapa vacío en lugar de null para evitar NullPointerException
        return Collections.emptyMap();
    }
}