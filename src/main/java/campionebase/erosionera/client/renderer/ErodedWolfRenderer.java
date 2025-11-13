package campionebase.erosionera.client.renderer;

import campionebase.erosionera.client.model.ErodedWolfModel;
import campionebase.erosionera.client.model.animations.ErodedWolfAnimation;
import campionebase.erosionera.entity.ErodedWolf;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.HierarchicalModel;

public class ErodedWolfRenderer extends MobRenderer<ErodedWolf, ErodedWolfModel<ErodedWolf>> {
    public ErodedWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new AnimatedModel(context.bakeLayer(ErodedWolfModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(ErodedWolf entity) {
        return ResourceLocation.parse("erosionera:textures/entity/eroded_wolf.png");
    }

    private static final class AnimatedModel extends ErodedWolfModel<ErodedWolf> {
        private final ModelPart root;
        private final HierarchicalModel animator = new HierarchicalModel<ErodedWolf>() {
            @Override
            public ModelPart root() {
                return root;
            }

            @Override
            public void setupAnim(ErodedWolf entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
                this.root().getAllParts().forEach(ModelPart::resetPose);
                this.animateWalk(ErodedWolfAnimation.walk, limbSwing, limbSwingAmount, 2.4f, 2f);
            }
        };

        public AnimatedModel(ModelPart root) {
            super(root);
            this.root = root;
        }

        @Override
        public void setupAnim(ErodedWolf entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }
}