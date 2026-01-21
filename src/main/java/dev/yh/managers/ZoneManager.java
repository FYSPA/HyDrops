package dev.yh.managers;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import com.hypixel.hytale.math.vector.Vector3d;
import dev.yh.utils.PlayerUtils;

public class ZoneManager {

    private static final ZoneManager instance = new ZoneManager();
    public static ZoneManager getInstance() { return instance; }

    public int getPlayerZoneId(Player player) {
        if (player == null) return 1;

        try {
            WorldMapTracker tracker = player.getWorldMapTracker();
            if (tracker != null) {
                WorldMapTracker.ZoneDiscoveryInfo currentZone = tracker.getCurrentZone();

                if (currentZone != null) {
                    String region = currentZone.regionName();
                    if (region != null && region.startsWith("Zone")) {
                        char zoneChar = region.charAt(4);
                        int id = Character.getNumericValue(zoneChar);
                        if (id >= 1 && id <= 4) return id;
                    }
                }
            }
        } catch (Exception e) {
            PlayerUtils.broadcast("[ZoneManager] Error leyendo WorldMapTracker: " + e.getMessage(), "F51E0F");
        }

        Vector3d pos = PlayerUtils.getPos(player);
        if (pos != null) {
            return calculateFakeZoneByDistance(pos.x, pos.z);
        }

        return 1;
    }

    private int calculateFakeZoneByDistance(double x, double z) {
        double dist = Math.sqrt(x * x + z * z);
        if (dist < 1000) return 1;
        if (dist < 2500) return 2;
        if (dist < 4000) return 3;
        return 4;
    }
}