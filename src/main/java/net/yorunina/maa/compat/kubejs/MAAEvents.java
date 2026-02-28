package net.yorunina.maa.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import net.yorunina.maa.compat.kubejs.events.*;

public class MAAEvents {
    public static EventGroup MAA_GROUP = EventGroup.of("MAAEvents");
    public static EventHandler TEXT_INPUT_TASK_SUBMIT = MAA_GROUP
            .server("textInputTaskSubmit", () -> TextInputTaskSubmitJS.class)
            .extra(Extra.STRING);

    public static EventHandler CAULDRON_CRAFT_EVENT = MAA_GROUP
            .server("cauldronCraft", () -> CauldronCraftEventJS.class);
    public static EventHandler FTB_PLAYER_JOIN_PARTY_TEAM = MAA_GROUP
            .server("ftbPlayerJoinParty", () -> FTBPlayerJoinPartyJS.class).hasResult();
    public static EventHandler FTB_CREATE_PARTY = MAA_GROUP
            .server("ftbCreateParty", () -> FTBCreatePartyJS.class).hasResult();
    public static EventHandler FTB_QUEST_CHECK_REPEATABLE = MAA_GROUP
            .server("ftbQuestCheckRepeatable", () -> FTBQuestCheckRepeatableJS.class).hasResult().extra(Extra.STRING);
}
