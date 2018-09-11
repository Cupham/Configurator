package configutator.echonetliteManager;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import configurator.utils.ConfigLoader;
import configutator.deviceObjs.EchonetLiteDevice;
import configutator.deviceObjs.NodeProfileObject;
import configutator.deviceObjs.eAirConditioner;
import configutator.deviceObjs.eDataObject;
import configutator.deviceObjs.eLighting;
import configutator.deviceObjs.eTemperatureSensor;
import echowand.common.EOJ;
import echowand.logic.TooManyObjectsException;
import echowand.monitor.Monitor;
import echowand.monitor.MonitorListener;
import echowand.net.Inet4Subnet;
import echowand.net.Node;
import echowand.net.SubnetException;
import echowand.object.EchonetObjectException;
import echowand.service.Core;
import echowand.service.Service;;

public class MainLoop {
	private static final Logger LOGGER = Logger.getLogger(MainLoop.class.getName());
	private static ConfigLoader config;
	private static int counter;
	public static ArrayList<EchonetLiteDevice> echonetLiteDevice;
	public static void main(String[] args) throws ParserConfigurationException, SocketException, SubnetException, TooManyObjectsException {
		counter = 0; 
		config = new ConfigLoader("./config.properties");
		echonetLiteDevice = new ArrayList<EchonetLiteDevice>();

		
		
		initEchonetLiteInterface(config.getNetworkInterfaceName());
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				counter +=1;
				System.out.println("------------------RUN: " + counter +  " ------------------");
				try {
					if(echonetLiteDevice.size() != 0)
						loadXML();
					else 
						System.out.println("Nothing to show");
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 5000, 5000);
	}
	public static void initEchonetLiteInterface(String networkInterfaceName) throws SocketException, SubnetException, TooManyObjectsException {
		NetworkInterface nif = NetworkInterface.getByName(networkInterfaceName);
		Core e_Core = new Core(Inet4Subnet.startSubnet(nif));
		e_Core.startService();
		Service e_Service = new Service(e_Core);
		Monitor monitor = new Monitor(e_Core);
		monitor.addMonitorListener(new MonitorListener() {
			
			@Override
			public void detectEOJsJoined(Monitor monitor, Node node, List<EOJ> eojs) {
				LOGGER.log(Level.INFO,"----ECHONET Interface Monitoring Joined EOJ: " + node + " " + eojs);
				EchonetLiteDevice eDevice = new EchonetLiteDevice(node);
				 NodeProfileObject profile = null;
	                for(EOJ eoj :  eojs) {
	                	if(eoj.isProfileObject()) {
	                		profile = new NodeProfileObject(node, eoj);
	                		profile.ParseProfileObjectFromEPC(e_Service);
	                		eDevice.setProfileObj(profile);
	                	} else if(eoj.isDeviceObject()) {
	                		try {
								eDevice.parseDataObject(eoj,node,e_Service);	
							} catch (EchonetObjectException e) {
								e.printStackTrace();
							}
	                	}
	                }
	                echonetLiteDevice.add(eDevice);  
	                LOGGER.log(Level.INFO,"DeviceListSize  = " + echonetLiteDevice.size());
				
			}
			
			@Override
			public void detectEOJsExpired(Monitor monitor, Node node, List<EOJ> eojs) {
				 LOGGER.log(Level.INFO,"----ECHONET Interface Monitoring Expired EOJ: " + node + " " + eojs);
				
			}
		});
		monitor.start();
	}
	
	public static void loadXML() throws TransformerException, ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("ECHONET_Lite");
		doc.appendChild(rootElement);
		
