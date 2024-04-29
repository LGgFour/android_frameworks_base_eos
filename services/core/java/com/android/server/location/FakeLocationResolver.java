/*
 * Copyright (C) 2023 MURENA SAS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.android.server.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationResult;
import android.location.util.identity.CallerIdentity;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FakeLocationResolver {
    private static final String FAKE_LOCATIONS_URI = "content://foundation.e.advancedprivacy.fakelocations";

    private static final String PARAM_UID = "uid";
    private static final String PARAM_LATITUDE = "latitude";
    private static final String PARAM_LONGITUDE = "longitude";

    public static LocationResult fakeLocations(Context context, LocationResult baseLocations, CallerIdentity identity) {
        int uid = identity.getUid();
        String packageName = identity.getPackageName();

        if (baseLocations == null || context == null || packageName == null || uid < 0) {
            Log.w("AP-FakeLocation", "FakeLocationResolver::fakeLocations invalid parameters");
            return baseLocations;
        }
        FakeLocation latLon = getFakeLocation(context, packageName, uid);
        if (latLon == null) return baseLocations;

        Log.d("AP-FakeLocation", "FakeLocationResolver::fakeLocation faked location for " + packageName);
        return LocationResult.wrap(overrideLatLons( baseLocations.asList(), latLon));
    }

    public static Location fakeLocation(Context context, Location baseLocation, CallerIdentity identity) {
        int uid = identity.getUid();
        String packageName = identity.getPackageName();

        if (baseLocation == null || context == null || packageName == null || uid < 0) {
            Log.w("AP-FakeLocation", "FakeLocationResolver::fakeLocation invalid parameters");
            return baseLocation;
        }
        FakeLocation latLon = getFakeLocation(context, packageName, uid);
        if (latLon == null) return baseLocation;

        Log.d("AP-FakeLocation", "FakeLocationResolver::fakeLocation faked location for " + packageName);
        return overrideLatLons( baseLocation, latLon);
    }

    public static boolean hasFakeLocation(Context context, CallerIdentity identity) {
        int uid = identity.getUid();
        String packageName = identity.getPackageName();

        if (context == null || packageName == null || uid < 0) {
            Log.w("AP-FakeLocation", "FakeLocationResolver::fakeLocation invalid parameters");
            return false;
        }
        FakeLocation latLon = getFakeLocation(context, packageName, uid);
        if (latLon == null) return false;

        Log.d("AP-FakeLocation", "FakeLocationResolver::fakeLocation faked location for " + packageName);
        return true;

    }

    private static class FakeLocation {
        Double latitude;
        Double longitude;

        public FakeLocation(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    private static FakeLocation getFakeLocation(Context context, String packageName, int uid) {
        try {
            Bundle extra = new Bundle();
            extra.putInt(PARAM_UID, uid);
            Bundle result = context.getContentResolver().call(
                    Uri.parse(FAKE_LOCATIONS_URI),
                    "",
                    packageName,
                    extra
            );

            if (result != null && result.containsKey(PARAM_LATITUDE) && result.containsKey(PARAM_LONGITUDE)) {
                return new FakeLocation(result.getDouble(PARAM_LATITUDE), result.getDouble(PARAM_LONGITUDE));
            }
        } catch(Exception e) {
            Log.w("AP-FakeLocation", "Can't getFakeLocation", e);
        }
        return null;
    }

    private static List<Location> overrideLatLons(List<Location> baseLocations, FakeLocation latLon) {
        ArrayList<Location> fakedLocations = new ArrayList<Location>(baseLocations.size());
        for (Location location: baseLocations) {
            Location fakedLocation = overrideLatLons(location, latLon);

            fakedLocations.add(fakedLocation);
        }
        return fakedLocations;
    }

    private static Location overrideLatLons(Location baseLocation, FakeLocation latLon) {
        Location fakedLocation = new Location(baseLocation);
        fakedLocation.setLatitude(latLon.latitude);
        fakedLocation.setLongitude(latLon.longitude);
        fakedLocation.setAltitude(3.0);
        fakedLocation.setSpeed(0.01f);

        return fakedLocation;
    }
}
