package fun.kaituo.kaituoSurvivalProps.props;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BlockBreaker implements Listener {

    private static final Material[] GLASS = {
            Material.TINTED_GLASS, // 遮光玻璃
            Material.GLASS, // 玻璃
            Material.WHITE_STAINED_GLASS,
            Material.ORANGE_STAINED_GLASS,
            Material.MAGENTA_STAINED_GLASS,
            Material.LIGHT_BLUE_STAINED_GLASS,
            Material.YELLOW_STAINED_GLASS,
            Material.LIME_STAINED_GLASS,
            Material.PINK_STAINED_GLASS,
            Material.GRAY_STAINED_GLASS,
            Material.LIGHT_GRAY_STAINED_GLASS,
            Material.CYAN_STAINED_GLASS,
            Material.PURPLE_STAINED_GLASS,
            Material.BLUE_STAINED_GLASS,
            Material.BROWN_STAINED_GLASS,
            Material.GREEN_STAINED_GLASS,
            Material.RED_STAINED_GLASS,
            Material.BLACK_STAINED_GLASS,
            Material.GLASS_PANE, // 玻璃板
            Material.WHITE_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.MAGENTA_STAINED_GLASS_PANE,
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.LIME_STAINED_GLASS_PANE,
            Material.PINK_STAINED_GLASS_PANE,
            Material.GRAY_STAINED_GLASS_PANE,
            Material.LIGHT_GRAY_STAINED_GLASS_PANE,
            Material.CYAN_STAINED_GLASS_PANE,
            Material.PURPLE_STAINED_GLASS_PANE,
            Material.BLUE_STAINED_GLASS_PANE,
            Material.BROWN_STAINED_GLASS_PANE,
            Material.GREEN_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE,
            Material.BLACK_STAINED_GLASS_PANE,
    };

    @EventHandler
    public void onMiningBlock(PlayerInteractEvent pie) {
        if (!pie.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }
        if (!pie.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
            return;
        }
        ItemStack tool = pie.getPlayer().getInventory().getItemInMainHand();
        if(tool.getEnchantments().isEmpty()) {
            return;
        }
        if (!tool.getEnchantments().containsKey(Enchantment.EFFICIENCY)) {
            return;
        }
        Block block = pie.getClickedBlock();
        Location aim = block.getLocation();
        Location visual = aim.clone().add(0.5, 0.5, 0.5);

        switch (tool.getType()) {
            case Material.FLINT:
                for (Material m : GLASS) {
                    if (block.getBlockData().getMaterial().equals(m)) {
                        pie.setCancelled(true);
                        block.setType(Material.AIR);
                        block.getWorld().playSound(aim, Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
                        block.getWorld().spawnParticle(Particle.BLOCK, visual, 100, m.createBlockData());
                        block.getWorld().dropItemNaturally(aim, new ItemStack(m, 1));
                        if (!tool.getItemMeta().isUnbreakable()) {
                            giveNewTool(tool, pie.getPlayer());
                        }
                        break;
                    }
                }
                break;
            case Material.PRISMARINE_SHARD:
                if (block.getBlockData().getMaterial().equals(Material.BEDROCK)) {
                    pie.setCancelled(true);
                    block.setType(Material.AIR);
                    block.getWorld().playSound(aim, Sound.BLOCK_DEEPSLATE_TILES_BREAK, 1.0f, 1.0f);
                    block.getWorld().spawnParticle(Particle.BLOCK, visual, 100, Material.BEDROCK.createBlockData());
                    if (!tool.getItemMeta().isUnbreakable()) {
                        giveNewTool(tool, pie.getPlayer());
                    }
                    break;
                }
                break;
        }
    }

    private void giveNewTool(ItemStack tool, Player player) {
        NBTItem ToolNBT = new NBTItem(tool);
        if (ToolNBT.getInteger("Durability") == null || ToolNBT.getInteger("BasicDurability") == null) {
            player.sendMessage("耐久值警告：未找到工具耐久");
            return;
        }
        int Durability = ToolNBT.getInteger("Durability");
        int BasicDurability = ToolNBT.getInteger("BasicDurability");
        if (Durability <= 1 || BasicDurability <= 1) {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            return;
        }

        List<String> lore = tool.getLore();
        StringBuilder DurabilityStr = new StringBuilder("§f耐久值：");
        if (Durability/(double)BasicDurability > 0.5) {
            DurabilityStr.append("§a");
        }
        else if (Durability/(double)BasicDurability > 0.2) {
            DurabilityStr.append("§6");
        }
        else {
            DurabilityStr.append("§c");
        }
        DurabilityStr.append(Durability - 1);
        DurabilityStr.append("§f/");
        DurabilityStr.append(BasicDurability);

        if (lore == null) {
            lore = new ArrayList<>();
            lore.addFirst("");
        }
        if (lore.size() == 1) {
            lore.addFirst("");
        }
        lore.set(1, DurabilityStr.toString());
        tool.setLore(lore);

        NBT.modify(tool, nbt -> {
            nbt.setInteger("Durability", Durability - 1);
            nbt.setInteger("BasicDurability", BasicDurability);
        });
    }
}
