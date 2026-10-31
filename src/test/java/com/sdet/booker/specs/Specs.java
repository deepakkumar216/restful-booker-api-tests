package com.sdet.booker.specs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.sdet.booker.config.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.lessThan;

/** Reusable request/response specifications shared by all API clients. */
public final class Specs {

    private static final long MAX_RESPONSE_MS = 10_000;

    private static final RestAssuredConfig REST_CONFIG = RestAssuredConfig.config()
            .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                    .jackson2ObjectMapperFactory((type, charset) -> new com.fasterxml.jackson.databind.ObjectMapper()
                            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)));

    private Specs() {
    }

    /** Base JSON spec: base URL, JSON in/out, Allure request/response attachments, log on failure. */
    public static RequestSpecification json() {
        return new RequestSpecBuilder()
                .setBaseUri(Config.baseUrl())
                .setContentType(ContentType.JSON)
                // ContentType.JSON sends four MIME types; Restful-Booker answers that with 418, so be explicit.
                .setAccept("application/json")
                .setConfig(REST_CONFIG)
                .addFilter(new AllureRestAssured())
                .log(LogDetail.URI)
                .build();
    }

    /** JSON spec plus the auth cookie required by PUT, PATCH and DELETE. */
    public static RequestSpecification authenticated(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(json())
                .addCookie("token", token)
                .build();
    }

    /** Common checks applied to every successful response. */
    public static ResponseSpecification ok() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(lessThan(MAX_RESPONSE_MS), TimeUnit.MILLISECONDS)
                .build();
    }
}
