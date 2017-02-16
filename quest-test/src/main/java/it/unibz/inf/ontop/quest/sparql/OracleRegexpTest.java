package it.unibz.inf.ontop.quest.sparql;

/*
 * #%L
 * ontop-test
 * %%
 * Copyright (C) 2009 - 2014 Free University of Bozen-Bolzano
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import it.unibz.inf.ontop.owlrefplatform.owlapi.OntopOWLStatement;
import it.unibz.inf.ontop.owlrefplatform.owlapi.QuestOWLResultSet;
import it.unibz.inf.ontop.quest.AbstractVirtualModeTest;
import org.semanticweb.owlapi.model.OWLIndividual;


/***
 * Tests that the system can handle the SPARQL "LIKE" keyword in the oracle setting
 * (i.e. that it is translated to REGEXP_LIKE and not LIKE in oracle sql)
 */
public class OracleRegexpTest extends AbstractVirtualModeTest {

	static final String owlfile = "resources/regexp/oracle-regexp.owl";
	static final String obdafile = "resources/regexp/oracle-regexp.obda";
	static final String propertyfile = "resources/regexp/oracle-regexp.properties";

	public OracleRegexpTest() {
		super(owlfile, obdafile, propertyfile);
	}


	private String runTest(OntopOWLStatement st, String query, boolean hasResult) throws Exception {
		String retval;
		QuestOWLResultSet rs = st.executeTuple(query);
		if(hasResult){
			assertTrue(rs.nextRow());
			OWLIndividual ind1 =	rs.getOWLIndividual("country")	 ;
			retval = ind1.toString();
		} else {
			assertFalse(rs.nextRow());
			retval = "";
		}

		return retval;
	}

	/**
	 * Tests the use of SPARQL like
	 * @throws Exception
	 */
	public void testSparql2OracleRegex() throws Exception {
		OntopOWLStatement st = null;
		try {
			st = conn.createStatement();

			String[] queries = {
					"'E[a-z]*t'", 
					"'^E[a-z]*t$'", 
					"'^E[a-z]*t$', 'm'", 
					"'Eg'", 
					"'^e[g|P|y]*T$', 'i'",
					"'^Egypt$', 'sm'"
					};
			for (String regex : queries){
				String query = "PREFIX : <http://www.semanticweb.org/ontologies/2013/7/untitled-ontology-150#> SELECT ?country WHERE {?country a :Country; :name ?country_name . FILTER regex(?country_name, " + regex + ")}";
				String countryName = runTest(st, query, true);
				assertEquals(countryName, "<http://www.semanticweb.org/ontologies/2013/7/untitled-ontology-150#Country-Egypt>");
			}
			String[] wrongs = {
					"'eGYPT'", 
					"'^Eg$'",
					"'.Egypt$'"
					};
			for (String regex : wrongs){
				String query = "PREFIX : <http://www.semanticweb.org/ontologies/2013/7/untitled-ontology-150#> SELECT ?country WHERE {?country a :Country; :name ?country_name . FILTER regex(?country_name, " + regex + ")}";
				String countryName = runTest(st, query, false);
				assertEquals(countryName, "");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (st != null)
				st.close();
		}
	}

}
