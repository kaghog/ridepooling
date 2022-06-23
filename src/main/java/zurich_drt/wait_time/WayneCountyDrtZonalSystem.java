package zurich_drt.wait_time;


/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2017 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.drt.analysis.zonal.DrtGridUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.collections.QuadTree;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author kaghog created on 13.04.2021
 * @project wayne_county
 */

public class WayneCountyDrtZonalSystem {

    private final Map<Id<Link>, String> link2zone = new LinkedHashMap<>();
    private final Network network;
    private final Map<String, PreparedGeometry> zones;
    private final QuadTree<Entry<String, PreparedGeometry>> quadtree;

    public WayneCountyDrtZonalSystem(Network network, double cellSize) {
        this.network = network;
        zones = DrtGridUtils.createGridFromNetwork(network, cellSize);

        // build a quadtree for the zones in the network with their centroid
        double[] bounds = NetworkUtils.getBoundingBox(network.getNodes().values());
        this.quadtree = new QuadTree<>(bounds[0], bounds[1], bounds[2], bounds[3]);
        for (Entry<String, PreparedGeometry> zone : zones.entrySet()) {


            double x = zone.getValue().getGeometry().getCentroid().getX();;
            double y = zone.getValue().getGeometry().getCentroid().getY();;

            // if(x < minX || x > maxX || y > maxY || y < minY)
            if (!(x < bounds[0] || y < bounds[1] || x > bounds[2] || y > bounds[3])) {
                quadtree.put(x, y, zone);
            }
        }

    }

    public WayneCountyDrtZonalSystem(Network network, Map<String, PreparedGeometry> zones) {
        this.network = network;
        this.zones = zones;
        double[] bounds = NetworkUtils.getBoundingBox(network.getNodes().values());
        this.quadtree = new QuadTree<>(bounds[0], bounds[1], bounds[2], bounds[3]);
        for (Entry<String, PreparedGeometry> zone : zones.entrySet()) {

            quadtree.put(zone.getValue().getGeometry().getCentroid().getX(), zone.getValue().getGeometry().getCentroid().getY(), zone);
        }
    }

    public Geometry getZone(String zone) {

        return (Geometry) zones.get(zone);
    }

    public String getZoneForLinkId(Id<Link> linkId) {
        if (this.link2zone.containsKey(linkId)) {
            return link2zone.get(linkId);
        }

        // get the nearest zone centroid to this linkId
        Coord coord = network.getLinks().get(linkId).getCoord();
        String zoneId = quadtree.getClosest(coord.getX(), coord.getY()).getKey();
        if (zoneId == null) {
            link2zone.put(linkId, null);
            return null;
        }
        link2zone.put(linkId, zoneId);
        return zoneId;

    }

}

