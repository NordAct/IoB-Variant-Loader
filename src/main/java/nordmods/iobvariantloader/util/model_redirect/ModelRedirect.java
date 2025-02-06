package nordmods.iobvariantloader.util.model_redirect;

import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.Nullable;

public record ModelRedirect(@Nullable String texture, @Nullable String model, @Nullable String animation, @Nullable String saddle,
                            @Nullable String babyTexture, @Nullable String babyModel, @Nullable String babyAnimation, @Nullable String babySaddle,
                            @Nullable String eggModel, @Nullable String eggTexture,
                            @Nullable TranslatableComponent eggItemName, @Nullable TranslatableComponent eggName, @Nullable TranslatableComponent dragonName,
                            boolean nametagAccessible) {
}
