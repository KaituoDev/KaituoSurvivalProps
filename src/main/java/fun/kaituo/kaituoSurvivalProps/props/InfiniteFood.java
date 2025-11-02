package fun.kaituo.kaituoSurvivalProps.props;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import fun.kaituo.kaituoSurvivalProps.KaituoSurvivalProps;
import fun.kaituo.kaituoSurvivalProps.props.infiniteFoods.BrownSugarSoup;
import fun.kaituo.kaituoSurvivalProps.props.infiniteFoods.FakeEnchantedGoldenApple;
import fun.kaituo.kaituoSurvivalProps.props.infiniteFoods.HighJumperCarrot;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Function;

public class InfiniteFood implements Listener {
    @EventHandler (priority = EventPriority.LOW)
    public void onFoodConsume(PlayerItemConsumeEvent pice) {
        ItemStack item = pice.getItem();
        UUID uuid = pice.getPlayer().getUniqueId();

        // 检查所吃的物品是否无限可吃
        Boolean isInfinite = NBT.get(item, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.getBoolean("InfiniteFood"));
        if (!isInfinite) {
            return;
        }

        // 决定是否对玩家应用所吃物品本身的能力或特性
        Boolean applyRawEffects = NBT.get(item, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.getBoolean("RawFoodEffects"));
        if (applyRawEffects) {
            ItemStack food = pice.getItem().clone();
            Bukkit.getScheduler().runTaskLater(KaituoSurvivalProps.getPlugin(), () -> {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    if (player.getGameMode().equals(GameMode.CREATIVE)) {
                        return;
                    }
                    if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                        player.dropItem(true);
                    }
                    player.getInventory().setItem(EquipmentSlot.HAND, food);
                }
            }, 1);
        }
        else {
            pice.setCancelled(true);
        }

        // 红糖水
        if (item.getType().equals(Material.BEETROOT_SOUP)) {
            Boolean isBrownSugarSoup = NBT.get(item, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.getBoolean("BrownSugarSoup"));
            if (isBrownSugarSoup) {
                BrownSugarSoup.applyWarmth(uuid);
                return;
            }
        }

        // 假金苹果
        if (item.getType().equals(Material.ENCHANTED_GOLDEN_APPLE)) {
            Boolean isFakeEnchantedGoldenApple = NBT.get(item, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.getBoolean("FakeEnchantedGoldenApple"));
            if (isFakeEnchantedGoldenApple) {
                FakeEnchantedGoldenApple.applyRandomNegativeEffect(uuid);
            }
        }

        // 跳跃金胡萝卜
        if (item.getType().equals(Material.GOLDEN_CARROT)) {
            Boolean isHighJumperCarrot = NBT.get(item, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.getBoolean("HighJumperCarrot"));
            if (isHighJumperCarrot) {
                HighJumperCarrot.applyJumpBoost(uuid);
            }
        }
    }
}
