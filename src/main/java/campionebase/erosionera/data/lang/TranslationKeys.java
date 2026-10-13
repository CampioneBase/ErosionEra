package campionebase.erosionera.data.lang;

import campionebase.erosionera.ErosionEra;
import net.minecraft.resources.ResourceLocation;

@Translation(namespace = ErosionEra.MODID)
public class TranslationKeys {
    public static String getKeyName(String name, String type){
        return ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID, name).toLanguageKey(type);
    }

    public static String getKeyName(String name, String type, String usage){
        return ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID, name).toLanguageKey(type, usage);
    }
    //region ======================== Gui ========================
    @Translation.Key("确认")
    public static final String BUTTON_CONFIRM = getKeyName("button","gui", "confirm");
    @Translation.Key("取消")
    public static final String BUTTON_CANCEL = getKeyName("button","gui", "cancel");
    @Translation.Key("重置")
    public static final String BUTTON_RESET = getKeyName("button","gui", "reset");
    @Translation.Key("命名")
    public static final String NAME = getKeyName("action", "name");
    //endregion
}