package net.yorunina.maa.compat.kubejs;

import dev.ftb.mods.ftbquests.api.QuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import net.mehvahdjukaar.amendments.common.recipe.FluidAndItemCraftResult;
import net.minecraft.stats.StatFormatter;
import net.minecraft.world.inventory.ClickAction;
import net.yorunina.maa.client.RenderHelper;
import net.yorunina.maa.entities.AoeArrowEntity;
import net.yorunina.maa.entities.SeekingArrowEntity;

import java.util.Comparator;

import static net.yorunina.maa.compat.kubejs.MAAEvents.MAA_GROUP;

public class Plugin extends KubeJSPlugin {
    @Override
    public void registerEvents() {
        MAA_GROUP.register();
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("MAAUtils", MAAUtils.INSTANCE);
        event.add("FluidAndItemCraftResult", FluidAndItemCraftResult.class);
        event.add("ClickAction", ClickAction.class);
        event.add("RenderHelper", RenderHelper.class);
        event.add("SeekingArrowEntity", SeekingArrowEntity.class);
        event.add("AoeArrowEntity", AoeArrowEntity.class);
        event.add("TeamData", TeamData.class);
        event.add("QuestFile", QuestFile.class);
        event.add("Comparator", Comparator.class);
        event.add("StatFormatter", StatFormatter.class);
    }
}
