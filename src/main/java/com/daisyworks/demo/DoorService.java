package com.daisyworks.demo;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

/**
 * This is the service class that handles the web browser requests
 */
@Path("/")
public class DoorService {

    private static final Logger LOGGER = Logger.getLogger(DoorService.class);

    public DoorService() {
    }

    /**
     * Fetch the list of ports; returns a JSON document with each {@link Port}
     * object rendered as JSON
     * 
     * @return a JSON document with each {@link Port} object rendered as JSON
     */
    @GET
    @Path("/door/ports")
    @Produces(MediaType.APPLICATION_JSON)
    public PortCollection getPorts() {
        List<Port> ports = new ArrayList<Port>();

        for (String name : RxTx2.getPortNames()) {
            ports.add(new Port(name));
        }

        LOGGER.info(ports);
        return new PortCollection(ports);
    }

    /**
     * Toggle the state of the door on/off
     * 
     * @param portId
     *            the id of the USB port you want to control
     * @param val
     *            the value of the door where <tt>1 => open</tt> and
     *            <tt>0 => closed</tt>
     * @return A string that indicates if it was successful or not as "true" or
     *         "false"
     */
    @GET
    @Path("/door/toggle/{portName}/{val}")
    @Produces(MediaType.TEXT_PLAIN)
    public String toggle(@PathParam("portName") String portName, @PathParam("val") Integer val) {
        LOGGER.info("Toggle USB port " + portName + " =>" + ((val == 1) ? "on" : "off"));
        return RxTx2.send(portName, ((val == 1) ? 'o' : 'c')) ? "true" : "false";
    }

}
