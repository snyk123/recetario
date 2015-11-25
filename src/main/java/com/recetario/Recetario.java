/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.recetario;

import com.hp.hpl.jena.rdf.model.Statement;
import com.recetario.inference.AltasCalorias;
import com.recetario.inference.Dietetica;
import com.recetario.inference.IncluyenCarne;
import com.recetario.inference.Inference;
import com.recetario.inference.Ingredientes;
import com.recetario.inference.SubClass;
import com.recetario.inference.SubRecetas;
import com.recetario.search.RecetarioIndexer;
import com.recetario.search.RecetarioSearcher;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author dennis
 */
public class Recetario {
    
    private static ArrayList<Inference> inferenceList;
    private static String text;
    private final static String inputRDF = "recetario.rdf";
    private final static String NS = "http://www.recetario.com/";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ParseException
    {
        
        if (args.length == 0) {
          text =  "Bolognesa"; 
        } else {
            text = args[0];
        }
        initializeInferenceList();
        
        ArrayList<String> clases = toStrings(search());

        RecetarioIndexer indexer =  new RecetarioIndexer("fileDirectory", "indexDirectory");
        indexer.Load();
        RecetarioSearcher searcher  = new RecetarioSearcher("indexDirectory");  
        
        searcher.Open();
        //Search by text
        Map<String,String> textResults  =  searcher.SearchText(text);
        
        System.out.println("==========================================");
        System.out.println("===========Free text result ==============");
                
        
        for(Map.Entry<String,String> texto : textResults.entrySet() )
        {
            System.out.println("Doc Id:  " + texto.getKey());
            System.out.println(texto.getValue());
        }
        
        System.out.println("==========================================");
        System.out.println("=========== Semantic result ==============");
            
        Map<String,String[]> result =  searcher.Search( clases );
        
        searcher.Close();
        
        for(Map.Entry<String, String[]> entry : result.entrySet())
        {
            System.out.println("=========================");
            System.out.println("Clase:  " + entry.getKey());
            
            String[] textos = entry.getValue();
            
            for (int i = 0; i < textos.length ; i++) {
                
                System.out.println("Result :  " + i );
                System.out.println(textos[i]);
                
            }
            
            System.out.println("=========================");
            
        }

    }
    
    public static ArrayList<String> findRelatedClasses(String text2Search) {
        ArrayList<String> result;
        text = text2Search;
        initializeInferenceList();
        result = toStrings(search());
        return result;
    }
    
    private static void initializeInferenceList() {
        inferenceList = new ArrayList<>();
        inferenceList.add(new SubClass(inputRDF, "rules.txt", NS));
        inferenceList.add(new AltasCalorias(inputRDF, "rules.txt", NS));
        inferenceList.add(new IncluyenCarne(inputRDF, "rules.txt", NS));
        inferenceList.add(new Ingredientes(inputRDF, "rules.txt", NS));
        inferenceList.add(new SubRecetas(inputRDF, "rules.txt", NS));
        inferenceList.add(new Dietetica(inputRDF, "rules_diet.txt", NS));
    }
    
    private static ArrayList<Statement> search() {
        ArrayList<Statement> result = new ArrayList<>();
        Iterator<Inference> iterator = inferenceList.iterator();
        while (iterator.hasNext()) {
            try {
                ArrayList<Statement> inferenceResult = iterator.next().search(text);
                result.removeAll(inferenceResult);
                result.addAll(inferenceResult);
            } catch (FileNotFoundException | ClassCastException ex) {
                Logger.getLogger(Recetario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }
    
    private static ArrayList<String> toStrings(ArrayList<Statement> statements) {
        ArrayList<String> result = new ArrayList<>();
        Iterator<Statement> iterator = statements.iterator();
        
        System.out.println("-- Resultado de la búsqueda --");
        while (iterator.hasNext()) {
            Statement stmt = iterator.next();
            String subject = stmt.getSubject().getLocalName();
            String object = stmt.getResource().getLocalName();
            System.out.println(stmt.toString());
            
            if (subject != null) {
                result.remove(subject);
                result.add(subject);
            }
            
            if (object != null) {
                result.remove(object);
                result.add(object);
            }
        }
        return result;
    }
    
}
