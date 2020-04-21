package com.darkblade12.itemslotmachine.command.slot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.command.CommandDetails;
import com.darkblade12.itemslotmachine.command.ICommand;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.util.ItemList;

@CommandDetails(name = "item", params = "<name> <deposit/set> <hand/items>", permission = "ItemSlotMachine.slot.item", infiniteParams = true)
public final class ItemCommand implements ICommand {
    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] params) {
        SlotMachine slot = plugin.slotMachineManager.getSlotMachine(params[0]);
        if (slot == null) {
            sender.sendMessage(plugin.messageManager.slot_machine_not_existent());
            return;
        } else if (!slot.isItemPotEnabled()) {
            sender.sendMessage(plugin.messageManager.slot_machine_item_pot_not_enabled());
            return;
        }

        String source = params[2];
        ItemList list = new ItemList();
        if (source.equalsIgnoreCase("hand")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.messageManager.command_no_console_executor());
                return;
            }

            ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) {
                sender.sendMessage(plugin.messageManager.player_no_item_in_hand());
                return;
            }
            list.add(item);
        } else {
            try {
                list = ItemList.fromString(StringUtils.join(Arrays.copyOfRange(params, 2, params.length), " "));
            } catch (Exception e) {
                sender.sendMessage(plugin.messageManager.slot_machine_item_pot_invalid_item_list(e.getMessage()));
                return;
            }
        }

        String operation = params[1].toLowerCase();
        switch (operation) {
            case "deposit":
                slot.depositPotItems(list);
                if (list.size() == 1) {
                    sender.sendMessage(plugin.messageManager.slot_machine_item_pot_deposit(list.get(0), slot.getName()));
                } else {
                    sender.sendMessage(plugin.messageManager.slot_machine_item_pot_deposit_multiple(list, slot.getName()));
                }
                break;
            case "set":
                slot.setItemPot(list);
                sender.sendMessage(plugin.messageManager.slot_machine_item_pot_set(slot.getName(), list));
                break;
            default:
                plugin.slotCommandHandler.showUsage(sender, label, this);
                return;
        }
    }

    @Override
    public List<String> getCompletions(ItemSlotMachine plugin, CommandSender sender, String[] params) {
        switch (params.length) {
            case 1:
                return plugin.slotMachineManager.getSlotMachines().getNames();
            case 2:
                return Arrays.asList(new String[] { "deposit", "set" });
            case 3:
                List<String> completions = new ArrayList<String>(getItemNames());
                completions.add("hand");
                return completions;
            default:
                return params.length > 3 ? getItemNames() : null;
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