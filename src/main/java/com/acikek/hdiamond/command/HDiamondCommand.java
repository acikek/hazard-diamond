package com.acikek.hdiamond.command;

import com.acikek.hdiamond.HDiamond;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

public class HDiamondCommand {

    public static final Identifier CHANNEL = HDiamond.id("open");

    public static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayNetworking.send(context.getSource().getPlayer(), CHANNEL, PacketByteBufs.create());
        return 0;
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("hdiamond")
                        .executes(HDiamondCommand::execute)));
    }
}
