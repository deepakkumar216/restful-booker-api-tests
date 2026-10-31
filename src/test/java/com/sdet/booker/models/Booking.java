package com.sdet.booker.models;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Request/response body for a booking. Null fields are left out when serialising,
 * so the same type also works for PATCH (partial update) payloads.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Booking(
        String firstname,
        String lastname,
        Integer totalprice,
        Boolean depositpaid,
        BookingDates bookingdates,
        String additionalneeds) {

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .firstname(firstname).lastname(lastname).totalprice(totalprice)
                .depositpaid(depositpaid).bookingdates(bookingdates).additionalneeds(additionalneeds);
    }

    public static final class Builder {
        private String firstname;
        private String lastname;
        private Integer totalprice;
        private Boolean depositpaid;
        private BookingDates bookingdates;
        private String additionalneeds;

        public Builder firstname(String v) { this.firstname = v; return this; }
        public Builder lastname(String v) { this.lastname = v; return this; }
        public Builder totalprice(Integer v) { this.totalprice = v; return this; }
        public Builder depositpaid(Boolean v) { this.depositpaid = v; return this; }
        public Builder bookingdates(BookingDates v) { this.bookingdates = v; return this; }
        public Builder additionalneeds(String v) { this.additionalneeds = v; return this; }

        public Booking build() {
            return new Booking(firstname, lastname, totalprice, depositpaid, bookingdates, additionalneeds);
        }
    }
}
