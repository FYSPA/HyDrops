package dev.yh.model;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.codec.builder.BuilderCodec; // Asegura este import

public class FallingDropComponent implements Component<EntityStore> {
    public double targetY;

    // EL CODEC DEBE IR AQUÍ (Dentro de la clase del componente)
    public static final BuilderCodec<FallingDropComponent> CODEC =
            BuilderCodec.builder(FallingDropComponent.class, () -> new FallingDropComponent(0)).build();

    private static ComponentType<EntityStore, FallingDropComponent> TYPE;

    public FallingDropComponent(double targetY) {
        this.targetY = targetY;
    }

    @Override
    public Component<EntityStore> clone() {
        return new FallingDropComponent(targetY);
    }

    public static ComponentType<EntityStore, FallingDropComponent> getComponentType() {
        return TYPE;
    }

    public static void setComponentType(ComponentType<EntityStore, FallingDropComponent> type) {
        TYPE = type;
    }
}