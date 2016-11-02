#!/bin/bash
javac -cp .:/home/meurisch/DAWardrivingServer/lib/* /home/meurisch/DAWardrivingServer/src/accessPointLocation/DAWAPLocation.java
java -cp .:/home/meurisch/DAWardrivingServer/lib/* accessPointLocation/DAWAPLocation
