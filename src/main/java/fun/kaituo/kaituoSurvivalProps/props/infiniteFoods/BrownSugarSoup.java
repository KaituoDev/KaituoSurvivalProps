package fun.kaituo.kaituoSurvivalProps.props.infiniteFoods;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BrownSugarSoup {
    public static void applyWarmth(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        if (player.getFreezeTicks() == 0) {
            return;
        }

        player.setFreezeTicks(0);
        player.sendMessage(Component.text("红糖水").color(TextColor.color(255, 66, 66)).decorate(TextDecoration.BOLD)
                .append(Component.text("给你带来了温暖！").color(TextColor.color(255, 243, 221))));
    }
}