package com.nilp.loader.mixins;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.nilp.loader.render.FolhasRealistas;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;

@Mixin(ModelBlockRenderer.class)
public class MixinFolhasRender {
    
    @Inject(method = "tesselateBlock", at = @At("HEAD"))
    private void nilp_aplicarBalancoFolhas(BlockAndTintGetter level, BakedModel model, 
                                            BlockState state, BlockPos pos, 
                                            PoseStack poseStack, VertexConsumer consumer,
                                            boolean checkSides, Random random, long seed,
                                            int packedOverlay, 
                                            net.minecraftforge.client.model.data.ModelData modelData,
                                            net.minecraft.client.renderer.RenderType renderType,
                                            CallbackInfoReturnable<Boolean> cir) {
        
        if (FolhasRealistas.ehFolha(state)) {
            poseStack.pushPose();
            
            net.minecraft.world.phys.Vec3 balanco = FolhasRealistas.calcularBalanco(pos, Minecraft.getInstance().getFrameTime());
            poseStack.translate(balanco.x, balanco.y, balanco.z);
            
            long seedFolha = pos.asLong();
            float anguloX = (float) Math.sin(seedFolha * 0.1) * 0.02f;
            float anguloZ = (float) Math.cos(seedFolha * 0.1) * 0.02f;
            
            poseStack.mulPose(com.mojang.math.Axis.XP.rotation(anguloX));
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotation(anguloZ));
        }
    }
}
