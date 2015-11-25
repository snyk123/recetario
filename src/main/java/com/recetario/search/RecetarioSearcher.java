/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.recetario.search;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


/**
 *
 * @author gregory
 */
public class RecetarioSearcher {
    
    private String IndexDirectory;  
    private Directory directory;
    private DirectoryReader directoryReader;
    private StandardAnalyzer analyzer;
    private IndexSearcher searcher;

    public RecetarioSearcher(String IndexDir) throws IOException
    {
        this.IndexDirectory = IndexDir;
        
    }
    
    public void Close() throws IOException
    {
        this.directoryReader.close();
    }
    public void Open() throws IOException
    {
        this.directory = FSDirectory.open(new File(this.IndexDirectory));
        
        this.directoryReader = DirectoryReader.open(directory);
               
        this.searcher = new IndexSearcher(this.directoryReader);
        
        this.analyzer = new StandardAnalyzer();
        
        this.directory.close();
    }
    public String[] Search(String clase) throws ParseException, IOException
    {
        ScoreDoc[] listaDocs;
        QueryParser parser  = new QueryParser("clase", this.analyzer); 
        Query query = parser.parse(clase);
               
        ArrayList<String> al = new ArrayList<>(); 
        
        listaDocs = this.searcher.search(query, 100).scoreDocs;
        
        for(ScoreDoc sdoc:listaDocs)
        {
            Document document = this.searcher.doc(sdoc.doc);
            String text = document.get("texto");
            al.add(text);         
        }
        return al.toArray(new String[al.size()]);
    }   
    public Map<String, String> SearchText(String Text) throws ParseException, IOException
    {
        ScoreDoc[] listaDocs;
        QueryParser parser  = new QueryParser("texto", this.analyzer); 
        Query query = parser.parse(Text);
               
        Map<String, String> result; 
        result = new HashMap<>();
        
        listaDocs = this.searcher.search(query, 100).scoreDocs;
        
        for(ScoreDoc sdoc:listaDocs)
        {
            Document document = this.searcher.doc(sdoc.doc);
            String text = document.get("texto");
            result.put( Integer.toString(sdoc.doc),  text);
        }
        return result;
    }
    public Map<String, String[]> Search(ArrayList<String> clases) throws ParseException, IOException
    {
        
        
        Map<String,String[]> results = new HashMap<>();  
        for(String clase:clases)
        {
            String[] result = this.Search(clase);
            results.put(clase, result);
            
        }
        return results;
    }
    
}


