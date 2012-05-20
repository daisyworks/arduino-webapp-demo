/*
 * Copyright © 2012 DaisyWorks.com
 */
package com.daisyworks.demo;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 * Dealing with UnsatisfiedLinkError: Native Library already loaded in another
 * classloader<br/>
 * <br/>
 * http://jf.blogs.teximus.com/2010/10/jetty-maven-plug
 * -in-vs-native-libraries.html <br/>
 * <br/>
 * Inputs
 * <ul>
 * <li>CD</li>
 * <li>CTS</li>
 * <li>DSR</li>
 * <li>RI</li>
 * </ul>
 * 
 * Outputs
 * <ul>
 * <li>DTR</li>
 * <li>RTS</li>
 * </ul>
 * 
 * @author Troy T. Collinsworth
 */
public class RxTx2 {

    private static final Logger LOGGER = Logger.getLogger(DoorService.class);

    private final static String ownerKey = "RxTx-" + Long.toString(System.currentTimeMillis());

    private final static Map<String, SerialPort> openPorts = new HashMap<String, SerialPort>();

    static {
        // bug fix https://bugs.launchpad.net/ubuntu/+source/rxtx/+bug/367833
        // without this, it only sees the USB, with only /dev/ttyACM0, it only
        // sees the ACM, have to list all candidates or it doesn't find them all
        System.setProperty("gnu.io.rxtx.SerialPorts",
                "/dev/ttyACM0:/dev/ttyACM1:/dev/ttyACM2:/dev/ttyUSB0:/dev/ttyUSB1:/dev/ttyUSB2:/dev/ttyUSB3:/dev/ttyUSB4:/dev/ttyUSB5");
    }

    public synchronized static List<String> getPortNames() {
        Map<String, CommPortIdentifier> portMap = getPortMap();

        List<String> ports = new ArrayList<String>(portMap.size());
        for (String p : portMap.keySet()) {
            String s = p.replaceAll("/", ":");
            s = s.replaceAll("\\\\", ":");
            s = s.replace("tty", "");
            ports.add(s.substring(s.lastIndexOf(':') + 1));
        }
        for (String p : openPorts.keySet()) {
            ports.add(p);
        }
        return ports;
    }

    private static Map<String, CommPortIdentifier> getPortMap() {
        Map<String, CommPortIdentifier> portMap = new HashMap<String, CommPortIdentifier>();

        @SuppressWarnings("unchecked")
        List<CommPortIdentifier> ids = Collections.list(CommPortIdentifier.getPortIdentifiers());

        for (CommPortIdentifier id : ids) {
            if (id.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                portMap.put(id.getName(), id);
                continue;
            }
        }

        return portMap;
    }

    public static void main(String[] args) throws PortInUseException, IOException, InterruptedException,
            UnsupportedCommOperationException {
        List<String> names = getPortNames();
        if (names.size() < 1) {
            return;
        }
        String name = names.get(0);
        send(name, 'o');
        send(name, 'c');
    }

    private static SerialPort getPort(String name) {
        SerialPort port = openPorts.get(name);
        if (port != null) {
            return port;
        }

        Map<String, CommPortIdentifier> portMap = getPortMap();
        CommPortIdentifier id = null;
        for (String portPath : portMap.keySet()) {
            if (portPath.contains(name)) {
                id = portMap.get(portPath);
                break;
            }
        }

        if (id == null) {
            throw new RuntimeException(String.format("Port %s not found", name));
        }

        try {
            port = (SerialPort) id.open(ownerKey, 50);
            port.setSerialPortParams(57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            openPorts.put(name, port);
            Thread.sleep(1000);
        } catch (PortInUseException e) {
            throw new RuntimeException(String.format("Port %s in use", name), e);
        } catch (UnsupportedCommOperationException e) {
            throw new RuntimeException(String.format("Port %s invalid configuration", e));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return port;
    }

    /**
     * @param portName
     * @param string
     * @throws UnsupportedCommOperationException
     */
    public synchronized static boolean send(String portName, char cmd) {
        LOGGER.info("port=" + portName + ",cmd=" + cmd);

        SerialPort p = null;

        try {
            p = getPort(portName);

            OutputStream os = null;
            try {
                os = p.getOutputStream();
                os.write(cmd);
                os.flush();
                Thread.sleep(1000);
            } catch (IOException e) {
                LOGGER.info("Error writing output, port " + portName, e);
                openPorts.remove(portName);
                p.close();
                return false;
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        // ignore
                        LOGGER.error("Error closing outputStream, port " + portName, e);
                    }
                }
            }

            return true;
        } catch (RuntimeException e) {
            LOGGER.info(e);
            openPorts.remove(portName);
            p.close();
            return false;
        } catch (InterruptedException e) {
            LOGGER.info(e);
            openPorts.remove(portName);
            p.close();
            return false;
        } finally {
            // if (p != null) {
            // p.close();
            // }
        }
    }
}
