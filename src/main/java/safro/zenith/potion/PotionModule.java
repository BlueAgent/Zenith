package safro.zenith.potion;

import dev.emi.trinkets.api.TrinketsApi;
import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import safro.zenith.Zenith;
import safro.zenith.api.config.Configuration;
import safro.zenith.potion.potions.KnowledgeEffect;
import safro.zenith.potion.potions.SunderingEffect;
import safro.zenith.util.ApotheosisUtil;

import java.io.File;

public class PotionModule {
    public static final Logger LOG = LogManager.getLogger("Zenith : Potion");
    public static final ResourceLocation POTION_TEX = new ResourceLocation(Zenith.MODID, "textures/potions.png");

    static int knowledgeMult = 4;

    // Items
    public static final Item LUCKY_FOOT = register("lucky_foot", new LuckyFootItem());
    public static final Item POTION_CHARM = register("potion_charm", new PotionCharmItem());

    // Effects
    public static final MobEffect SUNDERING_EFFECT = register("sundering", new SunderingEffect());
    public static final MobEffect KNOWLEDGE_EFFECT = register("knowledge", new KnowledgeEffect());

    // Serializers
    public static final RecipeSerializer<PotionCharmRecipe> POTION_CHARM_SERIALIZER = register("potion_charm", PotionCharmRecipe.Serializer.INSTANCE);
    public static final RecipeSerializer<PotionEnchantingRecipe> POTION_ENCHANTING_SERIALIZER = register("potion_charm_enchanting", PotionEnchantingRecipe.SERIALIZER);

