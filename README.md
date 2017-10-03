# Users-Social-Profile-based-WebService-Discovery
Created a distributed system for web service discovery based on the user’s social ego-centric networks. Achieved satisfactory web service discovery results by considering the user’s social profile and its past invocation history.

How to run the code:
1.	The “ReadWSDL” folder contains the code which was used to extract web services information from the internet using WSDL links. The output of this code is a csv file which contains web service name, description, endpoint and category. This file will be used by the next step.
2.	Import the “UserSocialGraphDb” folder in Intellij / Eclipse. This file contains the actual implementation code of the project. To run this code, you must install neo4j in your machine with username as “neo4j” and password as “sa”.  Run the GUI3.java file.



Implementation Steps for User’s Social Profile-based Web Services Discovery:
1. Built social relationship filtering and User past invocation history:
- We have successfully created User’s social model using Neo4j (Graph Database).
The social model consists of 10 User nodes and 50 to 60 Web Service nodes (This number will change as we get more web services WSDL files).
- Each User node has “name”, “age” and “hobby” as property where as each Web Service node has “name”, “end-point”,” description”,” link” and “category” as property.
- The social graph consists of 2 types of relationships which are User-To-User(UTU) and User-To-Web Services(UTWS) relationships. In UTU relationship, the user 1 “knows” user 2 (i.e. both the users are friends). Each relationship of “knows” has “mutual_friends” as property. In UTWS relationship, the user “uses” one or more web services (i.e. User node is connected to Web Service node if and only if that user has in the past used this web service). Each relationship of “uses” has “UsedCount” as property (i.e. How many time in the past the user has used this web service).
How user social model was prepared?
We created a synthetic social model as such type of dataset does not exist. Using the paper as reference we created synthetic graph database using neo4j. We created 10 user nodes with UTU relationships. We created 5 categories of Web Services and searched the different web services that comes under this category. After getting WSDL links for different Web Services, we wrote a code which fetches the WSDL document and extracts the required information from the document. Using this information, we created Web services nodes and random relationship “uses” between the web service node and different users. This way we have built our user social model which will be our input. The social graph database was created using neo4j Java drivers.

2. Matching user Query and web services:
- We have successfully implemented the algorithm mentioned in the paper for matching the user query and web services using friend of a friend(FOAF) model in Java.
- The code will take user query and graph database as input. Based on the user query, first the User past invocation history will be checked, if user had used any web services that matches the query then it will be displayed.
- Also, all the close friends of the user are extracted from the graph data base. The close friends are the ones which have maximum number of mutual friends. The code will extract close friends which has mutual friends greater than a threshold value.
- After getting a list of all close friends, all the past web service invocation history of close friends is searched. Only, those web services that matches the user query are extracted and shown to the user.

3. Web service ranking (Use of alpha similarity degree):
- We have successfully implemented this step of ranking the extracted web services from the previous step in the appropriate order.
- The extracted web services will be displayed to the user based on the alpha similarity degree.
- The extracted web services of the closest friends will be ranked based on the number of mutual friends count.
After showing the result to the user, if the user selects and uses any of the recommended Web service then its entry will be made in the graph database (i.e. A relationship link will be created between the user and the newly used web service)
