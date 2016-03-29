# Randy-Web-App
### URL: https://randy-app.herokuapp.com/
<br>
This is a web version of our <a href="https://github.com/CS3733-Team6/WPI-Path-Finding-Project">WPI Path Finding Project</a>
It is a light-weighted Java Servlet running on Apache Tomcat web server. 
<br>
<br>
This web application have two functionalities:
<ol>
  <li>Through the website: https://randy-app.herokuapp.com/, user can obtain the shortest path in text by entering a starting location and a destination. </li>
  <li>Through the photo number: <b>(347)836-5454</b>, user can also obtain the path by sending a text message to the number and specifying a starting location and a destination in the message. </li>
</ol>
<br>
- All the available rooms in our database is included in the <a href="https://github.com/KewenGu/Randy-Web-App/blob/master/Rooms.csv">Rooms.csv</a> file.
<br>
- The format to input starting location and destination is "The capitalized initials of the building name" + "-" + "room number", eg. "SH-106", "AK-116", "FL-332".
<br>
- The format to send text messages is "@" + "name of starting location" + "@" + "name of destination", eg. "@AK310@FL307", "@Fountain@FL-308".
<br>
<br>
<br>

Libraries used in this web application:
- Apache Tomcat web server
- Twilio API, for sending and receiving text messages.
- Apache Lucene Library, for the "Do you mean..." feature (auto suggestion).
