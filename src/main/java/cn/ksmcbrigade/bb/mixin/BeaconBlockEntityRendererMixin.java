package cn.ksmcbrigade.bb.mixin;

import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeaconBlockEntityRenderer.class)
public class BeaconBlockEntityRendererMixin {
    @Mutable
    @Shadow @Final public static int MAX_BEAM_HEIGHT;

    @Unique
    private static float render$scale;

    @Inject(method = "<init>",at = @At("TAIL"))
    public void init(BlockEntityRendererFactory.Context ctx, CallbackInfo ci){
        MAX_BEAM_HEIGHT = 2048;
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/BeaconBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",at = @At("HEAD"))
    public void render(BeaconBlockEntity beaconBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci){
        float g = (float) MinecraftClient.getInstance().gameRenderer.getCamera().getPos().subtract(beaconBlockEntity.getPos().toCenterPos()).horizontalLength();
        render$scale = Math.max(1.0f, g / 96.0f);
    }

    @ModifyConstant(method = "render(Lnet/minecraft/block/entity/BeaconBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",constant = @Constant(intValue = 1024))
    public int render(int value){
        return MAX_BEAM_HEIGHT;
    }

    @ModifyConstant(method = "renderBeam(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;FJIII)V",constant = @Constant(floatValue = 0.2F))
    private static float renderBeam(float constant){
        return constant*render$scale;
    }

    @ModifyConstant(method = "renderBeam(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;FJIII)V",constant = @Constant(floatValue = 0.25F))
    private static float renderBeam2(float constant){
        return constant*render$scale;
    }


    @Inject(method = "getRenderDistance",at = @At(value = "RETURN"),cancellable = true)
    public void distance(CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(Integer.MAX_VALUE);
    }
}
