package com.nitron.nickname.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class NicknameConfig extends MidnightConfig{
    public static final String MAIN = "main";

    @Entry(category = MAIN) public static boolean showColor = false;
    @Entry(category = MAIN) public static int maxNick = 32;
}
