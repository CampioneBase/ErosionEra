package campionebase.erosionera.client.renderer;

import campionebase.erosionera.client.model.ModelErodedWolf;
import campionebase.erosionera.client.model.animations.ErodedWolfAnimation;
import campionebase.erosionera.entity.ErodedWolf;
import campionebase.erosionera.entity.ErodedWolfMaster;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ErodedWolfMasterRenderer extends MobRenderer<ErodedWolfMaster, ModelErodedWolf<ErodedWolfMaster>> {
    public ErodedWolfMasterRenderer(EntityRendererProvider.Context context) {
        super(context, new AnimatedModel(context.bakeLayer(ModelErodedWolf.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(ErodedWolfMaster entity) {
        return ResourceLocation.parse("erosionera:textures/entity/eroded_wolf.png");
    }

    @Override
    protected void scale(ErodedWolfMaster entity, PoseStack poseStack, float f) {
        poseStack.scale(1.8f, 1.8f, 1.8f);
    }

    private static final class AnimatedModel extends ModelErodedWolf<ErodedWolfMaster> {
        private final ModelPart root;
        private final HierarchicalModel animator = new HierarchicalModel<ErodedWolfMaster>() {
            @Override
            public ModelPart root() {
                return root;
            }

            @Override
            public void setupAnim(ErodedWolfMaster entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
                this.root().getAllParts().forEach(ModelPart::resetPose);
                this.animateWalk(ErodedWolfAnimation.walk, limbSwing, limbSwingAmount, 2.4f, 2f);
            }
        };

        public AnimatedModel(ModelPart root) {
            super(root);
            this.root = root;
        }

        @Override
        public void setupAnim(ErodedWolfMaster entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }
}