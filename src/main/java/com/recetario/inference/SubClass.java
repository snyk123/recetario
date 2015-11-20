/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.recetario.inference;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.recetario.lib.Validator;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 *
 * @author dennis
 */
public class SubClass extends Inference {

    public SubClass(String inputRDF, String rulesFileName, String NS) {
        super(inputRDF, rulesFileName, NS);
    }

    @Override
    public ArrayList<Statement> search() throws FileNotFoundException, ClassCastException {
        try {
            if (ontology.validate(new Validator())) {
                Resource resource = ontology.getResource(this.text2Search);
                ArrayList<Statement> child = ontology.query(resource, RDFS.subClassOf, (RDFNode) null);
                ArrayList<Statement> parent = ontology.query(null, RDFS.subClassOf, resource);
                ArrayList<Statement> result = new ArrayList<>();
                result.removeAll(child);
                result.addAll(child);
                result.removeAll(parent);
                result.addAll(parent);
                return result;
            } else {
                return new ArrayList<>();
            }
        } catch (FileNotFoundException | ClassCastException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
}
