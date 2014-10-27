package Trubby.co.th;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin{
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(sender instanceof Player){
			Player p = (Player) sender;
			
			if(label.equalsIgnoreCase("sortchest")){
			
				Block block = p.getTargetBlock(null, 5);
				
				if(block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST){
					Chest chest = (Chest) block.getState();
					Inventory inv = chest.getInventory();
					
					SortInventory(inv, p);
					p.sendMessage(ChatColor.GREEN + "Chest is sorted for you :)");
				}else{
					p.sendMessage(ChatColor.RED + "That's not a chest!");
					p.sendMessage(ChatColor.RED + "That's " + block.getType().toString().toLowerCase() + ".");
				}
				
			}
		}
		
		return false;
	}
	
	public static void SortInventory(Inventory i, Player p) {
		InventoryType type = i.getType();
		PlayerInventory pi = null;
		boolean isPlayerInventory = false;
		boolean good = true;
		if (type == InventoryType.CHEST
				&& p.hasPermission("magicchest.sort.chest"))
			good = true;
		if (type == InventoryType.DISPENSER
				&& p.hasPermission("magicchest.sort.dispenser"))
			good = true;
		if (type == InventoryType.DROPPER
				&& p.hasPermission("magicchest.sort.dropper"))
			good = true;
		if (type == InventoryType.ENDER_CHEST
				&& p.hasPermission("magicchest.sort.enderchest"))
			good = true;
		if (type == InventoryType.HOPPER
				&& p.hasPermission("magicchest.sort.hopper"))
			good = true;
		if (type == InventoryType.PLAYER
				&& p.hasPermission("magicchest.sort.player")) {
			isPlayerInventory = true;
			pi = (PlayerInventory) i;
			good = true;
		}

		if (good) {
			List<ItemStack> stacks = new ArrayList<ItemStack>();
			if (isPlayerInventory) {
				int skip = 9;
				for (ItemStack is : pi.getContents()) {
					if (skip < 1) {
						if (is == null)
							continue;
						for (ItemStack check : stacks) {
							if (check == null)
								continue;
							if (check.isSimilar(is)) {
								int transfer = Math.min(
										is.getAmount(),
										check.getMaxStackSize()
												- check.getAmount());
								is.setAmount(is.getAmount() - transfer);
								check.setAmount(check.getAmount()
										+ transfer);
							}
						}
						if (is.getAmount() > 0) {
							stacks.add(is);
						}
					} else {
						skip--;
					}
				}
			} else {
				for (ItemStack is : i.getContents()) {
					if (is == null)
						continue;
					for (ItemStack check : stacks) {
						if (check == null)
							continue;
						if (check.isSimilar(is)) {
							int transfer = Math.min(
									is.getAmount(),
									check.getMaxStackSize()
											- check.getAmount());
							is.setAmount(is.getAmount() - transfer);
							check.setAmount(check.getAmount()
									+ transfer);
						}
					}
					if (is.getAmount() > 0) {
						stacks.add(is);
					}
				}
			}
			Collections.sort(stacks, new Comparator<ItemStack>() {

				@Override
				public int compare(ItemStack o1, ItemStack o2) {
					if (o1.getType().toString()
							.compareTo(o2.getType().toString()) > 0) {
						return 1;
					} else if (o1.getType().toString()
							.compareTo(o2.getType().toString()) < 0) {
						return -1;
					} else if (o1.getDurability() > o2.getDurability()) {
						return 1;
					} else if (o1.getDurability() < o2.getDurability()) {
						return -1;
					} else if (o1.getAmount() > o2.getAmount()) {
						return -1;
					} else if (o1.getAmount() < o2.getAmount()) {
						return 1;
					} else {
						return 0;
					}
				}
			});
			if (!isPlayerInventory) {
				i.clear();
				i.setContents(stacks.toArray(new ItemStack[0]));
			} else {
				int slot = 9;
				for (int n = 9; n < 36; n++) {
					p.getInventory().setItem(n,
							new ItemStack(Material.AIR));
				}
				for (ItemStack is : stacks) {
					if (slot <= 35) {
						p.getInventory().setItem(slot, is);
						slot++;
					}
				}
			}
		}
	}
}
