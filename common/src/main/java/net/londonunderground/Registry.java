package net.londonunderground;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Registry {

	@ExpectPlatform
	public static boolean isModLoaded() {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static CreativeModeTab getItemGroup(ResourceLocation id, Supplier<ItemStack> supplier) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void registerPlayerJoinEvent(Consumer<ServerPlayer> consumer) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void registerPlayerQuitEvent(Consumer<ServerPlayer> consumer) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void registerServerStartingEvent(Consumer<MinecraftServer> consumer) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void registerServerStoppingEvent(Consumer<MinecraftServer> consumer) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void registerTickEvent(Consumer<MinecraftServer> consumer) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void sendToPlayer(ServerPlayer player, ResourceLocation id, FriendlyByteBuf packet) {
		throw new AssertionError();
	}
}
