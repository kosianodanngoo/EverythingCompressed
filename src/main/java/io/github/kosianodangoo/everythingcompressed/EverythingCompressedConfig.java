package io.github.kosianodangoo.everythingcompressed;

import net.minecraftforge.common.ForgeConfigSpec;

public class EverythingCompressedConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.LongValue SINGULARITY_DENSITY = BUILDER.comment("Number of items required to create the Singularity").defineInRange("singularityDensity", 10000, 1, Long.MAX_VALUE);
    public static final ForgeConfigSpec.LongValue EXTRACTION_TIME = BUILDER.comment("Ticks required for singularity extraction").defineInRange("extractionTime", 600, 1, Long.MAX_VALUE);

    static final ForgeConfigSpec SPEC = BUILDER.build();
}
