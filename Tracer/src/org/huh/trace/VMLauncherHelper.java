package org.huh.trace;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;




import org.huh.util.PropertyManager;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.ListeningConnector;
import com.sun.jdi.connect.VMStartException;

@SuppressWarnings("unchecked")
public class VMLauncherHelper {

	//  Do we want to watch assignments to fields
    private boolean watchFields = false;
    
    private String classpath=PropertyManager.getProperty("classpath");
    
    
    
	private VirtualMachineManager vmManager = null;
    
	/**
	 * Determines what Launching connector to use
	 */
	private String connectorType = null;
	
	public VMLauncherHelper(VirtualMachineManager vmManager, String connectorType) {
		this.vmManager = vmManager;
		this.connectorType = connectorType;
	}

	/**
	 * Find a com.sun.jdi.CommandLineLaunch connector.
	 * 
	 * @param vmManager
	 * @return
	 */
	public static LaunchingConnector findCLLaunchingConnector(VirtualMachineManager vmManager) {
		List<Connector> connectors = vmManager.allConnectors();
		//vmManager.l
		Iterator<Connector> iter = connectors.iterator();
		while (iter.hasNext()) {
			Connector connector = (Connector) iter.next();
			if (connector.name().equals("com.sun.jdi.CommandLineLaunch")) {
				return (LaunchingConnector) connector;
			}
		}
		throw new Error("No com.sun.jdi.CommandLineLaunch connector");
	}

	/**
	 * Find a com.sun.jdi.SocketAttach connector.
	 * 
	 * @param vmManager
	 * @return
	 */
	public static AttachingConnector findSocketAttachConnector(VirtualMachineManager vmManager)
	{
		List<Connector> connectors = vmManager.allConnectors();
		//List<Connector> connectors = vmManager.attachingConnectors();
		Iterator<Connector> iter = connectors.iterator();
		while (iter.hasNext()) {
			Connector connector = (Connector) iter.next();
			if (connector.name().equals("com.sun.jdi.SocketAttach")) {
				return (AttachingConnector) connector;
			}
		}
		throw new Error("No com.sun.jdi.SocketAttach connector");
	}
	
	public static ListeningConnector findSocketListenConnector(VirtualMachineManager vmManager)
	{
		List<Connector> connectors = vmManager.allConnectors();
		Iterator<Connector> iter = connectors.iterator();
		while(iter.hasNext()) {
			Connector connector = (Connector) iter.next();
			if (connector.name().equals("com.sun.jdi.SocketListen")) {
				return (ListeningConnector) connector;
			}
		}
		throw new Error("No com.sun.jdi.SocketListen connector");
	}
	
	private Map<String, ? extends Connector.Argument> connectorAttachArguments(AttachingConnector connector, String mainArgs)
	{
		Map<String, ? extends Connector.Argument> arguments = connector.defaultArguments();
		arguments.get("port").setValue("8000");
		
		return arguments;
	}
	
	private Map<String, ? extends Connector.Argument> connectorListenArguments(ListeningConnector connector, String mainArgs)
	{
		Map<String, ? extends Connector.Argument> arguments = connector.defaultArguments();
		arguments.get("port").setValue("8000");
		arguments.get("localAddress").setValue("localhost");
		
		
		return arguments;
	}
	
	// set arguments for the connector
	/**
	 * Return the launching connector's arguments.
	 */
	private Map<String, ? extends Connector.Argument> connectorLaunchArguments(LaunchingConnector connector, String mainArgs) {
		Map<String, ? extends Connector.Argument> arguments = connector.defaultArguments();
		Connector.Argument mainArg = (Connector.Argument) arguments.get("main");
		if (mainArg == null) {
			throw new Error("Bad launching connector");
		}
		mainArg.setValue(mainArgs);
		
		Connector.Argument opts = (Connector.Argument) arguments.get("options");
		
		String optionValue = "-Xss8m -Xms256m -Xmx1024m -XX:MaxPermSize=256m -server " + "-classpath " + classpath;
		opts.setValue(optionValue);
		
		Connector.Argument susp = (Connector.Argument) arguments.get("suspend");
		susp.setValue("true");
		
		if (watchFields) {
			// We need a VM that supports watchpoints
			Connector.Argument optionArg = (Connector.Argument) arguments.get("options");
			if (optionArg == null) {
				throw new Error("Bad launching connector");
			}
			optionArg.setValue("-classic");
		}
		return arguments;
	}
	
	private VirtualMachine attachToTarget(String mainArgs)
	{
		AttachingConnector connector = null;
		connector = VMLauncherHelper.findSocketAttachConnector(this.vmManager);
		
		Map<String, ? extends Connector.Argument> arguments = connectorAttachArguments(connector, mainArgs);
		try {
			return connector.attach(arguments);
		} catch (IOException exc) {
			throw new Error("Unable to attach target VM: " + exc);
		} catch (IllegalConnectorArgumentsException exc) {
			throw new Error("Internal error: " + exc);
		}
	}
	
	/**
	 * target vm should have server=n in the -Xrunjdwp or
	 * -Xrunjdwp:transport=dt_socket,address=8000
	 * @param mainArgs
	 * @return
	 */
	private VirtualMachine listenForTarget(String mainArgs){
		ListeningConnector connector = null;
		connector = VMLauncherHelper.findSocketListenConnector(this.vmManager);
		
		Map<String, ? extends Connector.Argument> arguments = connectorListenArguments(connector, mainArgs);
		try {
			return connector.accept(arguments);
		} catch (IOException exc) {
			throw new Error("Unable to listen for target VM: " + exc);
		} catch (IllegalConnectorArgumentsException exc) {
			throw new Error("Internal error: " + exc);
		}
	}
	
	// attach to target vm (target vm runs the stratego program)
	/**
	 * Launch target VM. Forward target's output and error.
	 */
	private VirtualMachine launchTarget(String mainArgs) {
		LaunchingConnector connector = null;
		
		connector = VMLauncherHelper.findCLLaunchingConnector(this.vmManager);
		Map<String, ? extends Connector.Argument> arguments = connectorLaunchArguments(connector, mainArgs);
		try {
			return connector.launch(arguments);
		} catch (IOException exc) {
			throw new Error("Unable to launch target VM: " + exc);
		} catch (IllegalConnectorArgumentsException exc) {
			throw new Error("Internal error: " + exc);
		} catch (VMStartException exc) {
			throw new Error("Target VM failed to initialize: "
					+ exc.getMessage());
		}
	}
	
	public VirtualMachine getTargetVM(String mainArgs)
	{
		if (this.connectorType == null || "LAUNCH".equals(this.connectorType))
		{
			return this.launchTarget(mainArgs);
		} else if ("LISTEN".equals(this.connectorType)) {
			return this.listenForTarget(mainArgs);
		} else if ("ATTACH".equals(this.connectorType)) { // server=y, start program before the debugger
			return this.attachToTarget(mainArgs);
		} else {
			return null;
		}
		
	}
	
	

}
