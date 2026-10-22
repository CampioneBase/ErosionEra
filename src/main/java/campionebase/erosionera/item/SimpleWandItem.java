package campionebase.erosionera.item;

import campionebase.erosionera.magic.IMagic;
import campionebase.erosionera.magic.IManaPool;
import campionebase.erosionera.magic.MagicContext;
import campionebase.erosionera.magic.MagicResult;
import campionebase.erosionera.magic.spell.ISpellContainer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 测试法杖：可装卸模块的施法器。
 *
 * <p>施法器<b>只关注 {@link IMagic}</b>：从容器取到魔法对象后直接
 * {@code cast(ctx)}，施法条件与资源消耗都由魔法自身负责。
 * 因此这里不区分 Spell / Mantra / 扩展魔法类型——新增魔法类型不需要改本类。
 */
public class SimpleWandItem extends AbstractMagicWeaponItem implements ISpellContainer {

    public SimpleWandItem() {
        super(new Item.Properties()
            .defaultDurability(1000)
        );
    }

    @Override
    protected void releaseMagic(ServerLevel level, ItemStack itemStack, int mana, LivingEntity caster) {
        IMagic magic = this.magicOf(itemStack);
        if (magic == null) {
            // 空槽，或载荷引用的魔法未注册（数据包缺失等）：静默失败，不消耗任何资源
            return;
        }
        MagicContext ctx = new MagicContext(caster, level, null, /*density*/Double.NaN, /*pool*/IManaPool.EMPTY);
        MagicResult result = magic.cast(ctx);
        if (caster instanceof Player player && !result.isSuccess() && result.message() != null) {
            player.displayClientMessage(result.message(), true);
        }
    }
}
