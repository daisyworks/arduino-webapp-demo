arduino-webapp-demo
===================

This is a simple webapp demo that illustrates how you can control any Arduino plugged into a USB port through a webapp (i.e. browser).  We hacked this together _very quickly_ just a demo bootstrap project for people that want to interact with their [Arduino](http://arduino.cc) hardware via a WebApp but weren't sure how to get started.  This project gets you 99% of the way there, and you can then modify to your use case.

You could, for example, connect a relay to your Arduino, plug it into the USB port of your computer, start the webapp, and toggle a light on/off from your browser, for example.

We have tried to keep the code here drop-dead simple.

## Pre-requisites
* [Install Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (if you don't already have it): specifically you need to download and install the JDK if you are going to build this software; you only need the JRE if you just intead to run it.
* [Install Apache Maven](http://maven.apache.org/download.html#Installation) (if you don't already have it): you need this to build the software, and you can also start the webapp using Maven
* **Hardware**: well you **do** need some actual hardware to interact with, right?  We recommend [our own hardware](http://daisyworks.com) but we may be biased :)

#### Verify Java/Maven Installation
You should be able to run the following command and get a similar result:
```
$ mvn -version
Apache Maven 3.0.1 (r1038046; 2010-11-23 05:58:32-0500)
Java version: 1.6.0_23
Java home: c:\Program Files\Java\jdk1.6.0_23\jre
Default locale: en_US, platform encoding: Cp1252
OS name: "windows 7" version: "6.1" arch: "amd64" Family: "windows"
```

...and get something similar (your environment may be different)

### Getting Started
You can either clone the repository using a git client, or you can [download the latest zip bundle here](https://github.com/daisyworks/arduino-webapp-demo/zipball/master) -- whichever is easiest for you.

#### Clone the repository
If you need to install git, see here: 
* Windows: http://help.github.com/win-set-up-git/
* Linux: http://help.github.com/linux-set-up-git/
* Mac: http://help.github.com/mac-set-up-git/

Now run this command:
```
$ git clone git://github.com/daisyworks/arduino-webapp-demo.git
```

Now, change into the directory you just checked out:
```
$ cd arduino-webapp.demo
```

#### Start The WebApp
Now, we can start the webapp.  The next command may take a while to download some `.jar` files, but you only need to do this one time, so grab some coffee, and when it is finished your webapp should be running.

```
$ mvn jetty:run
... whole bunch of output here...
2012-05-16 23:48:44.581::INFO:  Started SocketConnector@0.0.0.0:8080
[INFO] Started Jetty Server
[INFO] Starting scanner at interval of 5 seconds.
```

Now, open your web browser to http://localhost:8080 -- works in any modern browser, including tablets, iphones, android, etc. (thanks to [Twitter Bootstrap](http://twitter.github.com/bootstrap/))

<a href="http://imgur.com/eDYCs"><img src="http://i.imgur.com/eDYCs.png" alt="" title="Hosted by imgur.com" /></a>

_Voila!_

#### How To Connect The Hardware To USB 
TODO

#### What If I Want To Change The Code?
The code is very simple.  There is a [Java servlet](http://en.wikipedia.org/wiki/Java_Servlet) running a RESTful web service that the client (i.e. browser) talks to.  The web browser makes request to the Servlet which handles task requests such as `toggle a switch on/off`.  

You'll find the java service code in [the DoorService](https://github.com/daisyworks/arduino-webapp-demo/blob/master/src/main/java/com/daisyworks/demo/DoorService.java) class.  We named it that b/c it is initially for opening / closing a dog kennel door that the [Arduino](http://arduino.cc) is connected to.

The client code is all merged into one file: [index.html](https://github.com/daisyworks/arduino-webapp-demo/blob/master/src/main/webapp/index.html) -- in there you'll find the JavaScript that makes AJAX requests to the service.  When you toggle a button in the browser, it fires the request to the server and then controls the Arduino.  It will help if you know some [jQuery](http://jquery.com), as we use the jQuery JavaScript library to handle the AJAX and web page manipulation.

### Problem?

Feel free to post any questions to the [Issues List](https://github.com/daisyworks/arduino-webapp-demo/issues)

