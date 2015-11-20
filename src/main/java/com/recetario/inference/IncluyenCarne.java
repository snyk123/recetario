/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.recetario.inference;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.recetario.lib.Validator;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 *
 * @author dennis
 */
public class IncluyenCarne extends Inference {

    public IncluyenCarne(String inputRDF, String rulesFileName, String NS) {
        super(inputRDF, rulesFileName, NS);
    }

    @Override
    public ArrayList<Statement> search() throws FileNotFoundException, ClassCastException {
        ArrayList<Statement> result = new ArrayList<>();
        try {
            if (ontology.validate(new Validator())) {
                String where1 = "{ ?receta rdfs:subClassOf ?class . \n" +
                " ?class owl:onProperty recetario:tiene_ingrediente . \n" +
                " ?class owl:allValuesFrom ?allValues .\n" +
                " ?allValues owl:unionOf ?union . \n" +
                " ?union rdf:rest* [ rdf:first ?ingrediente ] . \n" +
                " ?ingrediente rdfs:subClassOf ?tipoIngrediente . \n" + 
                " ?tipoIngrediente rdfs:subClassOf recetario:Ingrediente . \n" + 
                " FILTER (strEnds(str(?tipoIngrediente), \"Carne\") || strEnds(str(?tipoIngrediente), \"Embutido\")) . \n }";
                
                String where2 = "{ ?receta rdfs:subClassOf ?class . \n" +
                " ?class owl:onProperty recetario:tiene_ingrediente . \n" +
                " ?class owl:allValuesFrom ?allValues .\n" +
                " ?allValues owl:unionOf ?union . \n" +
                " ?union rdf:rest* [ rdf:first ?subReceta ] . \n" +
                " ?subReceta rdfs:subClassOf ?esReceta . \n" + 
                " ?esReceta rdfs:subClassOf recetario:Receta . \n" + 
                " ?subReceta rdfs:subClassOf ?class2 . \n " +
                " ?class2 owl:onProperty recetario:tiene_ingrediente . \n " +
                " ?class2 owl:someValuesFrom ?ingrediente . \n " +
                " ?ingrediente rdfs:subClassOf ?tipoIngrediente . \n" + 
                " ?tipoIngrediente rdfs:subClassOf recetario:Ingrediente . \n" + 
                " FILTER (strEnds(str(?tipoIngrediente), \"Carne\") || strEnds(str(?tipoIngrediente), \"Embutido\")) . \n }";
                
                String query = "select ?receta where { " + where1 + " UNION " + where2 + 
                " } GROUP BY ?receta \n " +
                " HAVING (count(?ingrediente) > 0) ";
                
                result = ontology.query(query, null);
                
            } else {
                return new ArrayList<>();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return result;
    }
    
}
