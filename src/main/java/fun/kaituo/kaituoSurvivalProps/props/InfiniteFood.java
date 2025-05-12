package fun.kaituo.kaituoSurvivalProps.props;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import fun.kaituo.kaituoSurvivalProps.KaituoSurvivalProps;
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
    @EventHandler (priority = EventPriority.HIGH)
    public void onItemConsume(PlayerItemConsumeEvent pice) {
        ItemStack item = pice.getItem();
        Boolean isInfinite = NBT.get(item, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.getBoolean("InfiniteFood"));
        if (Boolean.TRUE.equals(isInfinite)) {
            UUID uuid = pice.getPlayer().getUniqueId();
            ItemStack food = pice.getItem().clone();
            Bukkit.getScheduler().runTaskLater(KaituoSurvivalProps.getPlugin(), () -> {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    if(player.getGameMode().equals(GameMode.CREATIVE)) {
                        return;
                    }
                    if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                        player.dropItem(true);
                    }
                    player.getInventory().setItem(EquipmentSlot.HAND, food);
                }
            }, 1);
        }
    }
}
