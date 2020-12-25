package de.tr7zw.nethermode.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

	@Shadow
	private MinecraftServer server;

	@Redirect(method = "createPlayer", at = @At(value = "INVOKE", target = "net/minecraft/server/MinecraftServer.getOverworld()Lnet/minecraft/server/world/ServerWorld;"))
	protected ServerWorld replaceStartWorld(MinecraftServer server) {
		return server.getWorld(World.NETHER);
	}
	
	@Redirect(method = "onPlayerConnect", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;OVERWORLD:Lnet/minecraft/util/registry/RegistryKey;"))
	public RegistryKey<World> onPlayerConnect() {
		return World.NETHER;
	}

	@Redirect(method = "respawnPlayer", at = @At(value = "INVOKE", target = "net/minecraft/server/MinecraftServer.getOverworld()Lnet/minecraft/server/world/ServerWorld;"))
	protected ServerWorld respawnPlayer(MinecraftServer server) {
		return server.getWorld(World.NETHER);
	}

}
