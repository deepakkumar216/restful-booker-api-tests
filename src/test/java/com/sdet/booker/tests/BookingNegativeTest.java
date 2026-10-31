package com.sdet.booker.tests;

import com.sdet.booker.data.BookingDataProviders;
import com.sdet.booker.data.BookingFactory;
import com.sdet.booker.models.Booking;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import java.util.Map;

@Epic("Restful-Booker")
@Feature("Booking negative cases")
public class BookingNegativeTest extends BaseTest {

    @Test(groups = {"regression"}, dataProvider = "nonExistentIds", dataProviderClass = BookingDataProviders.class,
            description = "GET for an unknown ID returns 404")
    @Story("Read")
    @Severity(SeverityLevel.NORMAL)
    public void unknownBookingReturns404(int id) {
        bookings.getBooking(id).then().statusCode(404);
    }

    @Test(groups = {"regression"}, dataProvider = "missingRequiredField", dataProviderClass = BookingDataProviders.class,
            description = "Create without a required field is rejected")
    @Story("Create")
    @Severity(SeverityLevel.CRITICAL)
    public void createWithMissingFieldIsRejected(String missingField, Map<String, Object> body) {
        // Known defect: Restful-Booker answers 500 Internal Server Error instead of 400 Bad Request.
        // Pinned to 500 so a change in behaviour (fixed or worse) is flagged.
        bookings.createBooking(body).then().statusCode(500);
    }

    @Test(groups = {"regression"}, description = "PUT without an auth token is forbidden")
    @Story("Authorisation")
    @Severity(SeverityLevel.BLOCKER)
    public void updateWithoutTokenIsForbidden() {
        int id = givenBookingExists(BookingFactory.validBooking()).bookingid();
        bookings.updateBooking(id, BookingFactory.validBooking(), null).then().statusCode(403);
    }

    @Test(groups = {"regression"}, description = "PATCH with an invalid token is forbidden")
    @Story("Authorisation")
    @Severity(SeverityLevel.BLOCKER)
    public void patchWithInvalidTokenIsForbidden() {
        int id = givenBookingExists(BookingFactory.validBooking()).bookingid();
        Booking patch = Booking.builder().firstname("Hacker").build();
        bookings.partialUpdateBooking(id, patch, "not-a-real-token").then().statusCode(403);
    }

    @Test(groups = {"regression"}, description = "DELETE without an auth token is forbidden and the booking survives")
    @Story("Authorisation")
    @Severity(SeverityLevel.BLOCKER)
    public void deleteWithoutTokenIsForbidden() {
        int id = givenBookingExists(BookingFactory.validBooking()).bookingid();
        bookings.deleteBooking(id, null).then().statusCode(403);
        bookings.getBooking(id).then().statusCode(200);
    }
}
