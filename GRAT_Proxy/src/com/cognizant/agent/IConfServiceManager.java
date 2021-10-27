package com.cognizant.agent;

import com.genesyslab.platform.applicationblocks.com.ConfServiceFactory;
import com.genesyslab.platform.applicationblocks.com.IConfService;
import com.genesyslab.platform.commons.protocol.ChannelState;
import com.genesyslab.platform.commons.protocol.Endpoint;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.configuration.protocol.ConfServerProtocol;
import com.genesyslab.platform.configuration.protocol.types.CfgAppType;

public class IConfServiceManager {
	
	public static IConfService initializeConfigService(
			final String     cfgsrvEndpointName,
			final String     cfgsrvHost,
			final int        cfgsrvPort,
			final String     username,
			final String     password) {

		CfgAppType clientType = CfgAppType.CFGSCE;
		String clientName = "default";
		return initializeConfigService(cfgsrvEndpointName,
				cfgsrvHost, cfgsrvPort,
				clientType, clientName,
				username, password);
	}

	public static IConfService initializeConfigService(
			final String     cfgsrvEndpointName,
			final String     cfgsrvHost,
			final int        cfgsrvPort,
			final CfgAppType clientType,
			final String     clientName,
			final String     username,
			final String     password) {
		try{
			Endpoint cfgServerEndpoint =
					new Endpoint(cfgsrvEndpointName, cfgsrvHost, cfgsrvPort);

			ConfServerProtocol protocol = new ConfServerProtocol(cfgServerEndpoint);
			protocol.setClientName(clientName);
			protocol.setClientApplicationType(clientType.ordinal());
			protocol.setUserName(username);
			protocol.setUserPassword(password);

			IConfService service = ConfServiceFactory.createConfService(protocol);
			protocol.open();

			return service;
		}catch(InterruptedException|ProtocolException e){
			System.out.println(e.getMessage());
		}
		return null;
	}

	public static void uninitializeConfigService(
			final IConfService service) {
		if (service.getProtocol().getState() != ChannelState.Closed) {
			try {
				service.getProtocol().close();
			} catch (ProtocolException| IllegalStateException| InterruptedException e) {
				System.out.println(e.getMessage());
			} 
		}
		ConfServiceFactory.releaseConfService(service);
	}
}
