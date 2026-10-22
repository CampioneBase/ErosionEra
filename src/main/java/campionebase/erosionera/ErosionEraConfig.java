package campionebase.erosionera;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public final class ErosionEraConfig {
    private ErosionEraConfig(){ throw new UnsupportedOperationException(); }
//    public static class Client{
//        private Client(ForgeConfigSpec.Builder builder){
//
//        }
//    }
//    public static class Common{
//        private Common(ForgeConfigSpec.Builder builder){
//
//        }
//    }
    public static class Server{
        public ForgeConfigSpec.EnumValue<RespawnMethodWithErosionStorm> respawnMethodWithErosionStorm;

        private Server(ForgeConfigSpec.Builder builder){
            builder.push("server");
            respawnMethodWithErosionStorm = builder
                    .comment(
                            "当玩家即将重生，重生点被侵蚀风暴覆盖时，选择重生的方法：",
                            "NONE=玩家可以立即在重生点重生——但是会提示重生点被风暴覆盖。",
                            "WAIT=玩家必须等待风暴结束才可以重生。",
                            "AWAY=玩家会尝试在风暴范围外重生。"
                    )
                    .defineEnum("respawnMethodWithErosionStorm", RespawnMethodWithErosionStorm.WAIT);
            builder.pop();
        }
    }

    public enum RespawnMethodWithErosionStorm {
        NONE, WAIT, AWAY
    }

    //region ================== 注册 ==========================
//    public static final Client CLIENT;
//    private static final ForgeConfigSpec CLIENT_SPEC;
//    public static final Common COMMON;
//    private static final ForgeConfigSpec COMMON_SPEC;
    public static final Server SERVER;
    private static final ForgeConfigSpec SERVER_SPEC;

    static {
//        final Pair<Client, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(Client::new);
//        CLIENT = client.getLeft();
//        CLIENT_SPEC = client.getRight();
//        final Pair<Common, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(Common::new);
//        COMMON = common.getLeft();
//        COMMON_SPEC = common.getRight();
        final Pair<Server, ForgeConfigSpec> server = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER = server.getLeft();
        SERVER_SPEC = server.getRight();
    }

    public static void register(ModLoadingContext context){
//        context.registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
//        context.registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
        context.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
    }
    //endregion
}
