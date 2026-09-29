package campionebase.erosionera.data.lang;

import campionebase.erosionera.ErosionEra;
import net.minecraft.resources.ResourceLocation;

@Translation
public class TranslationKeys {
    public static String getKeyName(String name, String type){
        return ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID, name).toLanguageKey(type);
    }

    public static String getKeyName(String name, String type, String usage){
        return ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID, name).toLanguageKey(type, usage);
    }
    //region ======================== Gui ========================
    @Translation.ZH_CN("确认")
    public static final String BUTTON_CONFIRM = getKeyName("button","gui", "confirm");
    @Translation.ZH_CN("取消")
    public static final String BUTTON_CANCEL = getKeyName("button","gui", "cancel");
    @Translation.ZH_CN("重置")
    public static final String BUTTON_RESET = getKeyName("button","gui", "reset");
    @Translation.ZH_CN("槽位 %s/%s")
    public static final String BIO_MACHINE_BARREL_SLOTS = getKeyName("bio_storage","gui", "barrel_slots");
    @Translation.ZH_CN("%s %s/%s")
    public static final String BIO_MACHINE_BIN_CONTENT = getKeyName("bio_storage","gui", "bin_content");
    @Translation.ZH_CN("空")
    public static final String BIO_MACHINE_EMPTY = getKeyName("bio_storage","gui", "empty");
    @Translation.ZH_CN("%s：%s")
    public static final String BIO_MACHINE_NAMED_STATUS = getKeyName("bio_storage","gui", "named_status");
    @Translation.ZH_CN("主视角")
    public static final String BIO_CAMERA_MAIN = getKeyName("bio_camera", "gui", "main");
    //region
    @Translation.ZH_CN("命名")
    public static final String NAME = getKeyName("action", "name");

}
