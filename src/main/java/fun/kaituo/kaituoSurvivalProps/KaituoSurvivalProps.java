package fun.kaituo.kaituoSurvivalProps;

import fun.kaituo.kaituoSurvivalProps.commands.GetBlockBreakerCommand;
import fun.kaituo.kaituoSurvivalProps.commands.GetSizeChangePotionCommand;
import fun.kaituo.kaituoSurvivalProps.props.BlockBreaker;
import fun.kaituo.kaituoSurvivalProps.props.BrownSugarSoup;
import fun.kaituo.kaituoSurvivalProps.props.InfiniteFood;
import fun.kaituo.kaituoSurvivalProps.props.SizeChangePotion;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

import static fun.kaituo.kaituoSurvivalProps.props.SizeChangePotion.Countdown;

public final class KaituoSurvivalProps extends JavaPlugin {
    private static KaituoSurvivalProps plugin;
    public static KaituoSurvivalProps getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        registerProps();
        registerCommands();
        plugin = this;
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    private void registerProps() {

        // 方块破坏者
        Bukkit.getPluginManager().registerEvents(new BlockBreaker(), this);

        // 变形药水
        if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective("SizeChangeDuration") == null) {
            Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("SizeChangeDuration", "dummy", "SizeChangeDuration");
        }
        Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("SizeChangeDuration");
        if (objective != null) {

            Bukkit.getPluginManager().registerEvents(new SizeChangePotion(objective), this);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Countdown(objective);
                }
            }.runTaskTimer(this, 0, 1);
        }

        // 无限食物
        Bukkit.getPluginManager().registerEvents(new InfiniteFood(), this);

        // 红糖水
        Bukkit.getPluginManager().registerEvents(new BrownSugarSoup(), this);
    }

    private void registerCommands() {
        if (getCommand("getsizepotion") != null) {
            getCommand("getsizepotion").setExecutor(new GetSizeChangePotionCommand());
        }
        if (getCommand("getbreaker") != null) {
            getCommand("getbreaker").setExecutor(new GetBlockBreakerCommand());
        }
    }
}
