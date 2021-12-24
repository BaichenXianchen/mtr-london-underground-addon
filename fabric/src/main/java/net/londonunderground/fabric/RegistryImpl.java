package net.londonunderground.fabric;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RegistryImpl {

	public static boolean isModLoaded() {
		return FabricLoader.getInstance().isModLoaded("mtr");
	}

	public static CreativeModeTab getItemGroup(ResourceLocation id, Supplier<ItemStack> supplier) {
		return FabricItemGroupBuilder.build(id, supplier);
	}

	public static void registerPlayerJoinEvent(Consumer<ServerPlayer> consumer) {
		ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
			if (entity instanceof ServerPlayer) {
				consumer.accept((ServerPlayer) entity);
			}
		});
	}

	public static void registerPlayerQuitEvent(Consumer<ServerPlayer> consumer) {
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> consumer.accept(handler.player));
	}

	public static void registerServerStartingEvent(Consumer<MinecraftServer> consumer) {
		ServerLifecycleEvents.SERVER_STARTING.register(consumer::accept);
	}

	public static void registerServerStoppingEvent(Consumer<MinecraftServer> consumer) {
		ServerLifecycleEvents.SERVER_STOPPING.register(consumer::accept);
	}

	public static void registerTickEvent(Consumer<MinecraftServer> consumer) {
		ServerTickEvents.START_SERVER_TICK.register(consumer::accept);
	}

	public static void sendToPlayer(ServerPlayer player, ResourceLocation id, FriendlyByteBuf packet) {
		ServerPlayNetworking.send(player, id, packet);
	}
}
