/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.common.geo;

import org.elasticsearch.geo.GeometryTestUtils;
import org.elasticsearch.geometry.Rectangle;
import org.elasticsearch.search.aggregations.bucket.geogrid.GeoTileUtils;
import org.elasticsearch.test.ESTestCase;
import org.hamcrest.Matchers;

import static org.elasticsearch.common.geo.SphericalMercatorUtils.MERCATOR_BOUNDS;
import static org.elasticsearch.common.geo.SphericalMercatorUtils.latToSphericalMercator;
import static org.elasticsearch.common.geo.SphericalMercatorUtils.lonToSphericalMercator;
import static org.elasticsearch.common.geo.SphericalMercatorUtils.recToSphericalMercator;

public class SphericalMercatorUtilTests extends ESTestCase {

    public void testLon() {
        assertThat(lonToSphericalMercator(180.0), Matchers.equalTo(MERCATOR_BOUNDS));
        assertThat(lonToSphericalMercator(-180.0), Matchers.equalTo(-MERCATOR_BOUNDS));
        assertThat(lonToSphericalMercator(0.0), Matchers.equalTo(0.0));
        final double lon = lonToSphericalMercator(GeometryTestUtils.randomLon());
        assertThat(lon, Matchers.greaterThanOrEqualTo(-MERCATOR_BOUNDS));
        assertThat(lon, Matchers.lessThanOrEqualTo(MERCATOR_BOUNDS));
    }

    public void testLat() {
        assertThat(latToSphericalMercator(GeoTileUtils.LATITUDE_MASK), Matchers.closeTo(MERCATOR_BOUNDS, 1e-7));
        assertThat(latToSphericalMercator(-GeoTileUtils.LATITUDE_MASK), Matchers.closeTo(-MERCATOR_BOUNDS, 1e-7));
        assertThat(latToSphericalMercator(0.0), Matchers.closeTo(0, 1e-7));
        final double lat = latToSphericalMercator(
            randomValueOtherThanMany(l -> l >= GeoTileUtils.LATITUDE_MASK || l <= -GeoTileUtils.LATITUDE_MASK, GeometryTestUtils::randomLat)
        );
        assertThat(lat, Matchers.greaterThanOrEqualTo(-MERCATOR_BOUNDS));
        assertThat(lat, Matchers.lessThanOrEqualTo(MERCATOR_BOUNDS));
    }

    public void testRectangle() {
        Rectangle rect = GeometryTestUtils.randomRectangle();
        Rectangle mercatorRect = recToSphericalMercator(rect);
        assertThat(mercatorRect.getMinX(), Matchers.equalTo(lonToSphericalMercator(rect.getMinX())));
        assertThat(mercatorRect.getMaxX(), Matchers.equalTo(lonToSphericalMercator(rect.getMaxX())));
        assertThat(mercatorRect.getMinY(), Matchers.equalTo(latToSphericalMercator(rect.getMinY())));
        assertThat(mercatorRect.getMaxY(), Matchers.equalTo(latToSphericalMercator(rect.getMaxY())));
    }
}
