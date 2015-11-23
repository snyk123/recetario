/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.recetario.search;

import java.io.File;
import java.io.IOException; 
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
/**
 *
 * @author gregory
 */
public class RecetarioIndexer {
    private final String nmCarpetaIndice = "T://Cursos//Ingeniería y Gestión del Conocimiento 2015-2//work//indiceT";
    private Directory Directory;
    //private StandardAnalyzer analizador;
    private SpanishAnalyzer Analyzer;
    private Version version;
    private IndexWriterConfig config;
    private IndexWriter Writter;
    
    public void CreateWriter() throws IOException
    {
        //this.directorio = FSDirectory.open(new File(this.nmCarpetaIndice));
        this.Directory =new RAMDirectory();  
        
        this.Analyzer = new SpanishAnalyzer(); 
        
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
        
        this.Writter.addDocument(document);  
    }
}
