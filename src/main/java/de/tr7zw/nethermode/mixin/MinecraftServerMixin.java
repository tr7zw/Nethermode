package de.tr7zw.nethermode.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

	@Shadow
	public abstract ServerWorld getWorld(DimensionType dimensionType);
	@Shadow
	public abstract PlayerManager getPlayerManager();

	@Redirect(method = "prepareStartRegion", at = @At(value = "INVOKE", target = "net/minecraft/server/MinecraftServer.getWorld(Lnet/minecraft/world/dimension/DimensionType;)Lnet/minecraft/server/world/ServerWorld;"))
	protected ServerWorld replaceStartWorld(MinecraftServer server, DimensionType dimension) {
		if (dimension == DimensionType.OVERWORLD) {
			return getWorld(DimensionType.THE_NETHER);
		} else if (dimension == DimensionType.THE_NETHER) {
			return getWorld(DimensionType.OVERWORLD);
		} else {
			return getWorld(dimension);
		}
	}

	@Inject(method = "createWorlds", at = @At(value = "RETURN"))
	protected void createWorlds(WorldSaveHandler worldSaveHandler, LevelProperties properties, LevelInfo levelInfo,
			WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo info) {
		getPlayerManager().setMainWorld(getWorld(DimensionType.THE_NETHER));
	}

}
