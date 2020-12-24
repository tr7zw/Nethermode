package de.tr7zw.nethermode.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

	@Shadow
	private MinecraftServer server;

	@Redirect(method = "createPlayer", at = @At(value = "INVOKE", target = "net/minecraft/server/MinecraftServer.getOverworld()Lnet/minecraft/server/world/ServerWorld;"))
	protected ServerWorld replaceStartWorld(MinecraftServer server) {
		return server.getWorld(World.NETHER);
	}

	private Optional<?> lastSpawnPos = Optional.empty();
	private ServerPlayerEntity lastPlayer = null;

	@Inject(method = "respawnPlayer", at = @At(value = "HEAD"))
	public ServerPlayerEntity respawnPlayer(ServerPlayerEntity player, boolean bl,
			CallbackInfoReturnable<ServerPlayerEntity> info) {
		lastPlayer = player;
		BlockPos blockPos = player.getSpawnPointPosition();
		boolean bl2 = player.isSpawnPointSet();
		if (blockPos != null) {
			lastSpawnPos = PlayerEntity.findRespawnPosition(this.server.getWorld(player.getSpawnPointDimension()),
					blockPos, player.getSpawnAngle(), bl2, bl);
		} else {
			lastSpawnPos = Optional.empty();
		}
		return null;
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
