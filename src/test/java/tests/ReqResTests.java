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
import static specs.CreateUserSpec.createUserResponseSpec;
import static specs.CreateUserSpec.createUserSpec;
import static specs.DeleteUserSpec.deleteUserResponseSpec;
import static specs.DeleteUserSpec.deleteUserSpec;
import static specs.RegisterUserSpec.registerUserResponseSpec;
import static specs.RegisterUserSpec.registerUserSpec;
import static specs.SingleUserSpec.*;
import static specs.UpdateUserSpec.updateUserResponseSpec;
import static specs.UpdateUserSpec.updateUserSpec;

public class ReqResTests extends TestBaseAPI {

    TestDataAPI testDataAPI = new TestDataAPI();

    @Test
    @Owner("Kwlad1ck")
    @DisplayName("Запрос наличия данных юзера в ответе")
    void containUserDataWithSchemaTest() {
        SingleUserResponseModel singleUserResponseModel = step("Make request", () ->
                given(singleUserSpec)

                .when()
                    .get("/users/2")

                .then()
                    .spec(singleUserResponseSpec)
                    .extract().as(SingleUserResponseModel.class));

        step("Check response", () -> {
            assertThat(singleUserResponseModel.getData().getId(), is(equalTo(testDataAPI.singleUserId)));
            assertThat(singleUserResponseModel.getData().getFirst_name(), is(equalTo(testDataAPI.singleUserFirstName)));
            assertThat(singleUserResponseModel.getData().getLast_name(), is(equalTo(testDataAPI.singleUserLastName)));
            assertThat(singleUserResponseModel.getData().getAvatar(), is(equalTo(testDataAPI.singleUserUrlAvatar)));
        });
    }

    @Test
    @Owner("Kwlad1ck")
    @DisplayName("Запрос несуществующего юзера")
    void notFoundSingleUserTest() {
        Response response = step("Make request", () ->
                given(notFoundSingleUserSpec)

                .when()
                    .get("/users/23")

                .then()
                    .spec(notFoundSingleUserResponseSpec)
                    .extract().response());

        step("Check response", () ->
                assertThat(response.getBody().asString(), is(equalTo("{}"))));
    }

    @Test
    @Owner("Kwlad1ck")
    @DisplayName("Запрос создание юзера")
    void createUserTest() {
        CreateUserBodyModel createUserData = new CreateUserBodyModel();
        createUserData.setName(testDataAPI.userName);
        createUserData.setJob(testDataAPI.userJob);

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
            assertThat(createUserResponseModel.getName(), is(equalTo(testDataAPI.userName)));
            assertThat(createUserResponseModel.getJob(), is(equalTo(testDataAPI.userJob)));
        });
    }

    @Test
    @Owner("Kwlad1ck")
    @DisplayName("Обновление данных юзера")
    void updateUserTest() {
        UpdateUserBodyModel updateUserData = new UpdateUserBodyModel();
        updateUserData.setName(testDataAPI.userName);
        updateUserData.setJob(testDataAPI.updateUserJob);

        UpdateUserResponseModel updateUserResponseModel = step("Make request", () ->
                given(updateUserSpec)
                    .body(updateUserData)

                .when()
                    .put("/api/users/2")

                .then()
                    .spec(updateUserResponseSpec)
                    .extract().as(UpdateUserResponseModel.class));
        step("Check response", () -> {
            assertThat(updateUserResponseModel.getName(), is(equalTo(testDataAPI.userName)));
            assertThat(updateUserResponseModel.getJob(), is(equalTo(testDataAPI.updateUserJob)));
        });
    }

    @Test
    @Owner("Kwlad1ck")
    @DisplayName("Запрос удаления юзера")
    void deleteUserTest() {
        Response response = step("Make request", () ->
        given(deleteUserSpec)

            .when()
            .delete("/api/users/2")

            .then()
            .spec(deleteUserResponseSpec)
            .extract().response());
        step("Check response", () -> {
            assertThat(response.getStatusCode(), is(equalTo(204)));
            assertThat(response.getBody().asString(), isEmptyString());
        });
    }

    @Test
    @Owner("Kwlad1ck")
    @DisplayName("Успешная регистрация пользователя")
    void successfulRegisterUserTest() {
        RegisterUserModel registerUserData = new RegisterUserModel();
        registerUserData.setEmail(testDataAPI.registerUserEmail);
        registerUserData.setPassword(testDataAPI.registerUserPassword);

        RegisterUserResponseModel registerUserResponseModel = step("Make request", () ->
                given(registerUserSpec)
                    .body(registerUserData)
                .when()
                    .post("/api/register")

                .then()
                    .spec(registerUserResponseSpec)
                    .extract().as(RegisterUserResponseModel.class));
        step("Check response", () -> {
            assertThat(registerUserResponseModel.getEmail(), is(equalTo(testDataAPI.registerUserEmail)));
            assertThat(registerUserResponseModel.getPassword(), is(equalTo(testDataAPI.registerUserPassword)));
        });
    }
}
