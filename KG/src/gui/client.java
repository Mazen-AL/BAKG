package gui;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import NER.ontologyMapping;
import NLP.preProcessing;
import ONTO.BioPontologyfactory;
import ONTO.ontologyfactory;
import util.NGramAnalyzer;
import util.ReadXMLFile;
import util.bioportal;
import util.readfiles;
import util.removestopwords;

public class client {
	  static  String skos = "http://www.w3.org/2004/02/skos/core#" ;
	   static String  rdfs = "http://www.w3.org/2000/01/rdf-schema#" ;
	   static String  rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#" ;
	   static String  owl = "http://www.w3.org/2002/07/owl#" ;
	   static String  lo = "http://www.lifeOnto.org/lifeOnto#" ;

	public static void main(String[] args) throws IOException, ParseException
	{
		// TODO Auto-generated method stub
		OntModel OntoGraph = ModelFactory.createOntologyModel();
		OntoGraph.setNsPrefix( "skos", skos ) ;
		OntoGraph.setNsPrefix( "lo", lo ) ;
		// is disease
		
		
		File CNs[] =  readfiles.readAllFileFromPath("C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\ClinicalNote") ;
		
		for (int i = 0; i < CNs.length; i++)
		{
			
			    String CN = readfiles.readLinestostring(CNs[i].toURL());
				int count = 0 ;
				int counter = 0 ; 
				List<String> titleList = preProcessing.getSentences(CN); 
				for (String title : titleList)
				{
					String text  = removestopwords.removestopwordfromsen(title) ;
					Map<String, Integer> mentions = NGramAnalyzer.entities(1,3, text) ;
					Map<String, String> allconcepts = null ;
					allconcepts = bioportal.getConcepts(mentions)  ;
					
					Map<String, String> concepts = new HashMap<String, String>();
					
					for ( String cpt : allconcepts.keySet())
					{
						if (!cpt.isEmpty())
						{
							String semGroup = allconcepts.get(cpt).trim() ;
							//if (allconcepts.get(cpt).contains("T184")||allconcepts.get(cpt).contains("T047")||allconcepts.get(cpt).contains("T190"))
							if (semGroup.compareTo("Disorders") == 0|| semGroup.compareTo("Chemicals & Drugs") == 0 || semGroup.compareTo("Anatomy") == 0  ||  semGroup.compareTo("Device") ==0 )
							{
								concepts.put(cpt, allconcepts.get(cpt))	;
							}
						} 
					}
					
					 String PrimaryConcept  = "brain aneurysm" ;
					 BioPontologyfactory.classToOnto("brain aneurysm",OntoGraph);
					 OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
					for ( String cpt : concepts.keySet())
					{
						if (!cpt.isEmpty())
						{
							

							String URI = BioPontologyfactory.classToOnto(cpt,concepts.get(cpt),OntoGraph);
							if (URI != null)
							{
								BioPontologyfactory.semTypeToOnto(cpt,URI,OntoGraph) ;
								BioPontologyfactory.synonymToOnto(cpt, URI, OntoGraph);
								BioPontologyfactory.definitionToOnto(cpt, URI, OntoGraph);
								BioPontologyfactory.prefLabelToOnto(cpt, URI, OntoGraph);
								BioPontologyfactory.loadTaxonomic(cpt, URI, OntoGraph);
								BioPontologyfactory.has_symptomToOnto(PrimaryConcept, cpt, OntoGraph);
								BioPontologyfactory.treated_byToOnto(PrimaryConcept, cpt, OntoGraph);
								BioPontologyfactory.diagnoses_byToOnto(PrimaryConcept, cpt, OntoGraph);
								BioPontologyfactory.locationToOnto(PrimaryConcept, cpt, OntoGraph);
								BioPontologyfactory.riskFactorToOnto1(PrimaryConcept, cpt, OntoGraph);
								OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
							}

							//BioPontologyfactory.SymptomsToOnto(cpt, concepts, OntoGraph);
							//BioPontologyfactory.TreatsToOnto(cpt, concepts, OntoGraph);
							//BioPontologyfactory.ComorbidToOnto1("brain aneurysm", concepts, OntoGraph) ;
							//OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
					        //BioPontologyfactory.SymptomsToOnto(cpt,concepts,OntoGraph);
						}
					}
					count++ ; 
					counter++; 
					 if (count == 50)
					 {
						     System.out.println("+++++++++++++++++++++++++++++++++");
						     System.out.println(counter);
						     System.out.println("+++++++++++++++++++++++++++++++++");
					        // Creating a File object that represents the disk file.
					        PrintStream o = new PrintStream(new File("C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\ANEBioPortal1.rdf"));
					        
					        // Store current System.out before assigning a new value
					        PrintStream console = System.out;
					 
					        // Assign o to output stream
					        System.setOut(o);	
							OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
							
					        // Use stored value for output stream
					        System.setOut(console);
					        count = 0 ; 
					 }
					
				}
		}
		
        // Creating a File object that represents the disk file.
        PrintStream o = new PrintStream(new File("C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\ANEBioPortal1.rdf"));
        
        // Store current System.out before assigning a new value
        PrintStream console = System.out;
 
        // Assign o to output stream
        System.setOut(o);	
		OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
				
		System.out.println("++++++++++++++Done+++++++++++++++");

	}
}