    // Potions
    public static final Potion RESISTANCE = register("resistance", new Potion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3600)));
    public static final Potion LONG_RESISTANCE = register("long_resistance", new Potion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 9600)));
    public static final Potion STRONG_RESISTANCE = register("strong_resistance", new Potion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1800, 1)));
    public static final Potion ABSORPTION = register("absorption", new Potion("absorption", new MobEffectInstance(MobEffects.ABSORPTION, 1200, 1)));
    public static final Potion LONG_ABSORPTION = register("long_absorption", new Potion("absorption", new MobEffectInstance(MobEffects.ABSORPTION, 3600, 1)));
    public static final Potion STRONG_ABSORPTION = register("strong_absorption", new Potion("absorption", new MobEffectInstance(MobEffects.ABSORPTION, 600, 3)));
    public static final Potion HASTE = register("haste", new Potion("haste", new MobEffectInstance(MobEffects.DIG_SPEED, 3600)));
    public static final Potion LONG_HASTE = register("long_haste", new Potion("haste", new MobEffectInstance(MobEffects.DIG_SPEED, 9600)));
    public static final Potion STRONG_HASTE = register("strong_haste", new Potion("haste", new MobEffectInstance(MobEffects.DIG_SPEED, 1800, 1)));
    public static final Potion FATIGUE = register("fatigue", new Potion("fatigue", new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 3600)));
    public static final Potion LONG_FATIGUE = register("long_fatigue", new Potion("fatigue", new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 9600)));
    public static final Potion STRONG_FATIGUE = register("strong_fatigue", new Potion("fatigue", new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1800, 1)));
    public static final Potion WITHER = register("wither", new Potion("wither", new MobEffectInstance(MobEffects.WITHER, 3600)));
    public static final Potion LONG_WITHER = register("long_wither", new Potion("wither", new MobEffectInstance(MobEffects.WITHER, 9600)));
    public static final Potion STRONG_WITHER = register("strong_wither", new Potion("wither", new MobEffectInstance(MobEffects.WITHER, 1800, 1)));
    public static final Potion SUNDERING = register("sundering", new Potion("sundering", new MobEffectInstance(SUNDERING_EFFECT, 3600)));
    public static final Potion LONG_SUNDERING = register("long_sundering", new Potion("sundering", new MobEffectInstance(SUNDERING_EFFECT, 9600)));
    public static final Potion STRONG_SUNDERING = register("strong_sundering", new Potion("sundering", new MobEffectInstance(SUNDERING_EFFECT, 1800, 1)));
    public static final Potion KNOWLEDGE = register("knowledge", new Potion("knowledge", new MobEffectInstance(KNOWLEDGE_EFFECT, 2400)));
    public static final Potion LONG_KNOWLEDGE = register("long_knowledge", new Potion("knowledge", new MobEffectInstance(KNOWLEDGE_EFFECT, 4800)));
    public static final Potion STRONG_KNOWLEDGE = register("strong_knowledge", new Potion("knowledge", new MobEffectInstance(KNOWLEDGE_EFFECT, 1200, 1)));

    public static void init() {
        reload(false);

        LivingEntityEvents.DROPS.register(((target, source, drops) -> {
            if (Zenith.enablePotion) {
                if (target instanceof Rabbit) {
                    Rabbit rabbit = (Rabbit) target;
                    if (rabbit.level.random.nextFloat() < 0.045F + 0.045F * ApotheosisUtil.getLootingLevel(source)) {
                        drops.clear();
                        drops.add(new ItemEntity(rabbit.level, rabbit.getX(), rabbit.getY(), rabbit.getZ(), new ItemStack(LUCKY_FOOT)));
                    }
                }
            }
            return false;
        }));

        LivingEntityEvents.EXPERIENCE_DROP.register(((i, player) -> {
            if (Zenith.enablePotion) {
                if (player != null && player.getEffect(KNOWLEDGE_EFFECT) != null) {
                    int level = player.getEffect(KNOWLEDGE_EFFECT).getAmplifier() + 1;
                    int curXp = i;
                    int newXp = curXp + i * level * knowledgeMult;
                    return newXp;
                }
            }
            return i;
        }));

        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            LivingEntityEvents.TICK.register(entity -> {
                TrinketsApi.getTrinketComponent(entity).ifPresent(c -> c.forEach((slotReference, stack) -> {
                    if (stack.getItem() instanceof PotionCharmItem charm) {
                        charm.inventoryTick(stack, entity.level, entity, slotReference.index(), false);
                    }
                }));
            });
        }

        PotionBrewing.addMix(Potions.AWKWARD, Items.SHULKER_SHELL, RESISTANCE);
        PotionBrewing.addMix(RESISTANCE, Items.REDSTONE, LONG_RESISTANCE);
        PotionBrewing.addMix(RESISTANCE, Items.GLOWSTONE_DUST, STRONG_RESISTANCE);

        PotionBrewing.addMix(RESISTANCE, Items.FERMENTED_SPIDER_EYE, SUNDERING);
        PotionBrewing.addMix(LONG_RESISTANCE, Items.FERMENTED_SPIDER_EYE, LONG_SUNDERING);
        PotionBrewing.addMix(STRONG_RESISTANCE, Items.FERMENTED_SPIDER_EYE, STRONG_SUNDERING);
        PotionBrewing.addMix(SUNDERING, Items.REDSTONE, LONG_SUNDERING);
        PotionBrewing.addMix(SUNDERING, Items.GLOWSTONE_DUST, STRONG_SUNDERING);

        PotionBrewing.addMix(Potions.AWKWARD, Items.GOLDEN_APPLE, ABSORPTION);
        PotionBrewing.addMix(ABSORPTION, Items.REDSTONE, LONG_ABSORPTION);
        PotionBrewing.addMix(ABSORPTION, Items.GLOWSTONE_DUST, STRONG_ABSORPTION);

        PotionBrewing.addMix(Potions.AWKWARD, Items.MUSHROOM_STEW, HASTE);
        PotionBrewing.addMix(HASTE, Items.REDSTONE, LONG_HASTE);
        PotionBrewing.addMix(HASTE, Items.GLOWSTONE_DUST, STRONG_HASTE);

        PotionBrewing.addMix(HASTE, Items.FERMENTED_SPIDER_EYE, FATIGUE);
        PotionBrewing.addMix(LONG_HASTE, Items.FERMENTED_SPIDER_EYE, LONG_FATIGUE);
        PotionBrewing.addMix(STRONG_HASTE, Items.FERMENTED_SPIDER_EYE, STRONG_FATIGUE);
        PotionBrewing.addMix(FATIGUE, Items.REDSTONE, LONG_FATIGUE);
        PotionBrewing.addMix(FATIGUE, Items.GLOWSTONE_DUST, STRONG_FATIGUE);

    //    if (Apoth.Items.SKULL_FRAGMENT != null) PotionBrewing.addMix(Potions.AWKWARD, Apoth.Items.SKULL_FRAGMENT, WITHER);
        PotionBrewing.addMix(Potions.AWKWARD, Items.WITHER_SKELETON_SKULL, WITHER);
        PotionBrewing.addMix(WITHER, Items.REDSTONE, LONG_WITHER);
        PotionBrewing.addMix(WITHER, Items.GLOWSTONE_DUST, STRONG_WITHER);

        PotionBrewing.addMix(Potions.AWKWARD, Items.EXPERIENCE_BOTTLE, KNOWLEDGE);
        PotionBrewing.addMix(KNOWLEDGE, Items.REDSTONE, LONG_KNOWLEDGE);
        PotionBrewing.addMix(KNOWLEDGE, Items.EXPERIENCE_BOTTLE, STRONG_KNOWLEDGE);

        PotionBrewing.addMix(Potions.AWKWARD, LUCKY_FOOT, Potions.LUCK);
    }

    public static void reload(boolean e) {
        Configuration config = new Configuration(new File(Zenith.configDir, "potion.cfg"));
        knowledgeMult = config.getInt("Knowledge XP Multiplier", "general", knowledgeMult, 1, Integer.MAX_VALUE, "The strength of Ancient Knowledge.  This multiplier determines how much additional xp is granted.");
        if (!e && config.hasChanged()) config.save();
    }

    private static Item register(String name, Item item) {
        return Registry.register(Registry.ITEM, new ResourceLocation(Zenith.MODID, name), item);
    }

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(Zenith.MODID, id), serializer);
    }

    private static MobEffect register(String name, MobEffect effect) {
        return Registry.register(Registry.MOB_EFFECT, new ResourceLocation(Zenith.MODID, name), effect);
    }

    private static Potion register(String name, Potion potion) {
        return Registry.register(Registry.POTION, new ResourceLocation(Zenith.MODID, name), potion);
    }
}
