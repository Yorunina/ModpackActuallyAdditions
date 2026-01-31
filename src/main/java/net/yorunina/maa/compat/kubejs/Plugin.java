package net.yorunina.maa.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class Plugin extends KubeJSPlugin {

    public static EventGroup MAAGROUP = EventGroup.of("MAAEvents");

    @Override
    public void registerEvents() {
        MAAGROUP.register();
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("MAAUtils", MAAUtils.INSTANCE);
    }
}
