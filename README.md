DAWardriving Project 

Developed by CodeChef

Supervised by Christian Meurisch, Telekooperation Dept.

The project has 2 different subparts.

1. Client

Technologies used : Android(Java)

The DAWardriving app requires GPS to be on and set to a high accuracy mode. If not, the app redirects to the Location Settings.  Once the app is launched, username should be entered in the input field. (Currently, only the names of CodeChef members can be given).The app has 3 operations: Start, Clear and Store. Start button starts scanning the Wifi accesspoints on the move. Clear button clears the currently scanned points from the application. Store pushes all the scanned Wifi access points to a database present on the server where the algorithm computation takes place.

2. Server

Technologies used : MySql,PHP,Java

The data from the app are collected and stored in the database. The centroid algorithm with Levenberg Marquardt optimizer fetches the data from the database, makes appropriate calculations and updates the location of access points with an approximate error rate upto 100 meters to the database.

Web Visualization - A simple web page shows all the calculated location of the access points on the map. The green markers shows the open access points and the red markers show the protected access points. 

TK server sync - The calculated location of access points stored in the local database on VM are pushed into TK server.

The automated scheduling of server sync is done using Linux's crontab scheduler.