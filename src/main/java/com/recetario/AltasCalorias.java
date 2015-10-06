/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.recetario;

import com.recetario.lib.Ontology;
import com.recetario.lib.Validator;

/**
 *
 * @author dennis
 */
public class AltasCalorias {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String inputRDF = "recetario.rdf";
        String rulesFileName = "rules.txt";
        String NS = "http://www.recetario.com/";
        
        Ontology ontology = new Ontology(NS);
        
        ontology.loadModel(inputRDF);
        ontology.setInferenceRules(rulesFileName);
        
        try {
            if (ontology.validate(new Validator())) {
                String query = "select DISTINCT ?receta where { " +
                " ?receta rdfs:subClassOf ?class . \n" +
                " ?class owl:onProperty recetario:tiene_ingrediente . \n" +
                " ?class owl:allValuesFrom ?allValues .\n" +
                " ?allValues owl:unionOf ?union . \n" +
                " ?union rdf:rest* [ rdf:first ?ingrediente ] . \n" +
                " ?ingrediente rdfs:subClassOf ?class2 .\n" +
                " ?class2 owl:onProperty recetario:nivelCalorias . \n" +
                " ?class2 owl:someValuesFrom ?nivel .\n" +
                " FILTER (strEnds(str(?nivel), \"Alto\")) }"; 
                
                ontology.query(query);
            } else {

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
