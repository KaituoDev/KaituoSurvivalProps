package fun.kaituo.kaituoSurvivalProps.props;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class BrownSugarSoup implements Listener {
    @EventHandler (priority = EventPriority.LOW)
    public void onPlayerDrinkBrownSugar(PlayerItemConsumeEvent pice) {
        ItemStack sugar = pice.getItem();
        if (!sugar.getType().equals(Material.BEETROOT_SOUP)) {
            return;
        }
        Boolean isInfinite = NBT.get(sugar, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.getBoolean("InfiniteFood"));
        if (!Boolean.TRUE.equals(isInfinite)) {
            return;

        }
        Boolean isBrownSugar = NBT.get(sugar, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.getBoolean("BrownSugarSoup"));
        if (!Boolean.TRUE.equals(isBrownSugar)) {
            return;
        }
        Player player = pice.getPlayer();
        if (player.getFreezeTicks() == 0) {
            return;
        }
        player.setFreezeTicks(0);
        player.sendMessage(Component.text("红糖水").color(TextColor.color(255, 66, 66)).decorate(TextDecoration.BOLD)
                .append(Component.text("给你带来了温暖！").color(TextColor.color(255, 243, 221))));
    }
}
