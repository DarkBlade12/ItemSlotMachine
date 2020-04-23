package com.darkblade12.itemslotmachine.core.command.slot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.core.Message;
import com.darkblade12.itemslotmachine.core.Permission;
import com.darkblade12.itemslotmachine.core.command.CommandBase;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.util.ItemList;
import com.darkblade12.itemslotmachine.util.MessageUtils;

public final class ItemCommand extends CommandBase<ItemSlotMachine> {
    public ItemCommand() {
        super("item", Permission.COMMAND_SLOT_ITEM, false, "<name>", "<clear/deposit/set>", "[default/hand/items]");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        String name = args[0];
        SlotMachine slot = plugin.slotMachineManager.getSlotMachine(name);
        if (slot == null) {
            plugin.sendMessage(sender, Message.SLOT_MACHINE_NOT_FOUND, name);
            return;
        }
        name = slot.getName();

        if (!slot.isItemPotEnabled()) {
            plugin.sendMessage(sender, Message.COMMAND_SLOT_ITEM_NOT_ENABLED, name);
            return;
        }

        String operation = args[1].toLowerCase();
        if (operation.equals("clear")) {
            slot.clearItemPot();
            plugin.sendMessage(sender, Message.COMMAND_SLOT_ITEM_CLEARED, name);
            return;
        } else if (args.length < 3) {
            plugin.sendMessage(sender, Message.COMMAND_SLOT_ITEM_NOT_SPECIFIED);
            return;
        }

        ItemList list;
        String source = args[2].toLowerCase();
        switch (source) {
            case "default":
                list = slot.getItemPotDefaultItems();
                break;
            case "hand":
                if (sender instanceof ConsoleCommandSender) {
                    plugin.sendMessage(sender, Message.COMMAND_NO_CONSOLE);
                    return;
                }

                Player player = (Player) sender;
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.getType() == Material.AIR) {
                    plugin.sendMessage(player, Message.COMMAND_SLOT_ITEM_EMPTY_HAND);
                    return;
                }
                list = new ItemList(item);
                break;
            default:
                String items = StringUtils.join(Arrays.copyOfRange(args, 2, args.length), " ");
                try {
                    list = ItemList.fromString(items);
                } catch (Exception ex) {
                    plugin.sendMessage(sender, Message.COMMAND_SLOT_ITEM_INVALID_LIST, ex.getMessage());
                    return;
                }
                break;
        }

        String itemsText = MessageUtils.toString(list);
        switch (operation) {
            case "deposit":
                slot.depositPotItems(list);
                Message message;
                if (list.size() == 1) {
                    message = Message.COMMAND_SLOT_ITEM_SINGLE_DEPOSITED;
                } else {
                    message = Message.COMMAND_SLOT_ITEM_DEPOSITED;
                }
                plugin.sendMessage(sender, message, itemsText, name);
                break;
            case "set":
                slot.setItemPot(list);
                plugin.sendMessage(sender, Message.COMMAND_SLOT_ITEM_SET, name, itemsText);
                break;
            default:
                displayUsage(sender, label);
                return;
        }
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return plugin.slotMachineManager.getNames();
            case 2:
                return Arrays.asList(new String[] { "clear", "deposit", "set" });
            case 3:
                List<String> completions = new ArrayList<String>(getItemNames());
                completions.add("default");
                completions.add("hand");
                return completions;
            default:
                return args.length > 3 ? getItemNames() : null;
        }
    }

    private static List<String> getItemNames() {
        List<String> names = new ArrayList<String>();
        for (Material material : Material.values()) {
            if (material.isItem()) {
                names.add(material.getKey().getKey());
            }
        }
        return names;
    }
}