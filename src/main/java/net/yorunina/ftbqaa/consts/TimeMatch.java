package net.yorunina.ftbqaa.consts;

import dev.ftb.mods.ftblibrary.config.NameMap;

public enum TimeMatch {
    GAME("game"),
    SYSTEM("system"),
    AUTO("auto");

    public static final NameMap<TimeMatch> NAME_MAP = NameMap.of(AUTO, values()).baseNameKey("ftbquests.ftbqaa.timematch").create();
    public String name;

    private TimeMatch(String name) {
        this.name = name;
    }
}
