package com.pickaid.pmmojs.config;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PmmoJSConfigScreen {

    public static Screen create(final Screen screen) {
//        try {
////            return ModConfigScreen.create(screen);
//        } catch (Exception ignored) {
            return new ConfirmScreen(
                    accept -> {
                        if (accept) {
                            final Minecraft client = Minecraft.getInstance();
                            client.setScreen(new ConfirmLinkScreen(confirm -> {
                                if (confirm) {
                                    Util.getPlatform().openUri("https://discord.gg/uPJHxU46td");
                                }
                                client.setScreen(screen);
                            }, "https://discord.gg/uPJHxU46td", true));
                        } else {
                            Minecraft.getInstance().setScreen(screen);
                        }
                    },
                    Component.literal("Haven't set up.").withStyle(ChatFormatting.BOLD, ChatFormatting.RED),
                    Component.literal("Please wait for the completion"),
                    Component.literal("go to the discord for the newest update for events and configs."),
                    Component.translatable("gui.back")
            );
//        }
    }
}
