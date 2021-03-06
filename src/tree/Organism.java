package tree;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import configuration.Configuration;
import manager.AccessManager;

public class Organism implements Serializable {
	private static final long serialVersionUID = -2867789287775171672L;
	private String kingdom;
	private String group;
	private String subgroup;
	private String name;
	private String bioproject;
	private String taxonomy;
	private String accession;
	private String creation_date;
	private String modification_date;
	private boolean activated;
	private Map<String, String> replicons;
	private ArrayList<String> processed_replicons;
	
	public Organism(String kingdom, String group, String subgroup, String name, String bioproject, String creation_date, String modification_date){
		this.kingdom = kingdom.replace("/", "_").replace(" ", "_").replace(":", "_");
		this.group = group.replace("/", "_").replace(" ", "_").replace(":", "_");
		this.subgroup = subgroup.replace("/", "_").replace(" ", "_").replace(":", "_");
		this.name = name.replace("/", "_").replace(" ", "_").replace(":", "_");
		this.bioproject = bioproject;
		this.creation_date = creation_date;
		this.modification_date = modification_date;
		this.replicons = new HashMap<String, String>();
		this.processed_replicons = new ArrayList<String>();
		this.activated = true;
	}
	
	public Organism(){
		this.replicons = new HashMap<String, String>();
		this.processed_replicons = new ArrayList<String>();
		this.activated = true;
	}
	
	public boolean addReplicon(String name, String id){
		if(this.replicons.containsKey(name)){
			return false;
		} else {
			this.replicons.put(name, id);
			return true;
		}
	}
	
	public void updateTree(Tree mainT){
		Tree kingdomT;
		Tree groupT;
		Tree subgroupT;
		if(mainT.contains(this.kingdom)){
			kingdomT = (Tree)mainT.get(this.kingdom);
		} else {
			kingdomT = new Tree<Tree>();
			mainT.add(this.kingdom, kingdomT);
		}
		
		if(kingdomT.contains(this.group)){
			groupT = (Tree)kingdomT.get(this.group);
		} else {
			groupT = new Tree<Tree>();
			kingdomT.add(this.group, groupT);
		}
		
		if(groupT.contains(this.subgroup)){
			subgroupT = (Tree)groupT.get(this.subgroup);
		} else {
			subgroupT = new Tree<Organism>();
			groupT.add(this.subgroup, subgroupT);
		}
		
		subgroupT.add(this.name, this);
	}

	@Override
	public String toString(){
		
		String str = this.kingdom+"/"+this.group+"/"+this.subgroup+"/"+this.name+"("+this.bioproject+")";
		str += "\nReplicons :";
		for(String name : this.replicons.keySet()){
			str += "\n - "+name+" - "+this.replicons.get(name);
		}
		return str;
	}
	
	public void readObject(ObjectInputStream inputstream) throws IOException, ClassNotFoundException 
	{
		kingdom = (String) inputstream.readObject();
		group = (String) inputstream.readObject();
		subgroup = (String) inputstream.readObject();
		name = (String) inputstream.readObject();
		bioproject = (String) inputstream.readObject();
		accession = (String) inputstream.readObject();
		taxonomy = (String) inputstream.readObject();
		creation_date = (String) inputstream.readObject();
		modification_date = (String) inputstream.readObject();
		replicons = (HashMap<String,String>) inputstream.readObject();
		processed_replicons = (ArrayList<String>) inputstream.readObject();
	}

	public void writeObject(ObjectOutputStream outputstream) throws IOException
	{
		outputstream.writeObject(kingdom);
		outputstream.writeObject(group);
		outputstream.writeObject(subgroup);
		outputstream.writeObject(name);
		outputstream.writeObject(bioproject);
		outputstream.writeObject(accession);
		outputstream.writeObject(taxonomy);
		outputstream.writeObject(creation_date);
		outputstream.writeObject(modification_date);
		outputstream.writeObject(replicons);
		outputstream.writeObject(processed_replicons);
	}

	public String getKingdom() {
		return kingdom;
	}

	public void setKingdom(String kingdom) {
		this.kingdom = kingdom.replace("/", "_").replace(" ", "_").replace(":", "_");
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group.replace("/", "_").replace(" ", "_").replace(":", "_");
	}

	public String getSubgroup() {
		return subgroup;
	}

	public void setSubgroup(String subgroup) {
		this.subgroup = subgroup.replace("/", "_").replace(" ", "_").replace(":", "_");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.replace("/", "_").replace(" ", "_").replace(":", "_");
	}

	public String getBioproject() {
		return bioproject;
	}

	public void setBioproject(String bioproject) {
		this.bioproject = bioproject;
	}

	public String getTaxonomy() {
		return taxonomy;
	}

	public void setTaxonomy(String taxonomy) {
		this.taxonomy = taxonomy;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public String getCreationDate() {
		return creation_date;
	}

	public void setCreationDate(String creation_date) {
		this.creation_date = creation_date;
	}

	public String getModificationDate() {
		return modification_date;
	}

	public void setModificationDate(String modification_date) {
		this.modification_date = modification_date;
	}

	public Map<String, String> getReplicons() {
		return replicons;
	}

	public void setReplicons(Map<String, String> replicons) {
		this.replicons = replicons;
	}
	
	public void setActivated(boolean a){
		this.activated = a;
	}
	
	public boolean getActivated(){
		return this.activated;
	}
	
	public int size(){
		return this.activated ? this.replicons.size() : 0;
	}
	
	public void addProcessedReplicon(String replicon){
		this.processed_replicons.add(replicon);
	}
	
	public ArrayList<String> getProcessedReplicons(){
		return this.processed_replicons;
	}
	
	public void removeReplicons(ArrayList<String> replicons){
		for(String replicon : replicons){
			this.replicons.remove(replicon);
		}
	}
	
	// Creation du dossier sur le système de fichiers local
	public String getPath(){
		// Construction de la chaine de charactere
		String cur = Configuration.BASE_FOLDER;
		cur += Configuration.FOLDER_SEPARATOR+this.getKingdom();
		cur += Configuration.FOLDER_SEPARATOR+this.getGroup();
		cur += Configuration.FOLDER_SEPARATOR+this.getSubgroup();
		
		return cur;
	}
	
	public boolean createPath(){
		String path = this.getPath();
		// Création du dossier
		
		AccessManager.accessFile(path);
		File p = new File(path);
		
		if(p.exists() && p.isDirectory()) {
			// Si le dossier existe déjà
			AccessManager.doneWithFile(path);
			return true;
		}else{
			// Si le fichier n'existe pas
			boolean ok = p.mkdirs();
			AccessManager.doneWithFile(path);
			return ok;
		}
	}
}
