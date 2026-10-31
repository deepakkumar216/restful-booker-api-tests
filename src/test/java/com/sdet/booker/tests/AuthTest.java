package com.sdet.booker.tests;

import com.sdet.booker.config.Config;
import com.sdet.booker.data.BookingDataProviders;
import com.sdet.booker.models.AuthRequest;
import com.sdet.booker.specs.Specs;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasKey;

@Epic("Restful-Booker")
@Feature("Authentication")
public class AuthTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "Valid credentials return a token")
    @Severity(SeverityLevel.BLOCKER)
    public void validCredentialsReturnToken() {
        auth.createToken(new AuthRequest(Config.username(), Config.password()))
                .then().spec(Specs.ok())
                .body("token", matchesPattern("^[A-Za-z0-9]{10,}$"));
    }

    @Test(groups = {"regression"}, dataProvider = "badCredentials", dataProviderClass = BookingDataProviders.class,
            description = "Invalid credentials never return a token")
    @Severity(SeverityLevel.CRITICAL)
    public void invalidCredentialsAreRejected(String scenario, String username, String password) {
        // Restful-Booker answers bad credentials with 200 + {"reason":"Bad credentials"} rather than 401.
        auth.createToken(new AuthRequest(username, password))
                .then().statusCode(200)
                .body("$", not(hasKey("token")))
                .body("reason", equalTo("Bad credentials"));
    }
}
