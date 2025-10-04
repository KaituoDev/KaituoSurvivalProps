package fun.kaituo.kaituoSurvivalProps.props;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import fun.kaituo.kaituoSurvivalProps.KaituoSurvivalProps;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;
import java.util.function.Function;

public class InfiniteExpBottle implements Listener {

    public static ItemStack getInfiniteExpBottle() {
        ItemStack bottle = new ItemStack(Material.EXPERIENCE_BOTTLE);

        // 设置NBT标签
        NBT.modify(bottle, nbt -> {
            nbt.setBoolean("InfiniteExpBottle", true);
        });
        return bottle;
    }

    @EventHandler
    public void onExpBottleLanding(ExpBottleEvent ebe) {
        ThrownExpBottle bottle = ebe.getEntity();

        if (bottle.hasMetadata("Experienceless")) {
            ebe.setExperience(0);
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerThrowBottle(PlayerLaunchProjectileEvent plpe) {
        ItemStack bottle = plpe.getItemStack().clone();
        if (bottle.getType() != Material.EXPERIENCE_BOTTLE) {
            return;
        }

        Boolean isInfinite = NBT.get(bottle, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.getBoolean("InfiniteExpBottle"));
        if (!Boolean.TRUE.equals(isInfinite)) {
            return;
        }

        Player player = plpe.getPlayer();
        if (player.hasCooldown(bottle.getType())) {
            return;
        }
        if (!player.getInventory().getItemInMainHand().isSimilar(bottle)) {
            plpe.setCancelled(true);
            return;
        }

        Entity thrownBottle = plpe.getProjectile();
        UUID uuid = player.getUniqueId();
        thrownBottle.setMetadata("Experienceless", new FixedMetadataValue(KaituoSurvivalProps.getPlugin(), true));
        player.setCooldown(Material.EXPERIENCE_BOTTLE, 40);

        Bukkit.getScheduler().runTaskLater(KaituoSurvivalProps.getPlugin(), () -> {
            Player shooter = Bukkit.getPlayer(uuid);
            if (shooter != null) {
                if(shooter.getGameMode().equals(GameMode.CREATIVE)) {
                    return;
                }
                if (!shooter.getInventory().getItemInMainHand().isSimilar(bottle)) {
                    shooter.dropItem(true);
                }
                shooter.getInventory().setItem(EquipmentSlot.HAND, bottle);
            }
        }, 1);
    }

    @EventHandler (priority = EventPriority.LOW)
    public void removeNormalBottleCooldown(PlayerInteractEvent pie) {
        ItemStack bottle = pie.getItem();
        if (bottle == null) {
            return;
        }
        if (bottle.getType() != Material.EXPERIENCE_BOTTLE) {
            return;
        }

        Player player = pie.getPlayer();
        if (!player.hasCooldown(bottle.getType())) {
            return;
        }

        Boolean isInfinite = NBT.get(bottle, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.getBoolean("InfiniteExpBottle"));
        if (Boolean.TRUE.equals(isInfinite)) {
            return;
        }

        player.setCooldown(Material.EXPERIENCE_BOTTLE, 0);
    }
}
