package dev.yh.commands.drop.sub;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import dev.yh.managers.ZoneManager;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class GetZoneCmd extends AbstractCommand {

    private final ZoneManager zoneManager;

    public GetZoneCmd(ZoneManager zoneManager) {
        super("whereami", "Muestra la zona actual donde te encuentras");
        this.zoneManager = zoneManager;
        addAliases("zone", "pos");
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!(context.sender() instanceof Player)) {
            return CompletableFuture.completedFuture(null);
        }

        Player player = (Player) context.sender();

        // 1. Obtener el ID y el Bioma
        int zoneId = zoneManager.getPlayerZoneId(player);
        WorldMapTracker tracker = player.getWorldMapTracker();
        String biomeName = (tracker != null) ? tracker.getCurrentBiomeName() : "Desconocido";

        // 2. Declarar variables para el nombre bonito y color
        String zoneName;
        String color;

        // 3. Lógica del switch (DEBE IR ANTES DE ENVIAR EL MENSAJE)
        switch (zoneId) {
            case 1:
                zoneName = "Tierras Esmeralda (Bosque)";
                color = "§a"; // Verde
                break;
            case 2:
                zoneName = "Aullido del Viento (Desierto)";
                color = "§e"; // Amarillo
                break;
            case 3:
                zoneName = "Cimas Blancas (Nieve)";
                color = "§b"; // Cian
                break;
            case 4:
                zoneName = "Tierras Devastadas (Lava)";
                color = "§c"; // Rojo
                break;
            default:
                zoneName = "Zona Desconocida";
                color = "§7"; // Gris
                break;
        }

        // 4. Enviar los mensajes finales
        player.sendMessage(Message.raw("§8[§6Localizador§8] " + color + "Zona: " + zoneName + " §8(ID: " + zoneId + ")"));
        player.sendMessage(Message.raw("§8[§6Localizador§8] §7Bioma: §f" + biomeName));

        return CompletableFuture.completedFuture(null);
    }
}