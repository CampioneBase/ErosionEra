package lunastic.erosion_era.item.armor;

import lunastic.erosion_era.init.ItemGroups;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        this(material, slot, new FabricItemSettings().group(ItemGroups.ARMOR));

    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {

    }
}
