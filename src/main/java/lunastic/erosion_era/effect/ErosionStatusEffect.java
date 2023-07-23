package lunastic.erosion_era.effect;

import lunastic.erosion_era.init.DamageSources;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import java.util.UUID;

public class ErosionStatusEffect extends StatusEffect {

    public ErosionStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0xd412e0);
        this.addAttributeModifier(
                // 增加通用伤害
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                UUID.randomUUID().toString(),
                0f,
                EntityAttributeModifier.Operation.ADDITION
        ).addAttributeModifier(
                EntityAttributes.GENERIC_MOVEMENT_SPEED,
                UUID.randomUUID().toString(),
                -0.15f,
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL
        );
    }

    // 这个方法在每个 tick 都会调用，以检查是否应应用药水效果
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // 在我们的例子中，为了确保每一 tick 药水效果都会被应用，我们只要这个方法返回 true 就行了。
        return true;
    }

    // 这个方法在每个 tick 都会调用
    // 这个方法在应用药水效果时会被调用，所以我们可以在这里实现自定义功能。
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // ErosionEraMod.LOGGER.info(entity.getArmorItems()); // [1 air, 1 air, 1 air, 1 air]
        entity.damage(DamageSources.EROSION, 1f + 0.5f * amplifier);
    }

    @Override
    public double adjustModifierAmount(int amplifier, EntityAttributeModifier modifier) {
        // 攻击伤害
        if (modifier.equals(this.getAttributeModifiers().get(EntityAttributes.GENERIC_ATTACK_DAMAGE)))
            return 1f;
        // 地面移动速度
        if (modifier.equals(this.getAttributeModifiers().get(EntityAttributes.GENERIC_MOVEMENT_SPEED)))
            // 相当于缓慢效果的75%效能
            return modifier.getValue() * (amplifier + 1) * 0.75f;

        //  默认
        return super.adjustModifierAmount(amplifier, modifier);
    }
}
