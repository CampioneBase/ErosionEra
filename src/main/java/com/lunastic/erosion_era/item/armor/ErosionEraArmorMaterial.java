package com.lunastic.erosion_era.item.armor;

import com.lunastic.erosion_era.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Lazy;

import java.util.function.Supplier;

public enum ErosionEraArmorMaterial implements ArmorMaterial {

    TEST_MATERIAL("test", 5, new int[]{1, 2, 3, 1}, 15,
            SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.1F, () -> Ingredient.ofItems(ModItems.TEST_ITEM))
    ;
    private static final int[] BASE_DURABILITY = {13, 15, 16, 11};
    private final String name;
    private final int durabilityMultiplier;
    private final int[] armorValues;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final Lazy<Ingredient> repairIngredient;

    ErosionEraArmorMaterial(String name,
                            int durabilityMultiplier,
                            int[] armorValueArr,
                            int enchantability,
                            SoundEvent soundEvent,
                            float toughness,
                            Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.armorValues = armorValueArr;
        this.enchantability = enchantability;
        this.equipSound = soundEvent;
        this.toughness = toughness;
        this.repairIngredient = new Lazy<>(repairIngredient);
    }

    // how many hits can armor take before breaking. Uses the int we wrote on 'BASE_DURABILITY' to calculate.
    // Leather uses 5, Diamond 33, Netherite 37.
    // 盔甲在破碎之前可以被击中多少次。使用我们在“BASE_DURABILITY”上写的int进行计算。
    // 皮革使用5，钻石33，下届合金37。
    public int getDurability(EquipmentSlot slot) {
        return BASE_DURABILITY[slot.getEntitySlotId()] * this.durabilityMultiplier;
    }

    // calls for the 'PROTECTION_VALUES' int we already wrote above.
    // 调用我们上面已经写过的“PROTECTION_VALUES”int。
    public int getProtectionAmount(EquipmentSlot slot) {
        return this.armorValues[slot.getEntitySlotId()];
    }

    // This will be how likely the armor can get high level or multiple enchantments in an enchantment book.
    // 这将是盔甲在一本附魔书中获得高等级或多重附魔的可能性。
    public int getEnchantability() {
        return this.enchantability;
    }

    // The standard used by vanilla armor is SoundEvents.ITEM_ARMOR_EQUIP_X, X being the type of armor.
    // 一般盔甲使用的标准是SoundEvents.ITEM_armor_EQUIP_X，X是盔甲的类型。
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    // what item are we gonna be using to repair the armor on an anvil. It can be either a vanilla item or one of your own.
    // 用来修复盔甲的物品，可以是一般物品或者自定义物品
    public Ingredient getRepairIngredient() {
        // We needed to make it a Lazy type so we can actually get the Ingredient from the Supplier.
        // 使用懒加载
        return this.repairIngredient.get();
    }

    @Environment(EnvType.CLIENT)
    public String getName() {
        return this.name;
    }

    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return 0;
    }
}
