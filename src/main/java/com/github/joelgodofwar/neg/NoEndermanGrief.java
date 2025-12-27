package com.github.joelgodofwar.neg;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.github.joelgodofwar.neg.common.PluginLibrary;
import com.github.joelgodofwar.neg.listeners.CreeperListener;
import com.github.joelgodofwar.neg.listeners.EndermanListener;
import com.github.joelgodofwar.neg.listeners.GhastListener;
import com.github.joelgodofwar.neg.listeners.SpawnListener;
import com.github.joelgodofwar.neg.common.PluginLogger;
import com.github.joelgodofwar.neg.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.neg.common.error.Report;
import com.github.joelgodofwar.neg.i18n.Translator;
import com.github.joelgodofwar.neg.util.PluginUtils;

@SuppressWarnings("unused")
public class NoEndermanGrief extends JavaPlugin implements Listener{
	/** Languages: čeština (cs_CZ), Deutsch (de_DE), English (en_US), Español (es_ES), Español (es_MX), Français (fr_FR), Italiano (it_IT), Magyar (hu_HU), 日本語 (ja_JP), 한국어 (ko_KR), Lolcat (lol_US), Melayu (my_MY), Nederlands (nl_NL), Polski (pl_PL), Português (pt_BR), Русский (ru_RU), Svenska (sv_SV), Türkçe (tr_TR), 中文(简体) (zh_CN), 中文(繁體) (zh_TW) */
	//public final static Logger logger = Logger.getLogger("Minecraft");
	public static String THIS_NAME;
	public static String THIS_VERSION;
	public static String daLang;
	public static boolean cancelbroadcast;
	public static boolean debug;
	File langFile;
	public FileConfiguration lang;
	YamlConfiguration oldconfig = new YamlConfiguration();
	String configVersion = "1.0.6";
	//String langVersion = "1.0.6";
	String pluginName = THIS_NAME;
	Translator lang2;
	public String jarfilename = this.getFile().getAbsoluteFile().toString();
	public static DetailedErrorReporter reporter;
	public static PluginLogger LOGGER;

