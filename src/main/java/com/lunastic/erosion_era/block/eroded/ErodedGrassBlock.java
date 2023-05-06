package com.lunastic.erosion_era.block.eroded;

import com.lunastic.erosion_era.ErosionEraMod;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ErodedGrassBlock extends ErodedBlockBase{
    public ErodedGrassBlock() {
        super("eroded_grass_block", FabricBlockSettings
                .copyOf(Blocks.GRASS_BLOCK)
                .requiresTool()
        );
    }


    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        ErosionEraMod.LOGGER.info("Eroded Grass has been placed！" + placer.getName().asString());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        player.sendMessage(Text.of(player.getName().asString() + " clicks this block with " + player.getActiveItem().toString()), false);
        return ActionResult.PASS;
    }


}
