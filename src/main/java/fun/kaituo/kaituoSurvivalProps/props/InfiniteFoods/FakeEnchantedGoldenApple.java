package fun.kaituo.kaituoSurvivalProps.props.infiniteFoods;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;
import java.util.UUID;

public class FakeEnchantedGoldenApple {
    private static final PotionEffectType[] NEGATIVE_EFFECTS = {
            PotionEffectType.SLOWNESS,
            PotionEffectType.MINING_FATIGUE,
            PotionEffectType.NAUSEA,
            PotionEffectType.BLINDNESS,
            PotionEffectType.HUNGER,
            PotionEffectType.WEAKNESS,
            PotionEffectType.POISON,
            PotionEffectType.WITHER,
            PotionEffectType.LEVITATION,
            PotionEffectType.INFESTED,
            PotionEffectType.OOZING,
            PotionEffectType.WEAVING,
            PotionEffectType.WIND_CHARGED,
            PotionEffectType.DARKNESS
    };

    public static void applyRandomNegativeEffect(UUID uuid) {
        Random random = new Random();

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        player.addPotionEffect(new PotionEffect(NEGATIVE_EFFECTS[random.nextInt(14)], 200, 1, true));
        player.setCooldown(Material.ENCHANTED_GOLDEN_APPLE, 20);
    }
}