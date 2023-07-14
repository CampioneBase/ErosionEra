package lunastic.erosion_era.command.argument;

import com.mojang.brigadier.context.CommandContext;
import lunastic.erosion_era.basic.data.ErosionData;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;

public class ErosionChangeArgumentType
        extends EnumArgumentType<ErosionData.ErosionChangeType> {

    private ErosionChangeArgumentType() {
        super(ErosionData.ErosionChangeType.CODEC, ErosionData.ErosionChangeType::values);
    }
    public static ErosionChangeArgumentType create() {
        return new ErosionChangeArgumentType();
    }

    public static ErosionData.ErosionChangeType get(CommandContext<ServerCommandSource> context, String id) {
        return context.getArgument(id, ErosionData.ErosionChangeType.class);
    }
}