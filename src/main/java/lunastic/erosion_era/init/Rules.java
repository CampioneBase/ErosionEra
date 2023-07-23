package lunastic.erosion_era.init;

import lunastic.erosion_era.ErosionEraMod;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.minecraft.world.GameRules;

public class Rules {

    /** 【更新规则】该规则确定是否只有原版及本模组物品、方块、实体等受到侵蚀影响 */
    public static final GameRules.Key<EnumRule<EROSION_SELECTOR_MODE>> EROSION_SELECTOR =
            GameRuleRegistry.register("erosionSelector", GameRules.Category.UPDATES, GameRuleFactory.createEnumRule(EROSION_SELECTOR_MODE.ORIGIN));


    public static void init(){
        ErosionEraMod.LOGGER.info("GameRules Loading......");
    }

    /**
     * 侵蚀对象选择模式
     * <ul>
     *     <li>ALL: 可对所有模组对象产生侵蚀效果</li>
     *     <li>ORIGIN: 对原版和本模组对象产生侵蚀效果（默认）</li>
     *     <li>OTHER: 没有具体含义，用于扩展使用（没有扩展定义则效果与 ORIGIN 一样）</li>
     *     <li>NONE: 不选择任何对象（即禁用）</li>
     * </ul>
     */
    public enum EROSION_SELECTOR_MODE {ALL, ORIGIN, OTHER, NONE}
}
