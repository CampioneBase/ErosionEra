package campionebase.erosionera.item;

import campionebase.erosionera.magic.spell.RuneSequence;
import campionebase.erosionera.magic.spell.RuneUnits;
import campionebase.erosionera.magic.spell.module.ISpellModule;
import campionebase.erosionera.magic.spell.module.RunePayload;
import campionebase.erosionera.magic.spell.module.SpellPayload;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 刻录石：记录一段**符文 id 列表**。
 *
 * <p>作为 {@link ISpellModule} 被装进容器（法杖）；容器与配方只看到载荷 {@link RunePayload}。
 */
public class SpellStoneItem extends Item implements ISpellModule {

    public SpellStoneItem() {
        super(new Item.Properties());
    }

    /** 读取本刻录石记录的符文序列。 */
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
