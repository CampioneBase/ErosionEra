package com.lunastic.erosion_era.item.tool;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.item.ModItems;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;

import java.util.function.Supplier;

public enum ErosionEraToolMaterials implements ToolMaterial {
    BASIC_MATERIAL(2, 121, 6.0F, 2.0F, 22,
            ()-> Ingredient.ofItems(ModItems.EROSION_DEBRIS))
    ;

    // 工具组
    public static final ItemGroup GROUP = FabricItemGroupBuilder.create(
            new Identifier(ErosionEraMod.NAMESPACE, "tool_group"))
            .icon(() -> new ItemStack(ModItems.EROSION_DEBRIS))
            .build();

    // 挖掘等级：木镐  0 = 金镐 0 < 石镐 1 < 铁镐 2 < 钻石镐 3 < 下界合金镐 4
    // 矿物：
    //  1级：石头、煤矿石/制品以及混凝土、部分石制方块 ...
    //  2级：煤矿石、铁矿石 ...
    //  3级：红石、黄金、钻石 ...
    //  5级：钴矿石(匠魂) ...
    //  6级：玛玉玲(匠魂) ...
    //
    // 镐子：
    //  1级 —— 木镐 金镐
    //  2级 —— 石镐
    //  3级 —— 铁镐
    //  4级 —— 钻石镐
    //  5级 —— 下届合金镐 黑曜石镐（匠魂）
    //  6级 —— 玛玉玲镐
    private final int miningLevel;
    // 耐久：金 32 < 木 59 < 石 131 < 铁 250 < 钻石 1561 < 下界合金 2031
    private final int itemDurability;
    // 挖掘速度：木镐 2 < 石镐 4 < 铁镐 6 < 钻石镐 8 < 下界合金镐 9 < 金镐 12
    private final float miningSpeed;
    // 攻击伤害：木 0 = 金 0 < 石 1 < 铁 2 < 钻石 3 < 下界合金 4
    private final float attackDamage;
    // 附魔等级：石 5 < 钻石 10 < 铁 14 < 木 15 = 下界合金 15 < 金 22
    private final int enchantability;
    // 修复材料/方式（懒加载）
    private final Lazy<Ingredient> repairIngredient;

    ErosionEraToolMaterials(int miningLevel,
                            int itemDurability,
                            float miningSpeed,
                            float attackDamage,
                            int enchantability,
                            Supplier<Ingredient> repairIngredient) {
        this.miningLevel = miningLevel;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = new Lazy<>(repairIngredient);
    }

    @Override
    public int getDurability() {
        return this.itemDurability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
