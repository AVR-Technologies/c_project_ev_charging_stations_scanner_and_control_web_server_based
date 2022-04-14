package com.example.ev.stations.evchargingstation;

import android.location.Location;

public class Station {
    String name; //or address
    Double latitude;
    Double longitude;
    Integer plugsAvailable;
    Float distance;

    Station(String _name, Double _latitude, Double _longitude, Integer _plugsAvailable, Float _distance) {
        name = _name;
        latitude = _latitude;
        longitude = _longitude;
        plugsAvailable = _plugsAvailable;
        distance = _distance;
    }

    void updateDistance(Location _from_location) {
        distance = _from_location.distanceTo(location()) / 1000; // distance in meters convert to km
    }

    Location location() {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }
}
