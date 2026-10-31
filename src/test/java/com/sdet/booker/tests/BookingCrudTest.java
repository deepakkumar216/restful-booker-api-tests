package com.sdet.booker.tests;

import com.sdet.booker.clients.AuthClient;
import com.sdet.booker.data.BookingDataProviders;
import com.sdet.booker.data.BookingFactory;
import com.sdet.booker.models.Booking;
import com.sdet.booker.models.BookingDates;
import com.sdet.booker.models.CreateBookingResponse;
import com.sdet.booker.specs.Specs;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Restful-Booker")
@Feature("Booking CRUD")
public class BookingCrudTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "Create a booking and read it back")
    @Story("Create")
    @Severity(SeverityLevel.BLOCKER)
    public void createBookingThenGetReturnsSameData() {
        Booking request = BookingFactory.validBooking();

        CreateBookingResponse created = bookings.createBooking(request)
                .then().spec(Specs.ok())
                .body(matchesJsonSchemaInClasspath("schemas/create-booking-response.json"))
                .extract().as(CreateBookingResponse.class);
        trackForCleanup(created.bookingid());

        assertThat(created.booking()).as("echoed booking").isEqualTo(request);

        Booking fetched = bookings.getBooking(created.bookingid())
                .then().spec(Specs.ok())
                .body(matchesJsonSchemaInClasspath("schemas/booking.json"))
                .extract().as(Booking.class);

        assertThat(fetched).as("persisted booking").isEqualTo(request);
    }

    @Test(groups = {"regression"}, dataProvider = "validBookings", dataProviderClass = BookingDataProviders.class,
            description = "Edge-case bookings are accepted and stored unchanged")
    @Story("Create")
    @Severity(SeverityLevel.NORMAL)
    public void validEdgeCaseBookingsAreAccepted(String scenario, Booking request) {
        CreateBookingResponse created = givenBookingExists(request);
        assertThat(created.booking()).as(scenario).isEqualTo(request);
    }

    @Test(groups = {"regression"}, description = "GET /booking lists IDs that match the schema")
    @Story("Read")
    @Severity(SeverityLevel.NORMAL)
    public void listBookingIdsMatchesSchema() {
        bookings.getBookingIds(java.util.Map.of())
                .then().spec(Specs.ok())
                .body(matchesJsonSchemaInClasspath("schemas/booking-ids.json"));
    }

    @Test(groups = {"regression"}, description = "Filtering by name returns the created booking")
    @Story("Read")
    @Severity(SeverityLevel.NORMAL)
    public void filterByNameReturnsCreatedBooking() {
        Booking request = BookingFactory.validBooking();
        int id = givenBookingExists(request).bookingid();

        java.util.List<Integer> ids = bookings.getBookingIds(java.util.Map.of(
                        "firstname", request.firstname(),
                        "lastname", request.lastname()))
                .then().spec(Specs.ok())
                .extract().jsonPath().getList("bookingid", Integer.class);

        assertThat(ids).contains(id);
    }

    @Test(groups = {"smoke", "regression"}, description = "PUT replaces the whole booking")
    @Story("Update")
    @Severity(SeverityLevel.CRITICAL)
    public void fullUpdateReplacesBooking() {
        int id = givenBookingExists(BookingFactory.validBooking()).bookingid();
        Booking replacement = BookingFactory.validBooking();

        Booking updated = bookings.updateBooking(id, replacement, AuthClient.adminToken())
                .then().spec(Specs.ok())
                .body(matchesJsonSchemaInClasspath("schemas/booking.json"))
                .extract().as(Booking.class);

        assertThat(updated).isEqualTo(replacement);
        assertThat(bookings.getBooking(id).as(Booking.class)).isEqualTo(replacement);
    }

    @Test(groups = {"regression"}, description = "PATCH changes only the fields sent")
    @Story("Update")
    @Severity(SeverityLevel.CRITICAL)
    public void partialUpdateChangesOnlyGivenFields() {
        Booking original = BookingFactory.validBooking();
        int id = givenBookingExists(original).bookingid();

        Booking patch = Booking.builder()
                .firstname("Patched")
                .bookingdates(new BookingDates("2031-05-01", "2031-05-04"))
                .build();

        Booking updated = bookings.partialUpdateBooking(id, patch, AuthClient.adminToken())
                .then().spec(Specs.ok())
                .extract().as(Booking.class);

        Booking expected = original.toBuilder()
                .firstname(patch.firstname())
                .bookingdates(patch.bookingdates())
                .build();
        assertThat(updated).isEqualTo(expected);
    }

    @Test(groups = {"smoke", "regression"}, description = "DELETE removes the booking")
    @Story("Delete")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteRemovesBooking() {
        int id = givenBookingExists(BookingFactory.validBooking()).bookingid();

        // Restful-Booker returns 201 Created for a successful delete.
        bookings.deleteBooking(id, AuthClient.adminToken()).then().statusCode(201);
        bookings.getBooking(id).then().statusCode(404);
    }
}
