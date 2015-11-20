/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.recetario.inference;

import com.recetario.lib.Ontology;
import java.util.ArrayList;
import com.hp.hpl.jena.rdf.model.Statement;
import java.io.FileNotFoundException;

/**
 *
 * @author dennis
 */
public abstract class Inference {
    
    protected final String inputRDF;
    protected final String rulesFileName;
    protected final String NS;

    protected Ontology ontology;
    
    public Inference(String inputRDF, String rulesFileName, String NS) {
        this.inputRDF = inputRDF;
        this.rulesFileName = rulesFileName;
        this.NS = NS;
        
        this.ontology = new Ontology(NS);
        ontology.loadModel(this.inputRDF);
        ontology.setInferenceRules(this.rulesFileName);
    }
    
    public abstract ArrayList<Statement> search() throws FileNotFoundException, ClassCastException;
    
}
