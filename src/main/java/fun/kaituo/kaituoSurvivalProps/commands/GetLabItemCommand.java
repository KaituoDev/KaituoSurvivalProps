package fun.kaituo.kaituoSurvivalProps.commands;

import fun.kaituo.kaituoSurvivalProps.props.InfiniteExpBottle;
import fun.kaituo.kaituoSurvivalProps.props.InfiniteInkSac;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GetLabItemCommand implements TabCompleter,CommandExecutor {
    public static final List<String> labItemNames = List.of(
            "InfiniteInkSac",
            "InfiniteExpBottole"
    );

    public static ItemStack getLabItem(String name) {
        switch(name.toLowerCase()) {
            case "infiniteinksac":
                return InfiniteInkSac.getInfiniteInk_Sac();
            case "infiniteexpbottole":
                return InfiniteExpBottle.getInfiniteExpBottle();
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("getlabitem")) {
            return false;
        }
        if (!sender.isOp()) {
            return true;
        }
        if (!(sender instanceof Player p)) {
            sender.sendMessage("只有玩家才能使用此指令！");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage("指令使用错误！使用方式：/getlabitem <实验品名称>");
            return true;
        }
        String name = args[0];
        ItemStack labItem = getLabItem(name);
        if (labItem == null) {
            p.sendMessage("该实验品不存在！");
            return true;
        }
        p.getInventory().addItem(labItem);
        return true;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, Command cmd, @Nonnull String alias, @Nonnull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("getlabitem")) {
            return new ArrayList<>();
        }
        if (args.length != 1) {
            return new ArrayList<>();
        }
        return labItemNames.stream().filter(
                completion -> completion.toLowerCase().startsWith(args[0].toLowerCase())
        ).toList();
    }
}
