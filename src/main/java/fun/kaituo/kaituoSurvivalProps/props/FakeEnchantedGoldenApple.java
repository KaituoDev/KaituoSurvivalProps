package fun.kaituo.kaituoSurvivalProps.props;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;
import java.util.function.Function;

public class FakeEnchantedGoldenApple implements Listener {
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

    Random random = new Random();

    @EventHandler (priority = EventPriority.LOW)
    public void onPlayerEatingGoldenApple(PlayerItemConsumeEvent pice) {
        ItemStack apple = pice.getItem();
        if (!apple.getType().equals(Material.ENCHANTED_GOLDEN_APPLE)) {
            return;
        }
        Boolean isFake = NBT.get(apple, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.getBoolean("FakeEnchantedGoldenApple"));
        if (!isFake) {
            return;
        }

        Player player = pice.getPlayer();
        player.addPotionEffect(new PotionEffect(NEGATIVE_EFFECTS[random.nextInt(14)], 200, 1, true));

        pice.setCancelled(true);
    }
}
