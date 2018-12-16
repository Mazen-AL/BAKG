package ONTO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;






















import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultMutableTreeNode;

import org.json.simple.parser.ParseException;

import util.bioportal;
import util.dataExtractor;
import HRCHY.hierarchy;
import RICH.Enrichment;
import Stat.Scoring;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class BioPontologyfactory {

   public static  String skos = "http://www.w3.org/2004/02/skos/core#" ;
   static String  rdfs = "http://www.w3.org/2000/01/rdf-schema#" ;
   static String  rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#" ;
   static String  owl = "http://www.w3.org/2002/07/owl#" ;
   static String  lo = "http://www.lifeOnto.org/lifeOnto#" ;
   static String  ica=  "http://www.mii.ucla.edu/~willhsu/ontologies/ica_ontology#" ;
   
   // An ontology model is an extension of the Jena RDF model 
  // static    OntModel OntoGraph = ModelFactory.createOntologyModel(); 
	
	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		riskFactorToOnto1(null,"alcohol", null) ;
//		ethnicityToOnto("Venous Thromboembolism whiteguy Black","alcohol",null);
	}

	
	public static OntModel createOntoBioP (String concept,OntModel OntoGraph) throws IOException, ParseException
	{

		// generating owl:class
		String URI = classToOnto (concept,OntoGraph);
		if(URI != null)
		{
			sameAsToOnto(concept,URI,OntoGraph) ;
		    prefLabelToOnto(concept,URI,OntoGraph) ;
		    //OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
			synonymToOnto(concept,URI,OntoGraph)	;
			definitionToOnto (concept,URI,OntoGraph) ;
			semTypeToOnto(concept,URI,OntoGraph) ;
			//OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
			//loadTaxonomic(concept,URI,1,OntoGraph);	
			//OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
		}
		return OntoGraph ;
	}
	
	public static OntModel createOntoBioP (String concept) throws IOException, ParseException
	{
		OntModel OntoGraph = ModelFactory.createOntologyModel();
		OntoGraph.setNsPrefix( "skos", skos ) ;
		
		// generating owl:class
		String URI = classToOnto (concept,OntoGraph);
		sameAsToOnto(concept,URI,OntoGraph) ;
	    prefLabelToOnto(concept,URI,OntoGraph) ;
	   // OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
		synonymToOnto(concept,URI,OntoGraph)	;
		definitionToOnto (concept,URI,OntoGraph) ;
		semTypeToOnto(concept,URI,OntoGraph) ;
		OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
//		loadTaxonomic(concept,URI,1,OntoGraph);	
		//OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
		return OntoGraph ;
	}
	
	
	// adding a concept as owl class and rdfs:label
		public static String classToOnto (String concept,String SemGroup, OntModel OntoGraph)
		{
			// the URI is equal to preflabel uri
			String conceptURI = bioportal.getConceptID(concept);
			System.out.println("classToOnto");
			Resource r = null ; 
			if (conceptURI != null )
			{
				if (( r= OntoGraph.getOntClass(conceptURI) ) == null)
				{
					OntClass rec = OntoGraph.createClass(conceptURI);
					// assign a Label 
					final Property p = ResourceFactory.createProperty(rdfs + "label") ;
					rec.addProperty(p, concept);
					
					final Property p1 = ResourceFactory.createProperty(lo + "Aneurasyms_related") ;
					rec.addLiteral(p1, 1);
					
					final Property p2 = ResourceFactory.createProperty(lo + "Semantic_Group") ;
					rec.addLiteral(p2, SemGroup);
					
					return conceptURI ;
				}
				else
				{
					final Property p2 = ResourceFactory.createProperty(lo + "Aneurasyms_related") ;
					Statement st = r.getProperty(p2) ;
					RDFNode node = st.getObject();
					long value = node.asLiteral().getLong() ;
					st = st.changeLiteralObject(value+1) ;
					return conceptURI ;
				}
			}
			
			return null;
		}
	
	// adding a concept as owl class and rdfs:label and the frequency 
	public static String classToOnto (String concept,OntModel OntoGraph)
	{
		// the URI is equal to preflabel uri
		String conceptURI = bioportal.getConceptID(concept);
		System.out.println("classToOnto");
		Resource r = null ; 
		if (conceptURI != null )
		{
			if (( r= OntoGraph.getOntClass(conceptURI) ) == null)
			{
				OntClass rec = OntoGraph.createClass(conceptURI);
				// assign a Label 
				final Property p = ResourceFactory.createProperty(rdfs + "label") ;
				rec.addProperty(p, concept);
				
				final Property p1 = ResourceFactory.createProperty(lo + "frequency") ;
				rec.addLiteral(p1, 1);
				return conceptURI ;
			}
			else
			{
				final Property p2 = ResourceFactory.createProperty(lo + "frequency") ;
				Statement st = r.getProperty(p2) ;
				RDFNode node = st.getObject();
				long value = node.asLiteral().getLong() ;
				st = st.changeLiteralObject(value+1) ;
			}
		}
		
		return null;
	}
	// adding a concept as owl class and rdfs:label
	public static OntClass classToOnto_URI (String conceptURI,OntModel OntoGraph)
	{
		// the URI is equal to preflabel uri
		System.out.println("classToOnto");
		if (conceptURI != null )
		{
			OntClass rec = OntoGraph.createClass(conceptURI);
			return rec ;
		}
		return null;
	}
	// adding a concept as owl class and rdfs:label
	public static OntClass classToOnto_URI (String conceptURI,String label, OntModel OntoGraph)
	{
		// the URI is equal to preflabel uri
		System.out.println("classToOnto");
		if (conceptURI != null )
		{
			OntClass rec = OntoGraph.createClass(conceptURI);
			// assign a Label 
			final Property p = ResourceFactory.createProperty(rdfs + "label") ;
			rec.addProperty(p, label);
			
			return rec ;
		}
		return null;
	}
	
	// generating synonyms with skos:altLabel
	public static void synonymToOnto (String concept,String URI,OntModel OntoGraph) throws IOException, ParseException
	{
		
		// generating synonyms wiht skos:altLabel
		Map<String, Integer> Synonyms =  bioportal.getSynonyms(concept);
		System.out.println("getontoSynonym");
		Resource r = null ; 
		for (String syn: Synonyms.keySet())
		{
			if ( ( r= OntoGraph.getOntClass(URI) ) != null)
			{
				final Property p = ResourceFactory.createProperty(skos + "altLabel") ;
				r.addProperty(p, syn);
			}
			else
				break ; 
			
		}
	}
	
	// generating prefLable with skos:prefLabel
	public static void prefLabelToOnto(String concept,String URI,OntModel OntoGraph) throws IOException, ParseException
	{
		
		// generating synonyms wiht skos:altLabel
		String prefLabel = bioportal.getPrefLabels(concept);
		System.out.println("prefLabel");
		if(prefLabel != null)
		{
			Resource r = null ; 
			if ( ( r= OntoGraph.getOntClass(URI) ) != null)
			{
					final Property p = ResourceFactory.createProperty(skos + "prefLabel") ;
					r.addProperty(p, prefLabel);
			}
		}
	}
	
	
	public static void loadTaxonomic(String concept,String URI,OntModel OntoGraph) throws IOException, ParseException {
		
		
		List<String>   listTaxon = bioportal.getTaxonomic(concept,1) ; 
 
		// get the class reference 
		OntClass child = OntoGraph.getOntClass(URI) ; 
		OntClass r1 = null ; 
		// loop though hierarchy 
		for(String hier: listTaxon)
		{
			// get the uri of the parent 
			String conceptURI = bioportal.getConceptID(hier);
			if(conceptURI ==  null)
				continue ; 
			// check if the parent already exit in the graph 
			if (( r1 = OntoGraph.getOntClass(conceptURI) ) != null)
			{
				child.addSuperClass(r1);
			}
			else
			{
				// create new class and assign it as parent 
				String uri = classToOnto (hier,OntoGraph);
				r1 = OntoGraph.getOntClass(uri) ;
				final Property p1 = ResourceFactory.createProperty(rdfs + "label") ;
				r1.addProperty(p1, hier);
				child.addSuperClass(r1);
			}
			child = r1 ; 
		}

	}
	
	// generating definition 
	public static void definitionToOnto (String concept,String URI,OntModel OntoGraph) throws IOException, ParseException
	{
		
		// generating synonyms wiht skos:altLabel
		Map<String, Integer> defs =  bioportal.getDefinitions(concept);
		System.out.println("getontoDefinition");
		Resource r = null ; 
		for (String def:  defs.keySet())
		{
			if ( ( r= OntoGraph.getOntClass(URI) ) != null)
			{
				final Property p = ResourceFactory.createProperty(skos + "definition") ;
				r.addProperty(p, def);
			}
			else
				break ; 
			
		}
	}
	
	public static void semTypeToOnto (String concept,String URI,OntModel OntoGraph) throws IOException, ParseException
	{
		
		// generating synonyms wiht skos:altLabel
		Map<String, Integer> semTypes =  bioportal.getSemanticTypes(concept);
		System.out.println("getontoSemantic Type");
		Resource r = null ; 
		for (String semType:  semTypes.keySet())
		{
			if ( ( r= OntoGraph.getOntClass(URI) ) != null)
			{
				final Property p = ResourceFactory.createProperty(skos + "type") ;
				r.addProperty(p, semType);
				
				
				final Property p1 = ResourceFactory.createProperty(lo + "Semantic_Type_Label") ;
				r.addProperty(p1, bioportal.getSemanticTypeNames(semType));
			}
			else
				break ; 
			
		}
	}
	public static void sameAsToOnto (String concept,String URI,OntModel OntoGraph) throws IOException, ParseException
	{
		
		// generating synonyms wiht skos:altLabel
		Map<String, Integer> sameases =  bioportal.getSameas(concept,URI);
		System.out.println("sameAsToOnto Type");
		Resource r = null ; 
		if (sameases == null)
			return ; 
		for (String sameas:  sameases.keySet())
		{
			if ( ( r= OntoGraph.getOntClass(URI) ) != null)
			{
				final Property p = ResourceFactory.createProperty(owl + "sameAs") ;
				r.addProperty(p,sameas);
			}
			else
				break ; 
			
		}
	}
	
	public static void has_symptomToOnto (String PrimaryConcept,String  cp ,OntModel OntoGraph) throws IOException, ParseException
	{
		
		// create new has symptom relation between the PrimaryConcept and discovered concept if discovered concept is symptom semantic type 
		String cptURI = bioportal.getConceptID(PrimaryConcept);
		Map<String, Integer> semTypeCp =  bioportal.getSemanticTypes(PrimaryConcept);
		
		// if the concept is symptom then skip it.
		if ( semTypeCp.size() == 1 && semTypeCp.containsKey("T184"))
		   return ; 
		
		
		System.out.println("has_symptomToOnto");
		Resource recPrimaryConcept = null ; 
		Resource r1 = null ;

		{
			// don't add relation for itself
			if(cp.equalsIgnoreCase(PrimaryConcept))
              return ; 
			
			Map<String, Integer> semType =  bioportal.getSemanticTypes(cp);
			
			for (String st:  semType.keySet())
			{
				if(st.equalsIgnoreCase("T184"))  
				{
					String conceptURI = bioportal.getConceptID(cp);
					if ( ( recPrimaryConcept= OntoGraph.getOntClass(cptURI) ) != null) // get the primary concept resource 
					{
						final Property has_Symptom = ResourceFactory.createProperty(lo + "has_Symptom") ; // create new property 
						
						
						if (( r1 = OntoGraph.getOntClass(conceptURI) ) != null) // get the concept resource 
						{
							
							// the symptom is already discovered before 
							// then add the frequency  
							recPrimaryConcept.addProperty(has_Symptom,r1);
							
							
							// update the frequency by one. 
							final Property p2 = ResourceFactory.createProperty(lo + "frequency") ;
							Statement stm = r1.getProperty(p2) ;
							RDFNode node = stm.getObject();
							long value = node.asLiteral().getLong() ;
							stm = stm.changeLiteralObject(value+1) ;
							
							
						}
						else
						{
							
							String uri = classToOnto (cp,OntoGraph);
							r1 = OntoGraph.getOntClass(uri) ;
							recPrimaryConcept.addProperty(has_Symptom,r1);
														
							// set frequency value to 1 
							final Property p2 = ResourceFactory.createProperty(lo + "frequency") ;
							r1.addLiteral(p2, 1);
							
						}
						
						Scoring.setOccurence_Probability(conceptURI, recPrimaryConcept, OntoGraph);
						Scoring.Tier_Rank(conceptURI, recPrimaryConcept, OntoGraph);
						
					}
					else
						break ; 
					System.out.println(st);
				}
			}
			
		}
	}
	
	public static void treated_byToOnto (String PrimaryConcept,String cp,OntModel OntoGraph) throws IOException, ParseException
	{
		// this function created a treated by relation between the discovred concept and primary concept if the discovered one is Chemicals & Drugs semantic group 
		
		String cptURI = bioportal.getConceptID(PrimaryConcept);
		Map<String, Integer> semGroupCp =  bioportal.getSemanticGroup(PrimaryConcept); 
		

		if ( semGroupCp.size() == 1 && semGroupCp.containsKey("Chemicals & Drugs"))
		   return ; 
		
		
		System.out.println("TreatsToOnto ");
		Resource recPrimaryConcept = null ; 
		Resource r1 = null ;
		
		{
			// don't add relation for itself
			if(cp.equalsIgnoreCase(PrimaryConcept))
				return ; 
			
			Map<String, Integer> semGroup =  bioportal.getSemanticGroup(cp);
			
			for (String st:  semGroup.keySet())
			{
				if(st.equalsIgnoreCase("Chemicals & Drugs"))
				{
					String conceptURI = bioportal.getConceptID(cp);
					if ( ( recPrimaryConcept= OntoGraph.getOntClass(cptURI) ) != null)
					{
						final Property p = ResourceFactory.createProperty(lo + "treated_by") ;
						
						
						if (( r1 = OntoGraph.getOntClass(conceptURI) ) != null)
						{
							recPrimaryConcept.addProperty(p,r1);
							
							// update the frequency by one. 
							final Property p2 = ResourceFactory.createProperty(lo + "frequency") ;
							Statement stm = r1.getProperty(p2) ;
							RDFNode node = stm.getObject();
							long value = node.asLiteral().getLong() ;
							stm = stm.changeLiteralObject(value+1) ;
						}
						else
						{
							String uri = classToOnto (cp,OntoGraph);
							r1 = OntoGraph.getOntClass(uri) ;
							recPrimaryConcept.addProperty(p,r1);
							
							// set frequency value to 1 
							final Property p2 = ResourceFactory.createProperty(lo + "frequency") ;
							r1.addLiteral(p2, 1);
						}
						
						Scoring.setOccurence_Probability(conceptURI, recPrimaryConcept, OntoGraph);
						Scoring.Tier_Rank(conceptURI, recPrimaryConcept, OntoGraph);
						
					}
					else
						break ; 
					System.out.println(st);
				}
			}
			
		}
	}
	
	public static void locationToOnto (String PrimaryConcept,String cp,OntModel OntoGraph) throws IOException, ParseException
	{
		
		String cptURI = bioportal.getConceptID(PrimaryConcept);
		Map<String, Integer> semGroupCp =  bioportal.getSemanticGroup(PrimaryConcept); 
		

		if ( semGroupCp.size() == 1 && semGroupCp.containsKey("Anatomy"))
		   return ; 
		
		
		System.out.println("locationToOnto ");
		Resource recPrimaryConcept = null ; 
		Resource r1 = null ;
		{
			// don't add relation for itself
			if(cp.equalsIgnoreCase(PrimaryConcept))
				return ; 
			
			Map<String, Integer> semGroup =  bioportal.getSemanticGroup(cp);
			
			for (String st:  semGroup.keySet())
			{
				if(st.equalsIgnoreCase("Anatomy"))
				{
					String conceptURI = bioportal.getConceptID(cp);
					if ( ( recPrimaryConcept= OntoGraph.getOntClass(cptURI) ) != null)
					{
						final Property p = ResourceFactory.createProperty(lo + "location") ;
						
						
						if (( r1 = OntoGraph.getOntClass(conceptURI) ) != null)
						{
							recPrimaryConcept.addProperty(p,r1);
							// update the frequency by one. 
							final Property p2 = ResourceFactory.createProperty(lo + "frequency") ;
							Statement stm = r1.getProperty(p2) ;
							RDFNode node = stm.getObject();
							long value = node.asLiteral().getLong() ;
							stm = stm.changeLiteralObject(value+1) ;
						}
						else
						{
							String uri = classToOnto (cp,OntoGraph);
							r1 = OntoGraph.getOntClass(uri) ;
							recPrimaryConcept.addProperty(p,r1);
							
							// set frequency value to 1 
							final Property p2 = ResourceFactory.createProperty(lo + "frequency") ;
							r1.addLiteral(p2, 1);
						}
						
						Scoring.setOccurence_Probability(conceptURI, recPrimaryConcept, OntoGraph);
						Scoring.Tier_Rank(conceptURI, recPrimaryConcept, OntoGraph);
						
					}
					else
						break ; 
					System.out.println(st);
				}
			}
			
		}
	}
	public static void diagnoses_byToOnto (String PrimaryConcept,String cp,OntModel OntoGraph) throws IOException, ParseException
	{
		
		String cptURI = bioportal.getConceptID(PrimaryConcept);
		Map<String, Integer> semGroupCp =  bioportal.getSemanticGroup(PrimaryConcept); 
		

		if ( semGroupCp.size() == 1 && semGroupCp.containsKey("Device"))
		   return ; 
		
		
		System.out.println("diagnoses_by");
		Resource recPrimaryConcept = null ; 
		Resource r1 = null ;
		{
			// don't add relation for itself
			if(cp.equalsIgnoreCase(PrimaryConcept))
				return ; 
			
			Map<String, Integer> semGroup =  bioportal.getSemanticGroup(cp);
			
			for (String st:  semGroup.keySet())
			{
				if(st.equalsIgnoreCase("Devices"))
				{
					String conceptURI = bioportal.getConceptID(cp);
					if ( ( recPrimaryConcept= OntoGraph.getOntClass(cptURI) ) != null)
					{
						final Property p = ResourceFactory.createProperty(lo + "diagnoses_by") ;
						
						
						if (( r1 = OntoGraph.getOntClass(conceptURI) ) != null)
						{
							recPrimaryConcept.addProperty(p,r1);
							// update the frequency by one. 
							final Property p2 = ResourceFactory.createProperty(lo + "frequency") ;
							Statement stm = r1.getProperty(p2) ;
							RDFNode node = stm.getObject();
							long value = node.asLiteral().getLong() ;
							stm = stm.changeLiteralObject(value+1) ;
						}
						else
						{
							String uri = classToOnto (cp,OntoGraph);
							r1 = OntoGraph.getOntClass(uri) ;
							recPrimaryConcept.addProperty(p,r1);
							
							// set frequency value to 1 
							final Property p2 = ResourceFactory.createProperty(lo + "frequency") ;
							r1.addLiteral(p2, 1);
						}
						
						
						Scoring.setOccurence_Probability(conceptURI, recPrimaryConcept, OntoGraph);
						Scoring.Tier_Rank(conceptURI, recPrimaryConcept, OntoGraph);
						
					}
					else
						break ; 
					System.out.println(st);
				}
			}
			
		}
	}
	
	public static int genderToOnto(String sentence) 
	{
		
		String[] tokens = sentence.split(sentence); 
		
		if ( tokens != null)
		{
			String[] flist= {"female","women", "miss","lady","mother","wife"} ; 
			String[] mlist= {"male","man","father","husband", "guy"} ; 
			int fCount = 0 ; 
			int mCount = 0 ; 
			for (int i =0; i < tokens.length; i++ )
			{
				
				for (int f = 0; f < flist.length; f++)
				{
					if (tokens[i].compareToIgnoreCase(flist[f]) == 0 )
					{
						fCount++ ; 
					}
				}
				
				for (int m = 0; m < mlist.length; m++)
				{
					if (tokens[i].compareToIgnoreCase(mlist[m]) == 0 )
					{
						mCount++ ; 
					}
				}
			}
			
			if( fCount > mCount)
			{
				return 1 ; 
			}
			else
			{
				return 2 ; 
			}

		}
		
		return 0 ; 
	}
	
	public static String ethnicityToOnto(String sentence,String concept,OntModel OntoGraph) 
	{
		
		String[] tokens = sentence.split(sentence); 
		 String URI = null ;
		
		if ( tokens != null)
		{
			String[] whiteRace= {"White","Europe", "Middle East","North Africa","caucasian"} ; 
			String[] blackRace= {"black","African American", "Africa"} ; 
			String[] asianRace= {"Asian","Far East", "Southeast Asia", "Indian"} ;
			String[] HawaiianRace= {"Hawaii","Guam", "Samoa", "Pacific Islands"} ;

			int[] race = {0,0,0,0} ;  

			
			for (int j = 0; j < whiteRace.length; j++)
			{
				if (isContain(sentence,whiteRace[j]))
				{
					race[0] =  race[0] + 1 ; 
				}
			}
			
			
			for (int j = 0; j < blackRace.length; j++)
			{
				if (isContain(sentence,blackRace[j]))
				{
					race[1] =  race[1] + 1 ; 
				}
			}
	
			for (int j = 0; j < asianRace.length; j++)
			{
				if (isContain(sentence,asianRace[j]))
				{
					race[2] =  race[2] + 1 ; 
				}
			}
			for (int j = 0; j < HawaiianRace.length; j++)
			{
				if (isContain(sentence,HawaiianRace[j]))
				{
					race[3] =  race[3] + 1 ; 
				}
			}	
			
			int max = 0 ; 
			for (int j = 0; j < race.length; j++)
			{
				if (race[j]> max)
				{
					max =  j ; 
				}
			}; 
			
		  
	       switch (max)
	       {
	       case 0:
	    	  URI = classToOnto ("White",OntoGraph) ;
              break ; 
	       case 1:
	    	   URI = classToOnto ("Black",OntoGraph) ;
	    	   break ; 
	       case 2:
	    	   URI = classToOnto ("Asian",OntoGraph) ;
	    	   break ; 
	       case 3:
	    	   URI = classToOnto ("Hawaiian",OntoGraph) ;
	    	   break ; 
	       default:
	    	   URI = classToOnto ("White",OntoGraph) ;
	    	   break ; 
	    	   
	       }
	       
	       Resource r = null ; 
	       if ( ( r= OntoGraph.getOntClass(URI) ) == null)
	       {
		       // adding new risckfactor relations 
	    	   String cptURI = bioportal.getConceptID(concept);
	    	   Resource resPrimery= OntoGraph.getOntClass(cptURI) ;
		       final Property p = ResourceFactory.createProperty(lo + "RiskFactor") ;//
			   Resource  BlankNodeComorbid_Relation = OntoGraph.createResource() ;
			   resPrimery.addProperty(p,BlankNodeComorbid_Relation) ; 
			   final Property p2 = ResourceFactory.createProperty(lo + "ethnicity") ;
			   Resource res= OntoGraph.getOntClass(URI) ;
			   BlankNodeComorbid_Relation.addProperty(p2,res);
	       }
	       
		}
		return URI ;

	}
	
	
    private static boolean isContain(String source, String subItem){
        String pattern = "\\b"+subItem.toLowerCase()+"\\b";
        Pattern p=Pattern.compile(pattern);
        Matcher m=p.matcher(source.toLowerCase());
        return m.find();
   }
    
    public static void riskFactorToOnto1 (String Primaryconcept,String concept,OntModel OntoGraph) throws IOException, ParseException
    
	{
    	String pcptURI = bioportal.getConceptID(Primaryconcept);
    	 Resource res = null ; 
    	// update ICA ontology with new riskfactors
    	OntModel ICAOnto = dataExtractor.riskFactorExtractor("C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\data\\ica_ontology_updated_aug_27.owl")  ;
    	
    	
    	// build query string to map the concept to on of the existing predefine riskfactors in ontology 
		String queryString=
				"PREFIX p: <http://dbpedia.org/property/>"+
				"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
				"PREFIX category: <http://dbpedia.org/resource/Category:>"+
				"PREFIX lo: <http://www.lifeOnto.org/lifeOnto#>" +
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				"Select ?concept ?riskfactCat  where { ?concept rdfs:label|skos:altLabel"  + "\"" +  concept + "\" ."  + 
				"?concept rdfs:subClassOf ?riskfactCat ." +
				"?riskfactCat rdfs:subClassOf ica:risk_factors" + "}";
		
		Model model = ICAOnto.getBaseModel() ;
		//model.write(System.out, "RDF/XML-ABBREV") ;
		Query query = QueryFactory.create(queryString) ;
		QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		ResultSet results = qexec.execSelect() ;
		String riskfactor = null ;
		String riskfactorCat = null ;

		for ( ; results.hasNext() ; )
	    {
	      QuerySolution soln = results.nextSolution() ;
	      riskfactor = soln.get("?concept").toString() ; 	
	      riskfactorCat= soln.get("?riskfactCat").toString() ;
	      break;
	    }
		
		Resource recPrimaryConcept= null ;
		OntClass recRiskfactor = null ;
		Resource recCatRiskfactor = null ;
		if ( ( recPrimaryConcept= OntoGraph.getOntClass(pcptURI) ) != null && riskfactor != null)
		{
			// create property
			final Property has_riskfactor = ResourceFactory.createProperty(lo + "has_riskfactor") ;//
			final Property frequency = ResourceFactory.createProperty(lo + "frequency") ;
			
			String rfURI = riskfactor ;
			String rgCatURI = riskfactorCat ;
			
			// the riskfactor exist 
			if (( recRiskfactor = OntoGraph.getOntClass(rfURI))  != null  &&   ( recCatRiskfactor = OntoGraph.getOntClass(rgCatURI))  != null )
			{
			    // update  Probability_values 
				
				final Property p2 = ResourceFactory.createProperty(lo + "frequency") ;
				Statement st = recRiskfactor.getProperty(p2) ;
				RDFNode node = st.getObject();
				long value = node.asLiteral().getLong() ;
				st = st.changeLiteralObject(value+1) ;

				
			}
			else
			{
                // adding the new concept and the label
				String uri = classToOnto (rfURI,concept,OntoGraph);
				recRiskfactor = OntoGraph.getOntClass(uri) ;
				recRiskfactor.addLiteral(frequency, 1);
				recRiskfactor.addSuperClass(recCatRiskfactor);
				recPrimaryConcept.addProperty(has_riskfactor,recRiskfactor);

			}
			
			Scoring.setOccurence_Probability(rfURI, recPrimaryConcept, OntoGraph);
			Scoring.Tier_Rank(rfURI, recPrimaryConcept, OntoGraph);
		}

	}
	
	
	public static void ComorbidToOnto (String Primaryconcept,Map<String, String> concepts,OntModel OntoGraph) throws IOException, ParseException
	{
		
		String cptURI = bioportal.getConceptID(Primaryconcept);
		Map<String, Integer> semTypeCp =  bioportal.getSemanticTypes(Primaryconcept);
		
		
		System.out.println("ComorbidToOnto");
		Resource r = null ; 
		Resource r1 = null ;
		for (String cp:  concepts.keySet())
		{
			// don't add relation for itself
			if(cp.equalsIgnoreCase(Primaryconcept))
				continue ; 
			
			Map<String, Integer> semType =  bioportal.getSemanticTypes(cp);
			
			if ( semTypeCp.size() == 1 && semTypeCp.containsKey("T047"))
			{
				
				String conceptURI = bioportal.getConceptID(cp);
				if ( ( r= OntoGraph.getOntClass(cptURI) ) != null)
				{
					final Property p = ResourceFactory.createProperty(lo + "has_Comorbid") ;
					
					
					if (( r1 = OntoGraph.getOntClass(conceptURI) ) != null)
					{
						r.addProperty(p,r1);
					}
					else
					{
						String uri = classToOnto (cp,OntoGraph);
						r1 = OntoGraph.getOntClass(uri) ;
						r.addProperty(p,r1);
					}
					
				}				
			}
		}

			
	}
	

	
		public static void ComorbidToOnto1 (String Primaryconcept,Map<String, String> concepts,OntModel OntoGraph) throws IOException, ParseException
		{
			
			String cptURI = bioportal.getConceptID(Primaryconcept);
			Map<String, Integer> semTypeCp =  bioportal.getSemanticTypes(Primaryconcept);
			
			
			System.out.println("ComorbidToOnto");
			Resource r = null ; 
			Resource r1 = null ;
			// loop of other concepts 
			for (String cp:  concepts.keySet())
			{
				// don't add relation for itself
				if(cp.equalsIgnoreCase(Primaryconcept))
					continue ; 
				
				// retrive the semantic type 
				Map<String, Integer> semType =  bioportal.getSemanticTypes(cp);
				
				// disease 
				if ( semType.containsKey("T047"))
				{
					
					
					if ( ( r= OntoGraph.getOntClass(cptURI) ) != null)
					{
						String queryString=
								"PREFIX p: <http://dbpedia.org/property/>"+
								"PREFIX dbpedia: <http://dbpedia.org/resource/>"+
								"PREFIX category: <http://dbpedia.org/resource/Category:>"+
								"PREFIX lo: <http://www.lifeOnto.org/lifeOnto#>" +
								"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
	 
								"Select ?condition ?value where { <" + cptURI + "> "  + "has_disease_association " +  "?Comorbid_Relation." + 
								        "?Comorbid_Relation lo:Disorder ?condition." +
								"?Comorbid_Relation lo:Probability_values ?value }";
						
                        
						final Property p = ResourceFactory.createProperty(lo + "has_disease_association") ;//
						
						// we need to check if this relation already exist in graph before we added it
						
						Model model = OntoGraph.getBaseModel() ;
						//model.write(System.out, "RDF/XML-ABBREV") ;
						Query query = QueryFactory.create(queryString) ;
						QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
						ResultSet results = qexec.execSelect() ;
						for ( ; results.hasNext() ; )
					    {
					      QuerySolution soln = results.nextSolution() ;
					      
					      String Comorbid_Relation = soln.get("?obj").asLiteral().getString() ; 
					      int i = 0 ; 
					      
					    }
						
						
						Resource  BlankNodeComorbid_Relation = OntoGraph.createResource() ;
						r.addProperty(p,BlankNodeComorbid_Relation) ; 
					
						String conceptURI = bioportal.getConceptID(cp);
						final Property p2 = ResourceFactory.createProperty(lo + "Disorder") ;
						final Property p3 = ResourceFactory.createProperty(lo + "Probability_values") ;
						
						if (( r1 = OntoGraph.getOntClass(conceptURI) ) != null)
						{
						
							BlankNodeComorbid_Relation.addProperty(p2,r1);
							BlankNodeComorbid_Relation.addLiteral(p3, 0);
						}
						else
						{
							String uri = classToOnto (cp,OntoGraph);
							r1 = OntoGraph.getOntClass(uri) ;
							BlankNodeComorbid_Relation.addProperty(p2,r1);
							BlankNodeComorbid_Relation.addLiteral(p3, 0);

						}
						
					}				
				}

				
			}
		}
		
		
		
	/*public static void getontoSemanticType (Map<String, Dataset> lookupresources)
	{
		System.out.println("getontoSemanticType");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		graph.setNsPrefix( "rdf", rdf ) ;
   			String uri = ""; 
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
   			
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
	   		}
	   		
	   		if (uri.isEmpty())
	   			   continue ; 
	   		
	   		// set the lexical alt label
	   		 List<String> Category = dataset.Category ;
	   		if (Category != null)
	   		{
	   			for (String Definition: Category)
	   			{
	   				Resource rec = graph.createResource(uri);
 	        		// add the property
	 	         	final Property p = ResourceFactory.createProperty(rdf  + "type") ;
	 	         	rec.addProperty(p, Definition);
	   			}
	   			
	   		}
	   		
   	 	}
	}
	


	
	public static void getontosameAs (Map<String, Dataset> lookupresources)
	{
		
		System.out.println("getontosameas");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		
   			String topuri = ""; 
   			Map<String, Double> uriconfident = dataset.gettopuriconfident() ;
	   		for (String onto: uriconfident.keySet())
	   		{
    			topuri = onto ;
	   		}
	   		
	   		if (topuri.isEmpty())
	   			   continue ; 
	   		
	   		
	   		
	   		graph.setNsPrefix( "owl", owl ) ;
   			
   			List<String> Topuriconfident = dataset.getTopBesturiconfident(3,0.5) ;
   			
   			if (Topuriconfident.size() > 1 )
   			{

		   		int count = 0 ; 
		   		for (String tempuri: Topuriconfident)
		   		{
		   			count++ ;
	    			if (count == 1)
	    				continue ; 
	    			
	    			
	    			
	    			
	    			// create sameas relation 
	    			String[] uri  = tempuri.split("!", 2) ;
	    			if(uri.length > 0 )
	    			{
		   				Resource rec = graph.createResource(topuri);
	 	        		// add the property
		 	         	final Property p = ResourceFactory.createProperty(owl  + "sameAs") ;
		 	         	rec.addProperty(p, uri[0]);
	    			}
	    			
		   		}
   			}
	   		
   	 	}
	}
	

	public static void getontodefinition (Map<String, Dataset> lookupresources)
	{
		
		System.out.println("getontodefinition");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		graph.setNsPrefix( "skos", skos ) ;
   			String uri = ""; 
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
	   		}
	   		if (uri.isEmpty())
	   			   continue ; 
	   		
	   		// set the lexical alt label
	   		 List<String> Definitions = dataset.Definition ;
	   		if (Definitions != null)
	   		{
	   			for (String Definition: Definitions)
	   			{
	   				Resource rec = graph.createResource(uri);
 	        		// add the property
	 	         	final Property p = ResourceFactory.createProperty( skos + "definition") ;
	 	         	rec.addProperty(p, Definition);
	   			}
	   			
	   		}
	   		
   	 	}
	}
	
	public static void getontoscheme (Map<String, Dataset> lookupresources)
	{
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		graph.setNsPrefix( "skos", skos ) ;
   			String uri = ""; 
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
	   		}
	   		
	   		if (uri.isEmpty())
	   			   continue ;  
	   		
	   		// set the lexical alt label
	   		 List<String> scheme = dataset.ontology ;
	   		if (scheme != null)
	   		{
	   			for (String label: scheme)
	   			{
	   				Resource rec = graph.createResource(uri);
 	        		// add the property
	 	         	final Property p = ResourceFactory.createProperty(skos  + "inScheme") ;
	 	         	rec.addProperty(p, label);
	   			}
	   			
	   		}
	   		
   	 	}
	}
	public static void getontoPreflabel (Map<String, Dataset> lookupresources)
	{
		System.out.println("getontoPreflabel");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		graph.setNsPrefix( "skos", skos ) ;
   			String uri = ""; 
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
   			
   			
   			if (Topuriconfident.size() == 0 )
   			   continue ; 	
   			 
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
	   		}
   			
	   		if (uri.isEmpty())
	   			   continue ; 
	   		
   			Map<String,List<String>> syn = new HashMap<String, List<String>>();
   			String PrefLabel = dataset.PrefLabel ;
   			
   			if (PrefLabel == null )
   				continue ; 
   			String tokens[] = PrefLabel.split(" ") ;
			Resource rec = graph.createResource(uri);
			Resource rec1 = graph.createResource(tokens[0]);
    		// add the property
         	final Property p = ResourceFactory.createProperty(skos+ "PrefLabel") ;
         	rec.addProperty(p, rec1);	
         	final Property p1 = ResourceFactory.createProperty(skos + "PrefLabel") ;
         	String Label = tokens[2] ;
         	
         	System.out.println(Label);
         	Label = Label.replace(")", " ") ;
         	Label = Label.replace("(", " ") ;
         	Label = Label.trim() ;
         	rec1.addProperty(p, Label);
   	 	}
	}
	
	public static void getontoSynonym (Map<String, Dataset> lookupresources)
	{
		System.out.println("getontoSynonym");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		graph.setNsPrefix( "skos", skos ) ;
   			String uri = ""; 
   			
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
	   		}
	   		
	   		if (uri.isEmpty())
	   			   continue ; 
	   		
   			Map<String,List<String>> syn = new HashMap<String, List<String>>();
   			List<String> Syns = dataset.Synonym ;
   			if (Syns == null )	
   				continue ;
   			
   			
   			double max = 0.0 ;
   			List<String> alts = new ArrayList<String>()  ;
   			
	    	for (String synon: Syns)
	    	{    		
	    		String[] words = synon.split("!");  
	    		
	    		if (syn.containsKey(words[1].toLowerCase()))
	    		{
	    			alts = syn.get(words[1].toLowerCase());
	    			if (!alts.contains(words[2].toLowerCase()))  
	    			{
	    				alts.add(words[2].toLowerCase()) ;
	    			}
	    		}
	    		else
	    		{
	    			alts.add(words[2].toLowerCase()) ;
	    			syn.put(words[1].toLowerCase(), alts) ;
	    		}

	    	}
	    	
	    	for (String label: syn.keySet())
	    	{
   				Resource rec = graph.createResource(uri);
	        		// add the property
 	         	final Property p = ResourceFactory.createProperty(skos + "altLabel") ;
 	         	rec.addProperty(p, label);
	    		
	    		
	    		
	    	}
   	 	}
	}
	
	
	public static void getontoassociate (Map<String, Dataset> lookupresources)
	{
		 Map<String, Dataset> tempresources = new HashMap<String, Dataset>();
		 tempresources.putAll(lookupresources);
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			System.out.println("******************" + concept + "******************");
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getGraph();
	   		Model candidategraph = dataset.getcandidateGraph() ;
			// list the statements in the Model
			StmtIterator iter =  graph.listStatements();
	   		
	    	while (iter.hasNext())
			{
			    Statement stmt      = iter.nextStatement();  // get next statement
			    Resource  subject   = stmt.getSubject();     // get the subject
			    Property  predicate = stmt.getPredicate();   // get the predicate
			    RDFNode   object    = stmt.getObject();      // get the object
			    for (String conceptin: tempresources.keySet()) 
			    {
			    	
			    	if (concept.equals(conceptin))
			    		continue ; 
			   		Dataset datasetin = lookupresources.get(concept) ;
			   		Model graphin = dataset.getGraph();
					// list the statements in the Model
					StmtIterator iterin =  graph.listStatements();
					while (iterin.hasNext())
					{
					    Statement stmtin      = iterin.nextStatement();  // get next statement
					    Resource  subjectin   = stmtin.getSubject();     // get the subject
					    Property  predicatein = stmtin.getPredicate();   // get the predicate
					    RDFNode   objectin    = stmtin.getObject();      // get the object
					    
					    // add a resource
					    if (object.toString().equals(subjectin.toString()) )
					    {
			   				Resource rec = candidategraph.createResource(subject);
		 	        		// add the property
			 	         	final Property p = ResourceFactory.createProperty(predicate.toString()) ;
			 	         	rec.addProperty(p, subjectin.toString());
			 	         	System.out.println(object.toString() + ", " + predicate.toString() + ", " + subjectin.toString());
					    }
					    
					    // add a resource
					    if(object.toString().equals(objectin.toString()) && predicate.toString().equals(predicatein))
					    {
			   				Resource rec = candidategraph.createResource(subject);
		 	        		// add the property
			 	         	final Property p = ResourceFactory.createProperty(predicate.toString()) ;
			 	         	rec.addProperty(p, subjectin.toString());
			 	         	System.out.println(object.toString() + ", " + predicate.toString() + ", " + subjectin.toString());
					    }
			    	
					}

			    }
	   		
   	 		}
   	 	}
	}
	
	
	public static void getontoPropertyHierarchy (String uri,String property ,Model graph) throws IOException
	{
		System.out.println("***********************************getontoHierarchy************************************************");

			
	   		graph.setNsPrefix( "skos", skos ) ;
	   		graph.setNsPrefix( "rdfs", rdfs ) ;
	   		
	   		
	   		
	   		List<String> Hierarchy = Enrichment.LLDHierarchyProperty(uri,property)  ;
	   		
	   		if (Hierarchy != null)
	   		{
	   			for (int i = Hierarchy.size()-2 ; i > -1; i--)
	   			{
	   				String hier = Hierarchy.get(i) ;
	   				String tokens[] = hier.split("!") ;
	   				Resource child = graph.createResource(uri);
	   				Resource parent = graph.createResource(tokens[0]);
	   				
 	        		// add the property
	 	         	final Property p = ResourceFactory.createProperty( rdfs + "subPropertyOf") ;
	 	         	child.addProperty(p, parent);
	 	         	
	 	         	
	 	         	final Property pp = ResourceFactory.createProperty(rdfs + "label") ;
	 	         	parent.addProperty(pp, tokens[1]);
	 	         	uri = tokens[0] ;
	   			}
	   			
	   		}
	   		

	}*/

}
