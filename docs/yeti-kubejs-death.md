# 雪怪的 KubeJS 死亡通知

Bosses' Rise 2.1.2 的雪怪在致命伤分支中手动计分、掉落，然后保留
0.1 生命值播放死亡动画，没有调用原版 `die()`。

`MixinYetiEntity` 在该分支的 `dropAllDeathLoot(source)` 调用之后，
直接派发 `EntityEvents.DEATH`，使用原始实体类型及 `DamageSource`
创建标准 `LivingEntityDeathEventJS`。现有脚本继续监听 `EntityEvents.death`。

执行节点由雪怪已有的死亡状态分支控制，不增加去重标记、缓存或独立事件。
上游的掉落、计分、动画和尸体处理保持原样。
通知不调用 `die()`，脚本取消通知不能撤回已经完成的上游结算。

更换 MAA jar 后必须完整重启 Minecraft，并实际验证近战、远程击败雪怪
能够触发原有死亡/击杀策略及寒冷结晶升级。构建成功不能代替游戏验证。
