package campionebase.erosionera.recipe;

import campionebase.erosionera.item.RuneStoneItem;
import campionebase.erosionera.registry.ErErItems;
import campionebase.erosionera.registry.ErErRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 符文台配方：核心物品 + 若干材料 → 一颗符文石。
 *
 * <p><b>字段语义</b>
 * <ul>
 *   <li>{@code id}：配方自身 id（由数据包文件名决定，如 {@code erosionera:rune_recipe/builder_simple}）</li>
 *   <li>{@code core}：核心物品（魔能石一类），恰好 1 个</li>
 *   <li>{@code materials}：其他材料，每个 {@link Ingredient} 对应一份</li>
 *   <li>{@code mpt}：每 tick 消耗的魔力（mana per tick）</li>
 *   <li>{@code tick}：完成所需 tick 数</li>
 *   <li>{@code result}：产出的**符文 id**（不是符文类型 id），产出物是带该 id 的符文石</li>
 * </ul>
 *
 * <p><b>容器约定</b>：{@link Container} 里只有非空槽位参与匹配，槽位顺序无意义
 * （先找出核心物品，剩余槽位再逐个匹配材料）。这样方块的槽位布局可以自由调整。
 */
public record RuneRecipe(
        ResourceLocation id,
        Ingredient core,
        List<Ingredient> materials,
        int mpt,
        int tick,
        ResourceLocation result
) implements Recipe<Container> {

    public static final String NAME = "rune_recipe";

    // ------------------------------------------------------------------ 匹配

    @Override
    public boolean matches(@NotNull Container container, @NotNull Level level) {
        List<ItemStack> stacks = nonEmptyStacks(container);
        if (stacks.isEmpty()) return false;
        // 输入数量必须恰好是 1 个核心 + N 份材料
        if (stacks.size() != 1 + this.materials.size()) {
            return false;
        }

        // 先定位核心物品
        if (!this.core.test(stacks.get(0))) return false;

        // 剩余槽位逐一匹配材料（贪心）
        stacks.remove(0);
        for (Ingredient material : this.materials) {
            int found = -1;
            for (int i = 0; i < stacks.size(); i++) {
                if (material.test(stacks.get(i))) {
                    found = i;
                    break;
                }
            }
            if (found < 0) {
                return false;
            }
            stacks.remove(found);
        }
        return stacks.isEmpty();
    }

    private static List<ItemStack> nonEmptyStacks(Container container) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
        return stacks;
    }

    // ------------------------------------------------------------------ 产物

    /** 构造产出物：{@code result} 是符文 id，所以生成带 {@code rune_id} 的符文石。 */
    @Override
    public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess registryAccess) {
        return this.createResult();
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return this.createResult();
    }

    private ItemStack createResult() {
        RuneStoneItem item = ErErItems.RUNE_STONE.get();
        return item.getInstanceWithRune(this.result);
    }

    // ------------------------------------------------------------------ 元数据

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        // 不是 2D 合成台配方：尺寸与本配方无关
        return true;
    }

    /** 不进入配方书（自定义工作站无法用配方书合成）。 */
    public boolean isSpecial() {
        return true;
    }

    /** JEI / REI 读取输入用：核心 + 材料。 */
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(this.core);
        list.addAll(this.materials);
        return list;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ErErRecipes.RUNE_CRAFT_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ErErRecipes.RUNE_CRAFT.get();
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RuneRecipe that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
