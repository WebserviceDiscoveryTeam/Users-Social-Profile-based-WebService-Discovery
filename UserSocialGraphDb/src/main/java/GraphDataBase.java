/**
 * Created by JINESH on 4/1/2017.
 */


import org.neo4j.driver.v1.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.neo4j.driver.v1.Values.parameters;


/**
 *
 * @author JINESH
 */
public class GraphDataBase {


    static Set<String> category_set;
    static String query;
    static HashMap resultDict;
    static ArrayList<String> user;
    static String currentUser;
    static Boolean allFOAFflag = false;


    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        GraphDataBase.currentUser = currentUser;
    }



    HashMap getResultDict() {
        return resultDict;
    }


    void setResultDict(HashMap resultDict) {
        GraphDataBase.resultDict = resultDict;
    }

    ArrayList<String> getUserList(){
        return user;
    }


    String getQuery() {
        return query;
    }

    void setQuery(String query) {
        GraphDataBase.query = query;
    }

    Set<String> getCategory_set() {
        return category_set;
    }

    void setCategory_set(Set<String> category_set) {
        this.category_set = category_set;
    }

    void create_user_node(String user_name, int age, String hobby){

        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "sa"));
        Session session = driver.session();

        session.run( "CREATE (a:UserSocialProfile {name: {name}, hobby: {hobby}, age:{age}})",
                parameters( "name", user_name, "hobby", hobby, "age", age));

        session.close();
        driver.close();
    }


    ArrayList<ArrayList<String>> readCsv(String inputFilename){

        ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
        try {
            BufferedReader buf = new BufferedReader(new FileReader(inputFilename));
            String line = "";
            String splitBy = ",";
            buf.readLine();
            while ((line = buf.readLine()) != null) {
                ArrayList<String> wsdlDocs = new ArrayList<String>();
                // use comma as separator
                String[] wsdl = line.split(splitBy);
                for(int i = 0; i < wsdl.length; i++){
                    wsdlDocs.add(wsdl[i]);
                }
//                System.out.println(wsdlDocs);
                list.add(wsdlDocs);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;

    }



    void create_WebService_node(String webServiceName, String description, String endpoint, String category){

        Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "sa" ) );
        Session session = driver.session();

        session.run( "CREATE (a:WebService {name: {name}, description: {description}, endpoint:{endpoint}, category:{category}})",
                parameters( "name", webServiceName, "description", description, "endpoint", endpoint,"category",category));

        session.close();
        driver.close();

    }

    void delete_label(String label){
        Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "sa" ) );
        Session session = driver.session();

        // detach keyword is responsible to remove all
        session.run( "MATCH (n:" + label + ") detach delete n"); // Remove node and all its relationship
        session.close();
        driver.close();

    }


    void set_userTouser_relationship(String node1_name, String node2_name, String type, int count){

        Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "sa" ) );
        Session session = driver.session();

        session.run( "match (a:UserSocialProfile {name:{node1_name}}),(b:UserSocialProfile {name: {node2_name}}) "
                        + "merge (a)-[c:" + type + "{mutual_friends:{mutual_friends_count}}]->(b)",
                parameters( "node1_name", node1_name, "node2_name", node2_name, "mutual_friends_count",count) );


        session.close();
        driver.close();

    }


    void set_userToWebservice_relationship(String node1_name, String node2_name, String type, int count){

        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "sa"));
        Session session = driver.session();

        session.run( "match (a:UserSocialProfile {name:{node1_name}}),(b:WebService {name: {node2_name}}) "
                        + "merge (a)-[c:" + type + "{UsedCount:{count}}]->(b)",
                parameters( "node1_name", node1_name, "node2_name", node2_name, "count",count) );


        session.close();
        driver.close();

    }


    ArrayList getCloseFriendList(String userName, String label){

        ArrayList friendList = new ArrayList<String>();

        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "sa"));
        Session session = driver.session();

        StatementResult result;
        result = session.run( "match (a:" + label + " {name:{userName}})-[rel:knows]->(b:"
                        + label + ") where rel.mutual_friends > 20  return b.name as name order by rel.mutual_friends desc",
                parameters( "userName", userName));

        while ( result.hasNext() )
        {
            Record record = result.next();
            friendList.add(record.get("name").asString());
        }

        session.close();
        driver.close();
        return friendList;

    }


    ArrayList getAllFriends(String userName, String label){

        ArrayList<String> friendList = new ArrayList<String>();

        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "sa"));
        Session session = driver.session();

        StatementResult result;
        result = session.run( "match (a:" + label + " {name:{userName}})-[rel:knows]->(b:"
                        + label + ") return b.name as name",
                parameters( "userName", userName));


        while ( result.hasNext() )
        {
            Record record = result.next();
            friendList.add(record.get("name").asString());
        }

        session.close();
        driver.close();

        return friendList;
    }


    HashMap getUserUsedWebService (String userName, String query, HashMap dict){

        ArrayList list = new ArrayList();

        Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "sa" ) );
        Session session = driver.session();

        StatementResult result;
        result = session.run( "match (a:UserSocialProfile{name:{userName}})-[rel:uses]->(b:WebService"
                        + ") where b.category = \"" + query + "\" return b.name as name, b.description as description, b.endpoint as endpoint",
                parameters( "userName", userName));

        while ( result.hasNext() )
        {
            Record record = result.next();
            if (dict.containsKey(record.get("name").asString())){

                ArrayList temp = (ArrayList) dict.get(record.get("name").asString());
                String name = temp.get(0).toString() +"-"+ userName;
                temp.set(0, name);
                dict.put(record.get("name").asString(), temp);

            }else{

                ArrayList propertyList = new ArrayList();
                propertyList.add(userName);
                propertyList.add(query);
                propertyList.add(record.get("description").asString());
                propertyList.add(record.get("endpoint").asString());

                dict.put(record.get("name").asString(), propertyList);

            }
        }

        session.close();
        driver.close();

        return dict;
    }


    HashMap getMatchedWebServiceOfCloseFriend(ArrayList friendList, String query){

        HashMap dict = new LinkedHashMap();
        for (Iterator it = friendList.iterator(); it.hasNext();) {
            String name = it.next().toString();
            dict  = getUserUsedWebService(name, query, dict);
        }
        return dict;

    }


    HashMap<String, ArrayList<String>> uddiSearch(String userName, String keyword){
        ArrayList<ArrayList<String>> webservice_list= readCsv("webServices_v2.csv");
        HashMap<String, ArrayList<String>> result = new HashMap();
        int counter = 0;
        Iterator it = webservice_list.iterator();
        while (it.hasNext()){

            ArrayList<String> eachWS = (ArrayList<String>) it.next();
            String webservice = eachWS.get(0).toString().toLowerCase();
            String description = eachWS.get(1).toString();
            String link = eachWS.get(2).toString();
            String category = eachWS.get(3).toString().toLowerCase();
            eachWS.remove(0);
            if (webservice.contains(keyword.toLowerCase())){
                result.put(webservice,eachWS);
            }
        }
        System.out.println(result);
        return result;

    }

    void startAlgo(String userName){

        ArrayList closeFriendsList = getCloseFriendList(userName, "UserSocialProfile");
        ArrayList allFriendList = getAllFriends(userName,"UserSocialProfile");

        ArrayList ownList = new ArrayList();
        ownList.add(userName);

        HashMap pastInvocationList = getMatchedWebServiceOfCloseFriend(ownList, getQuery());
        if (pastInvocationList.isEmpty()){
            System.out.println("\nYou Have Not Invoked This Web Service");
            HashMap matchedWebServiceOfCloseFriend = new HashMap();
            if (allFOAFflag){
                matchedWebServiceOfCloseFriend = getMatchedWebServiceOfCloseFriend(allFriendList, getQuery());
            }else {
                matchedWebServiceOfCloseFriend = getMatchedWebServiceOfCloseFriend(closeFriendsList, getQuery());
            }
            if(matchedWebServiceOfCloseFriend.isEmpty()){
                System.out.println("\nNone of your friends have used any web service in this category");
                System.out.println("Your Best Friends extracted are:");
                System.out.println(closeFriendsList);
            }else {
                setResultDict(matchedWebServiceOfCloseFriend);
            }

        }else{
            HashMap matchedWebServiceOfCloseFriend = new HashMap();
            if (allFOAFflag){
                matchedWebServiceOfCloseFriend = getMatchedWebServiceOfCloseFriend(allFriendList, getQuery());
            }else{
                matchedWebServiceOfCloseFriend = getMatchedWebServiceOfCloseFriend(closeFriendsList, getQuery());
            }
            System.out.println("Close friend = " + matchedWebServiceOfCloseFriend);
            HashMap result = new HashMap();
            result.putAll(matchedWebServiceOfCloseFriend);
            System.out.println("Result = " + result);

            Iterator it = pastInvocationList.entrySet().iterator();

            while (it.hasNext()) {

                Map.Entry pair = (Map.Entry) it.next();
                String key = pair.getKey().toString();
                if (result.containsKey(key)){

                    ArrayList propList1 = (ArrayList) result.get(key);
                    ArrayList propList2 = (ArrayList) pair.getValue();
                    String value = propList1.get(0).toString() + ", " + propList2.get(0).toString();

                    propList1.add(0,value);
                    result.put(key, propList1);
                }else{

                    result.put(key, (ArrayList) pair.getValue());
                }

            }
            setResultDict(result);
        }
    }


    void updateUserPastInvocationList(String webservice, String userName){
        set_userToWebservice_relationship(userName, webservice, "uses", 1);
    }


    void createGraphDB(){

        ArrayList<ArrayList<String>> webservice_list= readCsv("webServices_v2.csv");
        user = new ArrayList<String>();
        Set<String> category_set = new HashSet<String>();

        user.add("Jinesh Dhruv");
        user.add("Bhaumik Doshi");
        user.add("Jay Jobalia");
        user.add("Jigar Shah");
        user.add("Ghoshil Bhatt");
        user.add("Darshil Shah");
        user.add("Dharmit Zaveri");
        user.add("Akash Sawant");
        user.add("Dharmesh Padia");
        user.add("Abhishek Sanghvi");

        delete_label("UserSocialProfile");
        delete_label("WebService");
        create_user_node("Jinesh Dhruv", 24, "badminton,cricket,video games,watch movies");
        create_user_node("Bhaumik Doshi", 24, "chess,table tennis,pool,cricket");
        create_user_node("Jay Jobalia", 24, "video games,watch movies,music");
        create_user_node("Jigar Shah", 25, "dance,table tennis,video games,watch movies");
        create_user_node("Ghoshil Bhatt", 25, "badminton,table tennis,video games,watch movies");
        create_user_node("Darshil Shah", 24, "cricket,video games,watch movies,party");
        create_user_node("Dharmit Zaveri", 24, "carrom,cricket,video games,watch movies");
        create_user_node("Akash Sawant", 25, "chess,badminton,cricket,video games");
        create_user_node("Dharmesh Padia", 27, "cards,party,travel,watch movies");
        create_user_node("Abhishek Sanghvi", 24, "cricket,travel,watch movies,dance");


        // Jinesh Dhruv Network
        set_userTouser_relationship("Jinesh Dhruv", "Bhaumik Doshi", "knows", 60);
        set_userTouser_relationship("Jinesh Dhruv", "Jay Jobalia", "knows", 58);
        set_userTouser_relationship("Jinesh Dhruv", "Jigar Shah", "knows", 50);
        set_userTouser_relationship("Jinesh Dhruv", "Ghoshil Bhatt", "knows", 30);
        set_userTouser_relationship("Jinesh Dhruv", "Darshil Shah", "knows", 20);
        set_userTouser_relationship("Jinesh Dhruv", "Dharmit Zaveri", "knows", 10);
        set_userTouser_relationship("Jinesh Dhruv", "Akash Sawant", "knows", 15);
        set_userTouser_relationship("Jinesh Dhruv", "Dharmesh Padia", "knows", 20);
        set_userTouser_relationship("Jinesh Dhruv", "Abhishek Sanghvi", "knows", 5);

        // Bhaumik Doshi Network
        set_userTouser_relationship("Bhaumik Doshi", "Jinesh Dhruv", "knows", 60);
        set_userTouser_relationship("Bhaumik Doshi", "Jay Jobalia", "knows", 30);
        set_userTouser_relationship("Bhaumik Doshi", "Jigar Shah", "knows", 40);
        set_userTouser_relationship("Bhaumik Doshi", "Ghoshil Bhatt", "knows", 10);
        set_userTouser_relationship("Bhaumik Doshi", "Darshil Shah", "knows", 5);

        // Jay Jobalia Network
        set_userTouser_relationship("Jay Jobalia", "Jinesh Dhruv", "knows", 58);
        set_userTouser_relationship("Jay Jobalia", "Bhaumik Doshi", "knows", 30);
        set_userTouser_relationship("Jay Jobalia", "Jigar Shah", "knows", 32);
        set_userTouser_relationship("Jay Jobalia", "Ghoshil Bhatt", "knows", 10);
        set_userTouser_relationship("Jay Jobalia", "Darshil Shah", "knows", 5);


        // Jigar Shah Network
        set_userTouser_relationship("Jigar Shah", "Jinesh Dhruv", "knows", 50);
        set_userTouser_relationship("Jigar Shah", "Jay Jobalia", "knows", 32);
        set_userTouser_relationship("Jigar Shah", "Bhaumik Doshi", "knows", 40);
        set_userTouser_relationship("Jigar Shah", "Ghoshil Bhatt", "knows", 30);
        set_userTouser_relationship("Jigar Shah", "Dharmesh Padia", "knows", 20);
        set_userTouser_relationship("Jigar Shah", "Abhishek Sanghvi", "knows", 5);


        // Ghoshil Bhatt Network
        set_userTouser_relationship("Ghoshil Bhatt", "Jinesh Dhruv", "knows", 30);
        set_userTouser_relationship("Ghoshil Bhatt", "Jay Jobalia", "knows", 10);
        set_userTouser_relationship("Ghoshil Bhatt", "Bhaumik Doshi", "knows", 10);
        set_userTouser_relationship("Ghoshil Bhatt", "Jigar Shah", "knows", 30);
        set_userTouser_relationship("Ghoshil Bhatt", "Dharmesh Padia", "knows", 40);
        set_userTouser_relationship("Ghoshil Bhatt", "Abhishek Sanghvi", "knows", 5);


        // Dharmesh Padia  Network
        set_userTouser_relationship("Dharmesh Padia", "Jinesh Dhruv", "knows", 20);
        set_userTouser_relationship("Dharmesh Padia", "Jigar Shah", "knows", 20);
        set_userTouser_relationship("Dharmesh Padia", "Ghoshil Bhatt", "knows", 40);


        // Abhishek Sanghvi Network
        set_userTouser_relationship("Abhishek Sanghvi", "Jinesh Dhruv", "knows", 5);
        set_userTouser_relationship("Abhishek Sanghvi", "Jigar Shah", "knows", 5);
        set_userTouser_relationship("Abhishek Sanghvi", "Ghoshil Bhatt", "knows", 5);
        set_userTouser_relationship("Abhishek Sanghvi", "Darshil Shah", "knows", 35);


        // Darshil Shah Network
        set_userTouser_relationship("Darshil Shah", "Jinesh Dhruv", "knows", 20);
        set_userTouser_relationship("Darshil Shah", "Abhishek Sanghvi", "knows", 35);
        set_userTouser_relationship("Darshil Shah", "Jay Jobalia", "knows", 5);

        // Akash Sawant
        set_userTouser_relationship("Akash Sawant", "Jinesh Dhruv", "knows", 10);
        set_userTouser_relationship("Akash Sawant", "Dharmit Zaveri", "knows", 35);

        // Dharmit Zaveri
        set_userTouser_relationship("Dharmit Zaveri", "Jinesh Dhruv", "knows", 15);
        set_userTouser_relationship("Dharmit Zaveri", "Akash Sawant", "knows", 35);


        int counter = 0;
        Iterator it = webservice_list.iterator();
        while (it.hasNext()){

            ArrayList<String> eachWS = (ArrayList<String>) it.next();
            String webservice = eachWS.get(0).toString();
            String description = eachWS.get(1).toString();
            String link = eachWS.get(2).toString();
            String category = eachWS.get(3).toString().toLowerCase();

            if (!category.equals("custom")){
                Random rand = new Random();
                int  user_id = rand.nextInt(9) + 1;
                int used_count = rand.nextInt(9) + 1;

                create_WebService_node(webservice, description, link, category);
                set_userToWebservice_relationship(user.get(user_id), webservice, "uses", used_count);
                category_set.add(category);

                if (counter % 3 == 0){
                    user_id = rand.nextInt(9) + 1;
                    set_userToWebservice_relationship(user.get(user_id), webservice, "uses", used_count);
                }
                counter += 1;
            }
        }

        setCategory_set(category_set);


    }


    void addWebServiceOfUser(String userName, String webservice, String link, String category,int used_count){

        category_set.add(category);
        setCategory_set(category_set);

        create_WebService_node(webservice, "", link, category);
        set_userToWebservice_relationship(userName, webservice, "uses", used_count);
    }


    public void display(HashMap dict){
        Iterator it = dict.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry)it.next();
            String name = pair.getKey().toString();
            System.out.println("\nName of the Web Service: "+ name);
            ArrayList propList = (ArrayList) pair.getValue();
            String description = propList.get(2).toString();
            String link = propList.get(3).toString();
            String user[] = propList.get(0).toString().split("-");
            String friend = "";
            for (int i = 0; i < user.length ; i++) {
                friend += user[i] + ",";
            }

            friend = friend.substring(0, friend.length() - 1);
            if (!description.equals(" ")){
                System.out.println("Description: \n" + description);

            }
            System.out.println("Link : "+ link);
            System.out.println("Used by : " + friend +"\n");
            it.remove(); // avoids a ConcurrentModificationException

        }
    }


    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {

        GUI gui = new GUI();
        GraphDataBase obj = new GraphDataBase();
        obj.createGraphDB();
    }


}
