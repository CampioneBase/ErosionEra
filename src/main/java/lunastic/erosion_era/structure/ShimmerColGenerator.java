package lunastic.erosion_era.structure;

import lunastic.erosion_era.ErosionEraMod;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.*;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.List;
import java.util.Random;

public class ShimmerColGenerator {

    public static final Identifier I = new Identifier(ErosionEraMod.ID, "i");

    public static void addPiece(StructureManager manager, BlockPos pos, BlockRotation rotation,
                                List<StructurePiece> pieces, Random random){
        pieces.add(new ShimmerColGenerator.Piece(manager, I, pos, rotation));
    }

    public static class Piece
    extends SimpleStructurePiece{
        private final Identifier identifier;
        private final BlockRotation rotation;

        public Piece(StructureManager structureManager, Identifier identifier,
                     BlockPos pos, BlockRotation rotation) {
            super(StructurePieceTypes.SHIMMER_COL, 0);
            this.rotation = rotation;
            this.identifier = identifier;
            this.setStructureData(structureManager);
        }

        public Piece(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceTypes.SHIMMER_COL, compoundTag);
            this.identifier = new Identifier(compoundTag.getString("Template"));
            this.rotation = BlockRotation.valueOf(compoundTag.getString("Rot"));
            this.setStructureData(structureManager);
        }

        @Override
        protected void toNbt(CompoundTag tag) {
            super.toNbt(tag);
        }

        public void setStructureData(StructureManager structureManager) {
            Structure structure = structureManager.getStructureOrBlank(this.identifier);
            StructurePlacementData data = (new StructurePlacementData())
                    .setRotation(this.rotation)
                    .setMirror(BlockMirror.NONE)
                    .setPosition(this.pos)
                    .addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
            this.setStructureData(structure, this.pos, data);
        }

        @Override
        protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess serverWorldAccess,
                                      Random random, BlockBox boundingBox) {
            serverWorldAccess.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }

        @Override
        public boolean generate(StructureWorldAccess structureWorldAccess, StructureAccessor structureAccessor,
                                ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            int y = structureWorldAccess.getTopY(Heightmap.Type.WORLD_SURFACE_WG, this.pos.getX() + 8, this.pos.getZ() + 8);
            this.pos = this.pos.add(0, y - 1, 0);
            return super.generate(structureWorldAccess, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, blockPos);
        }
    }
}
