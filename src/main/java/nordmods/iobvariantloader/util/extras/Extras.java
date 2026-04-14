package nordmods.iobvariantloader.util.extras;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record Extras(
        Optional<String> variantGroup,
        Optional<ResourceLocation> lootTableRedirect,
        Optional<List<VariantAttributeModifier>> variantAttributeModifiers,
        Optional<ItemRestriction> tamingItems,
        Optional<ItemRestriction> breedingItems,
        Optional<List<CustomItemInteraction>> customItemInteractions
) {
    public static final Codec<Extras> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("variant_group").forGetter(Extras::variantGroup),
            ResourceLocation.CODEC.optionalFieldOf("loot_table_redirect").forGetter(Extras::lootTableRedirect),
            VariantAttributeModifier.CODEC.listOf().optionalFieldOf("attribute_modifiers").forGetter(Extras::variantAttributeModifiers),
            ItemRestriction.CODEC.optionalFieldOf("taming_items").forGetter(Extras::tamingItems),
            ItemRestriction.CODEC.optionalFieldOf("breeding_items").forGetter(Extras::breedingItems),
            CustomItemInteraction.CODEC.listOf().optionalFieldOf("custom_item_interactions").forGetter(Extras::customItemInteractions)
    ).apply(instance, Extras::new));

    public record VariantAttributeModifier(ResourceLocation id, double amount, String operation) {
        public static final Codec<VariantAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(VariantAttributeModifier::id),
                        Codec.DOUBLE.fieldOf("amount").forGetter(VariantAttributeModifier::amount),
                        Codec.STRING.fieldOf("operation").forGetter(VariantAttributeModifier::operation)
                ).apply(instance, VariantAttributeModifier::new));
    }

    public record ItemRestriction(@NotNull List<ResourceLocation> itemsById, @NotNull List<ResourceLocation> itemsByTag) {
        public static Codec<ItemRestriction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.listOf().optionalFieldOf("item", List.of()).forGetter(ItemRestriction::itemsById),
                ResourceLocation.CODEC.listOf().optionalFieldOf("tag", List.of()).forGetter(ItemRestriction::itemsByTag)
        ).apply(instance, ItemRestriction::new));

        public static ItemRestriction dummy() {
            return new ItemRestriction(List.of(), List.of());
        }
    }

    public record CustomItemInteraction(
            ItemRestriction items,
            int requiredAmount,
            boolean consumeOnUse,
            boolean canInteractWithUntamed,
            boolean canInteractIfNotOwner,
            List<Command> commands
    ) {
        public static Codec<CustomItemInteraction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemRestriction.CODEC.fieldOf("items").forGetter(CustomItemInteraction::items),
                ExtraCodecs.POSITIVE_INT.fieldOf("required_amount").forGetter(CustomItemInteraction::requiredAmount),
                Codec.BOOL.fieldOf("consume_on_use").forGetter(CustomItemInteraction::consumeOnUse),
                Codec.BOOL.fieldOf("can_interact_with_untamed").forGetter(CustomItemInteraction::canInteractWithUntamed),
                Codec.BOOL.fieldOf("can_interact_if_not_owner").forGetter(CustomItemInteraction::canInteractIfNotOwner),
                Command.CODEC.listOf().fieldOf("commands").forGetter(CustomItemInteraction::commands)
        ).apply(instance, CustomItemInteraction::new));

        public record Command(String executedCommand, Executor executor) {

            public static Codec<Command> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("executed_command").forGetter(Command::executedCommand),
                    Codec.STRING.fieldOf("executor").forGetter(command -> command.executor.name())
            ).apply(instance, (command, executor) -> new Command(command, Executor.valueOf(executor))));

            public enum Executor {
                USER,
                DRAGON,
                SERVER
            }

            public void execute(Player pPlayer, ADragonBase dragon) {
                if (!(dragon.level instanceof ServerLevel level)) return;
                CommandSourceStack sourceStack = switch (executor) {
                    case USER -> new CommandSourceStack(
                            pPlayer,
                            pPlayer.position(),
                            pPlayer.getRotationVector(),
                            level,
                            2,
                            pPlayer.getName().getString(),
                            pPlayer.getDisplayName(),
                            level.getServer(),
                            pPlayer
                    ).withSuppressedOutput();
                    case DRAGON -> new CommandSourceStack(
                            dragon,
                            dragon.position(),
                            dragon.getRotationVector(),
                            level,
                            2,
                            dragon.getName().getString(),
                            dragon.getDisplayName(),
                            level.getServer(),
                            dragon
                    ).withSuppressedOutput();
                    case SERVER -> new CommandSourceStack(
                            level.getServer(),
                            Vec3.atLowerCornerOf(level.getSharedSpawnPos()),
                            Vec2.ZERO,
                            level,
                            2,
                            "Server",
                            new TextComponent("Server"),
                            level.getServer(),
                            null
                    ).withSuppressedOutput();
                };
                level.getServer().getCommands().performCommand(sourceStack, executedCommand);
            }
        }
    }
}