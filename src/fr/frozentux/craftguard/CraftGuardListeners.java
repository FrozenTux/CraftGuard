package fr.frozentux.craftguard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;

public class CraftGuardListeners implements Listener {
	
	private CraftGuardPlugin plugin;
	
	private CraftGuardConfig conf;

	
	public CraftGuardListeners(CraftGuardPlugin plugin, CraftGuardConfig conf){
		this.plugin = plugin;
		this.conf = conf;
	}
	
	@EventHandler
	public void onInventoryCraft(InventoryCraftEvent ev){
		
		int id = ev.getResult().getTypeId();
		Player sender = ev.getPlayer();
		boolean ok = false;
		boolean inList = (conf.getCheckList().contains(id)) ? true : false;
		if(inList && !sender.hasPermission(conf.getBasePerm() + ".*")){
			for(int i = 0 ; i<conf.getNomGroupes().size() && !ok ; i++){
				boolean permSpec = sender.hasPermission(conf.getBasePerm() + "." + conf.getPermissions().get(i));
				if(permSpec && conf.getListeGroupes().get(i).contains(id)){
					if(conf.getDamage().containsKey(conf.getNomGroupes().get(i) + ":" + id)){
						byte dId = ev.getResult().getData().getData();
						for(int j = 0 ; j<conf.getDamage().get(conf.getNomGroupes().get(i) + ":" + id).split(":").length ; j++){
							if(dId == Integer.valueOf(conf.getDamage().get(conf.getNomGroupes().get(i) + ":" + id).split(":")[j]))ok = true;
						}
					}else ok = true;
				}
			}
		
			
		}else if(sender.hasPermission(conf.getBasePerm() + ".*"))ok = true;
		
		if(!ok){
			ev.setCancelled(true);
			sender.sendMessage(ChatColor.RED + conf.getDenyMessage());
			if(conf.isLog()) plugin.sendConsoleMessage("[CraftGuard] " + sender.getName() + " tried to craft " + ev.getResult().getType().toString() + " but did not have permission. Craft denied");
		}
		
		
	}
}
