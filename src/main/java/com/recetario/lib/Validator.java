package com.recetario.lib;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.reasoner.ValidityReport;
import java.util.Iterator;

/**
 * @author Dennis Cohn Muroy
 */
public class Validator {
    public boolean validate(InfModel infModel) {
        ValidityReport validityReport = infModel.validate();
        if (validityReport.isValid()) {
            return true;
        } else {
            System.out.println("Modelo con conflictos ...");
            for (Iterator i = validityReport.getReports(); i.hasNext() ;) {
                System.out.println(" - " + i.next().toString());
            }
            return false;
        }
    }
}
