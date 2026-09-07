package com.nitron.nickname.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.nitron.nickname.RealNickname;
import com.nitron.nickname.cca.PlayerNickComponent;
import com.nitron.nickname.config.NicknameConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;

public class NickCommand {
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
                                        player.sendSystemMessage(Component.translatable("commands.nickname.nick.clear").withStyle(ChatFormatting.GRAY));
                                        RealNickname.LOGGER.info(player+" cleared their Nickname");
                                    } else {
                                        int length = nick.length();
                                        if(length > NicknameConfig.maxNick){
                                            player.sendSystemMessage(Component.translatable("commands.nickname.nick.maxNick").withStyle(ChatFormatting.RED));
                                        } else if(nick.equals(" ")) {
                                            player.sendSystemMessage(Component.translatable("commands.nickname.nick.nickEmpty").withStyle(ChatFormatting.RED));
                                        } else {
                                            component.setNickname(nick);
                                            component.setHasNickname(true);
                                            player.sendSystemMessage(Component.translatable("commands.nickname.nick.setNick").withStyle(ChatFormatting.GRAY).append(nick));
                                            RealNickname.LOGGER.info(player+" set their Nickname to "+nick);
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
                            player.sendSystemMessage(Component.translatable("commands.nickname.nick.clear").withStyle(ChatFormatting.GRAY));
                            RealNickname.LOGGER.info(player+" cleared their Nickname");
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("color").requires((serverCommandSource -> {
                            return NicknameConfig.showColor;
                        })).then(Commands.argument("color", StringArgumentType.string())
                                .executes(commandContext -> {
                                    String color = StringArgumentType.getString(commandContext, "color");
                                    CommandSourceStack source = commandContext.getSource();
                                    Player player = source.getPlayer();
                                    PlayerNickComponent component = PlayerNickComponent.get(player);
                                    if(!NicknameConfig.showColor){
                                        player.sendSystemMessage(Component.translatable("commands.nickname.nick.noColor")
                                                .withStyle(ChatFormatting.GRAY));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    if (RealNickname.isValidHex(color)) {
                                        component.setColor(color);
                                        component.setHasColor(true);
                                        int fin = RealNickname.convertToHex(color);

                                        player.sendSystemMessage(Component.translatable("commands.nickname.nick.setColor")
                                                .withStyle(ChatFormatting.GRAY)
                                                .append(Component.literal("0x" + color.toUpperCase())
                                                        .setStyle(Style.EMPTY.withColor(fin)))
                                        );

                                    } else {
                                        component.setColor("");
                                        component.setHasColor(false);
                                        player.sendSystemMessage(Component.translatable("commands.nickname.nick.invaidHex")
                                                .withStyle(ChatFormatting.GRAY));
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("other").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.literal("set")
                                        .then(Commands.argument("nick", StringArgumentType.string())
                                                .executes(commandContext -> {
                                                    Player player = EntityArgument.getPlayer(commandContext, "target");
                                                    CommandSourceStack source = (CommandSourceStack) commandContext.getSource();
                                                    Player user = source.getPlayer();
                                                    PlayerNickComponent component = PlayerNickComponent.get(player);
                                                    String nick = StringArgumentType.getString(commandContext, "nick");
                                                    if(nick.isEmpty()){
                                                        component.setNickname("");
                                                        component.setHasNickname(false);
                                                        player.sendSystemMessage(Component.translatable("commands.nickname.nick.clear").withStyle(ChatFormatting.GRAY));
                                                        RealNickname.LOGGER.info(user+" cleared "+player+"'s Nickname");
                                                    } else {
                                                        int length = nick.length();
                                                        if(length > NicknameConfig.maxNick){
                                                            player.sendSystemMessage(Component.translatable("commands.nickname.nick.maxNick").withStyle(ChatFormatting.RED));
                                                        } else if(nick.equals(" ")) {
                                                            player.sendSystemMessage(Component.translatable("commands.nickname.nick.nickEmpty").withStyle(ChatFormatting.RED));
                                                        } else {
                                                            component.setNickname(nick);
                                                            component.setHasNickname(true);
                                                            player.sendSystemMessage(Component.translatable("commands.nickname.nick.setNick").withStyle(ChatFormatting.GRAY).append(nick));
                                                            RealNickname.LOGGER.info(user+" set "+player+"'s Nickname to "+nick);
                                                        }
                                                    }
                                                    return Command.SINGLE_SUCCESS;
                                                })))
                                .then(Commands.literal("clear")
                                        .executes(commandContext -> {
                                            Player player = EntityArgument.getPlayer(commandContext, "target");
                                            CommandSourceStack source = (CommandSourceStack) commandContext.getSource();
                                            Player user = source.getPlayer();
                                            PlayerNickComponent component = PlayerNickComponent.get(player);
                                            component.setHasNickname(false);
                                            component.setNickname("");
                                            player.sendSystemMessage(Component.translatable("commands.nickname.nick.clear").withStyle(ChatFormatting.GRAY));
                                            RealNickname.LOGGER.info(user+" cleared "+player+"'s Nickname");
                                            return Command.SINGLE_SUCCESS;
                                        }))
                                .then(Commands.literal("color").requires((serverCommandSource -> {
                                    return NicknameConfig.showColor;
                                })).then(Commands.argument("color", StringArgumentType.string())
                                        .executes(commandContext -> {
                                            String color = StringArgumentType.getString(commandContext, "color");
                                            Player player = EntityArgument.getPlayer(commandContext, "target");
                                            PlayerNickComponent component = PlayerNickComponent.get(player);
                                            if(!NicknameConfig.showColor){
                                                player.sendSystemMessage(Component.translatable("commands.nickname.nick.noColor")
                                                        .withStyle(ChatFormatting.GRAY));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            if (RealNickname.isValidHex(color)) {
                                                component.setColor(color);
                                                component.setHasColor(true);
                                                int fin = RealNickname.convertToHex(color);

                                                player.sendSystemMessage(Component.translatable("commands.nickname.nick.setColor")
                                                        .withStyle(ChatFormatting.GRAY)
                                                        .append(Component.literal("0x" + color.toUpperCase())
                                                                .setStyle(Style.EMPTY.withColor(fin)))
                                                );

                                            } else {
                                                component.setColor("");
                                                component.setHasColor(false);
                                                player.sendSystemMessage(Component.translatable("commands.nickname.nick.invaidHex")
                                                        .withStyle(ChatFormatting.GRAY));
                                            }
                                            return Command.SINGLE_SUCCESS;
                                        }))))
                ));
    }
}
