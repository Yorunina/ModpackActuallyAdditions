package net.yorunina.maa.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.latvian.mods.kubejs.bindings.event.EntityEvents;
import dev.latvian.mods.kubejs.entity.LivingEntityDeathEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.unusual.block_factorys_bosses.entity.boss.yeti.YetiEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复 Bosses'Rise (block_factorys_bosses 2.1.2) 雪人 BOSS 在服务端主线程上
 * 因 getBlockState() 触发"同步加载区块"而导致的服务器看门狗超时 (ServerHangWatchdog)。
 *
 * todo 版本更新时记得检查是否还有这个问题，没问题就删了
 */
@Mixin(YetiEntity.class)
public abstract class MixinYetiEntity {

    @Inject(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/unusual/block_factorys_bosses/entity/boss/yeti/YetiEntity;dropAllDeathLoot(Lnet/minecraft/world/damagesource/DamageSource;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void maa$notifyKubeJSDeath(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        YetiEntity yeti = (YetiEntity) (Object) this;
        // This server-side branch is guarded by Yeti's own DEATH state. It
        // settles loot without die(), so notify the existing KJS listeners here.
        if (EntityEvents.DEATH.hasListeners()) {
            EntityEvents.DEATH.post(ScriptType.SERVER, yeti.getType(), new LivingEntityDeathEventJS(yeti, source));
        }
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private BlockState maa$yetigetBlockStateSafe(Level level, BlockPos pos, Operation<BlockState> original) {
        // 目标区块未加载时, 不要用 getBlockState() 去强制同步加载区块,
        // 否则会在服务端主线程卡住, 导致 tick 超时。
        if (pos == null || !level.hasChunkAt(pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return original.call(level, pos);
    }
}
