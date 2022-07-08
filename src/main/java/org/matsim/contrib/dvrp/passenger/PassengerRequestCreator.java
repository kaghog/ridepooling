package org.matsim.contrib.dvrp.passenger;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Route;
import org.matsim.contrib.dvrp.optimizer.Request;

import java.util.List;

/**
 * @author michalm
 */
public interface PassengerRequestCreator {
    /**
     * Thread safety: This method can be called concurrently from multiple QSim worker threads.
     * Prefer stateless implementation, otherwise provide other ways to achieve thread-safety.
     *
     * @param id             request ID
     * @param passengerId    passenger ID
     * @param route          planned route (the required route type depends on the optimizer)
     * @param fromLink       start location
     * @param toLink         end location
     * @param departureTime  requested time of departure
     * @param submissionTime time at which request was submitted
     * @return
     */
    PassengerRequest createRequest(Id<Request> id, Id<Person> passengerId, Route route, Link fromLink, Link toLink,
                                   double departureTime, double submissionTime);

    //@kaghog todo
    default PassengerRequest createRequest(Id<Request> id, Id<Person> passengerId, Route route, Link fromLink, Link toLink,
                                           double departureTime, double submissionTime, List<Id<Person>> reservedPassengerIds) {
        return createRequest(id,passengerId, route, fromLink, toLink, departureTime, submissionTime);
    }
}