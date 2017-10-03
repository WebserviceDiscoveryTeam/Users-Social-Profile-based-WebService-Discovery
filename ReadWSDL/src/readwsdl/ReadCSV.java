/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package readwsdl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nisha Bhanushali
 */
public class ReadCSV {
    String filename;
    BufferedReader buf;
    String splitby = ",";
    /*
    This method reads the csv file 
    */
    public  HashMap<String, ArrayList<String>>  reading(String filename){
        // Storing the documents category wise
        HashMap<String, ArrayList<String>> Documents = new HashMap<>();
        ArrayList<String> wsdlDocs;
        try {
            buf = new BufferedReader(new FileReader(filename));
            
            String line = "";
            while ((line = buf.readLine()) != null) {
                
                wsdlDocs = new ArrayList<>();
                String[] wsdl = line.split(splitby);
                String category = wsdl[0];
                for(int i = 1; i < wsdl.length; i++){
                    wsdlDocs.add(wsdl[i]);
                    
                }
                Documents.put(category,wsdlDocs);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadCSV.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReadCSV.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Documents;
    }
}
