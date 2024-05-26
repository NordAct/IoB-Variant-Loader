package nordmods.iobvariantloader.util.model_redirect;

import org.jetbrains.annotations.Nullable;

public record ModelRedirect(@Nullable String texture, @Nullable String model, @Nullable String animation, @Nullable String saddle, @Nullable
                            String eggModel, boolean nametagAccessible) {
}
