package campionebase.erosionera.magic.spell;

import campionebase.erosionera.magic.spell.module.ISpellModule;
import campionebase.erosionera.magic.spell.module.SpellPayload;
import campionebase.erosionera.registry.ErErRecipes;
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
 * 装载：空容器 + 模块 → 已装载的容器。
 *
 * <p>只对接两个抽象：{@link ISpellContainer}（容器）与
 * {@link ISpellModule}（模块，刻录石 / Mantra 石等）。
 * 新增模块类型不需要改动本配方。
 */
public class SpellBindingRecipe extends CustomRecipe {

    public SpellBindingRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(@NotNull CraftingContainer container, @NotNull Level level) {
        ItemStack containerStack = ItemStack.EMPTY;
        ItemStack moduleStack = ItemStack.EMPTY;
        int count = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (++count > 2) {
                return false;
            }

            ISpellContainer spellContainer = ISpellContainer.of(stack);
            if (spellContainer != null) {
                if (!spellContainer.canBind(stack) || !containerStack.isEmpty()) {
                    return false;
                }
                containerStack = stack;
                continue;
            }

            ISpellModule module = ISpellModule.of(stack);
            if (module != null) {
                if (!module.canInstall(stack) || !moduleStack.isEmpty()) {
                    return false;
                }
                moduleStack = stack;
                continue;
            }

            return false;
        }
        return count == 2 && !containerStack.isEmpty() && !moduleStack.isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer container, @NotNull RegistryAccess access) {
        ItemStack containerStack = ItemStack.EMPTY;
        ItemStack moduleStack = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (ISpellContainer.of(stack) != null) {
                containerStack = stack;
            } else if (ISpellModule.of(stack) != null) {
                moduleStack = stack;
            }
        }

        ISpellContainer spellContainer = ISpellContainer.of(containerStack);
        ISpellModule module = ISpellModule.of(moduleStack);
        if (spellContainer == null || module == null) {
            return ItemStack.EMPTY;
        }

        SpellPayload payload = module.payload(moduleStack);
        if (payload == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = containerStack.copy();
        result.setCount(1);
        spellContainer.setPayload(result, payload);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ErErRecipes.SPELL_BINDING.get();
    }
}
