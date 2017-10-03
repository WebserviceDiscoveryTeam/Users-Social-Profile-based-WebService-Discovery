/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package readwsdl;

import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Nisha Bhanushali
 */
public class WsdlReader {
    
    private SAXParserFactory factory;
    private SAXParser saxParser;
    private DefaultHandler handler;
    private static ArrayList<wsdlDoc> docParas;
    private String category;
    wsdlDoc document;
    
    public WsdlReader() throws ParserConfigurationException, SAXException{
        
        factory = SAXParserFactory.newInstance();
        saxParser = factory.newSAXParser();
        docParas = new ArrayList<>();
        handleDocuments();
    }
    
    public static void main(String[] args) throws SAXException, ParserConfigurationException, IOException {
        
        WsdlReader demo = new WsdlReader();
        demo.processWSDL();
        demo.writeToFile();
    }
    /*
    This method reads the Services.csv file which consists of WSDL links.
    */
    private void processWSDL() throws SAXException, IOException{
        
        //Class which reads the csv file
        ReadCSV read = new ReadCSV();
        
        //The data extracted from the wsdl documents
        HashMap<String, ArrayList<String>> wsdlNames = read.reading("Services.csv");
        
        //Traversing the hashmap by the categories of wsdl documents
        for (String wsdlName : wsdlNames.keySet()) {
            setCategory(wsdlName);
            
            //Accessing the list of wsdl documents under one category
            ArrayList<String> wsdlDocs = wsdlNames.get(wsdlName);
            
            //Storing each wsdl documents and parsing each wsdl document
            for(String wsdl : wsdlDocs){
               
                try {
                    sleep(500);
                    document = new wsdlDoc();
                    
                    //Parse each wsdl document
                    parseWSDL(wsdl);
                    docParas.add(document);
                } catch (InterruptedException ex) {
                        Logger.getLogger(WsdlReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    /*
    This parses each document to extract important information from the document 
    */
    private void handleDocuments() {
        
       
        handler = new DefaultHandler(){
        boolean bservice = false;
        
            //Reading the start tags in the wsdl documents
            @Override
            public void startElement(String uri, String localName,String qName,
                    Attributes attributes)  {
  
                    if (qName.equalsIgnoreCase("wsdl:service")) {

                        bservice = true;
                        int attrib_len = attributes.getLength(); 

                        for(int i = 0 ; i < attrib_len ; i++){
                            String name = attributes.getQName(i);
                            String value = attributes.getValue(i);
                            document.setName(value);
                            document.setCategory(getCategory());


                        }
                    }
                            
                    if(qName.equalsIgnoreCase("soap:address")) {

                        int attrib_len = attributes.getLength();

                        for( int i = 0 ; i < attrib_len ; i++ ) {
                            String name = attributes.getQName(i);
                            String value = attributes.getValue(i);
                            document.setEndPoint(value);

                        }
                    }
            }
     };
     
    }
    /*
    Parsing each wsdl document using SAX parser
    */
    private void parseWSDL(String name) throws SAXException, IOException {
                
               saxParser.parse(name, handler);

    }
    /*
    Returns all the wsdl documents as an array of objects
    */
    public ArrayList<wsdlDoc> getwsdlDetails(){

        return docParas;
    }
    /*
    Writes the wsdl content to the csv file for further processing
    */
    public void writeToFile(){
     
            
            ArrayList<wsdlDoc> document = getwsdlDetails();
            
            //Header of the csv file
            String fileHeader = "Web Service Name,Description,Endpoint,Category";
            
            // Delimiter for the csv file
            String delimiter = ",";
            
            //File seperator for the csv file
            String fileSeperator = "\n";
            FileWriter filewriter=null;
            
            try {
                // Appending header to the csv file
                filewriter = new FileWriter("Results.csv");
                filewriter.append(fileHeader);
                filewriter.append(fileSeperator);
                
            //Traversing each document 
            for(wsdlDoc doc: document){
                // Appending the name of the wsdl document
                filewriter.append(doc.getName());
                // Appending delimiter
                filewriter.append(delimiter);
                // Appending description of the wsdl document
                filewriter.append(" ");
                // Appending delimiter
                filewriter.append(delimiter);
                // Appending the endpoint of the wsdl document
                filewriter.append(doc.getEndPoint());
                // Appending delimiter
                filewriter.append(delimiter);
                // Appending the category of the wsdl document
                filewriter.append(doc.getCategory());
                // Appending the file seperator to the wsdl document
                filewriter.append(fileSeperator);
            }
            } catch (IOException ex) {
                Logger.getLogger(WsdlReader.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
                try{
                    filewriter.flush();
                    filewriter.close();
                    
                }catch(IOException e){
                }
            }
    }
    /*
    Set the category of the document
    */
    private void setCategory(String name){
        this.category = name;
    }
    /*
    Get the category of the document
    */
    private String getCategory(){
        return this.category;
    }
}
