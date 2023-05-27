package lunastic.erosion_era.item.tool;

import lunastic.erosion_era.init.ErErGroups;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;

public class BasicPickaxeItem extends PickaxeItem {

    public BasicPickaxeItem(
            ToolMaterial material,
            int attackDamage,
            float attackSpeed,
            Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    public BasicPickaxeItem(
            ToolMaterial material,
            int attackDamage,
            float attackSpeed) {
        this(material, attackDamage, attackSpeed, new FabricItemSettings().group(ErErGroups.TOOL));
    }
}
