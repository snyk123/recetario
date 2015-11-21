/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.recetario.inference;

import com.recetario.lib.Validator;
import java.util.ArrayList;
import com.hp.hpl.jena.rdf.model.Statement;
import java.io.FileNotFoundException;

/**
 *
 * @author dennis
 */
public class SubRecetas extends Inference {

    public SubRecetas(String inputRDF, String rulesFileName, String NS) {
        super(inputRDF, rulesFileName, NS);
    }

    @Override
    public ArrayList<Statement> search() throws FileNotFoundException, ClassCastException {
        ArrayList<Statement> result = new ArrayList<>();
        try {
            if (ontology.validate(new Validator())) {
                String query = "select ?receta ?property ?subReceta where { " +
                        " ?receta rdfs:subClassOf ?class . \n" +
                        " ?class owl:onProperty ?property . \n" +
                        " ?class owl:onProperty recetario:tiene_ingrediente . \n" +
                        " ?class owl:allValuesFrom ?allValues .\n" +
                        " ?allValues owl:unionOf ?union . \n" +
                        " ?union rdf:rest* [ rdf:first ?subReceta ] . \n" +
                        " ?subReceta rdfs:subClassOf ?esReceta . \n" + 
                        " ?esReceta rdfs:subClassOf recetario:Receta . \n" + 
                        " FILTER (strEnds(str(?receta), \""+ this.text2Search +"\") || strEnds(str(?subReceta), \"" + this.text2Search + "\")) } ";
                
                result = ontology.query(query, new String[]{"receta", "property", "subReceta"});
                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return result;
    }
    
}
