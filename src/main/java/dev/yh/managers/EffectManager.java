package dev.yh.managers;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class EffectManager {

    /**
     * Aplica un efecto visual permanente a una entidad (faro).
     */
    public void applyBeaconEffect(Ref<EntityStore> entityRef, String effectId) {
        try {
            Store<EntityStore> store = entityRef.getStore();

            // 1. Obtener el efecto del registro de assets
            EntityEffect effect = EntityEffect.getAssetMap().getAsset(effectId);
            if (effect == null) {
                System.err.println("[EffectManager] No se encontró el efecto: " + effectId);
                return;
            }

            // 2. Obtener o crear el controlador de efectos de la entidad
            EffectControllerComponent controller = store.getComponent(entityRef, EffectControllerComponent.getComponentType());
            if (controller == null) {
                controller = new EffectControllerComponent();
                store.addComponent(entityRef, EffectControllerComponent.getComponentType(), controller);
            }

            // 3. Aplicar el efecto de forma infinita (según tu documentación)
            int effectIndex = EntityEffect.getAssetMap().getIndex(effectId);

            // Necesitamos un accessor para el mundo
            ComponentAccessor<EntityStore> accessor = store;

            controller.addInfiniteEffect(
                    entityRef,
                    effectIndex,
                    effect,
                    accessor
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}