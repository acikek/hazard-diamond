package com.acikek.hdiamond.command;

import com.acikek.hdiamond.api.HazardDiamondAPI;
import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.load.HazardDataLoader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

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
        HazardDiamondAPI.open(players, data);
        return 0;
    }

    public static CompletableFuture<Suggestions> suggest(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        for (var id : HazardDataLoader.hazardData.keySet()) {
            builder.suggest(id.toString());
        }
        return builder.buildFuture();
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
                                                .suggests(HDiamondCommand::suggest)
                                                .executes(HDiamondCommand::executeId))))
                        .requires(source -> source.hasPermissionLevel(4))));
    }
}
