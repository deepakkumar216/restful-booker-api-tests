package com.sdet.booker.clients;

import com.sdet.booker.models.Booking;
import com.sdet.booker.specs.Specs;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

/** Thin client over the /booking endpoints. Returns raw responses so tests own the assertions. */
public class BookingClient {

    @Step("GET /ping")
    public Response ping() {
        return given().spec(Specs.json()).when().get("/ping");
    }

    @Step("GET /booking with filters {filters}")
    public Response getBookingIds(Map<String, ?> filters) {
        return given().spec(Specs.json()).queryParams(filters).when().get("/booking");
    }

    @Step("GET /booking/{id}")
    public Response getBooking(int id) {
        return given().spec(Specs.json()).pathParam("id", id).when().get("/booking/{id}");
    }

    @Step("POST /booking")
    public Response createBooking(Object body) {
        return given().spec(Specs.json()).body(body).when().post("/booking");
    }

    @Step("PUT /booking/{id}")
    public Response updateBooking(int id, Booking body, String token) {
        return given().spec(token == null ? Specs.json() : Specs.authenticated(token))
                .pathParam("id", id).body(body)
                .when().put("/booking/{id}");
    }

    @Step("PATCH /booking/{id}")
    public Response partialUpdateBooking(int id, Booking body, String token) {
        return given().spec(token == null ? Specs.json() : Specs.authenticated(token))
                .pathParam("id", id).body(body)
                .when().patch("/booking/{id}");
    }

    @Step("DELETE /booking/{id}")
    public Response deleteBooking(int id, String token) {
        return given().spec(token == null ? Specs.json() : Specs.authenticated(token))
                .pathParam("id", id)
                .when().delete("/booking/{id}");
    }
}
