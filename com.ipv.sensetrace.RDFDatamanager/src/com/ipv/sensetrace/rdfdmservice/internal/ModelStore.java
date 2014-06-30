package com.ipv.sensetrace.rdfdmservice.internal;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import com.hp.hpl.jena.rdf.model.*;


/**
 * Tutorial 5 - read RDF XML from a file and write it to standard out
 */
public class ModelStore extends Object {

	/**
	 * NOTE that the file is loaded from the class-path and so requires that the
	 * data-directory, as well as the directory containing the compiled class,
	 * must be added to the class-path when running this and subsequent
	 * examples.
	 */
	static Model model = null;
	static final String inputFileName = "test.rdf";

	public ModelStore(){
		model = ModelFactory.createDefaultModel();
	}
	
	public Model GetModel()
	{
		return model;
	}
	
	public String GetN3String() {
		// TODO Auto-generated method stub
		 Writer n3 = new StringWriter();
		model.write(n3,"N-TRIPLE");
		//n3.toString();
		//System.out.println(n3.toString());
		return n3.toString();
	}
	
	public void create(String input) {
		
		 StringReader in = new StringReader(input);

		// read the RDF/XML file
		// model.read(in, "");
		model.read(in, "");
		// write it to standard out
		//model.write(System.out);
		//model.write(System.out, "RDF/XML-ABBREV");
	
	}



}