package lunastic.erosion_era.item.armor;

import lunastic.erosion_era.item.items.ArmorItems;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;

/** 盔甲物品总类 */
public class ArmorItemBase extends ArmorItem {

    /**
     * 盔甲物品总类
     * @param material 盔甲游戏材质
     * @param slot 盔甲部位
     * @param settings 设定类
     */
    public ArmorItemBase(ArmorMaterial material, EquipmentSlot slot, Item.Settings settings) {
        super(material, slot, settings);
    }

    public ArmorItemBase(ArmorMaterial material, EquipmentSlot slot){
        this(material, slot, new FabricItemSettings()
                .group(ArmorItems.GROUP));
    }
}
