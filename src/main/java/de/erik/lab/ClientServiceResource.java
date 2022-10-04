package de.erik.lab;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

/**
 * Zum Testen/Aufrufen von {@link ClientService} von ausserhalb
 * (Vollstaendigkeit Technology Stack, Concurrency, Performance).
 */
@Path("clientservice")
public class ClientServiceResource {

	private static final Logger LOGGER =
			Logger.getLogger(ClientServiceResource.class.getName());

	@Inject
	ClientService clientService;

	@GET
	@Path("standard")
	@Produces(MediaType.TEXT_PLAIN)
	public String callClientServiceStandard() {
		LOGGER.info("Calling ClientService#callWs");
		return clientService.callWs();
	}
}
