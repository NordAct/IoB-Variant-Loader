package nordmods.iobvariantloader.mixin.common;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import nordmods.iobvariantloader.util.dragon_variant_spawner.DragonVariantSpawnerUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpawnPlacements.class)
public abstract class SpawnPlacementsMixin {
    @Unique private static EntityType<? extends Mob> entityTypeCapture;

    @Inject(method = "register", at = @At("HEAD"))
    private static <T extends Mob> void modifyDragonSpawnPredicate(EntityType<T> pEntityType, SpawnPlacements.Type pDecoratorType, Heightmap.Types pHeightMapType, SpawnPlacements.SpawnPredicate<T> pDecoratorPredicate, CallbackInfo ci) {
        entityTypeCapture = pEntityType;
    }

    //let the higher forces forgive me for my sins
    @ModifyArg(method = "register", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/SpawnPlacements$Data;<init>(Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/world/entity/SpawnPlacements$Type;Lnet/minecraft/world/entity/SpawnPlacements$SpawnPredicate;)V"), index = 2)
    private static <T extends Mob> SpawnPlacements.SpawnPredicate<T> modifyDragonSpawnPredicate(SpawnPlacements.SpawnPredicate<T> pPredicate) {
        if (!(entityTypeCapture != null && entityTypeCapture.getRegistryName() != null && entityTypeCapture.getRegistryName().getNamespace().equals("isleofberk"))) return pPredicate;
        return ((pEntityType, pServerLevel, pSpawnType, pPos, pRandom) -> {
            boolean hasVariants = DragonVariantSpawnerUtil.hasNaturalVariantsForSpot(entityTypeCapture.getRegistryName().getPath(), pServerLevel, pPos);
            return hasVariants && pPredicate.test(pEntityType, pServerLevel, pSpawnType, pPos, pRandom);
        });
    }
}
