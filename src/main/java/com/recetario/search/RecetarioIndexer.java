/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.recetario.search;

import com.hp.hpl.jena.shared.NotFoundException;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
//import jdk.nashorn.internal.objects.NativeArray;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
/**
 *
 * @author gregory
 */
public class RecetarioIndexer {
    final int NUMFILES = 12;
    private String FileDirectory = "fileDirectory";
    private String IndexDirectory = "indexDirectory";
    private Directory Directory;
    //private StandardAnalyzer analizador;
    private StandardAnalyzer Analyzer;
    private Version version;
    private IndexWriterConfig config;
    private IndexWriter Writter;
    
    public RecetarioIndexer(String FileDir, String IndexDir)
    {
        this.FileDirectory  = FileDir; 
        this.IndexDirectory = IndexDir;  
    }
    public void ClearDocuments() throws IOException
    {
        this.Writter.deleteAll();
    }
    public void CreateWriter() throws IOException
    {
        this.Directory = FSDirectory.open(new File(this.IndexDirectory));
        //this.Directory =new RAMDirectory();       
        this.Analyzer = new StandardAnalyzer();        
        this.version = Version.LUCENE_4_10_1;       
        this.config =  new IndexWriterConfig(this.version, this.Analyzer);      
        this.Writter = new IndexWriter(this.Directory, this.config); 
    }
    
    public void CloseWritter() throws IOException
    {
        this.Writter.close(); 
        this.Directory.close();  
    }
    public void Index(String texto, String clase) throws IOException
    {
        Document document = new Document(); 
        Field fieldTexto = new Field("texto", texto, TextField.TYPE_STORED );
        Field fieldClase = new Field("clase",  clase,  TextField.TYPE_STORED); 
        document.add(fieldTexto);
        document.add(fieldClase);
        this.Writter.addDocument(document);  
    }
    private String ReadText(String Path) throws NotFoundException, IOException
    {
        FileInputStream fstream = new FileInputStream(Path); 
        DataInputStream entrada = new DataInputStream(fstream);
        BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada, "UTF8"));
        String strLinea;
        String texto = "";
        while ((strLinea = buffer.readLine()) != null) {
            texto = texto + strLinea + "\n";
        }
        return texto;
    }
    
//    private String[] ReadClases(String Path) throws FileNotFoundException, IOException
//    {
//        
//        ArrayList<String> al = new ArrayList<>();         
//        FileInputStream fstream = new FileInputStream(Path); 
//        DataInputStream entrada = new DataInputStream(fstream);
//        BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada));
//        String strLinea;
//  
//        while ((strLinea = buffer.readLine()) != null) {
//            
//            al.add(strLinea);
//
//        }
//        
//        return al.toArray(new String[al.size()]);
//    }
    
    public void Load() throws IOException
    {
        this.CreateWriter(); 
        this.ClearDocuments(); 
        for (int i=1; i < NUMFILES+1; i++) {
            String texto = ReadText("fileDirectory/texto" + i +  ".txt");
            String clases = ReadText("fileDirectory/clase" + i + ".txt");
          
/*            for(String clase:clases)
            { */
                this.Index(texto, clases);
            /*}*/
        }
        this.CloseWritter();
    }
}
