package com.aliware.tasktracker;

import com.aliware.tasktracker.dto.AuthRequest;
import com.aliware.tasktracker.dto.TaskRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskTrackerFlowTest {

    static String token;
    static Long taskId;
    // Usamos un usuario único para cada ejecución del test
    static String username = "testuser_" + System.currentTimeMillis() + "@aliware.com";
    static String password = "password123";

    @Test
    @Order(1)
    public void testRegister() {
        AuthRequest request = new AuthRequest(username, password);

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201);
    }

    @Test
    @Order(2)
    public void testLogin() {
        AuthRequest request = new AuthRequest(username, password);

        token = given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue()) // Verificamos que devuelva token
            .extract().path("token");
    }

    @Test
    @Order(3)
    public void testCreateTask() {
        TaskRequest task = new TaskRequest("Test Task", "Description from test", "PENDING");

        taskId = given()
            .header("Authorization", "Bearer " + token) // Usamos el token obtenido
            .contentType(ContentType.JSON)
            .body(task)
        .when()
            .post("/api/tasks")
        .then()
            .statusCode(201)
            .body("title", equalTo("Test Task"))
            .extract().path("id").toString().transform(Long::parseLong);
    }

    @Test
    @Order(4)
    public void testGetTasks() {
        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/tasks")
        .then()
            .statusCode(200)
            .body("items.size()", greaterThanOrEqualTo(1)); // Debe haber al menos la que creamos
    }

    @Test
    @Order(5)
    public void testDeleteTask() {
        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .delete("/api/tasks/" + taskId)
        .then()
            .statusCode(204);
    }

    @Test
    @Order(6)
    public void testAccessProtectedWithoutToken() {
        given()
        .when()
            .get("/api/tasks")
        .then()
            .statusCode(401); // Debe fallar sin token
    }
}