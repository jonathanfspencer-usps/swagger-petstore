package ip.swagger.petstore;

import io.restassured.RestAssured;
import io.restassured.RestAssured.*;
import io.restassured.http.ContentType;
import io.restassured.matcher.RestAssuredMatchers.*;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Before;

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

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.basePath = "/api/v3";
    }

    @AfterClass
    public static void stopApplication() throws Exception {
        // Stop the application
        ProcessBuilder builder = new ProcessBuilder("mvn", "jetty:stop");
        builder.start();
        httpClient.close();
    }

    @Test
    public void testApi() {
        // Send GET request to the openapi.json endpoint
        Response response = given()
                .when()
                .get("/openapi.json")
                .then()
                .statusCode(200) // Assert that the response status code is 200
                .extract()
                .response();

        System.out.println("Response: " + response.asString());
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
                .post("/pet")
                .then()
                .statusCode(200)
                .body("id", equalTo(123456))
                .body("name", equalTo("TestPet"))
                .body("status", equalTo("available"))
                .extract()
                .response();

        System.out.println("Response: " + response.asString());
    }

    @Test
    public void testGetPetById() {
        // Assuming a pet with ID 123456 already exists in the store
        // NOTE: This is not a great test, since it assumes a pet with ID 123456 exists.
        int petId = 123456;

        // Send GET request to retrieve the pet by ID
        Response response = given()
                .contentType(ContentType.JSON)
                .pathParam("petId", petId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(200)
                .body("id", equalTo(petId))
                .body("name", equalTo("TestPet"))
                .body("status", equalTo("available"))
                .extract()
                .response();

        System.out.println("Response: " + response.asString());
    }

    @Test  
    public void testPlaceOrder() {  
        // Create a new order object  
        // NOTE: This is not a great test, since it assumes a pet with ID 123456 exists.

        String newOrder = "{\n" +  
                "  \"id\": 1,\n" +  
                "  \"petId\": 123456,\n" +  
                "  \"quantity\": 1,\n" +  
                "  \"shipDate\": \"2023-01-01T00:00:00.000Z\",\n" +  
                "  \"status\": \"placed\",\n" +  
                "  \"complete\": true\n" +  
                "}";  
  
        // Send POST request to place the new order  
        Response response = given()  
                .contentType(ContentType.JSON)  
                .body(newOrder)  
                .when()  
                .post("/store/order")  
                .then()  
                .statusCode(200)  
                .body("id", equalTo(1))  
                .body("petId", equalTo(123456))  
                .body("quantity", equalTo(1))  
                .body("status", equalTo("placed"))  
                .body("complete", equalTo(true))  
                .extract()  
                .response();  
          
        System.out.println("Response: " + response.asString());  
    }  

}
