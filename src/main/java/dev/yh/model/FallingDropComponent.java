package dev.yh.model;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class FallingDropComponent implements Component<EntityStore> {
    public double targetY;

    // --- ESTA ES LA VARIABLE QUE FALTABA ---
    public int ticksExisted = 0;

    public static final BuilderCodec<FallingDropComponent> CODEC =
            BuilderCodec.builder(FallingDropComponent.class, () -> new FallingDropComponent(0)).build();

    private static ComponentType<EntityStore, FallingDropComponent> TYPE;

    public FallingDropComponent(double targetY) {
        this.targetY = targetY;
        this.ticksExisted = 0; // Empieza en cero
    }

    @Override
    public Component<EntityStore> clone() {
        FallingDropComponent clone = new FallingDropComponent(targetY);
        clone.ticksExisted = this.ticksExisted; // Copiamos el valor al clonar
        return clone;
    }

    public static ComponentType<EntityStore, FallingDropComponent> getComponentType() {
        return TYPE;
    }

    public static void setComponentType(ComponentType<EntityStore, FallingDropComponent> type) {
        TYPE = type;
    }
}