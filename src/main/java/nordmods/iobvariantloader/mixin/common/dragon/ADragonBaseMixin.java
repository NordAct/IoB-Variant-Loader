package nordmods.iobvariantloader.mixin.common.dragon;

import com.GACMD.isleofberk.entity.AI.breed.DragonBreedGoal;
import com.GACMD.isleofberk.entity.AI.goal.FollowOwnerNoTPGoal;
import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.entity.dragons.gronckle.Gronckle;
import com.GACMD.isleofberk.entity.dragons.stinger.Stinger;
import com.GACMD.isleofberk.entity.dragons.triple_stryke.TripleStryke;
import com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase;
import com.GACMD.isleofberk.entity.eggs.entity.eggs.*;
import com.GACMD.isleofberk.registery.ModEntities;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import nordmods.iobvariantloader.IoBVariantLoader;
import nordmods.iobvariantloader.util.ResourceUtil;
import nordmods.iobvariantloader.util.VLDragonBreedGoal;
import nordmods.iobvariantloader.util.alt_navigation.AltFollowGoal;
import nordmods.iobvariantloader.util.alt_navigation.AltLandNavigation;
import nordmods.iobvariantloader.util.breeding_list.BreedingListUtil;
import nordmods.iobvariantloader.util.dragon_variant_spawner.DragonVariantSpawner;
import nordmods.iobvariantloader.util.dragon_variant_spawner.DragonVariantSpawnerUtil;
import nordmods.iobvariantloader.util.ducks.*;
import nordmods.iobvariantloader.util.extras.Extras;
import nordmods.iobvariantloader.util.extras.ExtrasUtil;
import nordmods.iobvariantloader.util.hitbox_redirect.HitboxRedirectUtil;
import nordmods.iobvariantloader.util.model_redirect.ModelRedirectUtil;
import nordmods.iobvariantloader.util.sound_redirect.SoundRedirectUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

@Mixin(ADragonBase.class)
public abstract class ADragonBaseMixin extends TamableAnimal implements VariantNameHelper, DragonModelCacheHelper, DragonSpeciesHelper, HitboxRedirectHelper, ModelSizeProvider, DefaultVariantNameHelper {
    @Shadow public abstract int getDragonVariant();

    @Shadow public abstract boolean isTitanWing();

    @Shadow public abstract double getX(double pScale);

    @Shadow public abstract double getY(double pScale);

    @Shadow public abstract double getZ(double pScale);

    @Unique private ResourceLocation modelLocationCache;
    @Unique private ResourceLocation textureLocationCache;
    @Unique private ResourceLocation animationLocationCache;
    @Unique private ResourceLocation saddleTextureLocationCache;
    @Unique private ResourceLocation glowLayerLocationCache;
    @Unique private boolean preventGlowLayer = false;
    @Unique private Component translationName;
    @Unique private EntityDimensions boxOverride;
    @Unique private EntityDimensions attackBoxOverride;
    @Unique private Vec3 attackBoxPos;
    @Unique private static final UUID VARIANT_BONUS_MODIFIER = UUID.fromString("7c152c39-d158-48ae-92c7-2655c8072705");
    @Unique private Float ogHealth; //bandaid fix for health reset
    protected ADragonBaseMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @SuppressWarnings("WrongEntityDataParameterClass")
    @Unique
    private static final EntityDataAccessor<String> VARIANT_NAME = SynchedEntityData.defineId(ADragonBase.class, EntityDataSerializers.STRING);

    @Unique
    public String getVariantName() {
        return entityData.get(VARIANT_NAME);
    }

