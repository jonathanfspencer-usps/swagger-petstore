package ip.swagger.petstore;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Before;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class PetStoreTest {
    private static CloseableHttpClient httpClient;
    private static final int TEST_PET_ID = 123456;  
    private static final String TEST_PET_NAME = "TestPet";  
    private static final String TEST_PET_STATUS = "available";  
  

    @BeforeClass
    public static void startApplication() throws Exception {
        // Start the application
        ProcessBuilder builder = new ProcessBuilder("mvn", "jetty:run");
        builder.start();

        // Wait for the application to start
        Thread.sleep(5000);

        // Create an HTTP client to make requests to the application
        httpClient = HttpClients.createDefault();
    }

    @Before  
    public void setup() {  
        RestAssured.baseURI = "http://localhost:8080";  
        RestAssured.basePath = "/api/v3";  
  
        // Ensure the test pet exists  
        ensurePetExists(TEST_PET_ID, TEST_PET_NAME, TEST_PET_STATUS);  
    }  
  
    private static void ensurePetExists(int petId, String petName, String petStatus) {  
        // Check if the pet exists  
        Response response = given()  
                .contentType(ContentType.JSON)  
                .pathParam("petId", petId)  
                .when()  
                .get("/pet/{petId}");  
  
        if (response.getStatusCode() == 404) {  
            // Pet does not exist, create it  
            String newPet = "{\n" +  
                    "  \"id\": " + petId + ",\n" +  
                    "  \"name\": \"" + petName + "\",\n" +  
                    "  \"photoUrls\": [\n" +  
                    "    \"http://example.com/photo1\"\n" +  
                    "  ],\n" +  
                    "  \"tags\": [\n" +  
                    "    {\n" +  
                    "      \"id\": 1,\n" +  
                    "      \"name\": \"tag1\"\n" +  
                    "    }\n" +  
                    "  ],\n" +  
                    "  \"status\": \"" + petStatus + "\"\n" +  
                    "}";  
  
            given()  
                    .contentType(ContentType.JSON)  
                    .body(newPet)  
                    .when()  
                    .post("/pet")  
                    .then()  
                    .statusCode(200);  
        }  
    } 

    @After  
    public void teardown() {  
        // Remove the test pet  
        given()  
                .contentType(ContentType.JSON)  
                .pathParam("petId", TEST_PET_ID)  
                .when()  
                .delete("/pet/{petId}")  
                .then()  
                .statusCode(200);  
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
                "  \"id\": "+TEST_PET_ID+",\n" +
                "  \"name\": \""+TEST_PET_NAME+"\",\n" +
                "  \"photoUrls\": [\n" +
                "    \"http://example.com/photo1\"\n" +
                "  ],\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"name\": \"tag1\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \""+TEST_PET_STATUS+"\"\n" +
                "}";

        // Send POST request to add the new pet
        Response response = given()
                .contentType(ContentType.JSON)
                .body(newPet)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .body("id", equalTo(TEST_PET_ID))
                .body("name", equalTo(TEST_PET_NAME))
                .body("status", equalTo(TEST_PET_STATUS))
                .extract()
                .response();

        System.out.println("Response: " + response.asString());
    }

    @Test
    public void testGetPetById() {
        // Assuming a pet with ID 123456 already exists in the store

        // Send GET request to retrieve the pet by ID
        Response response = given()
                .contentType(ContentType.JSON)
                .pathParam("petId", TEST_PET_ID)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(200)
                .body("id", equalTo(TEST_PET_ID))
                .body("name", equalTo(TEST_PET_NAME))
                .body("status", equalTo(TEST_PET_STATUS))
                .extract()
                .response();

        System.out.println("Response: " + response.asString());
    }

    @Test  
    public void testPlaceOrder() {  
        // Create a new order object  

        String newOrder = "{\n" +  
                "  \"id\": 1,\n" +  
                "  \"petId\": "+TEST_PET_ID+",\n" +  
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
                .body("petId", equalTo(TEST_PET_ID))  
                .body("quantity", equalTo(1))  
                .body("status", equalTo("placed"))  
                .body("complete", equalTo(true))  
                .extract()  
                .response();  
          
        System.out.println("Response: " + response.asString());  
    }  

}
