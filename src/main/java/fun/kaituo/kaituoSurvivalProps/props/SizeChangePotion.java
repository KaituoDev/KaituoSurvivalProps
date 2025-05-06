package fun.kaituo.kaituoSurvivalProps.props;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.Objects;

public class SizeChangePotion implements Listener {
    private final Objective SizeChangeCountdown;
    public SizeChangePotion(Objective SizeChangeCountdown) {
        this.SizeChangeCountdown = SizeChangeCountdown;
    }

    @EventHandler
    public void onDrinkingSizeChangePotion(PlayerItemConsumeEvent pice){
        if (!pice.getItem().getType().equals(Material.POTION)) {
            return;
        }
        ItemStack potion = pice.getItem();
        NBTItem nbtItem = new NBTItem(potion);
        if (!nbtItem.hasTag("scale")) {
            return; // 用custom_data信息防止伪造药水
        }

        Double Size = NBT.get(potion, nbt -> (Double) nbt.getDouble("scale"));
        int EffectDuration = NBT.get(potion, nbt -> (Integer) nbt.getInteger("duration"));
        double bir = NBT.get(potion, nbt -> (Double) nbt.getDouble("blockInteractionRange"));
        double eir = NBT.get(potion, nbt -> (Double) nbt.getDouble("entityInteractionRange"));
        double fdm = NBT.get(potion, nbt -> (Double) nbt.getDouble("fallDamageMultiplier"));
        double sfd = NBT.get(potion, nbt -> (Double) nbt.getDouble("safeFallDistance"));
        int SpeedLevel = NBT.get(potion, nbt -> (Integer) nbt.getInteger("speedEffectLevel")) - 1;
        int JumpLevel = NBT.get(potion, nbt -> (Integer) nbt.getInteger("jumpEffectLevel")) - 1;
        int SlownessLevel = NBT.get(potion, nbt -> (Integer) nbt.getInteger("slownessEffectLevel")) - 1;

        Player p = pice.getPlayer();

        if (!SizeChangeCountdown.getScore(p.getName()).isScoreSet()) {
            SizeChangeCountdown.getScore(p.getName()).setScore(0);
        }
        else if (SizeChangeCountdown.getScore(p.getName()).getScore() >= 1) {
            p.sendMessage("§c您已经使用过变形药剂了！请等待上一支药剂失效，或饮用牛奶/断开重连服务器来清除变形效果！");
            pice.setCancelled(true);
            return;
        }

        StringBuilder MessageBuilder = new StringBuilder("§l你将保持变");
        int minutes = EffectDuration/60;
        int seconds = EffectDuration - minutes*60;
        if (Size > 1) {
            p.sendMessage("§b§o上面的空气有更新鲜吗？");
            MessageBuilder.append("大的状态§r§a");
        }
        else if (Size < 1) {
            p.sendMessage("§e§o下面有什么奇特的景观吗？");
            MessageBuilder.append("小的状态§r§a");
        }
        else {
            p.sendMessage("§c无效的变形参数！请联系管理员重新发放药水，或联系YFShadaow/APairOfMoons反馈bug");
            pice.setCancelled(true);
            return;
        }

        if (minutes >= 1) {
            MessageBuilder.append(minutes);
            MessageBuilder.append("§a§l分§r§a");
            MessageBuilder.append(seconds);
            MessageBuilder.append("§a§l秒");
        }
        else {
            MessageBuilder.append(EffectDuration);
            MessageBuilder.append("§a§l秒");
        }
        MessageBuilder.append("§f§l，可以通过饮用牛奶解除。");

        p.sendMessage(MessageBuilder.toString());

        runConsoleCommand("attribute " + p.getName() + " minecraft:generic.scale base set " + Size); // 设置玩家大小倍率
        runConsoleCommand("attribute " + p.getName() + " minecraft:player.block_interaction_range base set " + bir); // 设置玩家的方块交互距离
        runConsoleCommand("attribute " + p.getName() + " minecraft:player.entity_interaction_range base set " + eir); // 设置玩家的实体交互距离
        runConsoleCommand("attribute " + p.getName() + " minecraft:generic.fall_damage_multiplier base set " + fdm); // 设置玩家的摔落伤害倍率
        runConsoleCommand("attribute " + p.getName() + " minecraft:generic.safe_fall_distance base set " + sfd); // 设置玩家受摔落伤害的起始高度

        if (SpeedLevel >= 0) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,EffectDuration*20,SpeedLevel));
        }
        if (JumpLevel >= 0) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST,EffectDuration*20,JumpLevel));
        }
        if (SlownessLevel >= 0) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,EffectDuration*20,SlownessLevel));
        }

        SizeChangeCountdown.getScore(p.getName()).setScore(EffectDuration*20); // 设置玩家剩余效果时间
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDrinkingMilk(PlayerItemConsumeEvent pice) {
        if (pice.getItem().getType().equals(Material.MILK_BUCKET)) {
            if (SizeChangeCountdown.getScore(pice.getPlayer().getName()).isScoreSet() && SizeChangeCountdown.getScore(pice.getPlayer().getName()).getScore() >= 1) {
                Player p = pice.getPlayer();
                p.sendMessage("§a§l您喝下了牛奶，已将您变回原本的大小");
                runConsoleCommand("attribute " + p.getName() + " minecraft:generic.scale base set 1"); // 恢复玩家大小
                runConsoleCommand("attribute " + p.getName() + " minecraft:player.block_interaction_range base set 4.5"); // 恢复玩家方块交互距离
                runConsoleCommand("attribute " + p.getName() + " minecraft:player.entity_interaction_range base set 3"); // 恢复玩家实体交互距离
                runConsoleCommand("attribute " + p.getName() + " minecraft:generic.fall_damage_multiplier base set 1"); // 恢复玩家摔落伤害
                runConsoleCommand("attribute " + p.getName() + " minecraft:generic.safe_fall_distance base set 3"); // 恢复玩家摔落受伤起始高度
                p.removePotionEffect(PotionEffectType.JUMP_BOOST);
                p.removePotionEffect(PotionEffectType.SPEED);
                p.removePotionEffect(PotionEffectType.SLOWNESS);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,60,0));
                SizeChangeCountdown.getScore(p.getName()).setScore(0);
            }
        }
    }

    public static void Countdown(Objective aimObjective) {
        for (String entry : Objects.requireNonNull(aimObjective.getScoreboard()).getEntries()) {
            Player aim = Bukkit.getPlayer(entry);
            if (aim != null) {
                Score score = aimObjective.getScore(entry);
                if (score.getScore() > 1) {
                    score.setScore(score.getScore() - 1);
                } else if (score.getScore() != 0){
                    score.setScore(0);
                    runConsoleCommand("attribute " + aim.getName() + " minecraft:generic.scale base set 1"); // 恢复玩家大小
                    runConsoleCommand("attribute " + aim.getName() + " minecraft:player.block_interaction_range base set 4.5"); // 恢复玩家方块交互距离
                    runConsoleCommand("attribute " + aim.getName() + " minecraft:player.entity_interaction_range base set 3"); // 恢复玩家实体交互距离
                    runConsoleCommand("attribute " + aim.getName() + " minecraft:generic.fall_damage_multiplier base set 1"); // 恢复玩家摔落伤害
                    runConsoleCommand("attribute " + aim.getName() + " minecraft:generic.safe_fall_distance base set 3"); // 恢复玩家摔落受伤起始高度
                    aim.removePotionEffect(PotionEffectType.SPEED);
                    aim.removePotionEffect(PotionEffectType.JUMP_BOOST);
                    aim.removePotionEffect(PotionEffectType.SLOWNESS);
                    aim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 60, 0));
                    aim.sendMessage("§a§l变形效果时长结束！");
                }
            }
        }
    }

    private static void runConsoleCommand(String command) { // 用于调用原版指令以更改玩家数据
        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        Bukkit.dispatchCommand(sender, command);
    }
}