    @Unique
    public void setVariantName(String variantName) {
        entityData.set(VARIANT_NAME, variantName);
    }

    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void saveVariantName(CompoundTag nbt, CallbackInfo ci) {
        nbt.putString("VariantName", getVariantName());
        String group = ExtrasUtil.getVariandGroup(getSpecies(false), getVariantName());
        if (group != null) {
            nbt.putString("VariantGroup", group);
        }
    }

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"))
    private void setOgHealth(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("Health")) ogHealth = nbt.getFloat("Health");
    }

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void readVariantName(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("VariantName")) setVariantName(nbt.getString("VariantName"));
        else setVariantName(getFromBaseVariant());
    }

    @Inject(method = "defineSynchedData()V", at = @At("TAIL"))
    private void defineVariantName(CallbackInfo ci) {
        entityData.define(VARIANT_NAME, "");
    }

    @Inject(method = "finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;", at = @At("HEAD"))
    private void assignVariantName(ServerLevelAccessor world, DifficultyInstance p_146747_, MobSpawnType p_146748_, SpawnGroupData p_146749_, CompoundTag p_146750_, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (getVariantName().isEmpty()) DragonVariantSpawnerUtil.assignVariant(world, this, true);
        setHealth(getMaxHealth()); //bandaid fix for health attribute modifier
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_CUSTOM_NAME.equals(key) || VARIANT_NAME.equals(key) || DATA_BABY_ID.equals(key)) {
            if (level.isClientSide()) resetCache();
            removeVariantModifiers();
            applyVariantModifiers();
            if (ogHealth != null) {
                setHealth(ogHealth);
                ogHealth = null;
            }
            resetHitboxData();
        }
    }

    @Override
    public void resetTranslationName() {
        translationName = null;
    }

    //All cache stuff should be called only from clientside... But I'm quite lazy to separate this mess
    public ResourceLocation getModelLocationCache() {
        return modelLocationCache;
    }
    public ResourceLocation getAnimationLocationCache() {
        return animationLocationCache;
    }
    public ResourceLocation getTextureLocationCache() {
        return textureLocationCache;
    }
    public ResourceLocation getSaddleTextureLocationCache() {
        return saddleTextureLocationCache;
    }
    public ResourceLocation getGlowLayerLocationCache() {
        return glowLayerLocationCache;
    }
    public void setModelLocationCache(ResourceLocation state) {
        modelLocationCache = state;
    }
    public void setAnimationLocationCache(ResourceLocation state) {
        animationLocationCache = state;
    }
    public void setTextureLocationCache(ResourceLocation state) {
        textureLocationCache = state;
    }
    public void setSaddleTextureLocationCache(ResourceLocation state) {
        saddleTextureLocationCache = state;
    }
    public void setGlowLayerLocationCache(ResourceLocation state) {
        glowLayerLocationCache = state;
    }

    public boolean shouldPreventGlowLayerRenderer() {
        return preventGlowLayer;
    }
    public void setPreventGlowLayer(boolean state) {
        preventGlowLayer = state;
    }

    @Redirect(method = "spawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;)V",
            at = @At(value = "INVOKE", target = "Lcom/GACMD/isleofberk/entity/base/dragon/ADragonBase;getBreedEggResult(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lcom/GACMD/isleofberk/entity/eggs/entity/base/ADragonEggBase;"),
            remap = false)
    private ADragonEggBase assignVariant(ADragonBase instance, ServerLevel world, AgeableMob parent) {
        if (parent instanceof ADragonBase dragonPartner) {

            ADragonEggBase egg = instance.getBreedEggResult(world, dragonPartner);
            if (!IoBVariantLoader.config.assignEggVariantOnBreeding.get()) return egg;

            if (IoBVariantLoader.config.breedingListsUse.get().canUseBreedingLists()) {
                Pair<String, String> dragonAndVariant = BreedingListUtil.getRandomDragonAndVariant(instance, dragonPartner);
                if (dragonAndVariant != null) {
                    // /data merge entity @e[type=isleofberk:triple_stryke, limit=1, sort=nearest] {Age:0}
                    // /summon isleofberk:triple_stryke ~ ~ ~ {VariantName:sappheral}
                    // /summon isleofberk:triple_stryke ~ ~ ~ {VariantName:deathgripper}
                    ADragonEggBase egg1 = switch (dragonAndVariant.getFirst()) {
                        case "deadly_nadder" -> new DeadlyNadderEgg(ModEntities.NADDER_EGG.get(), world);
                        case "gronckle" -> new GronkleEgg(ModEntities.GRONCKLE_EGG.get(), world);
                        case "light_fury" -> new LightFuryEgg(ModEntities.LIGHT_FURY_EGG.get(), world);
                        case "monstrous_nightmare" -> new MonstrousNightmareEgg(ModEntities.M_NIGHTMARE_EGG.get(), world);
                        case "night_fury" -> new NightFuryEgg(ModEntities.NIGHT_FURY_EGG.get(), world);
                        case "night_light" -> new NightLightEgg(ModEntities.NIGHT_LIGHT_EGG.get(), world);
                        case "skrill" -> new SkrillEgg(ModEntities.SKRILL_EGG.get(), world);
                        case "speed_stinger", "speed_stinger_leader" -> new SpeedStingerEgg(ModEntities.SPEED_STINGER_EGG.get(), world);
                        case "stinger" -> new StingerEgg(ModEntities.STINGER_EGG.get(), world);
                        case "terrible_terror" -> new TerribleTerrorEgg(ModEntities.TERRIBLE_TERROR_EGG.get(), world);
                        case "triple_stryke" -> new TripleStrykeEgg(ModEntities.TRIPLE_STRYKE_EGG.get(), world);
                        case "zippleback" -> new ZippleBackEgg(ModEntities.ZIPPLEBACK_EGG.get(), world);
                        default -> null;
                    };
                    if (egg1 instanceof VariantNameHelper helper) {
                        helper.setVariantName(dragonAndVariant.getSecond());
                        return egg1;
                    } else if (!IoBVariantLoader.config.breedingListsUse.get().canUseFallback()) return null;
                } else if (!IoBVariantLoader.config.breedingListsUse.get().canUseFallback()) return null;
            }

            if (egg instanceof VariantNameHelper helper) {
                if (instance instanceof VariantNameHelper parent1 && dragonPartner instanceof VariantNameHelper parent2) {
                    String parent1Variant = parent1.getVariantName();
                    String parent2Variant = parent2.getVariantName();

                    if (instance.getRandom().nextDouble() < IoBVariantLoader.config.inheritanceChance.get()) {
                        DragonVariantSpawner variant1 = DragonVariantSpawnerUtil.getVariantByName(parent1, parent1Variant);
                        DragonVariantSpawner variant2 = DragonVariantSpawnerUtil.getVariantByName(parent2, parent2Variant);

                        if (variant1 != null && variant2 != null) {
                            int weight1 = variant1.breedingWeight();
                            int weight2 = variant2.breedingWeight();
                            if (weight1 > 0 || weight2 > 0) {
                                if (weight1 <= 0) helper.setVariantName(parent2Variant);
                                else if (weight2 <= 0) helper.setVariantName(parent1Variant);
                                else if (getRandom().nextInt(weight1 + weight2) < weight1) helper.setVariantName(parent1Variant);
                                else helper.setVariantName(parent2Variant);
                            }
                        }

                        if (variant1 == null && variant2 != null) helper.setVariantName(parent2Variant);
                        else if (variant2 == null && variant1 != null) helper.setVariantName(parent1Variant);
                    }
                    if (helper.getVariantName().isEmpty()) DragonVariantSpawnerUtil.assignVariant(world, egg, false, parent1);
                    return egg;
                }
            }
        }
        return null;
    }

    @ModifyArg(method = "spawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private Entity assignNightLightVariant(Entity egg) {
        if (!IoBVariantLoader.config.assignEggVariantOnBreeding.get()) return egg;

        if (egg instanceof VariantNameHelper helper && helper.getVariantName().isEmpty() && egg instanceof NightLightEgg && level instanceof ServerLevelAccessor serverLevelAccessor) {
            List<Pair<List<String>, DragonVariantSpawner>> variants = DragonVariantSpawnerUtil.getVariantsFor("night_light");
            DragonVariantSpawnerUtil.assignVariantFromList(serverLevelAccessor, (LivingEntity) egg, false, variants);
        }
        return egg;
    }

    @Override
    protected Component getTypeName() {
        if (level.isClientSide() && !ResourceUtil.isResourceReloadFinished) return super.getTypeName();
        if (translationName == null) {
            String key = null;
            if (ModelRedirectUtil.DRAGON_MODEL_REDIRECTS.containsKey(getSpecies(true)) && ModelRedirectUtil.DRAGON_MODEL_REDIRECTS.get(getSpecies(true)).containsKey(getVariantName())) {
                key = ModelRedirectUtil.DRAGON_MODEL_REDIRECTS.get(getSpecies(true)).get(getVariantName()).dragonName().orElse(null);
            }
            if (key == null) translationName = super.getTypeName();
            else translationName = new TranslatableComponent(key);
        }
        return translationName;
    }

    @Unique
    public String getSpecies(boolean isClient) {
        String dragonID = EntityType.getKey(getType()).getPath();
        if (isClient) if (dragonID.equals("monstrous_nightmare")) return "nightmare";
        return dragonID;
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        if (boxOverride == null) {
            EntityDimensions original = super.getDimensions(pPose);
            EntityDimensions override = HitboxRedirectUtil.getHitboxOverride((ADragonBase) (Object)this);
            boxOverride = override == null ? original : override.scale(getScale());
        }
        if (getParts() != null && getParts()[0] instanceof AttackBoxRedirectHelper helper) {
            EntityDimensions newBox = getAttackBox();
            if (newBox != getParts()[0].getDimensions(pPose)) {
                helper.setAttackBoxOverride(newBox);
                getParts()[0].refreshDimensions();
            }
        }
        return boxOverride;
    }

    @Override
    public void resetHitboxData() {
        boxOverride = null;
        attackBoxPos = null;
        attackBoxOverride = null;
        refreshDimensions();
    }

    @Override
    public EntityDimensions getAttackBox() {
        if (getVariantName().isEmpty()) return getDefaultAttackBox((ADragonBase) (Object) this);
        if (attackBoxOverride == null) {
            attackBoxOverride = HitboxRedirectUtil.getAttackBoxOverride((ADragonBase) (Object) this);
            if (attackBoxOverride == null) attackBoxOverride = getDefaultAttackBox((ADragonBase) (Object) this);
        }
        return attackBoxOverride;
    }

    @Override
    public Vec3 getAttackBoxPos() {
        if (getVariantName().isEmpty()) return getDefaultAttackBoxPos((ADragonBase) (Object) this);
        if (attackBoxPos == null) {
            attackBoxPos = HitboxRedirectUtil.getAttackBoxPos((ADragonBase) (Object) this);
            if (attackBoxPos == null) attackBoxPos = getDefaultAttackBoxPos((ADragonBase) (Object) this);
        }
        return attackBoxPos;
    }

    private Vec3 getDefaultAttackBoxPos(ADragonBase dragon) {
        if (dragon instanceof Gronckle) return new Vec3(2.2, 0.4, 2.2);
        if (dragon instanceof TripleStryke) return new Vec3(3, 0.4, 3);
        if (dragon instanceof Stinger stinger) return new Vec3(3, stinger.isUsingAbility() ? 0.4 : 2.0, 3);
        return Vec3.ZERO;
    }

    private EntityDimensions getDefaultAttackBox(ADragonBase dragon) {
        if (dragon instanceof Gronckle) return EntityDimensions.scalable(1.6f, 1.6f);
        if (dragon instanceof TripleStryke) return EntityDimensions.scalable(1.8f, 1.8f);
        if (dragon instanceof Stinger) return EntityDimensions.scalable(1.5f, 1.5f);
        return EntityDimensions.scalable(1f, 1f);
    }

    @Override
    public float getModelSize() {
        String species = getSpecies(true);
        if (isBaby()) {
            return switch (species) {
                case "terrible_terror" -> 0.3f;
                default -> 0.4f;
            };
        }
        if (isTitanWing()) {
            return switch (species) {
                case "terrible_terror" -> 1;
                default -> 1.4f;
            };
        }

        return switch (species) {
            case "night_fury" -> 1.1f;
            case "skrill" -> 1.4f;
            case "terrible_terror" -> 0.7f;
            case "triple_stryke" -> 1.3f;
            case "speed_stinger_leader" -> 1.4f;
            default -> 1;
        };
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader levelReader) {
        return 0;
    }

    //SOUNDS
    @Override
    public void playAmbientSound() {
        if (!SoundRedirectUtil.playSound(this, isSleeping() ? SoundRedirectUtil.SLEEP : SoundRedirectUtil.GROWL)) super.playAmbientSound();
    }

    @Inject(method = "playHurtSound", at = @At("HEAD"), cancellable = true)
    private void swapHurtSound(DamageSource pSource, CallbackInfo ci) {
        if (SoundRedirectUtil.playSound(this, SoundRedirectUtil.HURT)) ci.cancel();
    }

    @Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
    public void swapDeathSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (SoundRedirectUtil.playSound(this, SoundRedirectUtil.DEATH)) cir.setReturnValue(null);
    }

    @Inject(method = "getTameSound", at = @At("HEAD"), cancellable = true, remap = false)
    public void swapTameSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (SoundRedirectUtil.playSound(this, SoundRedirectUtil.TAME)) cir.setReturnValue(null);
    }

    @Inject(method = "get1stAttackSound", at = @At("HEAD"), cancellable = true, remap = false)
    public void swapBiteSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (SoundRedirectUtil.playSound(this, SoundRedirectUtil.BITE)) cir.setReturnValue(null);
    }

    @Inject(method = "get2ndAttackSound", at = @At("HEAD"), cancellable = true, remap = false)
    public void swapStingSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (SoundRedirectUtil.playSound(this, SoundRedirectUtil.STING)) cir.setReturnValue(null);
    }

    @Inject(method = "getProjectileSound", at = @At("HEAD"), cancellable = true, remap = false)
    public void swapFireSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (SoundRedirectUtil.playSound(this, SoundRedirectUtil.FIRE)) cir.setReturnValue(null);
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState state) {
        if (!SoundRedirectUtil.playSound(this, SoundRedirectUtil.STEP)) super.playStepSound(pos, state);
    }

    @Override
    protected @NotNull ResourceLocation getDefaultLootTable() {
        ResourceLocation lootTable = ExtrasUtil.getLootTableRedirect(getSpecies(false), getVariantName());
        return lootTable != null ? lootTable : super.getDefaultLootTable();
    }

    @Unique
    private void applyVariantModifiers() {
        List<Extras.VariantAttributeModifier> modifiers = ExtrasUtil.getVariantAttributeModifiers(getSpecies(false), getVariantName());
        if (modifiers == null) return;

        modifiers.forEach(modifier -> {
            Attribute attribute = getLevel().registryAccess().registry(Registry.ATTRIBUTE_REGISTRY).get().get(modifier.id());
            if (attribute == null) {
                IoBVariantLoader.LOGGER.warn("Failed to find attribute {} for {} for variant {}. Modifier will not be applied", modifier.id(), getSpecies(false), getVariantName());
                return;
            }
            AttributeInstance instance = getAttribute(attribute);
            AttributeModifier attributeModifier = new AttributeModifier(VARIANT_BONUS_MODIFIER, "Variant Bonus", modifier.amount(), AttributeModifier.Operation.valueOf(modifier.operation()));
            if (instance != null && !instance.hasModifier(attributeModifier))
                instance.addTransientModifier(attributeModifier);
        });
    }

    @Unique
    private void removeVariantModifiers() {
        AttributeMap container = getAttributes();
        getLevel().registryAccess().registry(Registry.ATTRIBUTE_REGISTRY).ifPresent(registry -> {
            registry.forEach(attribute -> {
                if (container.hasAttribute(attribute))
                    container.getInstance(attribute).removeModifier(VARIANT_BONUS_MODIFIER);
            });
        });
    }

    @Inject(method = "isItemStackForTaming", at = @At("HEAD"), cancellable = true, remap = false)
    private void getTamingItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Boolean isCorrect = ExtrasUtil.isTamingItem(getSpecies(false), getVariantName(), stack);
        if (isCorrect != null) cir.setReturnValue(isCorrect);
    }

    @Inject(method = "isBreedingFood", at = @At("HEAD"), cancellable = true, remap = false)
    private void getBreedingItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Boolean isCorrect = ExtrasUtil.isBreedingItem(getSpecies(false), getVariantName(), stack);
        if (isCorrect != null) cir.setReturnValue(isCorrect);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return IoBVariantLoader.config.alternativeLandNavigation.get() ? new AltLandNavigation<>((ADragonBase) (Object)this, level) : super.createNavigation(pLevel);
    }

    @Inject(method = "tame", at = @At("TAIL"))
    private void triggerVariantTamed(Player pPlayer, CallbackInfo ci) {
        if (pPlayer instanceof ServerPlayer player)
            IoBVariantLoader.TAME_VARIANT_FROM_GROUP_TRIGGER.trigger(player, (ADragonBase) (Object) this);
    }

    @Inject(method = "tameWithName", at = @At("TAIL"), remap = false)
    private void alsoTriggerVariantTamed(Player pPlayer, CallbackInfoReturnable<Boolean> cir) {
        if (pPlayer instanceof ServerPlayer player)
            IoBVariantLoader.TAME_VARIANT_FROM_GROUP_TRIGGER.trigger(player, (ADragonBase) (Object) this);
    }

    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V"), index = 1)
    private Goal replaceBreedingGoal(Goal pGoal) {
        if (IoBVariantLoader.config.breedingListsUse.get().canUseBreedingLists() && pGoal instanceof DragonBreedGoal)
            return new VLDragonBreedGoal((ADragonBase)(Object)this, 1);
        return pGoal;
    }

    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V"), index = 1)
    private Goal replaceFollowGoal(Goal pGoal) {
        if (IoBVariantLoader.config.alternativeLandNavigation.get() && pGoal instanceof FollowOwnerNoTPGoal)
            return new AltFollowGoal((ADragonBase)(Object)this, 1.1, 4.0F, 4.0F, false);
        return pGoal;
    }
}
