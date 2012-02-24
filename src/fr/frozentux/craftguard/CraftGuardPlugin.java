package fr.frozentux.craftguard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;



public class CraftGuardPlugin extends JavaPlugin{
	
	private Logger log;
	private CraftGuardConfig conf;
	private CraftGuardListeners listen;
	private String version;
	@Override
	public void onDisable() {
		this.sendConsoleMessage("CraftGuard ver. " + version + " succesfully stopped !");
	}

	@Override
	public void onEnable() {
		version = this.getDescription().getVersion();
		log = this.getServer().getLogger();
		conf = new CraftGuardConfig(this);
		conf.initConf();
		listen = new CraftGuardListeners(this, conf);
		PluginManager pm = this.getServer().getPluginManager(); 
		pm.registerEvents(listen, this);
		this.sendConsoleMessage("CraftGuard ver. " + version + " succesfully started !");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(args.length < 1) return false;
		if(cmd.getName().equals("cg") && args[0].equals("reload")){
			if(sender instanceof Player){
				Player player = (Player) sender;
				System.out.println(player.hasPermission("craftguard.admin"));
				if(player.hasPermission("craftguard.admin")){
					conf.reloadConf();
					player.sendMessage(ChatColor.GREEN + "[CraftGuard] Configuration reloaded !");
				}else player.sendMessage(ChatColor.RED + "You don't have permission to do this !");
			}else{
				conf.reloadConf();
				sendConsoleMessage("[CraftGuard] Configuration reloaded !");
			}
			return true;
		}
		if(cmd.getName().equals("cg") && args.length == 2 && args[0].equals("list") ){
			if(sender instanceof Player){
				Player player = (Player) sender;
				if(player.hasPermission("craftguard.admin")){
					if(conf.getNomGroupes().contains(args[1])){
						player.sendMessage("CraftGuard : Content of list " + args[1]);
						ArrayList<Integer> listeAAfficher = conf.getListeGroupes().get(conf.getNomGroupes().indexOf(args[1]));
						Iterator<Integer> it = listeAAfficher.iterator();
						while(it.hasNext()){
							int id = it.next();
							if(conf.getDamage().containsKey(args[1] + ":" + id))player.sendMessage("- " + Material.getMaterial(id).toString() + ChatColor.GOLD + " (" + conf.getDamage().get(args[1] + ":" + id) + ")");
							else player.sendMessage("- " + Material.getMaterial(id).toString());
						}
					}else player.sendMessage(ChatColor.RED + "[CraftGuard] Group " + args[1] + " does not exist !");
				}else player.sendMessage(ChatColor.RED + "You don't have permission to do this !");
			}else{
				if(conf.getNomGroupes().contains(args[1])){
					sendConsoleMessage("CraftGuard : Content of list " + args[1]);
					ArrayList<Integer> listeAAfficher = conf.getListeGroupes().get(conf.getNomGroupes().indexOf(args[1]));
					Iterator<Integer> it = listeAAfficher.iterator();
					while(it.hasNext()){
						int id = it.next();
						if(conf.getDamage().containsKey(args[1] + ":" + id))sendConsoleMessage("- " + Material.getMaterial(id).toString() + " (" + conf.getDamage().get(args[1] + ":" + id) + ")");
						else sendConsoleMessage("- " + Material.getMaterial(id).toString());
					}
				}else sendConsoleMessage("[CraftGuard] Group " + args[1] + " does not exist !");
			}
			return true;
		}else if(args.length != 2){
			sender.sendMessage("Usage : /cg list <groupname>");
			return true;
		}
		return false;
		
	}
	
	public void sendConsoleMessage(String message){
		log.info(message);
	}
	
	public void sendConsoleWarning(String message){
		log.warning(message);
	}

}
