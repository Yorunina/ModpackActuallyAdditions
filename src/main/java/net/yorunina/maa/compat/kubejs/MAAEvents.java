package net.yorunina.maa.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import net.yorunina.maa.compat.kubejs.events.CauldronCraftEventJS;
import net.yorunina.maa.compat.kubejs.events.TextInputTaskSubmitJS;

public class MAAEvents {
    public static EventGroup MAA_GROUP = EventGroup.of("MAAEvents");
    public static EventHandler TEXT_INPUT_TASK_SUBMIT = MAA_GROUP
            .server("textInputTaskSubmit", () -> TextInputTaskSubmitJS.class)
            .extra(Extra.STRING);

    public static EventHandler CAULDRON_CRAFT_EVENT = MAA_GROUP
            .server("cauldronCraft", () -> CauldronCraftEventJS.class);
}
