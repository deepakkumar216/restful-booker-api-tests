package com.sdet.booker.clients;

import com.sdet.booker.config.Config;
import com.sdet.booker.models.AuthRequest;
import com.sdet.booker.specs.Specs;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthClient {

    private static volatile String cachedToken;

    @Step("POST /auth as {request.username}")
    public Response createToken(AuthRequest request) {
        return given().spec(Specs.json())
                .body(request)
                .when().post("/auth");
    }

    /** Token for the configured admin user, fetched once and shared across threads. */
    public static String adminToken() {
        if (cachedToken == null) {
            synchronized (AuthClient.class) {
                if (cachedToken == null) {
                    String token = new AuthClient()
                            .createToken(new AuthRequest(Config.username(), Config.password()))
                            .then().spec(Specs.ok())
                            .extract().path("token");
                    if (token == null) {
                        throw new IllegalStateException("Auth failed: no token returned. Check credentials.");
                    }
                    cachedToken = token;
                }
            }
        }
        return cachedToken;
    }
}
