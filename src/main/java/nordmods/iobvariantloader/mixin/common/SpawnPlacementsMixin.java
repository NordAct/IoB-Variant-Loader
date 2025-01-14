package nordmods.iobvariantloader.mixin.common;

import com.GACMD.isleofberk.registery.ModEntities;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import nordmods.iobvariantloader.util.dragon_variant_spawner.DragonVariantSpawnerUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SpawnPlacements.class)
public abstract class SpawnPlacementsMixin {

    //let the higher forces forgive me for my sins
    @ModifyArg(method = "register", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/SpawnPlacements$Data;<init>(Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/world/entity/SpawnPlacements$Type;Lnet/minecraft/world/entity/SpawnPlacements$SpawnPredicate;)V"), index = 2)
    private static <T extends Mob> SpawnPlacements.SpawnPredicate<T> modifyDragonSpawnPredicate(SpawnPlacements.SpawnPredicate<T> pPredicate) {
        return ((pEntityType, pServerLevel, pSpawnType, pPos, pRandom) -> {
            if (!(pEntityType.getRegistryName() != null && pEntityType.getRegistryName().getNamespace().equals("isleofberk"))) return pPredicate.test(pEntityType, pServerLevel, pSpawnType, pPos, pRandom);
            boolean hasVariant = DragonVariantSpawnerUtil.hasNaturalVariantsForSpot(pEntityType.getRegistryName().getPath(), pServerLevel, pPos);
            return pEntityType.equals(ModEntities.SPEED_STINGER.get()) ? hasVariant && pPredicate.test(pEntityType, pServerLevel, pSpawnType, pPos, pRandom) : hasVariant;
        });
    }
}
