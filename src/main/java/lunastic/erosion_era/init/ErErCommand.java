package lunastic.erosion_era.init;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lunastic.erosion_era.ErosionEraMod;
import lunastic.erosion_era.server.command.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ErErCommand {

    public static final String ENTER_TEXT = "lee";

    public static void init(){
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            ErosionEraMod.LOGGER.info("GameCommands Loading......");
            ErosionCommand.register(dispatcher);
        }));
    }
}
