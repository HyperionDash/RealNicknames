package com.nitron.nickname.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.nitron.nickname.RealNickname;
import com.nitron.nickname.cca.PlayerNickComponent;
import com.nitron.nickname.config.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;

public class NicknameCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("nick")
                .then(Commands.literal("set")
                        .then(Commands.argument("nick", StringArgumentType.string())
                                .executes(commandContext -> {
                                    CommandSourceStack source = (CommandSourceStack) commandContext.getSource();
                                    Player player = source.getPlayer();
                                    PlayerNickComponent component = PlayerNickComponent.get(player);
                                    String nick = StringArgumentType.getString(commandContext, "nick");
                                    if(nick.isEmpty()){
                                        component.setNickname("");
                                        component.setHasNickname(false);
                                        player.sendSystemMessage(Component.literal("Removed your nickname").withStyle(ChatFormatting.GRAY));
                                    } else {
                                        int length = nick.length();
                                        if(length > Config.maxNick){
                                            player.sendSystemMessage(Component.literal("Nickname exceeds character limit").withStyle(ChatFormatting.RED));
                                        } else if(nick.equals(" ")) {
                                            player.sendSystemMessage(Component.literal("Nickname cannot be empty").withStyle(ChatFormatting.RED));
                                        } else {
                                            component.setNickname(nick);
                                            component.setHasNickname(true);
                                            player.sendSystemMessage(Component.literal("Set nickname to: " + nick).withStyle(ChatFormatting.GRAY));
                                        }
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("clear")
                        .executes(commandContext -> {
                            CommandSourceStack source = (CommandSourceStack) commandContext.getSource();
                            Player player = source.getPlayer();
                            PlayerNickComponent component = PlayerNickComponent.get(player);
                            component.setHasNickname(false);
                            component.setNickname("");
                            player.sendSystemMessage(Component.literal("Removed your nickname").withStyle(ChatFormatting.GRAY));
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("color").requires((serverCommandSource -> {
                            return Config.showColor;
                        })).then(Commands.argument("color", StringArgumentType.string())
                                .executes(commandContext -> {
                                    String color = StringArgumentType.getString(commandContext, "color");
                                    CommandSourceStack source = commandContext.getSource();
                                    Player player = source.getPlayer();
                                    PlayerNickComponent component = PlayerNickComponent.get(player);
                                    if(!Config.showColor){
                                        player.sendSystemMessage(Component.literal("Changing your color has been disabled")
                                                .withStyle(ChatFormatting.GRAY));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    if (RealNickname.isValidHex(color)) {
                                        component.setColor(color);
                                        component.setHasColor(true);
                                        int fin = RealNickname.convertToHex(color);

                                        player.sendSystemMessage(Component.literal("Changed name color to: ")
                                                .withStyle(ChatFormatting.GRAY)
                                                .append(Component.literal("0x" + color.toUpperCase())
                                                        .setStyle(Style.EMPTY.withColor(fin)))
                                        );

                                    } else {
                                        component.setColor("");
                                        component.setHasColor(false);
                                        player.sendSystemMessage(Component.literal("Should be a valid hex code, removed your color.")
                                                .withStyle(ChatFormatting.GRAY));
                                    }
                                    return Command.SINGLE_SUCCESS;
                                }))));
    }
}
