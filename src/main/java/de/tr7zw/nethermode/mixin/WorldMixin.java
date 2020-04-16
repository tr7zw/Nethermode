package de.tr7zw.nethermode.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;

@Mixin(World.class)
public abstract class WorldMixin{

	@Shadow
	public Dimension dimension;
	@Shadow
	protected LevelProperties properties;
	@Shadow
	public abstract ChunkManager getChunkManager();
	
	private BlockPos spawnPosCache = null;
	
	@Inject(method = "getSpawnPos", at = @At(value = "HEAD"), cancellable = true)
	public BlockPos getSpawnPos(CallbackInfoReturnable<BlockPos> info) {
		if(dimension.getType() == DimensionType.THE_NETHER) {
			if(spawnPosCache != null)return spawnPosCache;
			ChunkManager manager = getChunkManager();
			if(manager instanceof ServerChunkManager && (Object)this instanceof ServerWorld) {
				ServerWorld world = (ServerWorld)(Object)this;
				world.getForcedSpawnPoint();
				BlockPos blockPos = new BlockPos(this.properties.getSpawnX(), this.properties.getSpawnY(), this.properties.getSpawnZ());
				BlockPos pos = ((ServerChunkManager)manager).getChunkGenerator().locateStructure(world, "Ruined_Portal", blockPos, 100, false);
				spawnPosCache = pos;
				info.setReturnValue(pos);
				return pos;
			}
		}
		return null;
	}
	
}
