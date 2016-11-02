#!/bin/bash
echo "Inside the upload script"
javac -cp .:/home/meurisch/ServerSync/lib/* /home/meurisch/ServerSync/src/tk/WarDriving.java
java -cp .:/home/meurisch/ServerSync/lib/* tk/WarDriving