		for(EchonetLiteDevice dev : echonetLiteDevice) {
			for(eDataObject obj : dev.getDataObjList()) {
				if(obj.getClass().equals(eTemperatureSensor.class)) {
					eTemperatureSensor sensor = (eTemperatureSensor) obj;
					Element ele = doc.createElement("Temperature_Sensor");
					Attr attr = doc.createAttribute("id");
					attr.setValue(String.valueOf(sensor.getInstanceCode()));
					ele.setAttributeNode(attr);
					
					Element ip = doc.createElement("IP_Address");
					ip.appendChild(doc.createTextNode(sensor.getNode().getNodeInfo().toString()));
					ele.appendChild(ip);
					
					Element eoj = doc.createElement("EOJ");
					eoj.appendChild(doc.createTextNode(String.valueOf(sensor.getEoj())));
					ele.appendChild(eoj);
					
					if(sensor.getProductNumber()!= null) {
						Element model = doc.createElement("Model_Number");
						model.appendChild(doc.createTextNode(sensor.getProductNumber()));
						ele.appendChild(model);	
					}
					if(sensor.getManufacturerCode()!= null) {
						Element manu = doc.createElement("Manufacturer");
						manu.appendChild(doc.createTextNode(sensor.getManufacturerCode()));
						ele.appendChild(manu);	
					}
					if(sensor.getProductCode()!= null) {
						Element model_name = doc.createElement("Model_Name");
						model_name.appendChild(doc.createTextNode(sensor.getProductCode()));
						ele.appendChild(model_name);
					}
					
					rootElement.appendChild(ele);
								
				} else if(dev.getClass().equals(eAirConditioner.class)) {
					eAirConditioner aircon = (eAirConditioner) obj;
					Element ele = doc.createElement("Airconditioner");
					
					Element ip = doc.createElement("IP_Address");
					ip.appendChild(doc.createTextNode(aircon.getNode().getNodeInfo().toString()));
					ele.appendChild(ip);
					
					Element eoj = doc.createElement("EOJ");
					eoj.appendChild(doc.createTextNode(String.valueOf(aircon.getEoj())));
					ele.appendChild(eoj);
					
					if(aircon.getProductNumber() != null) {
						Element model = doc.createElement("Model_Number");
						model.appendChild(doc.createTextNode(aircon.getProductNumber()));
						ele.appendChild(model);		
					}
					if(aircon.getManufacturerCode() != null) {
						Element manu = doc.createElement("Manufacturer");
						manu.appendChild(doc.createTextNode(aircon.getManufacturerCode()));
						ele.appendChild(manu);	
					}
					if(aircon.getProductCode()!= null){
						Element model_name = doc.createElement("Model_Name");
						model_name.appendChild(doc.createTextNode(aircon.getProductCode()));
						ele.appendChild(model_name);
					}
					
					rootElement.appendChild(ele);
					
				} else if(dev.getClass().equals(eLighting.class)) {
					eLighting light = (eLighting) obj;
					Element ele = doc.createElement("LightBub");
					
					Element ip = doc.createElement("IP_Address");
					ip.appendChild(doc.createTextNode(light.getNode().getNodeInfo().toString()));
					ele.appendChild(ip);
					
					Element eoj = doc.createElement("EOJ");
					eoj.appendChild(doc.createTextNode(String.valueOf(light.getEoj())));
					ele.appendChild(eoj);
					
					if(light.getProductNumber() != null) {
						Element model = doc.createElement("Model_Number");
						model.appendChild(doc.createTextNode(light.getProductNumber()));
						ele.appendChild(model);		
					}
					if(light.getManufacturerCode() != null) {
						Element manu = doc.createElement("Manufacturer");
						manu.appendChild(doc.createTextNode(light.getManufacturerCode()));
						ele.appendChild(manu);	
					}
					if(light.getProductCode()!= null){
						Element model_name = doc.createElement("Model_Name");
						model_name.appendChild(doc.createTextNode(light.getProductCode()));
						ele.appendChild(model_name);
					}
					
					rootElement.appendChild(ele);
					
				}
				
			}
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("/tmp/echonetLite.xml"));

		// Output to console for testing
		//StreamResult result = new StreamResult(System.out);
		transformer.transform(source, result);

		System.out.println("File saved!");
		
	}
}
