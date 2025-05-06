package fun.kaituo.kaituoSurvivalProps.commands;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GetSizeChangePotionCommand implements CommandExecutor {

    private static final Set<Character> VALID_COLOR_CODES = new HashSet<>(Arrays.asList(
            '0','1','2','3','4','5','6','7','8','9',
            'a','b','c','d','e','f','k','l','m','n','o','r'
    ));

    public static final String WRONG_COMMAND_USAGE = "§c正确用法: /getsizepotion <大小倍率> <持续时间(秒)> [物品描述(空格=换行)...]";

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("getsizepotion")) {
            if (!sender.hasPermission("kaituoprops.getsizepotion")) {
                sender.sendMessage("§c你没有权限使用此指令！");
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c只有玩家才能使用此指令！");
                return true;
            }
            Player player = (Player) sender;
            if (args.length < 2) {
                player.sendMessage(WRONG_COMMAND_USAGE);
                return true;
            }

            double scale;
            int duration;
            try {
                scale = Double.parseDouble(args[0]);
                duration = Integer.parseInt(args[1]);
            } catch (Exception e) {
                sender.sendMessage(WRONG_COMMAND_USAGE);
                return true;
            }
            if (scale > 16 || scale < 0.1) {
                sender.sendMessage("§c大小倍率必须在0.1到16之间！");
                return true;
            }
            if (duration > 65535 || duration < 1) {
                sender.sendMessage("§c效果持续时间必须在1秒到65535秒之间！");
                return true;
            }
            if (scale == 1) {
                sender.sendMessage("§c大小倍率不能为1！");
                return true;
            }

            List<String> descriptions = new ArrayList<>();
            if (args.length >= 3) {
                for (int i = 2; i < args.length; ++i) {
                    descriptions.add(processColorCodes(args[i]));
                }
            }

            player.getInventory().addItem(createSizePotion(scale, duration, descriptions));
            return true;
        }
        return false;
    }

    private ItemStack createSizePotion(double scale, int duration, List<String> lore) {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        double blockInteractionRange;
        double entityInteractionRange;
        double safeFallDistance;
        int speedEffectLevel;
        int jumpEffectLevel;
        int slownessEffectLevel;
        int minutes = duration/60;
        int seconds = duration - minutes*60;

        if ((scale*3 + 1.5) >= 3) {
            blockInteractionRange = Math.round((scale*3 + 1.5) * 10) / 10.0;
        } else {
            blockInteractionRange = 3;
        }
        if (scale*3 >= 2) {
            entityInteractionRange = Math.round((scale*3) * 10) / 10.0;
        } else {
            entityInteractionRange = 2;
        }
        if ((scale*2 + 1) >= 2) {
            safeFallDistance = Math.round((scale*2 + 1) * 10) / 10.0;
        } else {
            safeFallDistance = 2.0;
        }

        if (scale > 1) {
            speedEffectLevel = (int) scale;
            jumpEffectLevel = (int) scale;
            slownessEffectLevel = 0;
            meta.setColor(Color.fromRGB(0x3AE4FF));
            meta.setDisplayName("§b放大§f药水");
            lore.addFirst("§9放大（" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds) + "）");
        }
        else {
            jumpEffectLevel = 0;
            speedEffectLevel = 0;
            if (-4*scale + 4.1 > 3) {
                slownessEffectLevel = 3;
            }
            else {
                slownessEffectLevel = (int) (-4 * scale + 4.1);
            }
            meta.setColor(Color.fromRGB(0xFFE164));
            meta.setDisplayName("§e缩小§f药水");
            lore.addFirst("§9缩小（" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds) + "）");
        }

        meta.setLore(lore);
        potion.setItemMeta(meta);

        NBT.modify(potion, nbt -> {
            nbt.setDouble("scale", scale);
            nbt.setInteger("duration", duration);
            nbt.setDouble("blockInteractionRange", blockInteractionRange);
            nbt.setDouble("entityInteractionRange", entityInteractionRange);
            nbt.setDouble("fallDamageMultiplier", 0.5);
            nbt.setDouble("safeFallDistance", safeFallDistance);
            nbt.setInteger("speedEffectLevel", speedEffectLevel);
            nbt.setInteger("jumpEffectLevel", jumpEffectLevel);
            nbt.setInteger("slownessEffectLevel", slownessEffectLevel);
        });

        return potion;
    }

    private String processColorCodes(String input) {
        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();

        for (int i = 0; i < chars.length; ++i) {
            char current = chars[i];
            if (current == '&' && i + 1 < chars.length) {
                char next = chars[i + 1];
                if (VALID_COLOR_CODES.contains(next)) {
                    result.append('§').append(next);
                    ++i;
                } else {
                    result.append(current);
                }
            } else {
                result.append(current);
            }
        }
        return result.toString();
    }
}
