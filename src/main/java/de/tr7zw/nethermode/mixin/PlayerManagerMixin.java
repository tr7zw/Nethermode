package de.tr7zw.nethermode.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

	@Shadow
	private MinecraftServer server;

	@Redirect(method = "createPlayer", at = @At(value = "INVOKE", target = "net/minecraft/server/MinecraftServer.getWorld(Lnet/minecraft/world/dimension/DimensionType;)Lnet/minecraft/server/world/ServerWorld;"))
	protected ServerWorld replaceStartWorld(MinecraftServer server, DimensionType dimension) {
		if (dimension == DimensionType.OVERWORLD) {
			return server.getWorld(DimensionType.THE_NETHER);
		} else if (dimension == DimensionType.THE_NETHER) {
			return server.getWorld(DimensionType.OVERWORLD);
		} else {
			return server.getWorld(dimension);
		}
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
					blockPos, bl2, bl);
		} else {
			lastSpawnPos = Optional.empty();
		}
		return null;
	}

	@Redirect(method = "respawnPlayer", at = @At(value = "INVOKE", target = "net/minecraft/server/MinecraftServer.getWorld(Lnet/minecraft/world/dimension/DimensionType;)Lnet/minecraft/server/world/ServerWorld;"))
	protected ServerWorld respawnPlayer(MinecraftServer server, DimensionType dimension) {
		if (lastSpawnPos.isPresent()) {
			return server.getWorld(dimension);
		} else {
			lastPlayer.dimension = DimensionType.THE_NETHER;
			return server.getWorld(DimensionType.THE_NETHER);
		}
	}

}
