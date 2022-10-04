package de.erik.lab;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class ClientUsageInsideIT {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8888);

	@ArquillianResource
	private URL deploymentUrl;

	@Inject
	ClientUsage clientUsage;

	@Deployment
	public static WebArchive createDeployment() {
		File[] dependencies = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("org.assertj:assertj-core",
						"com.github.tomakehurst:wiremock-jre8",
						"commons-logging:commons-logging")
				.withTransitivity().asFile();
		return ShrinkWrap.create(WebArchive.class, "test.war")
				.addPackage(ClientUsage.class.getPackage())
				.addAsLibraries(dependencies)
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Test
	public void callServiceReusedClientAndTarget() {
		// setup
		WireMock.stubFor(WireMock.get("/json").willReturn(WireMock.okJson("{\"m\":\"v\"}")));
		// test, assert
		clientUsage.callWsReusedClientAndTarget("SINGLE", 1000);
	}

	@Test
	public void callServiceReusedClientNewTarget() {
		// setup
		WireMock.stubFor(WireMock.get("/json").willReturn(WireMock.okJson("{\"m\":\"v\"}")));
		// test, assert
		clientUsage.callWsReusedClientNewTarget("SINGLE", 1000);
	}

	@Test
	public void callServiceNewClientAndTarget() {
		// setup
		WireMock.stubFor(WireMock.get("/json").willReturn(WireMock.okJson("{\"m\":\"v\"}")));
		// test, assert
		clientUsage.callWsNewClientAndTarget("SINGLE", 1000);
	}

}