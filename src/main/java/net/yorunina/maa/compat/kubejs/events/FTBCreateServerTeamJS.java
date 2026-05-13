package net.yorunina.maa.compat.kubejs.events;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.commands.CommandSourceStack;

import java.util.UUID;

public class FTBCreateServerTeamJS extends EventJS {
    public CommandSourceStack source;
    public UUID teamUUID;
    public String name;
    public String description;
    public String color;

    public FTBCreateServerTeamJS(CommandSourceStack source, String name, String description, Color4I color, UUID teamUUID) {
        super();
        this.teamUUID = teamUUID;
        this.name = name;
        this.description = description;
        this.color = color.toString();
        this.source = source;
    }

    public CommandSourceStack getSource() {
        return source;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public UUID getTeamUUID() {
        return teamUUID;
    }
}
