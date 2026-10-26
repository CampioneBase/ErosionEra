package campionebase.erosionera.item;

import campionebase.erosionera.magic.spell.RuneSequence;
import campionebase.erosionera.magic.spell.RuneUnits;
import campionebase.erosionera.magic.spell.module.ISpellModule;
import campionebase.erosionera.magic.spell.module.RunePayload;
import campionebase.erosionera.magic.spell.module.SpellPayload;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 魔法石 _Magic Stone_：**存储一个魔法（类型 + 数据）**。
 *
 * <p>当前实现的魔法类型只有"符文序列"（{@link RunePayload}）；
 * 将来会有 Mantra 等类型。作为 {@link ISpellModule} 可被装进容器（法杖）。
 *
 * <p>物品自身的持久化数据只有符文 id 列表；`magic_type` / `magic_data`
 * 是装进容器后才写入的（见 docs/_data_packet_form.md §6.3）。
 */
public class MagicStoneItem extends Item implements ISpellModule {

    public MagicStoneItem() {
        super(new Item.Properties());
    }

    /** 读取本魔法石记录的符文序列。 */
    public static RuneSequence sequence(ItemStack stack) {
        return RuneUnits.read(stack);
    }

    @Override
    public boolean canInstall(ItemStack moduleStack) {
        return !sequence(moduleStack).isEmpty();
    }

    @Override
    public SpellPayload payload(ItemStack moduleStack) {
        return new RunePayload(sequence(moduleStack));
    }
}
