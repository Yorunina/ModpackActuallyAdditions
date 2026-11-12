# KubeJS DamageSource 扩展

MAA 提供全局绑定 `MAADamageSources`，创建实现 `IDamageSourceData` 的 `ScriptDamageSource`。
只有这种来源拥有按实例隔离的 `customData`；不通过 Mixin 扩展原版伤害源。
Java 不预设武器、蓄力或其他玩法字段，业务数据由 KJS 写入，伤害规则由数据包定义。

## 注册

在 `startup_scripts` 中一行注册，默认关联同名数据包伤害类型：

```js
// priority: 500
MAADamageSources.register('kubejs:damage_echo')
```

来源 ID 与伤害类型需要分开时，传入第二个参数即可：

```js
MAADamageSources.register('kubejs:example', 'minecraft:player_attack')
```

这是 MAA 的来源定义表，不是 Minecraft 的新注册表。注册只保存 ID，创建实例时才读取世界的
伤害类型注册表。同一 ID 重复注册时以后一次为准。修改 startup 注册后完整重启游戏；
普通 `/reload` 不会清空注册定义。

## 实例数据

- `MAADamageSources.supportsData(source)`：判断来源是否实现数据接口；原版普通来源为 false。
- 支持该能力的 `source.customData` / `source.getCustomData()`：返回可读写的 `CompoundTag`，首次读取时创建。
- `source.hasCustomData()`：判断是否有非空自定义数据，不会创建容器。
- `source.getItemData(key)` / `source.setItemData(key, stack)`：按实例保存 `ItemStack`，不经过 NBT。传入 `null` 等同移除。
- 数据属于该 Java 实例，不会自动保存到世界或同步到客户端。
- 工厂每次创建独立实例，不向原版缓存来源写入数据。

NBT 容器适合布尔、数字、字符串这类可序列化的短字段；`ItemStack` 用 `setItemData` 直接存对象
（存入前自行 `copy()`，避免和活体共享可变状态），不需要 `save` / `of` 往返。复制来源时这些
`ItemStack` 会逐个 `copy()`，副本与原来源不共享实例。

## 创建与复制

| 调用 | 行为 |
|---|---|
| `MAADamageSources.create(level, id)` | 按已注册的来源 ID 创建无来源实体的独立实例 |
| `MAADamageSources.create(level, id, attacker)` | 攻击者同时作为直接来源和归属来源 |
| `MAADamageSources.create(level, id, direct, attacker)` | 分别指定投射物等直接来源及归属攻击者 |
| `MAADamageSources.createAt(level, id, position)` | 指定来源位置，不绑定实体 |
| `MAADamageSources.copy(source)` | 复制 MAA 脚本来源的类型、实体、原始位置及自定义数据 |
| `MAADamageSources.copyWithType(level, id, source)` | 用另一注册定义复制 MAA 脚本来源 |

`id` 是 startup 注册的来源 ID。关联的伤害类型必须已经存在于世界的注册表；工厂不负责生成
数据包。未注册来源或缺失伤害类型会报错。
新建来源的数据为空；复制来源的数据进行深拷贝，修改副本不会改动原来源。
复制保留实体引用，不复制实体；保留原始位置，而非把实体当前位置固定下来。
复制只接受 `ScriptDamageSource`；原版来源与其他模组的专用法术来源不能传入复制接口。

## 示例：携带武器与蓄力快照

以下代码放在已有服务器事件处理器中，`attacker` 和 `target` 均为活体实体。
`$CompoundTag`、`$ItemStack` 沿用脚本域 `import.js` 中的集中导入。

```js
// 对应上方注册的 kubejs:example。
let source = MAADamageSources.create(target.level, 'kubejs:example', attacker)
let data = source.customData
data.putString('kind', 'example_segment')
data.putBoolean('full_charge', true)
source.setItemData('weapon', attacker.mainHandItem.copy())
target.attack(source, 10)
```

伤害事件中按同一个实例读取：

```js
let source = event.source
if (!MAADamageSources.supportsData(source)) return
let data = source.customData
if (data.getString('kind') != 'example_segment') return
let fullCharge = data.getBoolean('full_charge')
let weapon = source.getItemData('weapon') ?? attacker.mainHandItem
```

复制后只改副本：

```js
// source 是上方创建的 MAA 脚本来源。
let derived = MAADamageSources.copy(source)
derived.customData.putString('kind', 'another_segment')
```

`copy` 会一并复制物品数据，并对每个 `ItemStack` 调用 `copy()`，副本可独立修改。

数据包定义并在 startup 注册 `kubejs:damage_echo` 后，可以用该 ID 创建实例；
或调用 `copyWithType(target.level, 'kubejs:damage_echo', source)` 复制已有的 MAA 脚本来源。
工厂和数据容器不改变护甲、免疫、受伤间隔、难度倍率或派生伤害的递归规则。

## 验证范围

替换 MAA jar 后必须完整重启游戏。编译、打包及独立逻辑测试不能替代游戏中的 startup
注册、KubeJS 事件读取和战斗行为验收。
