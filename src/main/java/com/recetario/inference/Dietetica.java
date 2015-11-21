/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.recetario.inference;

import com.recetario.lib.Validator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 *
 * @author dennis
 */
public class Dietetica extends  Inference {

    public Dietetica(String inputRDF, String rulesFileName, String NS) {
        super(inputRDF, rulesFileName, NS);
    }

    @Override
    public ArrayList<Statement> search() throws FileNotFoundException, ClassCastException {
        ArrayList<Statement> result = new ArrayList<>();
        try {
            if (ontology.validate(new Validator())) {
                Resource subject = ontology.getResource("dietetica");
                Resource object = ontology.getResource(this.text2Search);
                result = ontology.query(subject, null, (RDFNode) object);
            }
        } catch (FileNotFoundException | ClassCastException ex) {
            ex.printStackTrace();
            throw ex;
        }
        return result;
    }
    
}
