/* 
 *              weupnp - Trivial upnp java library 
 *
 * Copyright (C) 2008 Alessandro Bahgat Shehata, Daniele Castagna
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, FΩifth Floor, Boston, MA  02110-1301  USA
 * 
 * Alessandro Bahgat Shehata - ale dot bahgat at gmail dot com
 * Daniele Castagna - daniele dot castagna at gmail dot com
 * 
 */

/*
 * refer to miniupnpc-1.0-RC8
 */
package org.bitlet.weupnp;

import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 *	The class which use the library "weupnp" wich implements upnp protocol 
 *	The purpose of this class is to open automaticaly a NAT rule in the router in order to let the conversation be possible
 *	between 2 instances of clavardeur.
 */
public class NatInit implements Runnable {

	private static int SAMPLE_PORT;
	private static boolean LIST_ALL_MAPPINGS = false;
	private GatewayDevice activeGW;

	public NatInit(int port) {
		Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
	         cleaning();
			    }});
		SAMPLE_PORT=port;
		Thread t=new Thread(this);
		t.start();
	}
	
	private void cleaning() {
		try {
			if(activeGW != null) {
			if (activeGW.deletePortMapping(SAMPLE_PORT,"TCP")) {
				addLogLine("Port mapping removed");
			} else {
				addLogLine("Port mapping removal FAILED");
			}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		addLogLine("Stopping weupnp");
	}

	private static void addLogLine(String line) {

		String timeStamp = DateFormat.getTimeInstance().format(new Date());
		String logline = timeStamp+": "+line+"\n";
		System.out.print(logline);
	}

	@Override
	public void run() {
		addLogLine("Starting weupnp");

		GatewayDiscover gatewayDiscover = new GatewayDiscover();
		addLogLine("Looking for Gateway Devices...");

		Map<InetAddress, GatewayDevice> gateways;
		try {
			gateways = gatewayDiscover.discover();
		if (gateways.isEmpty()) {
			addLogLine("No gateways found");
			addLogLine("Stopping weupnp");
			return;
		}
		addLogLine(gateways.size()+" gateway(s) found\n");

		int counter=0;
		for (GatewayDevice gw: gateways.values()) {
			counter++;
			addLogLine("Listing gateway details of device #" + counter+
					"\n\tFriendly name: " + gw.getFriendlyName()+
					"\n\tPresentation URL: " + gw.getPresentationURL()+
					"\n\tModel name: " + gw.getModelName()+
					"\n\tModel number: " + gw.getModelNumber()+
					"\n\tLocal interface address: " + gw.getLocalAddress().getHostAddress()+"\n");
		}

		// choose the first active gateway for the tests
		activeGW = gatewayDiscover.getValidGateway();

		if (null != activeGW) {
			addLogLine("Using gateway: " + activeGW.getFriendlyName());
		} else {
			addLogLine("No active gateway device found, open manually or with the portMapper in ./util the port");
			addLogLine("Stopping weupnp");
			return;
		}


		// testing PortMappingNumberOfEntries
		Integer portMapCount = activeGW.getPortMappingNumberOfEntries();
		addLogLine("GetPortMappingNumberOfEntries: " + (portMapCount!=null?portMapCount.toString():"(unsupported)"));

		// testing getGenericPortMappingEntry
		PortMappingEntry portMapping = new PortMappingEntry();
		if (LIST_ALL_MAPPINGS) {
			int pmCount = 0;
			do {
				if (activeGW.getGenericPortMappingEntry(pmCount,portMapping))
					addLogLine("Portmapping #"+pmCount+" successfully retrieved ("+portMapping.getPortMappingDescription()+":"+portMapping.getExternalPort()+")");
				else{
					addLogLine("Portmapping #"+pmCount+" retrieval failed"); 
					break;
				}
				pmCount++;
			} while (portMapping!=null);
		} else {
			if (activeGW.getGenericPortMappingEntry(0,portMapping))
				addLogLine("Portmapping #0 successfully retrieved ("+portMapping.getPortMappingDescription()+":"+portMapping.getExternalPort()+")");
			else
				addLogLine("Portmapping #0 retrival failed");        	
		}

		InetAddress localAddress = activeGW.getLocalAddress();
		addLogLine("Using local address: "+ localAddress.getHostAddress());
		String externalIPAddress = activeGW.getExternalIPAddress();
		addLogLine("External address: "+ externalIPAddress);

		addLogLine("Querying device to see if a port mapping already exists for port "+ SAMPLE_PORT);

		if (activeGW.getSpecificPortMappingEntry(SAMPLE_PORT,"TCP",portMapping)) {
			addLogLine("Port "+SAMPLE_PORT+" is already mapped. Aborting test.");
			return;
		} else {
			addLogLine("Mapping free. Sending port mapping request for port "+SAMPLE_PORT);

			// test static lease duration mapping
			if (activeGW.addPortMapping(SAMPLE_PORT,SAMPLE_PORT,localAddress.getHostAddress(),"TCP","test")) {
				addLogLine("Mapping SUCCESSFUL.");
				
			}
		} 

		
		} catch (IOException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		
	}

}
