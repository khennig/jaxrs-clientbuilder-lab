package de.erik.lab;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Deploy sample, full stack call via javax.ws.rs, let it use a JAX RS Client
 * calling a Web Service Mock.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ClientUsageOutsideIT {

	private static final Logger LOGGER =
			Logger.getLogger(ClientUsageOutsideIT.class.getName());

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8888);

	@ArquillianResource
	private URL deploymentUrl;

	@Deployment
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class, "test.war")
				.addPackage(ClientUsage.class.getPackage())
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Test
	public void loggertest() throws InterruptedException, ExecutionException {
		LOGGER.info("UPS");
	}

	@Test
	public void callServiceCachedClients() throws InterruptedException, ExecutionException {
		// setup
		WireMock.stubFor(WireMock.get("/json")
				.willReturn(WireMock.okJson("{\"m\":\"v\"}")));
		ExecutorService executorsService = Executors.newCachedThreadPool();
		// test
		List<Future> futures =
				IntStream.range(1, 100).<Callable<Void>>mapToObj(id -> () -> {
					callC();
					return null;
				}).map(executorsService::submit).collect(Collectors.toList());

		// check for swallowed exceptions
		for (Future future : futures) {
			future.get();
		}
	}

	private void callC() throws URISyntaxException {
		JerseyClient client = JerseyClientBuilder.createClient();
		JerseyWebTarget target = client.target(deploymentUrl.toURI());
		LOGGER.info("Cycle requests");
		for (int ii = 10; ii > 0; ii--) {
			Response response =
					target.path("clientservice/standard").request().get();
			// assert
			assertThat(response.getStatus()).isEqualTo(200);
			String responseString = response.readEntity(String.class);
			LOGGER.info(String.format("Caller %s/%s: WS Response: %s", ii, ii,
					responseString));
			assertThat(responseString).isEqualTo("{\"m\":\"v\"}");
			response.close();
		}
	}

	@Test
	@Ignore
	public void callServiceCachedClient() throws URISyntaxException {
		// setup
		WireMock.stubFor(WireMock.get("/json")
				.willReturn(WireMock.okJson("{\"m\":\"v\"}")));
		JerseyClient client = JerseyClientBuilder.createClient();
		JerseyWebTarget target = client.target(deploymentUrl.toURI());
		// test
		Response response = target.path("clientusage").request().get();
		// assert
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.readEntity(String.class)).isEqualTo("done");
		response.close();
	}
}