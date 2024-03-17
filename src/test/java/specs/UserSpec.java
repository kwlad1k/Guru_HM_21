package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static helpers.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.BODY;
import static io.restassured.filter.log.LogDetail.STATUS;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class UserSpec {

    public static final RequestSpecification defaultLoggingSpec = with()
            .filter(withCustomTemplates()) 
            .log().method()
            .log().uri()
            .contentType(JSON);

    public static final ResponseSpecification defaultLoggingResponseSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .log(BODY)
            .build();

    public static final ResponseSpecification singleUserResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .log(STATUS)
            .log(BODY)
            .expectBody(matchesJsonSchemaInClasspath("schemas/user-id-response-schema.json"))
            .build();

    public static final ResponseSpecification createUserResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(201)
            .log(STATUS)
            .log(BODY)
            .build();

    public static final ResponseSpecification updateUserResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .log(STATUS)
            .log(BODY)
            .expectBody(matchesJsonSchemaInClasspath("schemas/update-user-schema.json"))
            .build();

    public static final ResponseSpecification registerUserResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(201)
            .log(STATUS)
            .log(BODY)
            .expectBody(matchesJsonSchemaInClasspath("schemas/successful-register-user-schema.json"))
            .build();
}
