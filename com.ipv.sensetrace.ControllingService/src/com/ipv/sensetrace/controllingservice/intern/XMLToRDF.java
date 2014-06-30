package com.ipv.sensetrace.controllingservice.intern;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class XMLToRDF {

    /**
     * XSLT-Transformation durchführen und Ergebnis
     * an System.out schicken.
     */
    @SuppressWarnings("null")
	public String convert(String string_xmlFile, String string_xsltFile) throws Exception {
     
    	String output = null;
        File xmlFile = new File(string_xmlFile);
        File xsltFile = new File(string_xsltFile);

        // JAXP liest Daten über die Source-Schnittstelle
        Source xmlSource = new StreamSource(xmlFile);
        Source xsltSource = new StreamSource(xsltFile);
        
        StreamResult result = new StreamResult(new StringWriter());
        
        // das Factory-Pattern unterstützt verschiedene XSLT-Prozessoren
        TransformerFactory transFact =
                TransformerFactory.newInstance();
        Transformer trans = transFact.newTransformer(xsltSource);
                
       // trans.transform(xmlSource, new StreamResult(System.out));
        trans.transform(xmlSource, result);
       // output=
        return result.getWriter().toString();
    }
}