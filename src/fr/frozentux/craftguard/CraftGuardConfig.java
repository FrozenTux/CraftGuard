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
	
	private String[] defautIntValues = {"22","26","44"};
	//private int[] damageableBlocks = {6, 17, 18, 44, 351, 35};
	
	private ArrayList<ArrayList<Integer>> listeGroupes = new ArrayList<ArrayList<Integer>>();
	private ArrayList<String> nomGroupes = new ArrayList<String>();
	private ArrayList<String> permissions = new ArrayList<String>();
	private ArrayList<Integer> checkList = new ArrayList<Integer>();
	
	private boolean log;
	
	private String denyMessage,
		basePerm;
	
	public CraftGuardConfig(CraftGuardPlugin plugin){
		this.plugin = plugin;
		//Remplissage de la collection default
		for(int i = 0 ; i<defautIntValues.length ; i++){
			defaultValues.add(defautIntValues[i]);
		}
	}
	
	/**
	 * M�thode d'initialisation externe
	 */
	public void initConf(){
		loadConf();
	}
	
	/**
	 * M�thode de r�initialisation externe
	 */
	public void reloadConf(){
		plugin.reloadConfig();
		listeGroupes = new ArrayList<ArrayList<Integer>>();
		nomGroupes = new ArrayList<String>();
		permissions = new ArrayList<String>();
		checkList = new ArrayList<Integer>();
		loadConf();
	}
	
	/**
	 * M�thode de chargement interne
	 */
	private void loadConf(){
		//Set pour r�cup�rer la section de config
		AbstractSet<String> set;
		//System.out.println(plugin.getDataFolder().toString());
		File f = new File(plugin.getDataFolder().toString() + File.separator + "config.yml");
		
		//Ecriture de la config par d�faut si aucun groupe d�fini
		//TODO V�rifier si �a marche
		if(!f.exists()){
			plugin.sendConsoleWarning("CraftGuard : Unable to find configuration file, writing defaults...");
			plugin.getConfig().set("craftguard.default.granted", defaultValues);
			plugin.getConfig().set("craftguard.default.permission", "default");
		}
		//On charge la config
		set = (AbstractSet<String>) plugin.getConfig().getConfigurationSection("craftguard").getKeys(false);
		
		//Tableau qui contient UNE liste d'Ids (en string) a la fois
		ArrayList<String> prov = new ArrayList<String>();
		
		//On r�cup�re les liste de tous les groupes une par une
		for(int i=0 ; i<set.size() ; i++){
			ArrayList<Integer> groupeEnCours = new ArrayList<Integer>();
			if(plugin.getConfig().contains("craftguard." + set.toArray()[i] + ".permission")){
				nomGroupes.add((String) set.toArray()[i]);
				permissions.add(plugin.getConfig().getString("craftguard." + set.toArray()[i] + ".permission")); //On r�cup�re la permission
				listeGroupes.add(groupeEnCours);	//On ajoute la liste aux liste des groupes
				
				prov = (ArrayList<String>)plugin.getConfig().getStringList("craftguard." + set.toArray()[i] + ".granted");
				Iterator<String> it = prov.iterator();
				
				//Parcours tous les ids pour les convertir et le cas �ch�ant r�cup�rer les damage values
				while(it.hasNext()){
					String valeur = it.next();
					//Si une damage value a �t� pr�cis�e
					if(valeur.split(":").length == 2){
						//Si une autre damage value pour cet id dans ce groupe a d�j� �t� pr�cis�e
						if(damage.containsKey(nomGroupes.get(i) + ":" + valeur.split(":")[0]))damage.put(nomGroupes.get(i) + ":" + valeur.split(":")[0], damage.get(nomGroupes.get(i) + ":" + valeur.split(":")[0]) + ":" + valeur.split(":")[1]);
						else damage.put(nomGroupes.get(i) + ":" + valeur.split(":")[0].toString(), valeur.split(":")[1]);
					}
					//Puis on ajoute l'id a la liste
					groupeEnCours.add(Integer.valueOf(valeur.split(":")[0]));
				}
				
				//On �num�re les blocs a checker
				for(int j = 0 ; j<prov.size() ; j++){
					if(!checkList.contains(prov.get(j)))checkList.add(Integer.valueOf(prov.get(j).split(":")[0]));				}
			}else plugin.sendConsoleWarning("[CraftGuard] Group " + set.toArray()[i] + "'s permission was not defined. Ignoring the group.");
			
		}
		
		plugin.sendConsoleMessage("[CraftGuard] Succesfully loaded " + set.size() + " groups !");
		
		//On charge la config g�n�rale
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
	
}