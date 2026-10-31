package com.sdet.booker.data;

import com.sdet.booker.models.Booking;
import com.sdet.booker.models.BookingDates;
import org.testng.annotations.DataProvider;

import java.util.HashMap;
import java.util.Map;

public final class BookingDataProviders {

    private BookingDataProviders() {
    }

    /** Valid edge-case bookings the API should accept. */
    @DataProvider(name = "validBookings", parallel = true)
    public static Object[][] validBookings() {
        Booking base = BookingFactory.validBooking();
        return new Object[][]{
                {"standard booking", base},
                {"zero total price", base.toBuilder().totalprice(0).build()},
                {"large total price", base.toBuilder().totalprice(999_999).build()},
                {"deposit not paid", base.toBuilder().depositpaid(false).build()},
                {"unicode names", base.toBuilder().firstname("Zoë").lastname("Ångström").build()},
                {"hyphenated surname", base.toBuilder().lastname("Smith-Jones").build()},
                {"same-day checkout", base.toBuilder()
                        .bookingdates(new BookingDates("2030-01-10", "2030-01-10")).build()},
        };
    }

    /**
     * Payloads with a required field removed. Restful-Booker answers these with 500
     * (a 400 would be correct). The test pins the current behaviour and records the defect.
     */
    @DataProvider(name = "missingRequiredField")
    public static Object[][] missingRequiredField() {
        String[] required = {"firstname", "lastname", "totalprice", "depositpaid", "bookingdates"};
        Object[][] data = new Object[required.length][];
        for (int i = 0; i < required.length; i++) {
            Map<String, Object> body = asMap(BookingFactory.validBooking());
            body.remove(required[i]);
            data[i] = new Object[]{required[i], body};
        }
        return data;
    }

    /** Booking IDs that should never exist. */
    @DataProvider(name = "nonExistentIds")
    public static Object[][] nonExistentIds() {
        return new Object[][]{{0}, {-1}, {Integer.MAX_VALUE}};
    }

    /** Credentials that must not yield a token. */
    @DataProvider(name = "badCredentials")
    public static Object[][] badCredentials() {
        return new Object[][]{
                {"wrong password", "admin", "wrong"},
                {"wrong username", "nobody", "password123"},
                {"empty values", "", ""},
                {"sql injection", "admin' OR '1'='1", "x' OR '1'='1"},
        };
    }

    private static Map<String, Object> asMap(Booking b) {
        Map<String, Object> m = new HashMap<>();
        m.put("firstname", b.firstname());
        m.put("lastname", b.lastname());
        m.put("totalprice", b.totalprice());
        m.put("depositpaid", b.depositpaid());
        m.put("bookingdates", Map.of(
                "checkin", b.bookingdates().checkin(),
                "checkout", b.bookingdates().checkout()));
        m.put("additionalneeds", b.additionalneeds());
        return m;
    }
}
