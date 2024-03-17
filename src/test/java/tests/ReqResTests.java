package tests;

import io.qameta.allure.Owner;
import io.restassured.response.Response;
import models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static specs.UserSpec.*;

public class ReqResTests extends TestBaseAPI {

    final String
            userName = "morpheus",
            userJob = "leader",
            registerUserEmail = "eve.holt@reqres.in",
            registerUserPassword = "pistol";

    @Test
    @Owner("Kwlad1ck")
    @DisplayName("Запрос наличия данных юзера в ответе")
    void containUserDataWithSchemaTest() {
        SingleUserResponseModel singleUserResponseModel = step("Make request", () ->
                given(defaultLoggingSpec)

                .when()
                    .get("/users/2")

                .then()
                    .spec(singleUserResponseSpec)
                    .extract().as(SingleUserResponseModel.class));

        step("Check response", () -> {
            assertThat(singleUserResponseModel.getData().getId(), is(equalTo("2")));
            assertThat(singleUserResponseModel.getData().getFirst_name(), is(equalTo("Janet")));
            assertThat(singleUserResponseModel.getData().getLast_name(), is(equalTo("Weaver")));
            assertThat(singleUserResponseModel.getData().getAvatar(), is(equalTo("https://reqres.in/img/faces/2-image.jpg")));
        });
    }

    @Test
    @Owner("Kwlad1ck")
    @DisplayName("Запрос несуществующего юзера")
    void notFoundSingleUserTest() {
        Response response = step("Make request", () ->
                given(defaultLoggingSpec)

                .when()
                    .get("/users/23")

                .then()
                    .spec(defaultLoggingResponseSpec)
                    .extract().response());

        step("Check response", () -> {
            assertThat(response.getStatusCode(), is(equalTo(404)));
            assertThat(response.getBody().asString(), is(equalTo("{}")));
                });
    }

    @Test
    @Owner("Kwlad1ck")
    @DisplayName("Запрос создание юзера")
    void createUserTest() {
        CreateUserBodyModel createUserData = new CreateUserBodyModel();
        createUserData.setName(userName);
        createUserData.setJob(userJob);

        CreateUserResponseModel createUserResponseModel = step("Make request", () ->
                given(defaultLoggingSpec)
                    .body(createUserData)

                .when()
                    .post("/api/users")

                .then()
                    .spec(createUserResponseSpec)
                    .body(matchesJsonSchemaInClasspath("schemas/create-user-schema.json"))
                    .extract().as(CreateUserResponseModel.class));

        step("Check response", () -> {
            assertThat(createUserResponseModel.getName(), is(equalTo(userName)));
            assertThat(createUserResponseModel.getJob(), is(equalTo(userJob)));
        });
    }

    @Test
    @Owner("Kwlad1ck")
    @DisplayName("Обновление данных юзера")
    void updateUserTest() {
        UpdateUserBodyModel updateUserData = new UpdateUserBodyModel();
        updateUserData.setName(userName);
        updateUserData.setJob(userJob);

        UpdateUserResponseModel updateUserResponseModel = step("Make request", () ->
                given(defaultLoggingSpec)
                    .body(updateUserData)

                .when()
                    .put("/api/users/2")

                .then()
                    .spec(updateUserResponseSpec)
                    .extract().as(UpdateUserResponseModel.class));

        step("Check response", () -> {
            assertThat(updateUserResponseModel.getName(), is(equalTo(userName)));
            assertThat(updateUserResponseModel.getJob(), is(equalTo("leader")));
        });
    }

    @Test
    @Owner("Kwlad1ck")
    @DisplayName("Запрос удаления юзера")
    void deleteUserTest() {
        Response response = step("Make request", () ->
        given(defaultLoggingSpec)

            .when()
            .delete("/api/users/2")

            .then()
            .spec(defaultLoggingResponseSpec)
            .extract().response());

        step("Check response", () ->
            assertThat(response.getStatusCode(), is(equalTo(204))));
    }

    @Test
    @Owner("Kwlad1ck")
    @DisplayName("Успешная регистрация пользователя")
    void successfulRegisterUserTest() {
        RegisterUserModel registerUserData = new RegisterUserModel();
        registerUserData.setEmail(registerUserEmail);
        registerUserData.setPassword(registerUserPassword);

        RegisterUserResponseModel registerUserResponseModel = step("Make request", () ->
                given(defaultLoggingSpec)
                    .body(registerUserData)
                .when()
                    .post("/api/register")

                .then()
                    .spec(registerUserResponseSpec)
                    .extract().as(RegisterUserResponseModel.class));

        step("Check response", () -> {
            assertThat(registerUserResponseModel.getEmail(), is(equalTo(registerUserEmail)));
            assertThat(registerUserResponseModel.getPassword(), is(equalTo(registerUserPassword)));
        });
    }
}
