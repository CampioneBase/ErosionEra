package campionebase.erosionera.data.lang;

import campionebase.erosionera.ErosionEra;
import net.minecraft.resources.ResourceLocation;

@Translation
public class TranslationKeys {
    @Translation.ZH_CN("确认")
    public static final String BUTTON_CONFIRM = getKeyName("default","button", "confirm");
    @Translation.ZH_CN("取消")
    public static final String BUTTON_CANCEL = getKeyName("default","button", "cancel");
    @Translation.ZH_CN("重置")
    public static final String BUTTON_RESET = getKeyName("default","button", "reset");

    public static String getKeyName(String name, String type){
        return ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID, name).toLanguageKey(type);
    }

    public static String getKeyName(String name, String type, String usage){
        return ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID, name).toLanguageKey(type, usage);
    }
}
