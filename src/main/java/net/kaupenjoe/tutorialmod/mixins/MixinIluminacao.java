package com.nilp.loader.mixins;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.nilp.loader.render.IluminacaoRealista;
import com.nilp.loader.render.FolhasRealistas;
import com.mojang.blaze3d.platform.GlStateManager;

@Mixin(LevelRenderer.class)
public class MixinIluminacao {
    
    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void nilp_preRenderizacao(CallbackInfo ci) {
        IluminacaoRealista.atualizarIluminacao();
        FolhasRealistas.atualizarVento();
        
        float brilho = IluminacaoRealista.getBrilhoAdaptativo();
        
        if (Minecraft.getInstance().player != null) {
            GlStateManager._clearColor(
                0.0f * brilho,
                0.0f * brilho,
                0.0f * brilho,
                1.0f
            );
        }
    }
    
    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void nilp_posRenderizacao(CallbackInfo ci) {
        GlStateManager._clearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }
}
