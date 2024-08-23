package ip.swagger.petstore;

import io.restassured.RestAssured.*;
import io.restassured.http.ContentType;
import io.restassured.matcher.RestAssuredMatchers.*;
import io.restassured.response.Response;
  
import static io.restassured.RestAssured.given;  
import static org.hamcrest.Matchers.equalTo;

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

  @Test  
  public void testAddPet() {  
      // Create a new pet object  
      String newPet = "{\n" +  
              "  \"id\": 123456,\n" +  
              "  \"name\": \"TestPet\",\n" +  
              "  \"photoUrls\": [\n" +  
              "    \"http://example.com/photo1\"\n" +  
              "  ],\n" +  
              "  \"tags\": [\n" +  
              "    {\n" +  
              "      \"id\": 1,\n" +  
              "      \"name\": \"tag1\"\n" +  
              "    }\n" +  
              "  ],\n" +  
              "  \"status\": \"available\"\n" +  
              "}";  

      // Send POST request to add the new pet  
      Response response = given()  
              .contentType(ContentType.JSON)  
              .body(newPet)  
              .when()  
              .post("http://localhost:8080/api/v3/pet")  
              .then()  
              .statusCode(200)  
              .body("id", equalTo(123456))  
              .body("name", equalTo("TestPet"))  
              .body("status", equalTo("available"))  
              .extract()  
              .response();  
        
      System.out.println("Response: " + response.asString());  
  } 
  
}
