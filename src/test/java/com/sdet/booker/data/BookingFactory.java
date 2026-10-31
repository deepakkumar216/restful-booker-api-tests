package com.sdet.booker.data;

import com.sdet.booker.models.Booking;
import com.sdet.booker.models.BookingDates;
import net.datafaker.Faker;

import java.time.LocalDate;

/** Builds valid, randomised bookings so tests don't collide when they run in parallel. */
public final class BookingFactory {

    private static final ThreadLocal<Faker> FAKER = ThreadLocal.withInitial(Faker::new);

    private BookingFactory() {
    }

    public static Booking validBooking() {
        Faker f = FAKER.get();
        LocalDate checkin = LocalDate.now().plusDays(f.number().numberBetween(1, 60));
        LocalDate checkout = checkin.plusDays(f.number().numberBetween(1, 14));
        return Booking.builder()
                .firstname(f.name().firstName())
                .lastname(f.name().lastName())
                .totalprice(f.number().numberBetween(50, 5000))
                .depositpaid(f.bool().bool())
                .bookingdates(new BookingDates(checkin.toString(), checkout.toString()))
                .additionalneeds(f.options().option("Breakfast", "Late checkout", "Airport transfer", "Extra bed"))
                .build();
    }
}
