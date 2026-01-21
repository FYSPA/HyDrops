package dev.yh.utils;

import com.hypixel.hytale.server.core.universe.world.World;

public class WorldUtils {

    public static int getHighestBlockY(World world, int x, int z) {
        for (int y = 150; y > 0; y--) {
            try {
                Object block = world.getBlock(x, y, z);
                if (block != null) {
                    String name = block.toString().toLowerCase();
                    if (!name.contains("air") && !name.contains("void") && !name.contains("null")) {
                        return y;
                    }
                }
            } catch (Exception ignored) {}
        }
        return 60;
    }
}