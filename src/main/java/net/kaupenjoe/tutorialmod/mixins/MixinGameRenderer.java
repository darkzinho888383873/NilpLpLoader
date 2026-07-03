package com.nilp.loader.mixins;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.CloudStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.nilp.loader.GerenciadorRAM;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    
    @Inject(method = "render", at = @At("HEAD"))
    private void nilp_forcarOtimizacao(float partialTick, long nanoTime, boolean renderLevel, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        
        if (mc.player != null && mc.level != null && !mc.isPaused()) {
            Options options = mc.options;
            boolean mudou = false;
            
            if (!options.enableVsync().get()) {
                options.enableVsync().set(true);
                mudou = true;
            }
            
            if (options.renderDistance().get() > 8) {
                options.renderDistance().set(8);
                mudou = true;
            }
            
            if (options.graphicsMode().get() != GraphicsStatus.FAST) {
                options.graphicsMode().set(GraphicsStatus.FAST);
                mudou = true;
            }
            
            if (options.particles().get() != ParticleStatus.MINIMAL) {
                options.particles().set(ParticleStatus.MINIMAL);
                mudou = true;
            }
            
            if (options.cloudStatus().get() != CloudStatus.OFF) {
                options.cloudStatus().set(CloudStatus.OFF);
                mudou = true;
            }
            
            if (mudou) {
                options.save();
            }
        }
    }
}
