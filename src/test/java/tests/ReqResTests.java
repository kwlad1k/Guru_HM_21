package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import models.*;
import org.junit.jupiter.api.Test;

import static helpers.CustomAllureListener.withCustomTemplates;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RerResTests extends TestBaseAPI{

    @Test
    void containUserDataWithSchemaTest() {

        SingleUserResponseModel response = step("Make request", () -> {
            return given()
                .filter(withCustomTemplates())
                .log().uri()
                .log().method()
                .log().body()
                .log().headers()

            .when()
                .get("/users/2")

            .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-id-response-schema.json"))
                .extract().as(SingleUserResponseModel.class);
        });

        step("Check response", ()-> {
            //assertThat(response.path("data.id"), is(2));
        });

        //assertThat(response.path("data.email"), is("janet.weaver@reqres.in"));
        //assertThat(response.path("data.first_name"), is("Janet"));
        //assertThat(response.path("data.last_name"), is("Weaver"));
        //assertThat(response.path("data.avatar"), is("https://reqres.in/img/faces/2-image.jpg"));
    }

    @Test
    void notFoundSingleUserTest() {
        Response response = given()
                .filter(withCustomTemplates())
                .log().uri()
                .log().method()
                .log().body()
                .log().headers()

        .when()
            .get("/users/23")

        .then()
            .log().status()
            .log().body()
            .statusCode(404)
            .extract().response();

        assertThat(response.getBody().asString(), is(equalTo("{}")));
    }

    @Test
    void createUserTest() {
        CreateUserBodyModel createUserData = new CreateUserBodyModel();
        createUserData.setName("morpheus");
        createUserData.setJob("leader");

        CreateUserResponseModel createUserResponseModel = step("Make request", ()-> {
            return given()
                    .filter(withCustomTemplates())
                    .log().uri()
                    .log().method()
                    .log().body()
                    .log().headers()
                    .contentType(JSON)
                    .body(createUserData)

                    .when()
                    .post("/api/users")

                    .then()
                    .log().status()
                    .log().body()
                    .statusCode(201)
                    .body(matchesJsonSchemaInClasspath("schemas/create-user-schema.json"))
                    .extract().as(CreateUserResponseModel.class);
        });
        step("Check response", ()-> {
            //assertThat(response.path(createUserData.getName()), is("morpheus"));

            //assertThat(createUserResponseModel.getName()).isEqualTo("morpheus");
        });
        //assertThat("name", response.getName());
        //assertThat(response.path("job"), is("leader"));

    }

    @Test
    void updateUserTest() {
        UpdateUserBodyModel updateUserData = new UpdateUserBodyModel();
        updateUserData.setName("morpheus");
        updateUserData.setJob("zion resident");
        UpdateUserResponseModel response = given()
                .filter(withCustomTemplates())
                .log().uri()
                .log().method()
                .log().body()
                .log().headers()
                .contentType(JSON)
                .body(updateUserData)

        .when()
            .put("/api/users/2")

        .then()
            .log().status()
            .log().body()
            .statusCode(200)
            .body(matchesJsonSchemaInClasspath("schemas/update-user-schema.json"))
            .extract().as(UpdateUserResponseModel.class);

        //assertThat(response.path("name"), is("morpheus"));
        //assertThat(response.path("job"), is("zion resident"));

    }

    @Test
    void deleteUserTest() {
        given()
                .filter(withCustomTemplates())
                .log().uri()
                .log().method()
                .log().body()
                .log().headers()

        .when()
            .delete("/api/users/2")

        .then()
            .log().status()
            .log().body()
            .statusCode(204)
            .extract().response();
    }

    @Test
    void successfulRegisterUserTest() {
        RegisterUserModel registerUserData = new RegisterUserModel();
        registerUserData.setEmail("eve.holt@reqres.in");
        registerUserData.setPassword("pistol");
        RegisterUserResponseModel response = given()
                .filter(withCustomTemplates())
                .log().uri()
                .log().method()
                .log().body()
                .log().headers()
                .contentType(JSON)
                .body(registerUserData)

        .when()
            .post("/api/register")

        .then()
            .log().status()
            .log().body()
            .statusCode(201)
            .body(matchesJsonSchemaInClasspath("schemas/successful-register-user-schema.json"))
            .extract().as(RegisterUserResponseModel.class);

        //assertThat(response.path("email"), is("eve.holt@reqres.in"));
        //assertThat(response.path("password"), is("pistol"));
    }
}
