package com.sdet.booker.tests;

import com.sdet.booker.clients.AuthClient;
import com.sdet.booker.clients.BookingClient;
import com.sdet.booker.models.Booking;
import com.sdet.booker.models.CreateBookingResponse;
import com.sdet.booker.specs.Specs;
import org.testng.annotations.AfterClass;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/** Shared clients, a helper to create test data, and cleanup of everything a class created. */
public abstract class BaseTest {

    protected final BookingClient bookings = new BookingClient();
    protected final AuthClient auth = new AuthClient();

    private final Queue<Integer> createdIds = new ConcurrentLinkedQueue<>();

    /** Creates a booking through the API and registers it for cleanup. */
    protected CreateBookingResponse givenBookingExists(Booking booking) {
        CreateBookingResponse created = bookings.createBooking(booking)
                .then().spec(Specs.ok())
                .extract().as(CreateBookingResponse.class);
        trackForCleanup(created.bookingid());
        return created;
    }

    protected void trackForCleanup(int id) {
        createdIds.add(id);
    }

    @AfterClass(alwaysRun = true)
    public void cleanUpCreatedBookings() {
        String token = AuthClient.adminToken();
        Integer id;
        while ((id = createdIds.poll()) != null) {
            bookings.deleteBooking(id, token); // best effort; already-deleted ids are fine
        }
    }
}
