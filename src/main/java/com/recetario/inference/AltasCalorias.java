/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.recetario.inference;

import com.hp.hpl.jena.rdf.model.Statement;
import com.recetario.lib.Validator;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 *
 * @author dennis
 */
public class AltasCalorias extends Inference {

    public AltasCalorias(String inputRDF, String rulesFileName, String NS) {
        super(inputRDF, rulesFileName, NS);
    }

    @Override
    public ArrayList<Statement> search() throws FileNotFoundException, ClassCastException {
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
                
                return ontology.query(query);
            } else {
                return new ArrayList<>();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
}
