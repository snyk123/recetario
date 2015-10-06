/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.recetario;

import com.recetario.lib.Ontology;
import com.recetario.lib.Validator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 *
 * @author dennis
 */
public class Dietetica {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String inputRDF = "recetario.rdf";
        String rulesFileName = "rules_diet.txt";
        String NS = "http://www.recetario.com/";
        
        Ontology ontology = new Ontology(NS);
        ontology.loadModel(inputRDF);
        ontology.setInferenceRules(rulesFileName);
        
        try {
            if (ontology.validate(new Validator())) {
                Resource object = ontology.getResource("dietetica");
                ontology.query(object, null, (RDFNode) null);
                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
}
