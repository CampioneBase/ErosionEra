package campionebase.erosionera.recipe.custom;

import campionebase.erosionera.magic.module.IMagicContainer;
import campionebase.erosionera.magic.module.MagicPayload;
import campionebase.erosionera.magic.module.MagicPayloads;
import campionebase.erosionera.registry.ErErRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * 卸下：已装载的容器 → 模块（空容器作为剩余物返还）。
 *
 * <p>只对接 {@link IMagicContainer}；模块物品由 {@link MagicPayloads} 按载荷类型还原。
 *
 * <p><b>关键约束</b>：{@link #getRemainingItems} 是只读语义（预览阶段也会调用），
 * 因此对副本执行 {@code removePayload}，绝不修改输入物品栈。
 */
public class MagicStoneUnbindingRecipe extends CustomRecipe {

    public MagicStoneUnbindingRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(@NotNull CraftingContainer container, @NotNull Level level) {
        int count = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (++count > 1) {
                return false;
            }
            IMagicContainer spellContainer = IMagicContainer.of(stack);
            if (spellContainer == null || !spellContainer.canUnbind(stack)) {
                return false;
            }
            // 载荷必须能还原成模块物品，否则卸下会把它吞掉
            MagicPayload payload = spellContainer.getPayload(stack);
            if (payload == null || !MagicPayloads.canProduceModule(payload)) {
                return false;
            }
        }
        return count == 1;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer container, @NotNull RegistryAccess access) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            IMagicContainer spellContainer = IMagicContainer.of(stack);
            if (spellContainer == null) {
                continue;
            }
            MagicPayload payload = spellContainer.getPayload(stack);
            if (payload == null) {
                continue;
            }
            // 载荷类型决定返还哪种模块物品（刻录石 / Mantra 石 …）
            return MagicPayloads.toModule(payload);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingContainer container) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            IMagicContainer spellContainer = IMagicContainer.of(stack);
            if (spellContainer == null || !spellContainer.hasPayload(stack)) {
                continue;
            }
            // 在副本上清除，输入栈保持原样
            ItemStack emptied = stack.copy();
            emptied.setCount(1);
            spellContainer.removePayload(emptied);
            remaining.set(i, emptied);
        }
        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ErErRecipes.SPELL_UNBINDING.get();
    }
}
