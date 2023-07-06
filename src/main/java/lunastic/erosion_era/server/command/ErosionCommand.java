package lunastic.erosion_era.server.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import lunastic.erosion_era.command.argument.ErosionChangeArgumentType;
import lunastic.erosion_era.data.ErosionData.*;
import lunastic.erosion_era.data.PlayerData;
import net.minecraft.command.argument.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static lunastic.erosion_era.init.ErErCommand.ENTER_TEXT;
import static net.minecraft.server.command.CommandManager.literal;

public class ErosionCommand {

    // 命令目录名称
    public static final String KEY_COMMAND = "erosion";
    // 数据命令权限等级
    public static final int DATA_COMMAND_LEVEL = 2;
    // 注册命令树
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        //lee erosion
        dispatcher.register(literal(ENTER_TEXT).requires(source -> source.hasPermissionLevel(DATA_COMMAND_LEVEL)).then(literal(KEY_COMMAND)
                .executes(ErosionCommand::getHelp)
                //lee erosion help
                .then(literal("help")
                        .executes(ErosionCommand::getHelp)
                )
                //lee erosion data <player>
                .then(literal("data")
                        .requires(source -> source.hasPermissionLevel(DATA_COMMAND_LEVEL))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(source ->{
                                    PlayerEntity player = EntityArgumentType.getPlayer(source, "player");
                                    return showData(source, player);
                                })
                        )
                        .executes(source ->{
                            PlayerEntity player = source.getSource().getPlayer();
                            return showData(source, player);
                        })
                )
                //lee erosion clear [<player>]
                .then(literal("clear")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(source -> {
                                    getPlayerData(EntityArgumentType.getPlayer(source, "player"))
                                            .erosionData.clear();
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .executes(source -> {
                            getPlayerData(source.getSource().getPlayer())
                                    .erosionData.clear();
                            return Command.SINGLE_SUCCESS;
                        })
                )
                //lee erosion clean [<player>]
                .then(literal("clean")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(source -> {
                                    getPlayerData(EntityArgumentType.getPlayer(source, "player"))
                                            .erosionData.clean();
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .executes(source -> {
                            getPlayerData(source.getSource().getPlayer()).erosionData.clean();
                            return Command.SINGLE_SUCCESS;
                        })
                )
                //lee erosion add <value> [<player>]
                .then(literal("add")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(source -> {
                                            PlayerEntity player = EntityArgumentType.getPlayer(source, "player");
                                            int value = source.getArgument("value", Integer.class);
                                            getPlayerData(player).erosionData.add(value);
                                            source.getSource().sendMessage(Text.of(player.getName().getString() + " 的侵蚀度增加了 " + value));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                                .executes(source -> {
                                    PlayerEntity player = source.getSource().getPlayerOrThrow();
                                    int value = source.getArgument("value", Integer.class);
                                    getPlayerData(player).erosionData.add(value);
                                    source.getSource().sendMessage(Text.of(player.getName().getString() + " 的侵蚀度增加了 " + value));
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                //lee erosion level up <type> [<player>]
                //lee erosion level down <type> [<player>]
                .then(literal("level")
                        .then(literal("up")
                                .then(CommandManager.argument("type", ErosionChangeArgumentType.create())
                                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(source -> {
                                                    PlayerEntity player = EntityArgumentType.getPlayer(source, "player");
                                                    ErosionChangeType type = ErosionChangeArgumentType.get(source, "type");
                                                    getPlayerData(player).erosionData.levelUp(type);
                                                    source.getSource().sendMessage(Text.of(player.getName().getString() + " 的侵蚀等级提升了"));
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                        .executes(source -> {
                                            PlayerEntity player = source.getSource().getPlayer();
                                            ErosionChangeType type = ErosionChangeArgumentType.get(source, "type");
                                            getPlayerData(player).erosionData.levelUp(type);
                                            source.getSource().sendMessage(Text.of(player.getName().getString() + " 的侵蚀等级提升了"));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(literal("down")
                                .then(CommandManager.argument("type", ErosionChangeArgumentType.create())
                                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(source -> {
                                                    PlayerEntity player = EntityArgumentType.getPlayer(source, "player");
                                                    ErosionChangeType type = ErosionChangeArgumentType.get(source, "type");
                                                    getPlayerData(player).erosionData.levelDown(type);
                                                    source.getSource().sendMessage(Text.of(player.getName().getString() + " 的侵蚀等级降低了"));
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                        .executes(source -> {
                                            PlayerEntity player = source.getSource().getPlayer();
                                            ErosionChangeType type = ErosionChangeArgumentType.get(source, "type");
                                            getPlayerData(player).erosionData.levelDown(type);
                                            source.getSource().sendMessage(Text.of(player.getName().getString() + " 的侵蚀等级降低了"));
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                )
        ));
    }

    private static int getHelp(CommandContext<ServerCommandSource> source){
        source.getSource().sendMessage(Text.of("erosion help!"));
        return Command.SINGLE_SUCCESS;
    }

    private static int showData(CommandContext<ServerCommandSource> source, PlayerEntity player) throws CommandSyntaxException {
        PlayerData data = getPlayerData(player);
        source.getSource().sendMessage(Text.of("玩家 " +
                player.getName().getString() +
                " 侵蚀度为 " +
                data.erosionData.getErosion() +
                " 侵蚀等级为 " +
                data.erosionData.getErosionLevel()
        ));
        return Command.SINGLE_SUCCESS;
    }

    private static PlayerData getPlayerData(PlayerEntity player) throws CommandSyntaxException {
        if (player == null) throw new SimpleCommandExceptionType(Text.of("不存在玩家实体")).create();
        PlayerData data = PlayerData.get(player);
        if(data == null) throw new SimpleCommandExceptionType(Text.of("玩家缺少侵蚀数据")).create();
        return data;
    }
}
