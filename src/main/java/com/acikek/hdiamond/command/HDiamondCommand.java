package com.acikek.hdiamond.command;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.api.HazardDiamondAPI;
import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.network.HDNetworking;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class HDiamondCommand {

    public static int executeData(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var nbt = NbtCompoundArgumentType.getNbtCompound(context, "nbt");
        var data = HazardData.fromNbt(nbt);
        return execute(context, data);
    }

    public static int executeId(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var id = IdentifierArgumentType.getIdentifier(context, "id");
        return execute(context, HazardDiamondAPI.getData(id));
    }

    public static int execute(CommandContext<ServerCommandSource> context, HazardData data) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "targets");
        HDNetworking.s2cOpenScreen(players, data);
        return 0;
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("hdiamond")
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                .then(CommandManager.literal("data")
                                        .then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
                                                .executes(HDiamondCommand::executeData)))
                                .then(CommandManager.literal("id")
                                        .then(CommandManager.argument("id", IdentifierArgumentType.identifier())
                                                .executes(HDiamondCommand::executeId))))
                        .requires(source -> source.hasPermissionLevel(4))));
    }
}
