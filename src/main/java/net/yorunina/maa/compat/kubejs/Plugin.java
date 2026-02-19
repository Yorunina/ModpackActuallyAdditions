package net.yorunina.maa.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;

import static net.yorunina.maa.compat.kubejs.MAAEvents.MAA_GROUP;

public class Plugin extends KubeJSPlugin {
    @Override
    public void registerEvents() {
        MAA_GROUP.register();
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("MAAUtils", MAAUtils.INSTANCE);
    }
}
