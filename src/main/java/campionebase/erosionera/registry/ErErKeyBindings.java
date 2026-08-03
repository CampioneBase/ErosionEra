package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.data.lang.Translation;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(
        modid = ErosionEra.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
@OnlyIn(Dist.CLIENT)
@Translation(key = "key")
public class ErErKeyBindings {
    @Translation.ZH_CN("侵蚀国度：活体机械")
    public static final String KEY_CATEGORY_BM = "key.category." + ErosionEra.MODID + ".bio_machinery";
    @Translation.ZH_CN("上一个摄像机")
    public static final KeyMapping PREV_BIO_CAMERA_KEY = new KeyMapping(
            getKeyId("prev_bio_camera"),
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Q,
            KEY_CATEGORY_BM
    );
    @Translation.ZH_CN("下一个摄像机")
    public static final KeyMapping NEXT_BIO_CAMERA_KEY = new KeyMapping(
            getKeyId("next_bio_camera"),
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_E,
            KEY_CATEGORY_BM
    );
    @Translation.ZH_CN("活体控制 上")
    public static final KeyMapping BIO_CONTROL_UP = new KeyMapping(
            getKeyId("bio_control_up"),
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_W,
            KEY_CATEGORY_BM
    );
    @Translation.ZH_CN("活体控制 下")
    public static final KeyMapping BIO_CONTROL_DOWN = new KeyMapping(
            getKeyId("bio_control_down"),
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_S,
            KEY_CATEGORY_BM
    );

    private static String getKeyId(String name){
        return ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID, name).toLanguageKey("key");
    }
    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ErErKeyBindings.PREV_BIO_CAMERA_KEY);
        event.register(ErErKeyBindings.NEXT_BIO_CAMERA_KEY);
        event.register(ErErKeyBindings.BIO_CONTROL_UP);
        event.register(ErErKeyBindings.BIO_CONTROL_DOWN);
    }
}
