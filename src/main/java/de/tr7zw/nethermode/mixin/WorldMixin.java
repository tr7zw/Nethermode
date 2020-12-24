package de.tr7zw.nethermode.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.gen.feature.StructureFeature;

@Mixin(ServerWorld.class)
public abstract class WorldMixin{

	
	private BlockPos spawnPosCache = null;
	
	@Inject(method = "getSpawnPos", at = @At(value = "HEAD"), cancellable = true)
	public BlockPos getSpawnPos(CallbackInfoReturnable<BlockPos> info) {
		if(((ServerWorld)(Object)this).getRegistryKey() == World.OVERWORLD) {
			if(spawnPosCache != null)return spawnPosCache;
			ChunkManager manager = ((ServerWorld)(Object)this).getChunkManager();
			if(manager instanceof ServerChunkManager && (Object)this instanceof ServerWorld) {
				ServerWorld world = (ServerWorld)(Object)this;
				WorldProperties properties = ((ServerWorld)(Object)this).getLevelProperties();
				BlockPos blockPos = new BlockPos(properties.getSpawnX(), properties.getSpawnY(), properties.getSpawnZ());
				BlockPos pos = ((ServerChunkManager)manager).getChunkGenerator().locateStructure(world, StructureFeature.RUINED_PORTAL, blockPos, 100, false);
				spawnPosCache = pos;
				info.setReturnValue(pos);
				return pos;
			}
		}
		return null;
	}
	
}
