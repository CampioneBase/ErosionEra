package campionebase.erosionera.item;

import campionebase.erosionera.magic.spell.RuneUnits;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RuneStoneItem extends Item {
    public ItemStack getInstanceWithRune(ResourceLocation id){
        ItemStack itemStack = this.getDefaultInstance();
        RuneUnits.setRune(itemStack, id);
        return itemStack;
    }
    public RuneStoneItem() {
        super(new Properties());
    }


}
