package net.yorunina.maa.compat.kubejs;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.yorunina.maa.model.IDamageSourceData;
import net.yorunina.maa.model.ScriptDamageSource;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class MAADamageSources {
    public static final MAADamageSources INSTANCE = new MAADamageSources();
    private final Map<ResourceLocation, ResourceLocation> types = new HashMap<>();

    private MAADamageSources() {
    }

    @Info("在 startup 脚本中注册伤害来源，默认关联同名数据包伤害类型")
    public void register(ResourceLocation id) {
        register(id, id);
    }

    @Info("在 startup 脚本中注册伤害来源，并指定已有的数据包伤害类型")
    public void register(ResourceLocation id, ResourceLocation type) {
        types.put(id, type);
    }

    @Info("创建已注册的独立伤害来源")
    public ScriptDamageSource create(Level level, ResourceLocation id) {
        return create(level, id, null, null);
    }

    @Info("创建由攻击者直接造成的独立伤害来源")
    public ScriptDamageSource create(Level level, ResourceLocation id, @Nullable Entity attacker) {
        return create(level, id, attacker, attacker);
    }

    @Info("创建独立伤害来源；direct 为直接来源，attacker 为归属攻击者")
    public ScriptDamageSource create(Level level, ResourceLocation id, @Nullable Entity direct, @Nullable Entity attacker) {
        return new ScriptDamageSource(resolveType(level, id), direct, attacker, null);
    }

    @Info("创建来自指定位置的独立伤害来源")
    public ScriptDamageSource createAt(Level level, ResourceLocation id, Vec3 position) {
        return new ScriptDamageSource(resolveType(level, id), null, null, position);
    }

    @Info("判断来源是否支持 customData；不改变原版或其他模组的伤害来源")
    public boolean supportsData(DamageSource source) {
        return source instanceof IDamageSourceData;
    }

    @Info("复制 MAA 脚本伤害来源；自定义数据深拷贝，原来源不变")
    public ScriptDamageSource copy(ScriptDamageSource source) {
        return copy(source.typeHolder(), source);
    }

    @Info("使用已注册的来源定义复制 MAA 脚本伤害来源，保留实体、位置和 customData")
    public ScriptDamageSource copyWithType(Level level, ResourceLocation id, ScriptDamageSource source) {
        return copy(resolveType(level, id), source);
    }

    private Holder<DamageType> resolveType(Level level, ResourceLocation id) {
        ResourceLocation type = types.get(id);
        if (type == null) throw new IllegalArgumentException("Unregistered damage source: " + id);
        return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, type));
    }

    private static ScriptDamageSource copy(Holder<DamageType> type, ScriptDamageSource source) {
        ScriptDamageSource result = new ScriptDamageSource(type, source.getDirectEntity(), source.getEntity(), source.sourcePositionRaw());
        if (source.hasCustomData()) {
            result.getCustomData().merge(source.getCustomData());
        }
        result.copyObjectDataFrom(source);
        return result;
    }
}
