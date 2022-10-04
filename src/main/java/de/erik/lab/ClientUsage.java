package de.erik.lab;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Zum Ausprobieren einer optimalen Nutzung/Erzeugung eines JAX RS Client unter
 * Laborbedingungen (Performance, ohne Concurrency!).
 */
public class ClientUsage {

	private static final Logger LOGGER =
			Logger.getLogger(ClientUsage.class.getName());

	public void callWsNewClientAndTarget(String callerId, int n) {
		for (int i = n; i > 0; i--) {
			Client client = createClient();
			try {
				WebTarget target = client.target("http://localhost:8888/json");
				String responseString =
						target.request(MediaType.APPLICATION_JSON)
								.get(String.class);
				LOGGER.info(
						String.format("Caller %s: WS Response: %s", callerId,
								responseString));
			} finally {
				client.close();
			}
		}
	}

	public void callWsReusedClientNewTarget(String callerId, int n) {
		Client client = createClient();
		try {
			for (int i = n; i > 0; i--) {
				WebTarget target = client.target("http://localhost:8888/json");
				String responseString =
						target.request(MediaType.APPLICATION_JSON)
								.get(String.class);

				try (Response response = target.request(MediaType.APPLICATION_JSON)
						.get()) {
					responseString = response.readEntity(String.class);
				}
				LOGGER.info(
						String.format("Caller %s: WS Response: %s", callerId,
								responseString));
			}
		} finally {
			client.close();
		}
	}

	public void callWsReusedClientAndTarget(String callerId, int n) {
		Client client = createClient();
		WebTarget target = client.target("http://localhost:8888/json");
		try {
			for (int i = n; i > 0; i--) {
				String responseString =
						target.request(MediaType.APPLICATION_JSON)
								.get(String.class);
				LOGGER.info(
						String.format("Caller %s: WS Response: %s", callerId,
								responseString));
			}
		} finally {
			client.close();
		}
	}

	private Client createClient() {
		return ClientBuilder.newBuilder().connectTimeout(1L, TimeUnit.SECONDS)
				.readTimeout(1L, TimeUnit.SECONDS).build();
		// return ClientBuilder.newBuilder().build();
	}
}
