package fun.kaituo.kaituoSurvivalProps.props.infiniteFoods;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class HighJumperCarrot {
    public static void applyJumpBoost(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 100, 1, true));
        player.setCooldown(Material.GOLDEN_CARROT, 300);
    }
}