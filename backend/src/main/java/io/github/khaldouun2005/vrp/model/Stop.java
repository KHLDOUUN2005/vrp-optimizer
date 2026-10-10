package io.github.khaldouun2005.vrp.model;

import java.util.Objects;

public record Stop(int id, String label, double latitude, double longitude){
	    public Stop {
	        Objects.requireNonNull(label, "label must not be null");
	        if (id < 0) throw new IllegalArgumentException("Id should be positif");
	        if (!(latitude >= -90 && latitude <= 90)) {
	            throw new IllegalArgumentException("latitude must be in [-90, 90], got " + latitude);
	        }
	        if (!(longitude >= -180 && longitude <= 180)) {
	            throw new IllegalArgumentException("longitude must be in [-180, 180], got " + longitude);
	        }
	    }
}