package lunastic.erosion_era.item.armor.material;

import lunastic.erosion_era.init.Items;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Lazy;

import java.util.function.Supplier;

public enum ErosionEraArmorMaterials implements ArmorMaterial {

    BASIC_MATERIAL("util", 5, new int[]{1, 2, 3, 1}, 15,
            SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0F, 0F,
            () -> Ingredient.ofItems(Items.EROSION_DEBRIS))
    ;

    // 所有盔甲的基础耐久数值 {头, 胸甲, 腿甲, 鞋}
    private static final int[] BASE_DURABILITY = {13, 15, 16, 11};

    // 材质名称
    private final String name;
    // 耐久倍率
    private final int durabilityMultiplier;
    // 护甲值 {头, 胸甲, 腿甲, 鞋}
    private final int[] armorValues;
    // 最大附魔等级
    private final int enchantability;
    // 穿戴音效
    private final SoundEvent equipSound;
    // 护甲韧性
    private final float toughness;
    // 击退抗性
    private final float knockbackResistance;
    // 修复材料/方式（懒加载）
    private final Lazy<Ingredient> repairIngredient;

    ErosionEraArmorMaterials(String name,
                             int durabilityMultiplier,
                             int[] armorValueArr,
                             int enchantability,
                             SoundEvent equipSound,
                             float toughness,
                             float knockbackResistance,
                             Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.armorValues = armorValueArr;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
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
    // leave this value at 0. If you want to implement it,
    // write '0.XF' (in which X is how much knockback protection you want),
    // and I'll teach you how to make it work later on.
    // 将此值保留为0。如果你想实现它，
    // 请写“0.XF”（其中X是你想要的击退保护程度），
    // 我稍后会教你如何使它发挥作用。
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
