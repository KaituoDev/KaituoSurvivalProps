package fun.kaituo.kaituoSurvivalProps.props;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class InfiniteApple implements Listener {
    private static final String APPLE_NAME = "TESTAPPLE";
    private final Map<UUID, Long> cooldowns = new HashMap<>(); // 存储玩家冷却时间

    @SuppressWarnings("unused")
    public static ItemStack getInfiniteApple() {
        ItemStack apple = new ItemStack(Material.APPLE);

        // 设置物品显示名称
        ItemMeta meta = apple.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(APPLE_NAME));
            apple.setItemMeta(meta);
        }

        // 设置NBT标签
        NBT.modify(apple, nbt -> {
            nbt.setBoolean("InfiniteApple", true);
        });
        return apple;
    }

    // 清理过期冷却时间的方法
    private void cleanExpiredCooldowns() {
        long currentTime = System.currentTimeMillis();
        cooldowns.entrySet().removeIf(entry -> currentTime > entry.getValue());

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemConsume(PlayerItemConsumeEvent pice) {
        // 清理过期冷却时间
        cleanExpiredCooldowns();
        Player player = pice.getPlayer();
        ItemStack item = pice.getItem();

        // 先进行严格的物品检查
        if (item.getType() == Material.AIR || item.getAmount() == 0) {
            return;
        }

        // 检查NBT标签
        boolean isInfinite = NBT.get(item, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.getBoolean("InfiniteApple"));
        if (!isInfinite) {
            return;
        }

        // 检查冷却时间
        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (cooldowns.containsKey(uuid)) {
            long cooldownEnd = cooldowns.get(uuid);
            if (currentTime < cooldownEnd) {
                // 仍在冷却中
                pice.setCancelled(true); // 取消食用
                return;
            }
        }

        // 设置冷却时间 (1200 ticks = 60秒)
        cooldowns.put(uuid, currentTime + 60000);

        // 给予跳跃效果 (100 ticks = 5秒)
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 100, 1));

        // 取消饱食度和饥饿度回复
        pice.setCancelled(true);
    }
}