package com.sdet.booker.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

@Epic("Restful-Booker")
@Feature("Health check")
public class HealthCheckTest extends BaseTest {

    @Test(groups = {"smoke"}, description = "GET /ping returns 201 Created when the service is up")
    @Severity(SeverityLevel.BLOCKER)
    public void pingReturnsCreated() {
        bookings.ping().then().statusCode(201);
    }
}
