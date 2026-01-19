package dev.yh.utils;

import com.hypixel.hytale.server.core.universe.world.World;

public class PrefabUtils {

    /**
     * Genera una plataforma de seguridad usando setBlock directo.
     */
    public static void spawnSafetyPlatform(World world, double x, double y, double z) {
        // Coordenada central (pies del jugador)
        int centerX = (int) x;
        int centerY = (int) y - 1; // Un bloque abajo
        int centerZ = (int) z;

        // IDs como Strings (Mucho más fácil)
        String stoneName = "Rock_Stone_Brick_Smooth";
        String goldName = "Rock_Gold_Brick_Omate";

        // Bucle 3x3
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int posX = centerX + dx;
                int posZ = centerZ + dz;

                // Si es el centro, ponemos cristal, si no, piedra
                String blockToPlace = (dx == 0 && dz == 0) ? goldName : stoneName;

                try {
                    // MÉTODO DIRECTO: setBlock usando el nombre
                    // Si te pide String, le pasamos el String directo
                    world.setBlock(posX, centerY, posZ, blockToPlace);

                } catch (Exception e) {
                    System.out.println("Error poniendo bloque en " + posX + "," + centerY + "," + posZ + ": " + e.getMessage());
                }
            }
        }
        System.out.println("Plataforma generada en Y=" + centerY);
    }
}