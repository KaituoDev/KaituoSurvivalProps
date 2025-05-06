package fun.kaituo.kaituoSurvivalProps.commands;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetBlockBreakerCommand implements CommandExecutor , TabCompleter {
    private static final String WRONG_COMMAND_USAGE = "§c正确用法: /getbreaker <目标方块名称> <工具耐久值(不填默认为无限耐久)>";
    private final List<String> SUBCOMMANDS = Arrays.asList("glass", "bedrock");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("getbreaker")) {
            if (!sender.hasPermission("kaituoprops.getbreaker")) {
                sender.sendMessage("§c你没有权限使用此指令！");
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c只有玩家才能使用此指令！");
                return true;
            }

            String Durability;
            int drb;
            if (args.length == 1) {
                Durability = "§a§l无限！";
                drb = -1;
            }
            else if (args.length == 2) {
                try {
                    drb = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    sender.sendMessage(WRONG_COMMAND_USAGE);
                    return true;
                }
                Durability = String.valueOf(drb);
            }
            else {
                sender.sendMessage(WRONG_COMMAND_USAGE);
                return true;
            }

            String type = args[0];
            Player player = (Player) sender;

            ItemStack item;
            ItemMeta meta;
            NBTItem nbtItem;
            ItemStack finalItem;
            switch (type) {
                case "bedrock":
                    item = new ItemStack(Material.PRISMARINE_SHARD);
                    meta = item.getItemMeta();

                    if (drb != -1) {
                        meta.setDisplayName("§f基岩钻头");
                    }
                    else {
                        meta.setDisplayName("§d基岩钻头");
                    }
                    meta.setLore(Arrays.asList(
                            "",
                            "§f耐久值：" + Durability,
                            "",
                            "§6§o由矮人族锻造的坚固钻头，能破坏世界上最难以破坏的岩石：基岩！"
                    ));
                    meta.addEnchant(Enchantment.EFFICIENCY, 10, true);
                    if (drb == -1) {
                        meta.setUnbreakable(true);
                    }
                    item.setItemMeta(meta);

                    nbtItem = new NBTItem(item);
                    nbtItem.setInteger("Durability", drb);
                    nbtItem.setInteger("BasicDurability", drb);
                    finalItem = nbtItem.getItem();
                    NBT.modifyComponents(finalItem, nbt -> {
                        nbt.setInteger("minecraft:max_stack_size", 1);
                    });
                    player.getInventory().addItem(finalItem);
                    return false;

                case "glass":
                    item = new ItemStack(Material.FLINT);
                    meta = item.getItemMeta();

                    if (drb != -1) {
                        meta.setDisplayName("§f玻璃刀");
                    }
                    else {
                        meta.setDisplayName("§d玻璃刀");
                    }
                    meta.setLore(Arrays.asList(
                            "",
                            "§f耐久值：" + Durability,
                            "",
                            "§b§o由工业级金刚石磨制而成，可用于快速切割玻璃！"
                    ));
                    meta.addEnchant(Enchantment.EFFICIENCY, 10, true);
                    meta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
                    if (drb == -1) {
                        meta.setUnbreakable(true);
                    }
                    item.setItemMeta(meta);

                    nbtItem = new NBTItem(item);
                    nbtItem.setInteger("Durability", drb);
                    nbtItem.setInteger("BasicDurability", drb);
                    finalItem = nbtItem.getItem();
                    NBT.modifyComponents(finalItem, nbt -> {
                        nbt.setInteger("minecraft:max_stack_size", 1);
                    });
                    player.getInventory().addItem(finalItem);
                    return false;
            }
            player.sendMessage("§c尚不支持的目标方块类型！目前支持以下方块：");
            player.sendMessage("§6玻璃(不区分颜色和玻璃板)(glass)");
            player.sendMessage("§6基岩(bedrock)");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) { // 第一个参数补全
            for (String sub : SUBCOMMANDS) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
        }
        return completions;
    }
}
