package configurator.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;



public class ConfigLoader {

	private static final Logger LOGGER = Logger.getLogger(ConfigLoader.class.getName());
	private String networkInterfaceName;
	
	public ConfigLoader(){
		
	}
	public ConfigLoader(String fileName) {
		Properties pp = loadConfigFile(fileName);
		if(pp != null) {
			setNetworkInterfaceName(pp.getProperty("NETWORK_INTERFACE_NAME"));
		} else {
			LOGGER.log(Level.SEVERE,"Can not load config information from file");
		}
		
	}
	private Properties loadConfigFile(String fileName) {
		Properties prop = new Properties();
	    InputStream input = null;
	 
	    try {
	    	LOGGER.log(Level.INFO,"Load config file");
	    	input = new FileInputStream(fileName);
	        prop.load(input);
	 
	    } catch (IOException ex) {
	    	LOGGER.log(Level.SEVERE,"Can not load config file");
	        ex.printStackTrace();
	    } finally {
	        if (input != null) {
	            try {
	                input.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    return prop;
}
	public String getNetworkInterfaceName() {
		return networkInterfaceName;
	}
	public void setNetworkInterfaceName(String nif) {
		this.networkInterfaceName = nif;
	}

}
