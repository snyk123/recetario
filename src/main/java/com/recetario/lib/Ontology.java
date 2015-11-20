/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.recetario.lib;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Derivation;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.PrintUtil;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Dennis
 */
public class Ontology {
    
    private final Model data;
    
    private final String NS;
    
    private final boolean debug;
    
    private InfModel infModel;

    private Reasoner reasoner;
    
    public Ontology(String NS) {
        this(NS, false);
    }
    
    public Ontology(String NS, boolean debug) {
        this.data = ModelFactory.createDefaultModel();
        this.infModel = null; 
        this.reasoner = null;
        this.NS = NS;
        this.debug = debug;
    }
    
    public void loadRDF (String inputFileName, String rules) {
        this.loadModel(inputFileName);
        this.setInferenceRules(rules);
    }
    
    public void loadModel(String inputFileName) {
        InputStream in = FileManager.get().open(inputFileName);
        this.data.read(in, "RDF/XML");
    }
    
    public void setInferenceRules(String rulesFileName) {
        this.reasoner = new GenericRuleReasoner(Rule.rulesFromURL(rulesFileName));
        this.reasoner.bindSchema(this.data);
        this.reasoner.setDerivationLogging(true);
        this.infModel = ModelFactory.createInfModel(this.reasoner, this.data);
    }
    
    public boolean validate (Validator validator) throws FileNotFoundException {
        if (this.infModel != null) {
            return validator.validate(infModel);
        } else {
            throw new FileNotFoundException("El archivo RDF no ha sido cargado");
        }
    }
    
    public ArrayList<Statement> query(Resource subject, Property predicate, Object object) throws FileNotFoundException, ClassCastException {
        ArrayList<Statement> result = new ArrayList();
        if (this.infModel != null) {
            StmtIterator i;
            if (object instanceof RDFNode || object == null) {
                i = this.infModel.listStatements(subject, predicate, (RDFNode)object);
            } else if (object instanceof String) {
                i = this.infModel.listStatements(subject, predicate, (String)object);
            } else {
                throw new ClassCastException("El objeto no es de tipo RDFNode ni de tipo String");
            }
            while(i.hasNext()) {
                Statement s = i.nextStatement(); 
                result.add(s);
                if (this.debug) {
                    System.out.println(PrintUtil.print(s));
                    this.printInferenceDeviation(s);
                }
            }
        } else {
            throw new FileNotFoundException("El archivo RDF no ha sido cargado");
        }
        return result;
    }
    
    public ArrayList<Statement> query(String sparql) {
        ArrayList<Statement> result = new ArrayList();
        String prefix = "prefix recetario: <" + this.NS + ">\n" +
                        "prefix rdfs: <" + RDFS.getURI() + ">\n" +
                        "prefix rdf: <" + RDF.getURI() + ">\n" +
                        "prefix owl: <" + OWL.getURI() + ">\n";
        Query query = QueryFactory.create(prefix + sparql);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, this.data)) {
            ResultSet results = qexec.execSelect();
            if (this.debug)
                ResultSetFormatter.out( results, this.data );
        }
        return result;
    }
    
    public Resource getResource(String name) {
        Resource resource = this.data.getResource(NS + name);
        return resource;
    }
    
    public Property getProperty(String name) {
        Property property = this.data.getProperty(NS + name);
        return property;
    }
    
    private void printInferenceDeviation(Statement s) {
        System.out.println("Resultado de derivación ...");
        PrintWriter out = new PrintWriter(System.out);
        for (Iterator i = this.infModel.getDerivation(s); i.hasNext(); ) {
            Derivation derivation = (Derivation) i.next();
            derivation.printTrace(out, true);
        }
        out.flush();
    }
    
    @Override
    public String toString() {
        return data.toString();
    }
    
    public void writeModel(OutputStream outputStream, String format) {
        if (this.infModel == null) {
            this.data.write(outputStream, format);
        } else {
            this.infModel.write(outputStream, format);
        }
    }
    
    public void writeModel(String format) {
        this.writeModel(System.out, format);
    }
}
