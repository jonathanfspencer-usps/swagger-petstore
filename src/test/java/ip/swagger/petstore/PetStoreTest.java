package ip.swagger.petstore;

import io.restassured.RestAssured.*;
import io.restassured.matcher.RestAssuredMatchers.*;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hamcrest.Matchers.*;

public class PetStoreTest {
  private static CloseableHttpClient httpClient;

  @BeforeClass
  public static void startApplication() throws Exception {
    // Start the application
    ProcessBuilder builder = new ProcessBuilder("mvn", "jetty:run");
    Process process = builder.start();

    // Wait for the application to start
    Thread.sleep(5000);

    // Create an HTTP client to make requests to the application
    httpClient = HttpClients.createDefault();
  }

  @AfterClass
  public static void stopApplication() throws Exception {
    // Stop the application
    ProcessBuilder builder = new ProcessBuilder("mvn", "jetty:stop");
    builder.start();
    httpClient.close();
  }

  @Test
  public void testApi() throws Exception {
    // Make a request to the API
    HttpGet request = new HttpGet("http://localhost:8080/api/v3/openapi.json");
    HttpResponse response = httpClient.execute(request);

    // Assert that the response is valid
    assertEquals(200, response.getStatusLine().getStatusCode());
  }
}
