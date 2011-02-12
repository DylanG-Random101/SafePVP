import java.util.logging.Logger;

public class SafePVP extends Plugin {
	static final ZombieListener listener = new ZombieListener();
	private Logger log;
	private String name = "SafePVP";
	private String version = "1.6";

	public void enable() {
		etc.getInstance().addCommand("/pvp", "- Usage: /pvp help");
	}

	public void disable() {
		etc.getInstance().removeCommand("/pvp");
	}



	public void initialize() {
		log = Logger.getLogger("Minecraft");
		log.info(name + " v" + version + " initialized.");
		etc.getLoader().addListener( 
				PluginLoader.Hook.DAMAGE,
				listener,
				this,
				PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener( 
				PluginLoader.Hook.DISCONNECT,
				listener,
				this,
				PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener( 
				PluginLoader.Hook.LOGIN,
				listener,
				this,
				PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener( 
				PluginLoader.Hook.COMMAND,
				listener,
				this,
				PluginListener.Priority.CRITICAL);
		etc.getLoader().addListener( 
				PluginLoader.Hook.SERVERCOMMAND,
				listener,
				this,
				PluginListener.Priority.MEDIUM);

	}
}
