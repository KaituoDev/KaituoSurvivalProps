package fun.kaituo.kaituoSurvivalProps.props;

import de.tr7zw.changeme.nbtapi.NBTItem;
import fun.kaituo.kaituoSurvivalProps.KaituoSurvivalProps;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class ColorChangeTicket implements Listener {
    public static HashMap<UUID, Integer> editTimer = new HashMap<>();

    private final static Component dividingLine = Component.text("--------------------------------")
            .color(TextColor.color(204, 246, 207));
    private final static Component startEditMessage = Component.text("已开始新的")
            .decorate(TextDecoration.BOLD).decorate(TextDecoration.ITALIC).color(TextColor.color(116, 229, 137))
            .append(Component.text("副手物品名称颜色")
                    .decorate(TextDecoration.BOLD)
                    .color(TextColor.color(230, 255, 235)))
            .append(Component.text("编辑任务！")
                    .decorate(TextDecoration.BOLD).decorate(TextDecoration.ITALIC)
                    .color(TextColor.color(116, 229, 137)));
    private final static Component colorInputRequest = Component.text("请在1分钟内，在下方输入你想要的颜色的 ").color(TextColor.color(204, 246, 207))
            .append(Component.text("R ").color(TextColor.color(243, 21, 63)).decorate(TextDecoration.BOLD))
            .append(Component.text("G ").color(TextColor.color(63, 243, 21)).decorate(TextDecoration.BOLD))
            .append(Component.text("B ").color(TextColor.color(0, 158, 255)).decorate(TextDecoration.BOLD))
            .append(Component.text("值：").color(TextColor.color(204, 246, 207)));
    private final static Component colorInputTips = Component.text("(由三个[0,255]之间的整数构成，三个数中间用两个空格隔开)")
            .color(TextColor.color(204, 246, 207));
    private final static Component colorChooseTips = Component.text("(若想不出满意的颜色，可以去").color(TextColor.color(204, 246, 207))
            .append(Component.text("[Gradients.app]")
                    .decorate(TextDecoration.BOLD)
                    .color(TextColor.color(230, 255, 235))
                    .clickEvent(ClickEvent.openUrl("https://gradients.app/zh/colorpalette"))
                    .hoverEvent(HoverEvent.showText(Component.text("Gradients.app中文版 配色方案网站")
                            .color(TextColor.color(230, 255, 235)))))
            .append(Component.text("找找灵感)").color(TextColor.color(204, 246, 207)));
    private final static Component itemTooMuchErrorWarn = Component.text("每次最多只能编辑副手中的一个物品！")
            .decorate(TextDecoration.ITALIC).color(TextColor.color(255, 197, 72));
    private final static Component inputErrorWarn = Component.text("无法识别输入内容，请重新进行编辑！")
            .decorate(TextDecoration.ITALIC).color(TextColor.color(255, 197, 72));
    private final static Component ticketNotFoundErrorWarn = Component.text("未在主手找到改色卡，请重新进行编辑！")
            .decorate(TextDecoration.ITALIC).color(TextColor.color(255, 197, 72));
    private final static Component itemNotFoundErrorWarn = Component.text("未在副手栏找到合适的目标物品，请重新进行编辑！")
            .decorate(TextDecoration.ITALIC).color(TextColor.color(255, 197, 72));
    private final static Component itemNoNameErrorWarn = Component.text("请先给目标物品重命名，再重新进行编辑！")
            .decorate(TextDecoration.ITALIC).color(TextColor.color(255, 197, 72));
    private final static Component editAutoCancelMessage = Component.text("由于长时间未输入颜色，编辑任务已取消！")
            .decorate(TextDecoration.ITALIC).color(TextColor.color(255, 197, 72));
    private final static Component editSuccessMessage = Component.text("物品名称颜色编辑成功！")
            .decorate(TextDecoration.BOLD).decorate(TextDecoration.ITALIC).color(TextColor.color(116, 229, 137));

    private static void editOffhandItemNameColor(Player player, int R, int G, int B) {
        if (!player.getInventory().getItemInMainHand().getType().equals(Material.FLOWER_BANNER_PATTERN)) {
            warnPlayer(player, ticketNotFoundErrorWarn);
            return;
        }

        ItemStack ticket = player.getInventory().getItemInMainHand();
        NBTItem nbtItem = new NBTItem(ticket);
        if (!nbtItem.hasTag("ColorChangeTicket")) {
            warnPlayer(player, ticketNotFoundErrorWarn);
            return;
        }

        ItemStack item = player.getInventory().getItemInOffHand();
        if (item.getType().equals(Material.AIR)) {
            warnPlayer(player, itemNotFoundErrorWarn);
            return;
        }
        if (item.getAmount() != 1) {
            warnPlayer(player, itemTooMuchErrorWarn);
            return;
        }

        ItemStack resultItem = item.clone();
        Component name = item.getItemMeta().displayName();
        if (name == null) {
            warnPlayer(player, itemNoNameErrorWarn);
            return;
        }
        String nameText = PlainTextComponentSerializer.plainText().serialize(name);
        if (nameText.isEmpty()) {
            warnPlayer(player, itemNoNameErrorWarn);
            return;
        }

        ItemMeta meta = resultItem.getItemMeta();
        meta.displayName(Component.text(nameText)
                .color(TextColor.color(R, G, B))
                .decorate(TextDecoration.ITALIC));
        resultItem.setItemMeta(meta);

        player.getInventory().setItem(EquipmentSlot.OFF_HAND, resultItem);

        if (ticket.getAmount() > 1) {
            ticket.setAmount(ticket.getAmount() - 1);
        }
        else {
            player.getInventory().setItem(EquipmentSlot.HAND, new ItemStack(Material.AIR));
        }

        warnPlayer(player, editSuccessMessage);
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerUseTicket(PlayerInteractEvent pie) {
        if (pie.getAction() != Action.RIGHT_CLICK_AIR && pie.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = pie.getPlayer();
        if (player.hasCooldown(Material.FLOWER_BANNER_PATTERN)) {
            return;
        }
        if (player.getInventory().getItemInOffHand().getType().equals(Material.AIR)) {
            return;
        }
        if (!player.getInventory().getItemInMainHand().getType().equals(Material.FLOWER_BANNER_PATTERN)) {
            return;
        }

        ItemStack ticket = player.getInventory().getItemInMainHand();
        NBTItem nbtItem = new NBTItem(ticket);
        if (!nbtItem.hasTag("ColorChangeTicket")) {
            return;
        }

        pie.setCancelled(true);
        player.setCooldown(Material.FLOWER_BANNER_PATTERN, 20);

        if (player.getInventory().getItemInOffHand().getAmount() != 1) {
            warnPlayer(player, itemTooMuchErrorWarn);
            return;
        }

        player.sendMessage(dividingLine);
        player.sendMessage(startEditMessage);
        player.sendMessage(dividingLine);
        player.sendMessage(colorInputRequest);
        player.sendMessage(colorInputTips);
        player.sendMessage(colorChooseTips);
        UUID uuid = player.getUniqueId();
        int timerID = KaituoSurvivalProps.getScheduler().runTaskLater(KaituoSurvivalProps.getPlugin(),
                () -> {
                    Player editor = Bukkit.getPlayer(uuid);
                    if (editor == null) {
                        return;
                    }
                    warnPlayer(editor, editAutoCancelMessage);
                    editTimer.remove(uuid);
                }, 1200).getTaskId();
        editTimer.put(uuid, timerID);
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerInputColor(AsyncChatEvent ace) {
        Player player = ace.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!editTimer.containsKey(uuid)) {
            return;
        }

        ace.setCancelled(true);

        String input = PlainTextComponentSerializer.plainText().serialize(ace.message());
        String[] parts = input.split("\\s+"); // 一个或多个空格
        int R = 255, G = 255, B = 255;

        if (parts.length != 3) {
            warnPlayer(player, inputErrorWarn);
            KaituoSurvivalProps.getScheduler().cancelTask(editTimer.get(uuid));
            editTimer.remove(uuid);
            return;
        }

        try {
            R = Integer.parseInt(parts[0]);
            G = Integer.parseInt(parts[1]);
            B = Integer.parseInt(parts[2]);
        }
        catch (NumberFormatException e) {
            warnPlayer(player, inputErrorWarn);
            KaituoSurvivalProps.getScheduler().cancelTask(editTimer.get(uuid));
            editTimer.remove(uuid);
            return;
        }

        if (R < 0 || R > 255 ||
                G < 0 || G > 255 ||
                B < 0 || B > 255) {
            warnPlayer(player, inputErrorWarn);
            KaituoSurvivalProps.getScheduler().cancelTask(editTimer.get(uuid));
            editTimer.remove(uuid);
            return;
        }

        editOffhandItemNameColor(player, R, G, B);
        KaituoSurvivalProps.getScheduler().cancelTask(editTimer.get(uuid));
        editTimer.remove(uuid);
    }

    private static void warnPlayer(Player player, Component warn) {
        player.sendMessage(dividingLine);
        player.sendMessage(warn);
        player.sendMessage(dividingLine);
    }
}
