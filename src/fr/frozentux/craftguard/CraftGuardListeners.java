package fr.frozentux.craftguard;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class CraftGuardListeners implements Listener {
	
	private CraftGuardPlugin plugin;
	
	private CraftGuardConfig conf;

	
	public CraftGuardListeners(CraftGuardPlugin plugin, CraftGuardConfig conf){
		this.plugin = plugin;
		this.conf = conf;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e){
		//Furnace
		if((e.getSlotType().equals(InventoryType.SlotType.CONTAINER) || e.getSlotType().equals(InventoryType.SlotType.FUEL) || e.getSlotType().equals(InventoryType.SlotType.QUICKBAR)) && e.getInventory().getType().equals(InventoryType.FURNACE) && conf.isFurnace() && !((Player)e.getWhoClicked()).hasPermission(conf.getBasePerm() + ".*")){
			Player p = (Player) e.getWhoClicked();
			int id;
			int boucle; //DEBUG
			System.out.println(e.isShiftClick());
			if(e.isShiftClick()){
				id = e.getCurrentItem().getTypeId();
				plugin.getLogger().info("Ca marche ! Id : " + id);
				boucle = 1;
			}else if(e.getSlot() == 0 || e.getSlot() == 1){
				if(e.getSlot() == 0 && e.getCursor() != null){
					id = e.getCursor().getTypeId();
					boucle = 2;
				}
				else if(e.getSlot() == 1 && e.getInventory().getItem(0) != null){
					id = e.getInventory().getItem(0).getTypeId();
					boucle = 3;
				}
				else return;
			}else return;
			System.out.println(id + ";" + boucle);
			int resultId;
			if(conf.getSmeltReference().containsKey(id)){
				resultId = conf.getSmeltReference().get(id);
				boolean inList = (conf.getCheckList().contains(resultId)) ? true : false;
				boolean ok = false;
				if(inList){
					
					for(int i = 0 ; i<conf.getNomGroupes().size() && !ok ; i++){
						boolean permSpec = p.hasPermission(conf.getBasePerm() + "." + conf.getPermissions().get(i));
						if(permSpec && conf.getListeGroupes().get(i).contains(resultId)){
							ok = true;
						}
					}
				}else ok = conf.isPreventive();
			
				if(!ok){
					e.setCancelled(true);
					p.sendMessage(ChatColor.RED + conf.getDenyMessage());
					if(conf.isLog()) plugin.sendConsoleMessage("[CraftGuard] " + p.getName() + " tried to smelt " + Material.getMaterial(resultId) + " but did not have permission. Smelting denied");
				}
			}
		}
		
		//Craft
		if(e.getSlotType().equals(InventoryType.SlotType.RESULT) && (e.getInventory().getType().equals(InventoryType.WORKBENCH)|| e.getInventory().getType().equals(InventoryType.CRAFTING)) && e.getSlot() == 0 && !((Player)e.getWhoClicked()).hasPermission(conf.getBasePerm() + ".*")){
			ItemStack objet = e.getInventory().getItem(0);
			int id;
			if(objet != null)id = objet.getTypeId();
			else return;
			Player sender = (Player)e.getWhoClicked();
			boolean ok = false;
			boolean inList = (conf.getCheckList().contains(id)) ? true : false;
			if(inList && !sender.hasPermission(conf.getBasePerm() + ".*")){
				for(int i = 0 ; i<conf.getNomGroupes().size() && !ok ; i++){
					boolean permSpec = sender.hasPermission(conf.getBasePerm() + "." + conf.getPermissions().get(i));
					if(permSpec && conf.getListeGroupes().get(i).contains(id)){
						if(conf.getDamage().containsKey(conf.getNomGroupes().get(i) + ":" + id)){
							int dId = e.getInventory().getItem(0).getData().getData();
							for(int j = 0 ; j<conf.getDamage().get(conf.getNomGroupes().get(i) + ":" + id).split(":").length ; j++){
								if(dId == Integer.valueOf(conf.getDamage().get(conf.getNomGroupes().get(i) + ":" + id).split(":")[j]))ok = true;
							}
						}else ok = true;
					}
				}
			
				
			}else ok = (sender.hasPermission(conf.getBasePerm() + ".*")) ? true : conf.isPreventive();
			if(!ok){
				e.setCancelled(true);
				sender.sendMessage(ChatColor.RED + conf.getDenyMessage());
				if(conf.isLog()) plugin.sendConsoleMessage("[CraftGuard] " + sender.getName() + " tried to craft " + Material.getMaterial(id) + " but did not have permission. Craft denied");
			}
		}
	}
}
//EOF
