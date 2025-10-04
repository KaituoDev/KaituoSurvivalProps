package fun.kaituo.kaituoSurvivalProps.props;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.function.Function;

public class InfiniteInkSac implements Listener {
    @SuppressWarnings("unused")
    public static ItemStack getInfiniteInk_Sac() {
        ItemStack monang = new ItemStack(Material.INK_SAC);

        // 设置物品显示名称
        ItemMeta meta = monang.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("§e随机墨囊"));
            monang.setItemMeta(meta);
        }

        // 设置NBT标签
        NBT.modify(monang, nbt -> {
            nbt.setBoolean("monang", true);
        });

        return monang;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // 先进行严格的物品检查，再调用NBT API
        if (item == null || item.getType() == Material.AIR || item.getAmount() == 0) {
            return;
        }

        // 然后再进行NBT操作
        boolean isInfinite = NBT.get(item, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.getBoolean("monang"));

        if (!isInfinite) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        var block = event.getClickedBlock();
        if (block == null || !(block.getState() instanceof Sign sign)) {
            return;
        }

        // 确定玩家点击的是正面还是背面
        // 这里使用一个简单的方法 - 总是编辑正面
        // 更复杂的实现可以根据玩家位置判断点击的是哪一面
        SignSide side = sign.getSide(Side.FRONT); // 使用Side.FRONT而不是BlockFace

        TextColor randomColor = generateRandomColor();

        for (int i = 0; i < 4; i++) {
            Component line = side.line(i);
            if (!line.equals(Component.empty())) {
                Component coloredLine = line.color(randomColor);
                side.line(i, coloredLine);
            }
        }
        sign.update();
        event.setCancelled(true);
    }

    private TextColor generateRandomColor() {
        DyeColor[] colors = DyeColor.values();
        int randomIndex = (int) (Math.random() * colors.length);
        return TextColor.color(colors[randomIndex].getColor().asRGB());
    }
}