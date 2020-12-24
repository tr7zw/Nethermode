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
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

	@Shadow
	public abstract ServerWorld getWorld(RegistryKey<World> key);
	@Shadow
	public abstract PlayerManager getPlayerManager();

	@Redirect(method = "prepareStartRegion", at = @At(value = "INVOKE", target = "net/minecraft/server/MinecraftServer.getOverworld()Lnet/minecraft/server/world/ServerWorld;"))
	protected ServerWorld replaceStartWorld(MinecraftServer server) {
		return server.getWorld(World.NETHER);
	}

	@Inject(method = "createWorlds", at = @At(value = "RETURN"))
	protected void createWorlds(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo info) {
		getPlayerManager().setMainWorld(getWorld(World.NETHER));
	}

}
