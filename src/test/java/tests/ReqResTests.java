package tests;

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
import static specs.CreateUserSpec.createUserResponseSpec;
import static specs.CreateUserSpec.createUserSpec;
import static specs.SingleUserSpec.*;

public class ReqResTests extends TestBaseAPI {

    @Test
    void containUserDataWithSchemaTest() {
        SingleUserResponseModel singleUserResponseModel = step("Make request", () ->
            given(singleUserSpec)

            .when()
                .get("/users/2")

            .then()
                .spec(singleUserResponseSpec)
                .extract().as(SingleUserResponseModel.class));

        step("Check response", ()-> {
            assertThat(singleUserResponseModel.getId(), is(equalTo("2")));
            assertThat(singleUserResponseModel.getFirst_name(), is(equalTo("Janet")));
            assertThat(singleUserResponseModel.getLast_name(), is(equalTo("Weaver")));
            assertThat(singleUserResponseModel.getAvatar(), is(equalTo("https://reqres.in/img/faces/2-image.jpg")));
        });
    }

    @Test
    void notFoundSingleUserTest() {
        Response response = step("Make request", () ->
                given(notFoundSingleUserSpec)

        .when()
            .get("/users/23")

        .then()
            .spec(notFoundSingleUserResponseSpec)
            .extract().as(Response.class));

        step("Check response", () ->
                assertThat(response.getBody().asString(), is(equalTo("{}"))));

    }

    @Test
    void createUserTest() {
        CreateUserBodyModel createUserData = new CreateUserBodyModel();
        createUserData.setName("morpheus");
        createUserData.setJob("leader");

        CreateUserResponseModel createUserResponseModel = step("Make request", () ->
                given(createUserSpec)
                    .body(createUserData)

                .when()
                    .post("/api/users")

                .then()
                        .spec(createUserResponseSpec)
                    .body(matchesJsonSchemaInClasspath("schemas/create-user-schema.json"))
                    .extract().as(CreateUserResponseModel.class));

        step("Check response", () -> {
            assertThat(createUserResponseModel.getName(), is(equalTo("morpheus")));
            assertThat(createUserResponseModel.getJob(), is(equalTo("leader")));
        });
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
