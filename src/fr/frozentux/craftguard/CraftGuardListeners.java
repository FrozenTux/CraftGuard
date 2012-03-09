package fr.frozentux.craftguard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class CraftGuardListeners implements Listener {
	
	private CraftGuardPlugin plugin;
	
	private CraftGuardConfig conf;

	
	public CraftGuardListeners(CraftGuardPlugin plugin, CraftGuardConfig conf){
		this.plugin = plugin;
		this.conf = conf;
	}
	
	@EventHandler
	public void onCraftItem(PrepareItemCraftEvent ev){
		
		int id = ev.getRecipe().getResult().getTypeId();
		System.out.println(id);
		Player sender = (Player)ev.getViewers().get(0);
		System.out.println(sender.toString());
		boolean ok = false;
		boolean inList = (conf.getCheckList().contains(id)) ? true : false;
		System.out.println(inList);
		System.out.println(inList);
		if(inList && !sender.hasPermission(conf.getBasePerm() + ".*")){
			for(int i = 0 ; i<conf.getNomGroupes().size() && !ok ; i++){
				boolean permSpec = sender.hasPermission(conf.getBasePerm() + "." + conf.getPermissions().get(i));
				if(permSpec && conf.getListeGroupes().get(i).contains(id)){
					if(conf.getDamage().containsKey(conf.getNomGroupes().get(i) + ":" + id)){
						int dId = ev.getRecipe().getResult().getTypeId();
						for(int j = 0 ; j<conf.getDamage().get(conf.getNomGroupes().get(i) + ":" + id).split(":").length ; j++){
							if(dId == Integer.valueOf(conf.getDamage().get(conf.getNomGroupes().get(i) + ":" + id).split(":")[j]))ok = true;
						}
					}else ok = true;
				}
			}
		
			
		}else ok = true;
		
		if(!ok){
			ev.getInventory().setResult(new ItemStack(0, 0));
			sender.sendMessage(ChatColor.RED + conf.getDenyMessage());
			if(conf.isLog()) plugin.sendConsoleMessage("[CraftGuard] " + sender.getName() + " tried to craft " + ev.getRecipe().getResult().getType().toString() + " but did not have permission. Craft denied");
		}
		
		
	}
}
