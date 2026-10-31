package com.sdet.booker.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingDates(String checkin, String checkout) {
}
