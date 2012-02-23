package fr.frozentux.craftguard;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
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
		return false;
		
	}
	
	public void sendConsoleMessage(String message){
		log.info(message);
	}
	
	public void sendConsoleWarning(String message){
		log.warning(message);
	}

}
