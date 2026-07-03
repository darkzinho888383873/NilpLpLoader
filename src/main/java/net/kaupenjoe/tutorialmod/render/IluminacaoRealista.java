package com.nilp.loader.render;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class IluminacaoRealista {
    
    private static final float LUZ_AMBIENTE_MINIMA = 0.15f;
    private static final float LUZ_AMBIENTE_MAXIMA = 0.4f;
    private static final float INTENSIDADE_SOL = 1.2f;
    
    private static float[] corAmbienteCache = {0.4f, 0.45f, 0.5f};
    private static long ultimaAtualizacao = 0;
    
    public static void atualizarIluminacao() {
        long agora = System.currentTimeMillis();
        if (agora - ultimaAtualizacao < 1000) return;
        ultimaAtualizacao = agora;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        
        Level world = mc.level;
        long tempoDia = world.getDayTime() % 24000;
        float progressoDia = tempoDia / 24000.0f;
        
        float[] corManha = {1.0f, 0.9f, 0.7f};
        float[] corDia = {1.0f, 1.0f, 0.95f};
        float[] corTarde = {0.95f, 0.85f, 0.65f};
        float[] corNoite = {0.3f, 0.35f, 0.6f};
        
        float[] corAtual;
        
        if (progressoDia < 0.25f) {
            float t = progressoDia / 0.25f;
            corAtual = interpolarCores(corNoite, corManha, suavizar(t));
        } else if (progressoDia < 0.45f) {
            corAtual = corDia;
        } else if (progressoDia < 0.55f) {
            float t = (progressoDia - 0.45f) / 0.1f;
            corAtual = interpolarCores(corDia, corTarde, suavizar(t));
        } else if (progressoDia < 0.75f) {
            corAtual = corTarde;
        } else {
            float t = (progressoDia - 0.75f) / 0.25f;
            corAtual = interpolarCores(corTarde, corNoite, suavizar(t));
        }
        
        if (world.isRaining()) {
            float intensidadeChuva = world.getRainLevel(1.0f);
            float[] corChuva = {0.5f, 0.55f, 0.65f};
            corAtual = interpolarCores(corAtual, corChuva, intensidadeChuva * 0.7f);
        }
        
        if (world.isThundering()) {
            float[] corTempestade = {0.3f, 0.35f, 0.45f};
            corAtual = interpolarCores(corAtual, corTempestade, 0.5f);
        }
        
        corAmbienteCache = corAtual;
    }
    
    public static float[] calcularLuzPosicao(BlockPos pos) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return new float[]{0.5f, 0.5f, 0.5f};
        
        Level world = mc.level;
        int luzCeu = world.getBrightness(LightLayer.SKY, pos);
        float luzCeuNorm = luzCeu / 15.0f;
        int luzBloco = world.getBrightness(LightLayer.BLOCK, pos);
        float luzBlocoNorm = luzBloco / 15.0f;
        
        float luzTotal = LUZ_AMBIENTE_MINIMA + (luzCeuNorm * INTENSIDADE_SOL) + (luzBlocoNorm * 0.8f);
        luzTotal = Math.min(LUZ_AMBIENTE_MAXIMA, Math.max(LUZ_AMBIENTE_MINIMA, luzTotal));
        
        return new float[]{
            corAmbienteCache[0] * luzTotal,
            corAmbienteCache[1] * luzTotal,
            corAmbienteCache[2] * luzTotal
        };
    }
    
    public static float getBrilhoAdaptativo() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return 1.0f;
        
        BlockPos posJogador = mc.player.blockPosition();
        int luzCeu = mc.level.getBrightness(LightLayer.SKY, posJogador);
        
        if (luzCeu < 5) {
            return 1.0f + (5 - luzCeu) * 0.05f;
        }
        return 1.0f;
    }
    
    public static float[] getCorCeuRealista() {
        atualizarIluminacao();
        return corAmbienteCache.clone();
    }
    
    private static float[] interpolarCores(float[] cor1, float[] cor2, float t) {
        return new float[]{
            cor1[0] + (cor2[0] - cor1[0]) * t,
            cor1[1] + (cor2[1] - cor1[1]) * t,
            cor1[2] + (cor2[2] - cor1[2]) * t
        };
    }
    
    private static float suavizar(float t) {
        return t * t * (3 - 2 * t);
    }
}
