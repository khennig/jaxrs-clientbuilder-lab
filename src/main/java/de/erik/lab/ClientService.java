package de.erik.lab;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.TimeUnit;

/**
 * Service-Klasse zum Ausprobieren einer optimalen Nutzung/Erzeugung eines JAX
 * RS Client unter realen Bedingungen (Einfachheit, Performance, Container
 * Agnostik, Konfigurierbarkeit, Concurrency).
 */
@ApplicationScoped
public class ClientService {

	private Client client;

	private WebTarget target;

	public String callWs() {
		return target.request(MediaType.APPLICATION_JSON).get(String.class);
	}

	@PostConstruct
	public void init() {
		client = createClient();
		target = createWebTarget(client);
	}

	private Client createClient() {
		Client client =
				ClientBuilder.newBuilder().connectTimeout(1L, TimeUnit.SECONDS)
						.readTimeout(1L, TimeUnit.SECONDS).build();
		// Client client = ClientBuilder.newBuilder().build();
		return client;
	}

	private WebTarget createWebTarget(Client client) {
		return client.target("http://localhost:8888/json");
	}
}
