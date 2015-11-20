/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.recetario.inference;

import com.hp.hpl.jena.rdf.model.Statement;
import com.recetario.lib.Ontology;
import com.recetario.lib.Validator;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 *
 * @author dennis
 */
public class Ingredientes extends Inference {

    public Ingredientes(String inputRDF, String rulesFileName, String NS) {
        super(inputRDF, rulesFileName, NS);
    }

    @Override
    public ArrayList<Statement> search() throws FileNotFoundException, ClassCastException {
        try {
            if (ontology.validate(new Validator())) {
                String query = "select ?receta ?ingrediente where { " +
                " ?receta rdfs:subClassOf ?class . \n" +
                " ?class owl:onProperty recetario:tiene_ingrediente . \n" +
                " ?class owl:allValuesFrom ?allValues .\n" +
                " ?allValues owl:unionOf ?union . \n" +
                " ?union rdf:rest* [ rdf:first ?ingrediente ] }";
                
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
