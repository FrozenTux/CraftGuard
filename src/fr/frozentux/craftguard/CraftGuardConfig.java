package fr.frozentux.craftguard;

import java.io.File;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CraftGuardConfig {
	
	private CraftGuardPlugin plugin;
	
	private ArrayList<String> defaultValues = new ArrayList<String>();
	
	

	private Map<String, String> damage = new HashMap<String, String>();
	private Map<Integer, Integer> smeltReference = new HashMap<Integer, Integer>();
	
	private String[] defautIntValues = {"22","26","44"};
	private int[] smeltable = 	{5, 319, 363, 365, 349, 15,  14,  56,  21,  73,  16,  12, 4, 337, 17,  81};
	private int[] smelted = 	{1, 320, 364, 366, 350, 265, 266, 264, 351, 331, 263, 20, 1, 336, 263, 351};
	//private int[] damageableBlocks = {6, 17, 18, 44, 351, 35};
	
	private ArrayList<ArrayList<Integer>> listeGroupes = new ArrayList<ArrayList<Integer>>();
	private ArrayList<String> nomGroupes = new ArrayList<String>();
	private ArrayList<String> permissions = new ArrayList<String>();
	private ArrayList<Integer> checkList = new ArrayList<Integer>();
	
	private boolean log, furnace, preventive;
	
	private String denyMessage,
		basePerm;
	
	public CraftGuardConfig(CraftGuardPlugin plugin){
		this.plugin = plugin;
		//Remplissage de la collection default
		for(int i = 0 ; i<defautIntValues.length ; i++){
			defaultValues.add(defautIntValues[i]);
		}
		for(int i = 0 ; i<smeltable.length ; i++){
			smeltReference.put(smeltable[i], smelted[i]);
		}
	}
	
	/**
	 * Methode d'initialisation externe
	 */
	public void initConf(){
		loadConf();
	}
	
	/**
	 * Methode de reinitialisation externe
	 */
	public void reloadConf(){
		plugin.reloadConfig();
		listeGroupes = new ArrayList<ArrayList<Integer>>();
		nomGroupes = new ArrayList<String>();
		permissions = new ArrayList<String>();
		checkList = new ArrayList<Integer>();
		damage = new HashMap<String, String>();
		loadConf();
	}
	
	/**
	 * Methode de chargement interne
	 */
	private void loadConf(){
		//Set pour recuperer la section de config
		AbstractSet<String> set;
		//System.out.println(plugin.getDataFolder().toString());
		File f = new File(plugin.getDataFolder().toString() + File.separator + "config.yml");
		
		//Ecriture de la config par defaut si aucun groupe defini
		if(!f.exists()){
			plugin.sendConsoleWarning("CraftGuard : Unable to find configuration file, writing defaults...");
			plugin.getConfig().set("craftguard.default.granted", defaultValues);
			plugin.getConfig().set("craftguard.default.permission", "default");
		}
		//On charge la config
		set = (AbstractSet<String>) plugin.getConfig().getConfigurationSection("craftguard").getKeys(false);
		
		//Tableau qui contient UNE liste d'Ids (en string) a la fois
		ArrayList<String> prov = new ArrayList<String>();
		
		//On recupere les liste de tous les groupes une par une
		for(int i=0 ; i<set.size() ; i++){
			ArrayList<Integer> groupeEnCours = new ArrayList<Integer>();
			if(plugin.getConfig().contains("craftguard." + set.toArray()[i] + ".permission")){
				nomGroupes.add((String) set.toArray()[i]);
				permissions.add(plugin.getConfig().getString("craftguard." + set.toArray()[i] + ".permission")); //On recupere la permission
				listeGroupes.add(groupeEnCours);	//On ajoute la liste aux liste des groupes
				
				prov = (ArrayList<String>)plugin.getConfig().getStringList("craftguard." + set.toArray()[i] + ".granted");
				
				Iterator<String> it = prov.iterator();
				
				//Parcours tous les ids pour les convertir et le cas echeant recuperer les damage values
				while(it.hasNext()){
					addId(nomGroupes.get(i), it.next(), false);
				}
				
				
				//On enumere les blocs a checker
				for(int j = 0 ; j<prov.size() ; j++){
					if(!checkList.contains(prov.get(j)))checkList.add(Integer.valueOf(prov.get(j).split(":")[0]));				
				}
				
				if(plugin.getConfig().contains("craftguard." + set.toArray()[i] + ".inheritance")){
					String inheritance = plugin.getConfig().getString("craftguard." + set.toArray()[i] + ".inheritance");
					
					int index = nomGroupes.indexOf(inheritance);
					ArrayList<Integer> liste = listeGroupes.get(index);
					Iterator<Integer> iter = liste.iterator();
					while(iter.hasNext()){
						int id = iter.next();
						listeGroupes.get(i).add(id);
						if(damage.containsKey(inheritance + ":" + id)){
							damage.put(nomGroupes.get(i) + ":" + id, damage.get(inheritance + ":" + id));
						}
					}
					
				}
			}else plugin.sendConsoleWarning("[CraftGuard] Group " + set.toArray()[i] + "'s permission was not defined. Ignoring the group.");
			
		}
		
		plugin.sendConsoleMessage("[CraftGuard] Succesfully loaded " + set.size() + " groups !");
		
		//On charge la config generale
		//Log
		if (!plugin.getConfig().contains("config.log")){
			log = false;
			plugin.getConfig().set("config.log", false);
			plugin.saveConfig();
		}else log = plugin.getConfig().getBoolean("config.log");
		
		//denyMessage
		if (!plugin.getConfig().contains("config.denymessage")){
			denyMessage = "You don't have permission to craft this !";
			plugin.getConfig().set("config.denymessage", denyMessage);
			plugin.saveConfig();
		}else denyMessage = plugin.getConfig().getString("config.denymessage");
		
		//basePerm
		if (!plugin.getConfig().contains("config.baseperm")){
			basePerm = "craftguard";
			plugin.getConfig().set("config.baseperm", basePerm);
			plugin.saveConfig();
		}else basePerm = plugin.getConfig().getString("config.baseperm");
		
		//furnace
		if (!plugin.getConfig().contains("config.checkfurnaces")){
			furnace = true;
			plugin.getConfig().set("config.checkfurnaces", true);
			plugin.saveConfig();
		}else furnace = plugin.getConfig().getBoolean("config.checkfurnaces");
		
		//preventive
		if (!plugin.getConfig().contains("config.preventiveallow")){
			preventive = true;
			plugin.getConfig().set("config.preventiveallow", true);
			plugin.saveConfig();
		}else preventive = plugin.getConfig().getBoolean("config.preventiveallow");
	}
	
	public boolean isPreventive() {
		return preventive;
	}

	public void addId(String group, String rawId, boolean write){
		ArrayList<Integer> groupeAModifier = listeGroupes.get(nomGroupes.indexOf(group));
		int id, damageid;
		id = Integer.valueOf(rawId.split(":")[0]);
		if(rawId.split(":").length == 2  && plugin.isInteger(rawId.split(":")[1])){
			damageid = Integer.valueOf(rawId.split(":")[1]);
			if(damage.containsKey(group + ":" + id))damage.put(group + ":" + id, damage.get(group + ":" + id) + ":" + damageid);
			else damage.put(group + ":" + id, String.valueOf(damageid));
			rawId = rawId.split(":")[0];
		}
		if(!groupeAModifier.contains(rawId)){
			groupeAModifier.add(id);
			checkList.add(id);
			if(write){
				ArrayList<String> newList = (ArrayList<String>)plugin.getConfig().getStringList("craftguard." + group + ".granted");
				newList.add(rawId);
				plugin.getConfig().set("craftguard." + group + ".granted", newList);
				plugin.saveConfig();
			}
		}
	}
	
	//Getters
	public Map<String, String> getDamage() {
		return damage;
	}

	public ArrayList<ArrayList<Integer>> getListeGroupes() {
		return listeGroupes;
	}

	public ArrayList<String> getPermissions() {
		return permissions;
	}

	public ArrayList<Integer> getCheckList() {
		return checkList;
	}

	public boolean isLog() {
		return log;
	}

	public ArrayList<String> getNomGroupes() {
		return nomGroupes;
	}

	public String getDenyMessage() {
		return denyMessage;
	}

	public String getBasePerm() {
		return basePerm;
	}

	public boolean isFurnace() {
		return furnace;
	}

	public Map<Integer, Integer> getSmeltReference() {
		return smeltReference;
	}
	
}
