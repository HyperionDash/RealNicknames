package com.nitron.nickname;

import com.nitron.nickname.commands.NickCommand;
import com.nitron.nickname.config.Config;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.regex.Pattern;

public class RealNickname implements ModInitializer {
	public static final String MOD_ID = "nickname";

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> {
			NickCommand.register(dispatcher);
		});
		MidnightConfig.init(MOD_ID, Config.class);
	}

	private static final Pattern HEX_PATTERN = Pattern.compile("^#?[a-fA-F0-9]{6}$");

	public static boolean isValidHex(String str) {
		return str != null && HEX_PATTERN.matcher(str).matches();
	}

	public static int convertToHex(String hexString) {
		hexString = hexString.replace("#", ""); // Remove # if present
		return Integer.parseInt(hexString, 16); // Convert to an int
	}
}