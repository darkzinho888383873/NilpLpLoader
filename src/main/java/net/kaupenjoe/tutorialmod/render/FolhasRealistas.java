package com.nilp.loader.render;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import java.util.Random;

public class FolhasRealistas {
    
    private static final Random RANDOM = new Random();
    private static float ventoGlobal = 0.0f;
    private static float tempoVento = 0.0f;
    
    private static final float FREQUENCIA_VENTO = 0.3f;
    private static final float AMPLITUDE_MAXIMA = 0.15f;
    
    public static void atualizarVento() {
        tempoVento += 0.016f;
        
        float ventoBase = (float) Math.sin(tempoVento * FREQUENCIA_VENTO) * AMPLITUDE_MAXIMA;
        float rajada = (float) Math.sin(tempoVento * 0.7f) * 0.05f;
        float microVento = (RANDOM.nextFloat() - 0.5f) * 0.02f;
        
        float intensidadeVento = ventoBase + rajada + microVento;
        ventoGlobal += (intensidadeVento - ventoGlobal) * 0.1f;
    }
    
    public static Vec3 calcularBalanco(BlockPos pos, float partialTicks) {
        Level world = Minecraft.getInstance().level;
        if (world == null) return Vec3.ZERO;
        
        double altura = pos.getY();
        double chuva = world.isRaining() ? 1.5 : 1.0;
        double tempestade = world.isThundering() ? 2.0 : 1.0;
        
        long seed = pos.asLong();
        float offsetUnico = (seed % 1000) / 1000.0f;
        double tempo = (System.currentTimeMillis() / 1000.0) + offsetUnico;
        
        double balancoX = Math.sin(tempo * 1.2 + altura * 0.5) * ventoGlobal * chuva * tempestade;
        double balancoZ = Math.cos(tempo * 1.1 + altura * 0.4) * ventoGlobal * chuva * tempestade;
        
        double microX = Math.sin(tempo * 2.5 + offsetUnico * 10) * 0.03;
        double microZ = Math.cos(tempo * 2.3 + offsetUnico * 8) * 0.03;
        
        double fatorAltura = Math.min(altura / 100.0, 1.0) * 0.8 + 0.2;
        
        return new Vec3(
            (balancoX + microX) * fatorAltura,
            Math.sin(tempo * 1.5) * 0.02,
            (balancoZ + microZ) * fatorAltura
        );
    }
    
    public static boolean ehFolha(BlockState state) {
        return state.getBlock() instanceof LeavesBlock;
    }
          }
