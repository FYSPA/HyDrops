package dev.yh.utils;

import com.hypixel.hytale.server.core.universe.world.World;

public class BlockUtils {

    /**
     * Coloca un bloque en el mundo usando su ID de texto.
     * @param blockId El nombre del bloque (ej: "hytale:chest")
     */
    public static void placeBlock(World world, int x, int y, int z, String blockId) {
        if (world == null) return;

        // Aseguramos que tenga el prefijo hytale:
        if (!blockId.contains(":")) {
            blockId = "hytale:" + blockId;
        }

        try {
            // El método setBlock generalmente acepta (x, y, z, String id)
            // No necesitamos importar la clase Block.
            world.setBlock(x, y, z, blockId);

            System.out.println("[BlockUtils] Bloque colocado: " + blockId + " en " + x + "," + y + "," + z);

        } catch (Exception e) {
            System.out.println("[BlockUtils] Error al poner bloque: " + e.getMessage());
            e.printStackTrace();
        }
    }
}