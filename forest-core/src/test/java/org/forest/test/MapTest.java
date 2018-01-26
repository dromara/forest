package org.forest.test;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.forest.client.MapClient;
import org.forest.config.ForestConfiguration;
import org.forest.model.Coordinate;
import org.forest.model.Location;
import org.forest.model.Result;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author gongjun
 * @since 2016-06-01
 */
/*
public class MapTest extends TestCase {
    private static Log log = LogFactory.getLog(MapTest.class);

    private ForestConfiguration configuration = ForestConfiguration.configuration();
    private MapClient mapClient = configuration.createInstance(MapClient.class);

    public void testLocation() {
        BigDecimal longitude = new BigDecimal("121.04925573429551");
        BigDecimal latitude = new BigDecimal("31.315590522490712");
        Map result = mapClient.getLocation(longitude, latitude);
        assertNotNull(result);
        assertEquals("1", result.get("status"));
    }

    public void testCoordinate() {
        Coordinate coordinate = new Coordinate("121.04925573429551", "31.315590522490712");
        Map result = mapClient.getLocation(coordinate);
        log.info(result);
        assertNotNull(result);
        assertEquals("1", result.get("status"));
    }


    public void testLocationWithJavaObject() {
        Coordinate coordinate = new Coordinate("121.04925573429551", "31.315590522490712");
        try {
            Result<Location> result = mapClient.getLocationWithJavaObject(coordinate);
            log.info(result);
            assertNotNull(result);
            assertEquals(new Integer(1), result.getStatus());
            assertNotNull(result.getData().getCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void testCoordinateProperites() {
        Coordinate coordinate = new Coordinate("121.04925573429551", "31.315590522490712");
        Map result = mapClient.getLocationByCoordinate(coordinate);
        assertNotNull(result);
        assertEquals("1", result.get("status"));
    }



}
*/
