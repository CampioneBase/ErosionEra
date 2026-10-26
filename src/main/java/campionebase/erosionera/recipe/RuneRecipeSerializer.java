package campionebase.erosionera.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RuneRecipe} 的序列化器。
 *
 * <p><b>1.20.1 的 {@link RecipeSerializer} 只有三个方法</b>（已由编译器确认）：
 * <ul>
 *   <li>{@code fromJson(ResourceLocation, JsonObject)} —— 数据包读取</li>
 *   <li>{@code fromNetwork(ResourceLocation, FriendlyByteBuf)} —— 客户端解码（<b>带 id</b>）</li>
 *   <li>{@code toNetwork(FriendlyByteBuf, RuneRecipe)} —— 服务端编码</li>
 * </ul>
 * 没有 {@code toJson}（想导出 JSON 要自己在 datagen 里拼）。
 *
 * <p>JSON 形态见 docs/_data_packet_form.md §3。
 */
public class RuneRecipeSerializer implements RecipeSerializer<RuneRecipe> {

    @Override
    public @NotNull RuneRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
        Ingredient core = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "core"));

        List<Ingredient> materials = new ArrayList<>();
        if (json.has("material")) {
            JsonArray array = GsonHelper.getAsJsonArray(json, "material");
            for (JsonElement element : array) {
                materials.add(Ingredient.fromJson(element));
            }
        }

        int mpt = GsonHelper.getAsInt(json, "mpt", 0);
        int tick = GsonHelper.getAsInt(json, "tick", 0);
        // result 是**符文 id**（不是符文类型 id）
        ResourceLocation result = ResourceLocation.parse(GsonHelper.getAsString(json, "result"));

        return new RuneRecipe(recipeId, core, List.copyOf(materials), mpt, tick, result);
    }

    @Override
    public RuneRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buf) {
        Ingredient core = Ingredient.fromNetwork(buf);
        int size = buf.readVarInt();
        List<Ingredient> materials = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            materials.add(Ingredient.fromNetwork(buf));
        }
        int mpt = buf.readVarInt();
        int tick = buf.readVarInt();
        ResourceLocation result = buf.readResourceLocation();
        return new RuneRecipe(recipeId, core, List.copyOf(materials), mpt, tick, result);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buf, RuneRecipe recipe) {
        // id 由 RecipeManager 传输，不需要再写进包里
        recipe.core().toNetwork(buf);
        buf.writeVarInt(recipe.materials().size());
        for (Ingredient material : recipe.materials()) {
            material.toNetwork(buf);
        }
        buf.writeVarInt(recipe.mpt());
        buf.writeVarInt(recipe.tick());
        buf.writeResourceLocation(recipe.result());
    }
}