	@Override // TODO: onEnable
	public void onEnable(){
		long startTime = System.currentTimeMillis();
		LOGGER = new PluginLogger(this);
		reporter = new DetailedErrorReporter(this);
		debug = getConfig().getBoolean("debug", false);
		daLang = getConfig().getString("lang", "en_US");
		lang2 = new Translator(daLang, getDataFolder().toString());
		THIS_NAME = this.getDescription().getName();
		THIS_VERSION = this.getDescription().getVersion();
		if(!getConfig().getBoolean("console.longpluginname", true)) {
			pluginName = "NEG";
		}else {
			pluginName = THIS_NAME;
		}

		LOGGER = new PluginLogger(this);

		LOGGER.log("**************************************");
		LOGGER.log(" v" + THIS_VERSION + " Loading...");
		LOGGER.log("Server Version: " + getServer().getVersion().toString());

		/** DEV check **/
		File jarfile = this.getFile().getAbsoluteFile();
		if(jarfile.toString().contains("-DEV")){
			debug = true;
			LOGGER.log("jarfile contains DEV, debug set to true.");
		}else {
			LOGGER.log("This is not a DEV version.");
		}

		LOGGER.log("Checking lang files...");

		LOGGER.log("Checking config file...");
		/**  Check for config */
		try{
			if(!getDataFolder().exists()){
				LOGGER.log("Data Folder doesn't exist");
				LOGGER.log("Creating Data Folder");
				getDataFolder().mkdirs();
				LOGGER.log("Data Folder Created at " + getDataFolder());
			}
			File  file = new File(getDataFolder(), "getConfig().yml");
			LOGGER.log("" + file);
			if(!file.exists()){
				LOGGER.log("getConfig().yml not found, creating!");
				saveResource("getConfig().yml", true);
			}
		}catch(Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
		}
		LOGGER.log("Checking config file version...");
		String checkconfigversion = getConfig().getString("version", "1.0.0");
		LOGGER.log("Config file version=" + checkconfigversion + " expected=" + configVersion);
		if(checkconfigversion != null){
			if(!checkconfigversion.equalsIgnoreCase(configVersion)){
				try {
					copyFile_Java7(getDataFolder() + "" + File.separatorChar + "getConfig().yml",getDataFolder() + "" + File.separatorChar + "old_getConfig().yml");
				} catch (Exception exception) {
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_COPY_FILE).error(exception));
				}
				try {
					oldconfig.load(new File(getDataFolder(), "getConfig().yml"));
				} catch (Exception exception) {
					LOGGER.warn("Could not load getConfig().yml");
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
				}
				saveResource("getConfig().yml", true);
				try {
					getConfig().load(new File(getDataFolder(), "getConfig().yml"));
				} catch (Exception exception) {
					LOGGER.warn("Could not load getConfig().yml");
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
				}
				try {
					oldconfig.load(new File(getDataFolder(), "old_getConfig().yml"));
				} catch (Exception exception) {
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
				}
				getConfig().set("debug", oldconfig.get("debug", false));
				getConfig().set("lang", oldconfig.get("lang", "en_US"));
				getConfig().set("console.longpluginname", oldconfig.get("console.longpluginname", true));
				getConfig().set("enderman_grief", oldconfig.get("enderman_grief", false));
				getConfig().set("skeleton_horse_spawn", oldconfig.get("skeleton_horse_spawn", false));
				getConfig().set("creeper_grief", oldconfig.get("creeper_grief", false));
				getConfig().set("wandering_trader_spawn", oldconfig.get("wandering_trader", false));
				getConfig().set("ghast_grief", oldconfig.get("ghast_grief", false));
				getConfig().set("phantom_spawn", oldconfig.get("phantom_spawn", false));
				getConfig().set("pillager_patrol_spawn", oldconfig.get("pillager_patrol_spawn", false));
				try {
					getConfig().save(new File(getDataFolder(), "getConfig().yml"));
				} catch (Exception exception) {
					LOGGER.warn("Could not save old settings to getConfig().yml");
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_SAVE_CONFIG).error(exception));
				}
				LOGGER.log("getConfig().yml has been updated");
			}
		}
		/** end config check */
		try {
			getConfig().load(new File(getDataFolder(), "getConfig().yml"));
		} catch (Exception exception) {
			LOGGER.warn("Could not load getConfig().yml");
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
		}

		if(debug){
			LOGGER.debug("Config.yml dump");
			LOGGER.debug("debug=" + getConfig().getBoolean("debug"));
			LOGGER.debug("lang=" + getConfig().getString("lang"));

			LOGGER.debug("console.longpluginname=" + getConfig().getBoolean("console.longpluginname"));
			LOGGER.debug("enderman_grief=" + getConfig().getBoolean("enderman_grief"));
			LOGGER.debug("skeleton_horse_spawn=" + getConfig().getBoolean("skeleton_horse_spawn"));
			LOGGER.debug("creeper_grief=" + getConfig().getBoolean("creeper_grief"));
			LOGGER.debug("wandering_trader_spawn=" + getConfig().getBoolean("wandering_trader_spawn"));
			LOGGER.debug("ghast_grief=" + getConfig().getBoolean("ghast_grief"));
			LOGGER.debug("phantom_spawn=" + getConfig().getBoolean("phantom_spawn"));
			LOGGER.debug("pillager_patrol_spawn=" + getConfig().getBoolean("pillager_patrol_spawn"));
		}

		// Register mob-specific listeners
		getServer().getPluginManager().registerEvents(new EndermanListener(this), this);
		getServer().getPluginManager().registerEvents(new CreeperListener(this), this);
		getServer().getPluginManager().registerEvents(new GhastListener(this), this);
		getServer().getPluginManager().registerEvents(new SpawnListener(this), this);
		consoleInfo("ENABLED - Loading took " + LoadTime(startTime));

	}

	@Override // TODO: onDisable
	public void onDisable(){
		//saveConfig();
		consoleInfo("DISABLED");
	}

	public void consoleInfo(String state) {
		loading(" v" + THIS_VERSION + " is " + state);
	}

	public void loading(String string) {
		LOGGER.log(string);
	}

	// Enderman event handler moved to EndermanListener

	// Creeper and Ghast explosion handlers moved to CreeperListener and GhastListener

	// Player join event handler moved to UpdateListener

	// Creature spawn event handlers moved to SpawnListener

	@SuppressWarnings("static-access")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){ // TODO: Commands
		try {
			//Player p = (Player)sender;
			if (cmd.getName().equalsIgnoreCase("NEG")){
				if (args.length == 0)
				{
					/** Check if sender has permission */
					if ( sender.hasPermission("noendermangrief.op") || sender.hasPermission("noendermangrief.admin") || sender.isOp() ) {
						/** Command code */
						sender.sendMessage("[]===============[" + "NoEndermanGrief" + "]===============[]");
						sender.sendMessage(" ");
						if( sender.isOp()||sender.hasPermission("noendermangrief.op") || sender.hasPermission("noendermangrief.admin") ){
							sender.sendMessage(" -<[" + " OP Commands " + "}>-");
							sender.sendMessage(" /NEG update - " + get("neg.command.update"));//Check for update.");
							sender.sendMessage(" /NEG reload - " + get("neg.command.reload") );//Reload config file.");
							if( sender.isOp() || sender.hasPermission("noendermangrief.toggledebug") ||
									!(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ){
								sender.sendMessage(" /NEG toggledebug - " + get("neg.message.debuguse") );
							}
						}
						if( sender.hasPermission("noendermangrief.admin") || !(sender instanceof Player) ){
							sender.sendMessage(" -<[" + " Admin Commands " + "}>-");
							if( sender instanceof Player ) {
								sender.sendMessage(" /NEG BOOK - " + get("neg.command.book"));
							}
							sender.sendMessage(" /NEG EG true/false - " + get("neg.command.endermen"));
							sender.sendMessage(" /NEG SH true/false - " + get("neg.command.skeleton_horse"));
							sender.sendMessage(" /NEG CG true/false - " + get("neg.command.creeper"));
							sender.sendMessage(" /NEG WT true/false - " + get("neg.command.wandering_trader"));
							sender.sendMessage(" /NEG GG true/false - " + get("neg.command.ghast"));
							sender.sendMessage(" /NEG PG true/false - " + get("neg.command.phantom"));
							sender.sendMessage(" /NEG PP true/false - " + get("neg.command.pillager_patrol"));
						}
						sender.sendMessage("[]===============[" + "NoEndermanGrief" + "]===============[]");
						return true;
					}else {
						sender.sendMessage("" + get("neg.message.no_perm"));
						return false;
					}
				}
				if(args[0].equalsIgnoreCase("dumpinfo") || args[0].equalsIgnoreCase("di")){
					if( sender.isOp() || sender.hasPermission("noendermangrief.op")
							|| sender.hasPermission("noendermangrief.admin") || !(sender instanceof Player)) {
						Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
						//StringBuilder messageBuilder = new StringBuilder();

						LOGGER.log("" + "Please copy from this line until the second line of dashes.");
						LOGGER.log("" + "------------------------------------------------------------");
						LOGGER.log("Config.yml dump");
						LOGGER.log("auto_update_check=" + getConfig().getBoolean("auto_update_check"));
						LOGGER.log("debug=" + getConfig().getBoolean("debug"));
						LOGGER.log("lang=" + getConfig().getString("lang"));

						LOGGER.log("console.longpluginname=" + getConfig().getBoolean("console.longpluginname"));
						LOGGER.log("enderman_grief=" + getConfig().getBoolean("enderman_grief"));
						LOGGER.log("skeleton_horse_spawn=" + getConfig().getBoolean("skeleton_horse_spawn"));
						LOGGER.log("creeper_grief=" + getConfig().getBoolean("creeper_grief"));
						LOGGER.log("wandering_trader_spawn=" + getConfig().getBoolean("wandering_trader_spawn"));
						LOGGER.log("ghast_grief=" + getConfig().getBoolean("ghast_grief"));
						LOGGER.log("phantom_spawn=" + getConfig().getBoolean("phantom_spawn"));
						LOGGER.log("pillager_patrol_spawn=" + getConfig().getBoolean("pillager_patrol_spawn"));
						LOGGER.log("");
						LOGGER.log("This server is running " + Bukkit.getName() + " version " + Bukkit.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ")");
						LOGGER.log("");
						LOGGER.log("Plugins dump");
						PluginUtils.loadPluginJarNames();
						for (Plugin plugin : plugins) {
							Map<String, Object> info = PluginUtils.getInfo(plugin);
							String pluginName = (String) info.get("Name");
							String pluginVersion = (String) info.get("Version");

							LOGGER.log(String.format("[%s] v%s", pluginName, pluginVersion));
							LOGGER.log(String.format("  FileName: %s", info.get("FileName")));
							LOGGER.log(String.format("  Main: %s", info.get("Main")));
							LOGGER.log(String.format("  Enabled: %b, API-Version: %s", info.get("Enabled"), info.get("API-Version")));
							LOGGER.log(String.format("  Description: %s", info.get("Description")));
							LOGGER.log(String.format("  Authors: %s", info.get("Authors")));
							LOGGER.log(String.format("  Website: %s", info.get("Website")));
							LOGGER.log(String.format("  Depends: %s", info.get("Depends")));
							LOGGER.log(String.format("  SoftDepends: %s", info.get("SoftDepends")));
							LOGGER.log(String.format("  Commands: %s", info.get("Commands")));
							LOGGER.log(String.format("  Permissions: %s", info.get("Permissions")));
							LOGGER.log(String.format("  Default Permissions: %s", info.get("Default Permissions")));
							LOGGER.log(String.format("  Load: %s", info.get("Load")));
							LOGGER.log(String.format("  LoadBefore: %s", info.get("LoadBefore")));
							LOGGER.log(String.format("  Provides: %s", info.get("Provides")));
							LOGGER.log("");
						}
						LOGGER.log("" + "------------------------------------------------------------");
						LOGGER.log("" + "This is the end of the debug dump.");
						//sender.sendMessage(messageBuilder.toString());
						return true;
					}
				}
				if(args[0].equalsIgnoreCase("config")){
					if(!(sender instanceof Player)) {
						sender.sendMessage("This command can not be sent by console");
						return false;
					}
					if( (sender instanceof Player) && sender.hasPermission("noendermangrief.admin") ){
						Player player = (Player) sender;
						Inventory gui = Bukkit.createInventory(player, 6*9, "Configurations");

						//Menu Options(Items)
						ItemStack btnSpace = new ItemStack(Material.AIR);
						ItemStack btnUpdate = new ItemStack(Material.WRITABLE_BOOK);
						//ItemStack btnUpdateTrue = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
						ItemStack btnUpdateTrue = new ItemStack(getConfig().getBoolean("auto_update_check") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnUpdateFalse = new ItemStack(getConfig().getBoolean("auto_update_check") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

						ItemStack btnLang = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnLangCZ = new ItemStack(getConfig().getString("lang").equalsIgnoreCase("cs_CZ") ? Material.LIME_WOOL : Material.LIGHT_GRAY_WOOL);
						ItemStack btnLangDE = new ItemStack(getConfig().getString("lang").equalsIgnoreCase("de_DE") ? Material.LIME_WOOL : Material.LIGHT_GRAY_WOOL);
						ItemStack btnLangEN = new ItemStack(getConfig().getString("lang").equalsIgnoreCase("en_US") ? Material.LIME_WOOL : Material.LIGHT_GRAY_WOOL);
						ItemStack btnLangFR = new ItemStack(getConfig().getString("lang").equalsIgnoreCase("fr_FR") ? Material.LIME_WOOL : Material.LIGHT_GRAY_WOOL);
						ItemStack btnLangLOL = new ItemStack(getConfig().getString("lang").equalsIgnoreCase("lol_US") ? Material.LIME_WOOL : Material.LIGHT_GRAY_WOOL);
						ItemStack btnLangNL = new ItemStack(getConfig().getString("lang").equalsIgnoreCase("nl_NL") ? Material.LIME_WOOL : Material.LIGHT_GRAY_WOOL);
						ItemStack btnLangBR = new ItemStack(getConfig().getString("lang").equalsIgnoreCase("pt_BR") ? Material.LIME_WOOL : Material.LIGHT_GRAY_WOOL);

						ItemStack btnDebug =  new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnDebugTrue =  new ItemStack(getConfig().getBoolean("debug") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnDebugFalse =  new ItemStack(getConfig().getBoolean("debug") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);


						ItemStack btnLongname = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnLongnameTrue =  new ItemStack(getConfig().getBoolean("console.longpluginname") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnLongnameFalse =  new ItemStack(getConfig().getBoolean("console.longpluginname") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);


						ItemStack btnTrader = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnTraderTrue =  new ItemStack(getConfig().getBoolean("wandering_trader_spawn") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnTraderFalse =  new ItemStack(getConfig().getBoolean("wandering_trader_spawn") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

						ItemStack btnPillager = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnPillagerTrue =  new ItemStack(getConfig().getBoolean("pillager_patrol_spawn") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnPillagerFalse =  new ItemStack(getConfig().getBoolean("pillager_patrol_spawn") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

						ItemStack btnEnder = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnEnderTrue =  new ItemStack(getConfig().getBoolean("enderman_grief") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnEnderFalse =  new ItemStack(getConfig().getBoolean("enderman_grief") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

						ItemStack btnGhast = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnGhastTrue =  new ItemStack(getConfig().getBoolean("ghast_grief") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnGhastFalse =  new ItemStack(getConfig().getBoolean("ghast_grief") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

						ItemStack btnHorse = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnHorseTrue =  new ItemStack(getConfig().getBoolean("skeleton_horse_spawn") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnHorseFalse =  new ItemStack(getConfig().getBoolean("skeleton_horse_spawn") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

						ItemStack btnPhantom = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnPhantomTrue =  new ItemStack(getConfig().getBoolean("phantom_spawn") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnPhantomFalse =  new ItemStack(getConfig().getBoolean("phantom_spawn") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

						ItemStack btnCreeper = new ItemStack(Material.WRITABLE_BOOK);
						ItemStack btnCreeperTrue =  new ItemStack(getConfig().getBoolean("creeper_grief") ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE);
						ItemStack btnCreeperFalse =  new ItemStack(getConfig().getBoolean("creeper_grief") ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

						ItemStack btnSave = new ItemStack(Material.SLIME_BLOCK);
						ItemStack btnCancel =  new ItemStack(Material.BARRIER);


						//Edit the items
						ItemMeta update_meta = btnUpdate.getItemMeta();
						update_meta.setDisplayName("auto_update_check");
						ArrayList<String> update_lore = new ArrayList<>();
						update_lore.add("Should this plugin ");
						update_lore.add("automatically" + " check");
						update_lore.add(" for updates?");
						update_meta.setLore(update_lore);
						btnUpdate.setItemMeta(update_meta);

						ItemMeta updatetrue_meta = btnUpdateTrue.getItemMeta();
						updatetrue_meta.setDisplayName("Set auto_update_check to True");
						ArrayList<String> updatetrue_lore = new ArrayList<>();
						updatetrue_lore.add("Will check for updates.");
						updatetrue_meta.setLore(updatetrue_lore);
						btnUpdateTrue.setItemMeta(updatetrue_meta);

						ItemMeta updatefalse_meta = btnUpdateTrue.getItemMeta();
						updatefalse_meta.setDisplayName("Set auto_update_check to False");
						ArrayList<String> updatefalse_lore = new ArrayList<>();
						updatefalse_lore.add("Will not check for updates.");
						updatefalse_meta.setLore(updatefalse_lore);
						btnUpdateFalse.setItemMeta(updatefalse_meta);



						ItemMeta lang_meta = btnLang.getItemMeta();
						lang_meta.setDisplayName("language");
						ArrayList<String> lang_lore = new ArrayList<>();
						lang_lore.add("Select your preferred language");
						lang_meta.setLore(lang_lore);
						btnLang.setItemMeta(lang_meta);

						ItemMeta langCZ_meta = btnLangCZ.getItemMeta();
						langCZ_meta.setDisplayName("čeština (cs-CZ)");
						ArrayList<String> langCZ_lore = new ArrayList<>();
						langCZ_lore.add("Jako jazyk vyberte češtinu.");
						langCZ_meta.setLore(langCZ_lore);
						btnLangCZ.setItemMeta(langCZ_meta);

						ItemMeta langDE_meta = btnLangDE.getItemMeta();
						langDE_meta.setDisplayName("Deutsche (de_DE)");
						ArrayList<String> langDE_lore = new ArrayList<>();
						langDE_lore.add("Wählen Sie Deutsch als Sprache aus.");
						langDE_meta.setLore(langDE_lore);
						btnLangDE.setItemMeta(langDE_meta);

						ItemMeta langEN_meta = btnLangEN.getItemMeta();
						langEN_meta.setDisplayName("English (en_US)");
						ArrayList<String> langEN_lore = new ArrayList<>();
						langEN_lore.add("Select English as your language.");
						langEN_meta.setLore(langEN_lore);
						btnLangEN.setItemMeta(langEN_meta);

						ItemMeta langFR_meta = btnLangFR.getItemMeta();
						langFR_meta.setDisplayName("Français (fr_FR)");
						ArrayList<String> langFR_lore = new ArrayList<>();
						langFR_lore.add("Sélectionnez Français comme langue.");
						langFR_meta.setLore(langFR_lore);
						btnLangFR.setItemMeta(langFR_meta);

						ItemMeta langLOL_meta = btnLangLOL.getItemMeta();
						langLOL_meta.setDisplayName("LoL Cat (lol_US)");
						ArrayList<String> langLOL_lore = new ArrayList<>();
						langLOL_lore.add("Select lulz kat az ur language.");
						langLOL_meta.setLore(langLOL_lore);
						btnLangLOL.setItemMeta(langLOL_meta);

						ItemMeta langNL_meta = btnLangNL.getItemMeta();
						langNL_meta.setDisplayName("Nederlands (nl_NL)");
						ArrayList<String> langNL_lore = new ArrayList<>();
						langNL_lore.add("Selecteer Nederlands als je taal.");
						langNL_meta.setLore(langNL_lore);
						btnLangNL.setItemMeta(langNL_meta);

						ItemMeta langBR_meta = btnLangBR.getItemMeta();
						langBR_meta.setDisplayName("Português (pt_BR)");
						ArrayList<String> langBR_lore = new ArrayList<>();
						langBR_lore.add("Selecione Português como seu idioma.");
						langBR_meta.setLore(langBR_lore);
						btnLangBR.setItemMeta(langBR_meta);



						ItemMeta debug_meta = btnDebug.getItemMeta();
						debug_meta.setDisplayName("debug");
						ArrayList<String> debug_lore = new ArrayList<>();
						debug_lore.add("Set to true before.");
						debug_lore.add("sending a log about");
						debug_lore.add("an issue.");
						debug_lore.add(" ");
						debug_lore.add(""  + "Logs trace data");
						debug_lore.add(""   + "required to pinpoint");
						debug_lore.add(""  + "where errors are.");
						debug_meta.setLore(debug_lore);
						btnDebug.setItemMeta(debug_meta);

						ItemMeta debugtrue_meta = btnUpdateTrue.getItemMeta();
						debugtrue_meta.setDisplayName("Set debug to True");
						ArrayList<String> debugtrue_lore = new ArrayList<>();
						//debugtrue_lore.add("Set debug to True");
						debugtrue_lore.add("Will log debug information.");
						debugtrue_meta.setLore(debugtrue_lore);
						btnDebugTrue.setItemMeta(debugtrue_meta);

						ItemMeta debugfalse_meta = btnUpdateTrue.getItemMeta();
						debugfalse_meta.setDisplayName("Set debug to False");
						ArrayList<String> debugfalse_lore = new ArrayList<>();
						//debugfalse_lore.add("Set debug to False");
						debugfalse_lore.add("Will not log debug information.");
						debugfalse_meta.setLore(debugfalse_lore);
						btnDebugFalse.setItemMeta(debugfalse_meta);







						ItemMeta longname_meta = btnLongname.getItemMeta();
						longname_meta.setDisplayName("console.longpluginname");
						ArrayList<String> longname_lore = new ArrayList<>();
						longname_lore.add("Logs use NoEndermanGrief");
						longname_lore.add("or NEG.");
						longname_meta.setLore(longname_lore);
						btnLongname.setItemMeta(longname_meta);

						ItemMeta longnametrue_meta = btnLongnameTrue.getItemMeta();
						longnametrue_meta.setDisplayName("Set longname.colorful_longname to True");
						ArrayList<String> longnametrue_lore = new ArrayList<>();
						longnametrue_lore.add("Will have colorful text in longname.");
						longnametrue_meta.setLore(longnametrue_lore);
						btnLongnameTrue.setItemMeta(longnametrue_meta);

						ItemMeta longnamefalse_meta = btnLongnameFalse.getItemMeta();
						longnamefalse_meta.setDisplayName("Set longname.colorful_longname to False");
						ArrayList<String> longnamefalse_lore = new ArrayList<>();
						longnamefalse_lore.add("Will not have colorful text in longname.");
						longnamefalse_meta.setLore(longnamefalse_lore);
						btnLongnameFalse.setItemMeta(longnamefalse_meta);


						ItemMeta trader_meta = btnTrader.getItemMeta();
						trader_meta.setDisplayName("wandering_trader_spawn");
						ArrayList<String> trader_lore = new ArrayList<>();
						trader_lore.add("Set if Wandering");
						trader_lore.add("Traders should spawn.");
						trader_lore.add(" ");
						trader_lore.add(""  + "false = no spawn");
						trader_meta.setLore(trader_lore);
						btnTrader.setItemMeta(trader_meta);

						ItemMeta tradertrue_meta = btnTraderTrue.getItemMeta();
						tradertrue_meta.setDisplayName("Set wandering_trader_spawn to True");
						ArrayList<String> tradertrue_lore = new ArrayList<>();
						//tradertrue_lore.add("Set wandering_trader_spawn to True");
						tradertrue_lore.add("Wandering Traders will spawn.");
						tradertrue_meta.setLore(tradertrue_lore);
						btnTraderTrue.setItemMeta(tradertrue_meta);

						ItemMeta traderfalse_meta = btnTraderFalse.getItemMeta();
						traderfalse_meta.setDisplayName("Set wandering_trader_spawn to False");
						ArrayList<String> traderfalse_lore = new ArrayList<>();
						//traderfalse_lore.add("Set wandering_trader_spawn to False");
						traderfalse_lore.add("Wandering Traders will NOT spawn.");
						traderfalse_meta.setLore(traderfalse_lore);
						btnTraderFalse.setItemMeta(traderfalse_meta);



						ItemMeta pillager_meta = btnPillager.getItemMeta();
						pillager_meta.setDisplayName("pillager_patrol_spawn");
						ArrayList<String> pillager_lore = new ArrayList<>();
						pillager_lore.add("Set if Pillager");
						pillager_lore.add("Patrols should spawn.");
						pillager_lore.add(" ");
						pillager_lore.add(""  + "false = no spawn");
						pillager_meta.setLore(pillager_lore);
						btnPillager.setItemMeta(pillager_meta);

						ItemMeta pillagertrue_meta = btnPillagerTrue.getItemMeta();
						pillagertrue_meta.setDisplayName("Set pillager_patrol_spawn to True");
						ArrayList<String> pillagertrue_lore = new ArrayList<>();
						//pillagertrue_lore.add("Set pillager_patrol_spawn to True");
						pillagertrue_lore.add("Pillagers will spawn.");
						pillagertrue_meta.setLore(pillagertrue_lore);
						btnPillagerTrue.setItemMeta(pillagertrue_meta);

						ItemMeta pillagerfalse_meta = btnPillagerFalse.getItemMeta();
						pillagerfalse_meta.setDisplayName("Set pillager_patrol_spawn to False");
						ArrayList<String> pillagerfalse_lore = new ArrayList<>();
						//pillagerfalse_lore.add("Set pillager_patrol_spawn to False");
						pillagerfalse_lore.add("Pillagers will NOT spawn.");
						pillagerfalse_meta.setLore(pillagerfalse_lore);
						btnPillagerFalse.setItemMeta(pillagerfalse_meta);



						ItemMeta ender_meta = btnEnder.getItemMeta();
						ender_meta.setDisplayName("enderman_grief");
						ArrayList<String> ender_lore = new ArrayList<>();
						ender_lore.add("Set if Endermen can");
						ender_lore.add("pick up blocks.");
						ender_lore.add(" ");
						ender_lore.add(""  + "false = no pickup");
						ender_meta.setLore(ender_lore);
						btnEnder.setItemMeta(ender_meta);

						ItemMeta endertrue_meta = btnEnderTrue.getItemMeta();
						endertrue_meta.setDisplayName("Set enderman_grief to True");
						ArrayList<String> endertrue_lore = new ArrayList<>();
						//endertrue_lore.add("Set enderman_grief to True");
						endertrue_lore.add("Endermen will pickup blocks.");
						endertrue_meta.setLore(endertrue_lore);
						btnEnderTrue.setItemMeta(endertrue_meta);

						ItemMeta enderfalse_meta = btnEnderFalse.getItemMeta();
						enderfalse_meta.setDisplayName("Set enderman_grief to False");
						ArrayList<String> enderfalse_lore = new ArrayList<>();
						//enderfalse_lore.add("Set enderman_grief to False");
						enderfalse_lore.add("Endermen will NOT pickup blocks.");
						enderfalse_meta.setLore(enderfalse_lore);
						btnEnderFalse.setItemMeta(enderfalse_meta);



						ItemMeta ghast_meta = btnGhast.getItemMeta();
						ghast_meta.setDisplayName("ghast_grief");
						ArrayList<String> ghast_lore = new ArrayList<>();
						ghast_lore.add("Set whether Ghast");
						ghast_lore.add("fireball explosions");
						ghast_lore.add("can destroy blocks.");
						ghast_lore.add(" ");
						ghast_lore.add(""  + "false = no grief");
						ghast_meta.setLore(ghast_lore);
						btnGhast.setItemMeta(ghast_meta);

						ItemMeta ghasttrue_meta = btnGhastTrue.getItemMeta();
						ghasttrue_meta.setDisplayName("Set ghast_grief to True");
						ArrayList<String> ghasttrue_lore = new ArrayList<>();
						//ghasttrue_lore.add("Set ghast_grief to True");
						ghasttrue_lore.add("Ghast fireballs will destroy blocks.");
						ghasttrue_meta.setLore(ghasttrue_lore);
						btnGhastTrue.setItemMeta(ghasttrue_meta);

						ItemMeta ghastfalse_meta = btnGhastFalse.getItemMeta();
						ghastfalse_meta.setDisplayName("Set ghast_grief to False");
						ArrayList<String> ghastfalse_lore = new ArrayList<>();
						//ghastfalse_lore.add("Set ghast_grief to False");
						ghastfalse_lore.add("Ghast fireballs will NOT");
						ghastfalse_lore.add("destroy blocks.");
						ghastfalse_meta.setLore(ghastfalse_lore);
						btnGhastFalse.setItemMeta(ghastfalse_meta);



						ItemMeta horse_meta = btnHorse.getItemMeta();
						horse_meta.setDisplayName("skeleton_horse_spawn");
						ArrayList<String> horse_lore = new ArrayList<>();
						horse_lore.add("Set whether Ghast");
						horse_lore.add("fireball explosions");
						horse_lore.add("can destroy blocks.");
						horse_lore.add(" ");
						horse_lore.add(""  + "false = no grief");
						horse_meta.setLore(horse_lore);
						btnHorse.setItemMeta(horse_meta);

						ItemMeta horsetrue_meta = btnHorseTrue.getItemMeta();
						horsetrue_meta.setDisplayName("Set skeleton_horse_spawn to True");
						ArrayList<String> horsetrue_lore = new ArrayList<>();
						//horsetrue_lore.add("Set skeleton_horse_spawn to True");
						horsetrue_lore.add("Skeleton Horses will spawn.");
						horsetrue_meta.setLore(horsetrue_lore);
						btnHorseTrue.setItemMeta(horsetrue_meta);

						ItemMeta horsefalse_meta = btnHorseFalse.getItemMeta();
						horsefalse_meta.setDisplayName("Set skeleton_horse_spawn to False");
						ArrayList<String> horsefalse_lore = new ArrayList<>();
						//horsefalse_lore.add("Set skeleton_horse_spawn to False");
						horsefalse_lore.add("Skeleton Horses will NOT spawn.");
						horsefalse_meta.setLore(horsefalse_lore);
						btnHorseFalse.setItemMeta(horsefalse_meta);



						ItemMeta phantom_meta = btnPhantom.getItemMeta();
						phantom_meta.setDisplayName("phantom_spawn");
						ArrayList<String> phantom_lore = new ArrayList<>();
						phantom_lore.add("Set whether Ghast");
						phantom_lore.add("fireball explosions");
						phantom_lore.add("can destroy blocks.");
						phantom_lore.add(" ");
						phantom_lore.add(""  + "false = no grief");
						phantom_meta.setLore(phantom_lore);
						btnPhantom.setItemMeta(phantom_meta);

						ItemMeta phantomtrue_meta = btnPhantomTrue.getItemMeta();
						phantomtrue_meta.setDisplayName("Set phantom_spawn to True");
						ArrayList<String> phantomtrue_lore = new ArrayList<>();
						//phantomtrue_lore.add("Set phantom_spawn to True");
						phantomtrue_lore.add("Phantoms will spawn.");
						phantomtrue_meta.setLore(phantomtrue_lore);
						btnPhantomTrue.setItemMeta(phantomtrue_meta);

						ItemMeta phantomfalse_meta = btnPhantomFalse.getItemMeta();
						phantomfalse_meta.setDisplayName("Set phantom_spawn to False");
						ArrayList<String> phantomfalse_lore = new ArrayList<>();
						//phantomfalse_lore.add("Set phantom_spawn to False");
						phantomfalse_lore.add("Phantoms will NOT spawn.");
						phantomfalse_meta.setLore(phantomfalse_lore);
						btnPhantomFalse.setItemMeta(phantomfalse_meta);



						ItemMeta creeper_meta = btnCreeper.getItemMeta();
						creeper_meta.setDisplayName("creeper_grief");
						ArrayList<String> creeper_lore = new ArrayList<>();
						creeper_lore.add("Set if Creeper");
						creeper_lore.add("explosions can");
						creeper_lore.add("destroy blocks.");
						creeper_lore.add(" ");
						creeper_lore.add(""  + "false = no grief");
						creeper_meta.setLore(creeper_lore);
						btnCreeper.setItemMeta(creeper_meta);

						ItemMeta creepertrue_meta = btnCreeperTrue.getItemMeta();
						creepertrue_meta.setDisplayName("Set creeper_grief to True");
						ArrayList<String> creepertrue_lore = new ArrayList<>();
						//creepertrue_lore.add("Set creeper_grief to True");
						creepertrue_lore.add("Creeper Explosions will destroy blocks.");
						creepertrue_meta.setLore(creepertrue_lore);
						btnCreeperTrue.setItemMeta(creepertrue_meta);

						ItemMeta creeperfalse_meta = btnCreeperFalse.getItemMeta();
						creeperfalse_meta.setDisplayName("Set creeper_grief to False");
						ArrayList<String> creeperfalse_lore = new ArrayList<>();
						//creeperfalse_lore.add("Set creeper_grief to False");
						creeperfalse_lore.add("Creeper Explosions will NOT destroy blocks.");
						creeperfalse_meta.setLore(creeperfalse_lore);
						btnCreeperFalse.setItemMeta(creeperfalse_meta);



						ItemMeta save_meta = btnSave.getItemMeta();
						save_meta.setDisplayName("Set configs and save.");
						ArrayList<String> save_lore = new ArrayList<>();
						//save_lore.add("Set configs and save.");
						save_lore.add("Your current changes");
						save_lore.add("will be set and saved");
						save_lore.add("to getConfig().yml");
						save_meta.setLore(save_lore);
						btnSave.setItemMeta(save_meta);

						ItemMeta cancel_meta = btnCancel.getItemMeta();
						cancel_meta.setDisplayName("Cancel");
						ArrayList<String> cancel_lore = new ArrayList<>();
						cancel_lore.add("Cancel without setting");
						cancel_lore.add("or saving.");
						save_lore.add("Your current changes");
						save_lore.add("will be lost.");
						cancel_meta.setLore(cancel_lore);
						btnCancel.setItemMeta(cancel_meta);



						//

						//Put the items in the inventory
						ItemStack[] menu_items = {
								btnUpdate, btnUpdateTrue, btnUpdateFalse, btnLang, btnLangCZ, btnLangDE, btnLangEN, btnLangFR, btnSpace,
								btnDebug, btnDebugTrue, btnDebugFalse, btnSpace, btnLangLOL, btnLangNL, btnLangBR, btnSpace, btnSpace,
								btnEnder, btnEnderTrue, btnEnderFalse, btnGhast, btnGhastTrue, btnGhastFalse, btnSpace, btnSpace, btnSpace,
								btnHorse, btnHorseTrue, btnHorseFalse, btnPhantom, btnPhantomTrue, btnPhantomFalse, btnSpace, btnSpace, btnSpace,
								btnCreeper, btnCreeperTrue, btnCreeperFalse, btnLongname, btnLongnameTrue, btnLongnameFalse, btnSpace, btnSave, btnCancel};
						gui.setContents(menu_items);
						player.openInventory(gui);
						return true;
					}else if(!sender.hasPermission("noendermangrief.admin")){
						sender.sendMessage(THIS_NAME + " " + get("neg.message.noperm"));
						return false;
					}
				}

				// BOOK command removed - GUI features no longer supported
				if(args[0].equalsIgnoreCase("toggledebug")||args[0].equalsIgnoreCase("td")){
					if( sender.isOp() || sender.hasPermission("noendermangrief.toggledebug") || !(sender instanceof Player) ||
							sender.hasPermission("noendermangrief.op") || sender.hasPermission("noendermangrief.admin") ){
						debug = !debug;
						sender.sendMessage(THIS_NAME + " " +
								get("neg.message.debugtrue").toString().replace("<boolean>", get("neg.message.boolean." + String.valueOf(debug).toLowerCase()) ));
						return true;
					}else{
						sender.sendMessage(THIS_NAME + " " + get("neg.message.noperm"));
						return false;
					}
				}

				if(args[0].equalsIgnoreCase("reload")){
					/** Check if player has permission */
					if ( sender.hasPermission("noendermangrief.op") || sender.isOp() || sender.hasPermission("noendermangrief.admin") || !(sender instanceof Player) ) {
						/** Command code */
						oldconfig = new YamlConfiguration();
						LOGGER.log("Checking config file version...");
						try {
							oldconfig.load(new File(getDataFolder() + "" + File.separatorChar + "getConfig().yml"));
						} catch (Exception exception) {
							LOGGER.warn("Could not load getConfig().yml");
							reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
						}
						String checkconfigversion = oldconfig.getString("version", "1.0.0");
						if(checkconfigversion != null){
							if(!checkconfigversion.equalsIgnoreCase(configVersion)){
								try {
									copyFile_Java7(getDataFolder() + "" + File.separatorChar + "getConfig().yml",getDataFolder() + "" + File.separatorChar + "old_getConfig().yml");
								} catch (Exception exception) {
									reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
								}
								saveResource("getConfig().yml", true);

								try {
									getConfig().load(new File(getDataFolder(), "getConfig().yml"));
								} catch (Exception exception) {
									LOGGER.warn("Could not load getConfig().yml");
									reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
								}
								try {
									oldconfig.load(new File(getDataFolder(), "old_getConfig().yml"));
								} catch (Exception exception) {
									LOGGER.warn("Could not load old_getConfig().yml");
									reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
								}
								getConfig().set("auto_update_check", oldconfig.get("auto_update_check", true));
								getConfig().set("debug", oldconfig.get("debug", false));
								getConfig().set("lang", oldconfig.get("lang", "en_US"));
								getConfig().set("console.longpluginname", oldconfig.get("console.longpluginname", true));
								getConfig().set("enderman_grief", oldconfig.get("enderman_grief", false));
								getConfig().set("skeleton_horse_spawn", oldconfig.get("skeleton_horse_spawn", false));
								getConfig().set("creeper_grief", oldconfig.get("creeper_grief", false));
								getConfig().set("wandering_trader_spawn", oldconfig.get("wandering_trader", false));
								getConfig().set("ghast_grief", oldconfig.get("ghast_grief", false));
								getConfig().set("phantom_spawn", oldconfig.get("phantom_spawn", false));
								getConfig().set("pillager_patrol_spawn", oldconfig.get("pillager_patrol_spawn", false));

								try {
									getConfig().save(new File(getDataFolder(), "getConfig().yml"));
								} catch (Exception exception) {
									LOGGER.warn("Could not save old settings to getConfig().yml");
									reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
								}

								LOGGER.log("getConfig().yml Updated! old config saved as old_getConfig().yml");
								LOGGER.log("chance_getConfig().yml saved.");
							}else{
								try {
									getConfig().load(new File(getDataFolder(), "getConfig().yml"));
								} catch (Exception exception) {
									LOGGER.warn("Could not load getConfig().yml");
									reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
								}
							}
							oldconfig = null;
						}
						LOGGER.log("Loading config file...");
						try {
							getConfig().load(new File(getDataFolder(), "getConfig().yml"));
						} catch (Exception exception) {
							reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
						}
						try {
							getConfig().load(new File(getDataFolder(), "getConfig().yml"));
						} catch (Exception exception) {
							LOGGER.warn("Could not load getConfig().yml");
							reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
						}

						debug = getConfig().getBoolean("debug", false);
						daLang = getConfig().getString("lang", "en_US");
						reloadConfig();

						sender.sendMessage(THIS_NAME + " has been " + "reloaded");
						return true;
					}else{
						sender.sendMessage("" + get("neg.message.no_perm"));
						return false;
					}
				}
				if( args[0].equalsIgnoreCase("eg") || args[0].equalsIgnoreCase("endermangrief") ){
					if(args.length <= 1){
						sender.sendMessage(THIS_NAME + " " + get("neg.entity.enderman.current" +
								"").toString().replace("[setting]", "" + getConfig().getBoolean("enderman_grief", false)));
						return true;
					}
					/** Check if player has permission */
					if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/** Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(THIS_NAME + " " + get("neg.var.boolean") + ": /neg eg True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							getConfig().set("enderman_grief", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(THIS_NAME + " " + get("neg.entity.enderman.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(THIS_NAME + " " + get("neg.entity.enderman.wont") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(THIS_NAME + " " + get("neg.entity.enderman.will") );
							}
							try {
								getConfig().load(new File(getDataFolder(), "getConfig().yml"));
							} catch (Exception exception) {
								LOGGER.warn("Could not load getConfig().yml");
								reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
							}
							return true;
						}
					}else {
						sender.sendMessage("" + get("neg.message.noperm"));
						return false;
					}

				}
				if( args[0].equalsIgnoreCase("sh") || args[0].equalsIgnoreCase("skeletonhorse") ){
					if(args.length <= 1){
						sender.sendMessage(THIS_NAME + " " + get("neg.entity.skeleton_horse.current" +
								"").toString().replace("[setting]", "" + getConfig().getBoolean("skeleton_horse_spawn", false)));
						return true;
					}
					/** Check if player has permission */
					if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/** Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(THIS_NAME + " " + get("neg.var.boolean") + ": /neg sh True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							getConfig().set("skeleton_horse_spawn", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(THIS_NAME + " " + get("neg.entity.skeleton_horse.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(THIS_NAME + " " + get("neg.entity.skeleton_horse.wont") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(THIS_NAME + " " + get("neg.entity.skeleton_horse.will") );
							}
							try {
								getConfig().load(new File(getDataFolder(), "getConfig().yml"));
							} catch (Exception exception) {
								LOGGER.warn("Could not load getConfig().yml");
								reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
							}
							return true;
						}
					}else {
						sender.sendMessage("" + get("neg.message.noperm"));
						return false;
					}
				}
				if( args[0].equalsIgnoreCase("wt") || args[0].equalsIgnoreCase("wanderingtrader") ){
					if(args.length <= 1){
						sender.sendMessage(THIS_NAME + " " + get("neg.entity.wandering_trader.current" +
								"").toString().replace("[setting]", "" + getConfig().getBoolean("wandering_trader_spawn", false)));
						return true;
					}
					LOGGER.log("args.length=" + args.length);
					/** Check if player has permission */
					if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/** Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(THIS_NAME + " " + get("neg.var.boolean") + ": /neg sh True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							getConfig().set("wandering_trader_spawn", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(THIS_NAME + " " + get("neg.entity.wandering_trader.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(THIS_NAME + " " + get("neg.entity.wandering_trader.wont") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(THIS_NAME + " " + get("neg.entity.wandering_trader.will") );
							}
							try {
								getConfig().load(new File(getDataFolder(), "getConfig().yml"));
							} catch (Exception exception) {
								LOGGER.warn("Could not load getConfig().yml");
								reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
							}
							return true;
						}
					}else {
						sender.sendMessage("" + get("neg.message.noperm"));
						return false;
					}
				}
				if( args[0].equalsIgnoreCase("cg") || args[0].equalsIgnoreCase("creepergrief") ){
					if(args.length <= 1){
						sender.sendMessage(THIS_NAME + " " + get("neg.entity.creeper.current" +
								"").toString().replace("[setting]", "" + getConfig().getBoolean("creeper_grief", false)));
						return true;
					}
					/** Check if player has permission */
					if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/** Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(THIS_NAME + " " + get("neg.var.boolean") + ": /neg eg True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							getConfig().set("creeper_grief", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(THIS_NAME + " " + get("neg.entity.creeper.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(THIS_NAME + " " + get("neg.entity.creeper.wont") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(THIS_NAME + " " + get("neg.entity.creeper.will") );
							}
							try {
								getConfig().load(new File(getDataFolder(), "getConfig().yml"));
							} catch (Exception exception) {
								LOGGER.warn("Could not load getConfig().yml");
								reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
							}
							return true;
						}
					}else {
						sender.sendMessage("" + get("neg.message.noperm"));
						return false;
					}
				}
				// Ghast Grief
				if( args[0].equalsIgnoreCase("gg") || args[0].equalsIgnoreCase("ghastgrief") ){
					if(args.length <= 1){
						sender.sendMessage(THIS_NAME + " " + get("neg.entity.ghast.current")
						.toString().replace("[setting]", "" + getConfig().getBoolean("ghast_grief", false)));
						return true;
					}
					/** Check if player has permission */
					if ( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/** Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(THIS_NAME + " " + get("neg.var.boolean") + ": /neg eg True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							getConfig().set("ghast_grief", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(THIS_NAME + " " + get("neg.entity.ghast.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(THIS_NAME + " " + get("neg.entity.ghast.wont") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(THIS_NAME + " " + get("neg.entity.ghast.will") );
							}
							try {
								getConfig().load(new File(getDataFolder(), "getConfig().yml"));
							} catch (Exception exception) {
								LOGGER.warn("Could not load getConfig().yml");
								reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
							}
							return true;
						}
					}else {
						sender.sendMessage("" + get("neg.message.noperm"));
						return false;
					}
				}
				if( args[0].equalsIgnoreCase("pg") || args[0].equalsIgnoreCase("phantomgrief") ){ // Phantom Grief
					if(args.length <= 1){
						sender.sendMessage(THIS_NAME + " " + get("neg.entity.phantom.current")
						.toString().replace("[setting]", "" + getConfig().getBoolean("phantom_spawn", false)));
						return true;
					}
					/** Check if player has permission */
					if( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/** Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(THIS_NAME + " " + get("neg.var.boolean") + ": /neg eg True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							getConfig().set("phantom_spawn", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(THIS_NAME + " " + get("neg.entity.phantom.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(THIS_NAME + " " + get("neg.entity.phantom.wont") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(THIS_NAME + " " + get("neg.entity.phantom.will") );
							}
							try {
								getConfig().load(new File(getDataFolder(), "getConfig().yml"));
							} catch (Exception exception) {
								LOGGER.warn("Could not load getConfig().yml");
								reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
							}
							return true;
						}
					}else {
						sender.sendMessage("" + get("neg.message.noperm"));
						return false;
					}
				}
				if( args[0].equalsIgnoreCase("pp") || args[0].equalsIgnoreCase("pillagerpatrol") ){ // Pillager Patrol Grief
					if(args.length <= 1){
						sender.sendMessage(THIS_NAME + " " + get("neg.entity.pillager_patrol.current")
						.toString().replace("[setting]", "" + getConfig().getBoolean("pillager_patrol_spawn", false)));
						return true;
					}
					/** Check if player has permission */
					if( !(sender instanceof Player) || sender.hasPermission("noendermangrief.admin") ) {
						/** Command code */
						if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
							sender.sendMessage(THIS_NAME + " " + get("neg.var.boolean") + ": /neg eg True/False");
							return false;
						}else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							getConfig().set("pillager_patrol_spawn", Boolean.parseBoolean(args[1]));
							saveConfig();

							sender.sendMessage(THIS_NAME + " " + get("neg.entity.pillager_patrol.set") + " " + args[1]);
							if(args[1].equalsIgnoreCase("false")){
								sender.sendMessage(THIS_NAME + " " + get("neg.entity.pillager_patrol.wont") );
							}else if(args[1].equalsIgnoreCase("true")){
								sender.sendMessage(THIS_NAME + " " + get("neg.entity.pillager_patrol.will") );
							}
							try {
								getConfig().load(new File(getDataFolder(), "getConfig().yml"));
							} catch (Exception exception) {
								LOGGER.warn("Could not load getConfig().yml");
								reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
							}
							return true;
						}
					}else {
						sender.sendMessage("" + get("neg.message.noperm"));
						return false;
					}
				}
			}
		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.UNHANDLED_COMMAND_ERROR).error(exception));
			// ERROR_RUNNING_DRAGON_DEATH_COMMAND "Error running command after dragon death."
		}
		return true;
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { // TODO: Tab Complete
		try {
			if (command.getName().equalsIgnoreCase("NEG")) {
				List<String> autoCompletes = new ArrayList<>(); //create a new string list for tab completion
				if (args.length == 1) { // reload, toggledebug, playerheads, customtrader, headfix
					autoCompletes.add("Update");
					autoCompletes.add("Reload");
					autoCompletes.add("ToggleDebug");
					autoCompletes.add("Book");
					autoCompletes.add("EndermanGrief");
					autoCompletes.add("SkeletonHorse");
					autoCompletes.add("CreeperGrief");
					autoCompletes.add("WanderingTrader");
					autoCompletes.add("GhastGrief");
					autoCompletes.add("PhantomGrief");
					autoCompletes.add("PillagerPatrol");
					return autoCompletes; // then return the list
				}
				if(args.length > 1) {
					if( args[0].equalsIgnoreCase("EndermanGrief") || args[0].equalsIgnoreCase("eg") ) {
						autoCompletes.add("true");
						autoCompletes.add("false");
						return autoCompletes; // then return the list
					}
					if( args[0].equalsIgnoreCase("SkeletonHorse") || args[0].equalsIgnoreCase("sh") ) {
						autoCompletes.add("true");
						autoCompletes.add("false");
						return autoCompletes; // then return the list
					}
					if( args[0].equalsIgnoreCase("CreeperGrief") || args[0].equalsIgnoreCase("cg") ) {
						autoCompletes.add("true");
						autoCompletes.add("false");
						return autoCompletes; // then return the list
					}
					if( args[0].equalsIgnoreCase("WanderingTrader") || args[0].equalsIgnoreCase("wt") ) {
						autoCompletes.add("true");
						autoCompletes.add("false");
						return autoCompletes; // then return the list
					}
					if( args[0].equalsIgnoreCase("GhastGrief") || args[0].equalsIgnoreCase("gg") ) {
						autoCompletes.add("true");
						autoCompletes.add("false");
						return autoCompletes; // then return the list
					}
					if( args[0].equalsIgnoreCase("PhantomGrief") || args[0].equalsIgnoreCase("pg") ) {
						autoCompletes.add("true");
						autoCompletes.add("false");
						return autoCompletes; // then return the list
					}
					if( args[0].equalsIgnoreCase("PillagerPatrol") || args[0].equalsIgnoreCase("pp") ) {
						autoCompletes.add("true");
						autoCompletes.add("false");
						return autoCompletes; // then return the list
					}
				}
			}
		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_TAB_COMPLETE_ERROR).error(exception));
			// ERROR_RUNNING_DRAGON_DEATH_COMMAND "Error running command after dragon death."
		}
		return null;
	}
	public boolean makeBoolean(String args){
		if(args.contains("true")){
			return true;
		}
		else if(args.contains("false")){
			return false;
		}
		return false;
	}

	public void copyFile_Java7(String origin, String destination) throws IOException {
		try {
			Path FROM = Paths.get(origin);
			Path TO = Paths.get(destination);
			//overwrite the destination file if it exists, and copy
			// the file attributes, including the rwx permissions
			CopyOption[] options = new CopyOption[]{
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES
			};
			Files.copy(FROM, TO, options);
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_COPY_FILE).error(exception));
		}
	}

	@Override
	public void saveConfig(){
		try {
			getConfig().save(new File(getDataFolder(), "getConfig().yml"));
		} catch (Exception exception) {
			LOGGER.warn("Could not save old settings to getConfig().yml");
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_SAVE_CONFIG).error(exception));
		}
	}

	public boolean isCorrectVersion(){
		// NMS version checking removed - always return true for Paper API compatibility
		return true;
	}

	public boolean saveConfig(boolean update, boolean Debug, boolean Console, boolean Longname, boolean Trader, boolean Pillager, boolean Ender, boolean Ghast, boolean Horse,
			boolean Phantom, boolean Creeper, String Lang) {
		//	debug	daLang	colorful_console
		debug = Debug;
		daLang = Lang;
		if(!Longname) {
			pluginName = "NEG";
		}else {
			pluginName = THIS_NAME;
		}
		getConfig().set("debug", Debug);
		getConfig().set("lang", Lang);
		getConfig().set("console.longpluginname", Longname);
		getConfig().set("enderman_grief", Ender);
		getConfig().set("skeleton_horse_spawn", Horse);
		getConfig().set("creeper_grief", Creeper);
		getConfig().set("wandering_trader_spawn", Trader);
		getConfig().set("ghast_grief", Ghast);
		getConfig().set("phantom_spawn", Phantom);
		getConfig().set("pillager_patrol_spawn", Pillager);
		try {
			getConfig().save(new File(getDataFolder(), "getConfig().yml"));
		} catch (IOException e) {
			LOGGER.warn("Could not save settings to getConfig().yml");
			e.printStackTrace();
			return false;
		}
		LOGGER.log("getConfig().yml has been updated");
		return true;
	}

	public String LoadTime(long startTime) {
		long elapsedTime = System.currentTimeMillis() - startTime;
		long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60;
		long milliseconds = elapsedTime % 1000;

		if (minutes > 0) {
			return String.format("%d min %d s %d ms.", minutes, seconds, milliseconds);
		} else if (seconds > 0) {
			return String.format("%d s %d ms.", seconds, milliseconds);
		} else {
			return String.format("%d ms.", elapsedTime);
		}
	}

	@SuppressWarnings("static-access")
	public String get(String key, String... defaultValue) {
		return lang2.get(key, defaultValue);
	}

	public String getjarfilename() {
		return jarfilename;
	}

	public boolean getDebug() {
		return debug;
	}

}
