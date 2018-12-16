package NER;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import util.NGramAnalyzer;
import util.ReadXMLFile;
import util.bioportal;
import util.readfiles;
import util.removestopwords;
import NLP.preProcessing;

public class Engine {

	
	static Map<String, String> goldConceptsfound = new HashMap<String, String>();
	
	public static void main(String[] args) throws Exception {
		
		
		String filename3 = "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\ClinicalNote\\CN_1GoldStandard.txt" ;
		String filenameq = "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\ClinicalNote\\CN_1.txt" ;
		File fXmlFileq = new File(filenameq);
		String CNq = readfiles.readLinestostring(fXmlFileq.toURL());
		List<String> sentencesq = preProcessing.getSentences(CNq) ; 
		
		for (String sentence: sentencesq)
		{
			readfiles.Writestringtofile("<sentence>", filename3);
			readfiles.Writestringtofile("	<Text>" + sentence + "</Text>", filename3);
			readfiles.Writestringtofile("	<Concept>" + "????????|????????????" + "</concept>", filename3);
			readfiles.Writestringtofile("</sentence>", filename3);
		} 
		
		
		boolean longmatch = true ; 
		//getIntracranialAneurysmAvaluation() ;
		String text = "Diabetes is a chronic condition associated with abnormally high levels of glucose in the blood" ;
		String Mfilename = "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\ClinicalNote\\CN2concepts.txt" ;
		File mfXmlFile = new File(Mfilename);
		String filename = "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\ClinicalNote\\CN 2R.txt" ;
		File fXmlFile = new File(filename);
		String CN = readfiles.readLinestostring(fXmlFile.toURL());
		// TODO Auto-generated method stub
		Map<String, Integer> mentions = new HashMap<String, Integer>();
		Map<String, Integer> concepts = new HashMap<String, Integer>();
		Map<String, Integer> longmatchcncepts = new HashMap<String, Integer>();
		Map<String, Integer> lmCconcepts = new HashMap<String, Integer>();
		List<String> sentences = preProcessing.getSentences(CN) ; 
		
/*		String filename3 = "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\ClinicalNote\\CNGoldStandard.txt" ;
		for (String sentence: sentences)
		{
			readfiles.Writestringtofile("<sentence>", filename3);
			readfiles.Writestringtofile("	<Text>" + sentence + "</Text>", filename3);
			readfiles.Writestringtofile("	<Concept>" + "????????|????????????" + "</concept>", filename3);
			readfiles.Writestringtofile("</sentence>", filename3);
		} */
		
		List<String>  listSG = new ArrayList<String>();
		listSG.add("Chemicals & Drugs") ;
		listSG.add("Disorders") ;
		listSG.add("Devices") ;
		listSG.add("Physiology") ;
		listSG.add("Anatomy") ;
		listSG.add("Physiology") ;
		listSG.add("Activities & Behaviors") ;
		double Totrecall = 0 ; 
		double Totpreciation = 0 ; 
		for (String sentence: sentences)
		{
			text = removestopwords.removestopwordfromsen(sentence) ;
			mentions = NGramAnalyzer.entities(1,3, text) ;	
			concepts.clear();
			
			// get concepts from linked life data
		   // concepts.putAll(ontologyMapping.getAnnotation(mentions,listSG)) ; 
		    
		    // get concept from bioportal 
			for (String mention : mentions.keySet())
			{
				if (bioportal.isConceptWithspecficSG(mention)) 
				   concepts.put(mention.toLowerCase(), 1); 
			} 
			  
			
			// Measurement 
			List<String> GoldStandard = ReadXMLFile.ReadAneCN("C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\ClinicalNote\\CNGoldStandardJ.xml",sentence) ; 
			
			double recall = 0 ; 
			double preciation = 0 ; 

			int conceptscount  =  0  ;
			double hits = 0 ;
			
			if (GoldStandard != null || (GoldStandard.size() != 0  && concepts.size() != 0 ) )
			{
				conceptscount = GoldStandard.size() ;
				for (String concept: concepts.keySet())
				{
					
					for (String Golds: GoldStandard)
					{
						if ( Golds.toLowerCase().contains(concept))
						{
							hits++ ; 
							break ; 
						}
					}
				}
				
				if (conceptscount != 0)
				{
					if (hits <= conceptscount)
						recall = hits/conceptscount ; 
					else
						recall = 1 ; 
				}
				
				if (concepts.size() != 0 )
					preciation = hits/concepts.size() ; 
				
				Totrecall += recall ; 
				Totpreciation += preciation ; 
			}	
		}
		
		System.out.println("Recall : " + Totrecall/sentences.size());
		System.out.println("preciation : " + Totpreciation/sentences.size());
		
		// do only long match , so we here remove all concepts that part of long match concept
		//******************************************************************************************
/*		if (longmatch )
		{
			    lmCconcepts.putAll(concepts);
				Map<String, Integer> Mconcepts = readfiles.readLinesbylinesToMap(mfXmlFile.toURL()) ;
				int count = 0 ; 
				 
				for (String concept: concepts.keySet())
				{
					boolean found = false ;
					
					for (String lmCconcept: lmCconcepts.keySet())
					{
						if (lmCconcept.length() > concept.length() &&  lmCconcept.contains(concept))
						{
							found = true ; 
							break; 
						}
					}
					
					if(!found)
						longmatchcncepts.put(concept.toLowerCase(), 1) ;
				}
				concepts.clear();
				concepts.putAll(longmatchcncepts);
		}*/
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		

		/*Map<String, Integer> Mconcepts = readfiles.readLinesbylinesToMap(mfXmlFile.toURL()) ;
		int count = 0 ; 
		for (String concept: concepts.keySet())
		{
			bioportal.isConceptWithspecficSG(concept) ;
		}
		System.out.println(bioportal.ActivitiesBehaviors);
		System.out.println(bioportal.Anatomy);
		System.out.println(bioportal.ChemicalsDrugs);
		System.out.println(bioportal.ConceptsIdeas);
		System.out.println(bioportal.Devices);
		System.out.println(bioportal.Disorders);
		System.out.println(bioportal.GenesMolecularSequences);
		System.out.println(bioportal.GeographicAreas);
		System.out.println(bioportal.LivingBeing);
		System.out.println(bioportal.Objects);
		System.out.println(bioportal.Occupations);
		System.out.println(bioportal.Organizations);
		System.out.println(bioportal.Phenomena);
		System.out.println(bioportal.Physiology);
		System.out.println(bioportal.Procedures);
		System.out.println(bioportal.others);

        
		bioportal.ActivitiesBehaviors = 0 ;
		bioportal.Anatomy = 0 ; 
		bioportal.ChemicalsDrugs = 0 ;
		bioportal.ConceptsIdeas = 0 ;
		bioportal.Devices = 0;
		bioportal.Disorders = 0;
		bioportal.GenesMolecularSequences = 0;
		bioportal.GeographicAreas = 0;
		bioportal.LivingBeing = 0;
		bioportal.Objects = 0;
		bioportal.Occupations= 0 ;
		bioportal.Organizations = 0 ;
		bioportal.Phenomena = 0 ;
		bioportal.Physiology = 0 ;
		bioportal.Procedures = 0 ;
		bioportal.others= 0 ;
        
		// measurement 
		
		
		
		
		for (String concept: concepts.keySet())
		{

			
			if(Mconcepts.containsKey(concept.toLowerCase()))
			{
				//bioportal.isConceptWithspecficSG(concept) ;
				count++ ;
			}
			else
			{
				bioportal.printSG(concept) ;
			}
		}
		
		System.out.println(bioportal.ActivitiesBehaviors);
		System.out.println(bioportal.Anatomy);
		System.out.println(bioportal.ChemicalsDrugs);
		System.out.println(bioportal.ConceptsIdeas);
		System.out.println(bioportal.Devices);
		System.out.println(bioportal.Disorders);
		System.out.println(bioportal.GenesMolecularSequences);
		System.out.println(bioportal.GeographicAreas);
		System.out.println(bioportal.LivingBeing);
		System.out.println(bioportal.Objects);
		System.out.println(bioportal.Occupations);
		System.out.println(bioportal.Organizations);
		System.out.println(bioportal.Phenomena);
		System.out.println(bioportal.Physiology);
		System.out.println(bioportal.Procedures);
		System.out.println(bioportal.others);
        System.out.println(concepts);
		
		//getSemRepAvaluation() ;
*/	}
	
	
	/*************************************************************************************************************************************************************************/
	/************************************************************ using SemRep Gold Standard Annotation : https://semrep.nlm.nih.gov/GoldStandard.html *******************************************************************************************/
	/*************************************************************************************Annotator C: Adjudication (adjudicated.xml) 
	 * @throws IOException ************************************************************************************/
	
	 public static void getSemRepAvaluation() throws IOException
	  {
		  Map<String, List<String>> sentence =  ReadSemRepGoldStandard()  ; 
		  
		  getmeasureSemRep(sentence);
		  
	  }
	  public static void getIntracranialAneurysmAvaluation() throws IOException
	  {
		  Map<String, List<String>> sentence =  ReadIntracranialAneurysmGoldStandard()  ; 
		  
		  getmeasureSemRep(sentence);
		  
	  }
	  
	  public static Map<String, List<String>> ReadIntracranialAneurysmGoldStandard() {

		    Map<String, List<String>> goldstandard = null ;
		    goldstandard =  null ;
	        int count = 0 ; 
		    if (goldstandard== null )
		    try {

	       String filename = "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\data\\Intracranialaneurysm.xml" ;


		    goldstandard  = new HashMap<String, List<String>>();
			File fXmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
	        
			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			NodeList rowList = doc.getElementsByTagName("ROW");

			for (int i = 0; i < rowList.getLength()  ; i++)
			{
				List<String> conclist = new ArrayList<String>() ;
				NodeList rowchildren = rowList.item(i).getChildNodes();
				
				// get the predications
				String Sentence = null ; 
				String subject = null ;
				String object = null ;
				for (int j = 0; j < rowchildren.getLength(); j++)
				{
					String sent = rowchildren.item(j).getNodeName() ;
					
					if ("SENTENCE".equals(rowchildren.item(j).getNodeName())) 
					{
						Sentence = rowchildren.item(j).getTextContent() ;
					}
					if ("SUBJECT_NAME".equals(rowchildren.item(j).getNodeName())) 
					{
						subject = rowchildren.item(j).getTextContent() ;
					}
					if ("OBJECT_NAME".equals(rowchildren.item(j).getNodeName())) 
					{
						object = rowchildren.item(j).getTextContent() ;
					}
	
				}
				
				if (Sentence != null)
				{
					count ++ ;
					conclist.add(subject);
					conclist.add(object) ;
					goldstandard.put(Sentence + " " + Integer.toString(count), conclist) ;
					 
				}
				else
				{
					conclist.add(subject);
				}
				
				
				//goldstandard.put(sentence, conclist) ;
			}

		    } 
		    catch (Exception e) {
			e.printStackTrace();
		    }
		    
		    return goldstandard;
		  } ;
	  public static Map<String, List<String>> ReadSemRepGoldStandard() {

		    Map<String, List<String>> goldstandard = null ;
		    goldstandard =  null ;
		    if (goldstandard== null )
		    try {

	       String filename = "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\data\\adjudicated.xml" ;


		    goldstandard  = new HashMap<String, List<String>>();
			File fXmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
	        
			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			NodeList sentenceList = doc.getElementsByTagName("Sentence");
	        int count = 0 ; 
			for (int i = 0; i < sentenceList.getLength()  ; i++)
			{
				List<String> conclist = new ArrayList<String>() ;
				NodeList predicationsList = sentenceList.item(i).getChildNodes();
				String sentence = sentenceList.item(i).getAttributes().item(2).getTextContent() ;
				
				// get the predications
				for (int j = 0; j < predicationsList.getLength(); j++)
				{
					NodeList predicationList = predicationsList.item(j).getChildNodes();
					 // get the predication
					for (int j1 = 0; j1 < predicationList.getLength(); j1++)
					{
						NodeList sub_objList = predicationList.item(j1).getChildNodes();
						// subject or object 
						for (int j2 = 0; j2 < sub_objList.getLength(); j2++)
						{
							if ("Subject".equals(sub_objList.item(j2).getNodeName()) || "Object".equals(sub_objList.item(j2).getNodeName()))
							{
								String concept = sub_objList.item(j2).getAttributes().item(1).getTextContent() ;
								conclist.add(concept.toLowerCase()) ;
							}
						}
						
					}
	
				}
				
				
				goldstandard.put(sentence, conclist) ;
			}

		    } 
		    catch (Exception e) {
			e.printStackTrace();
		    }
		    return goldstandard;
		  } ;
	
		  
		  
		  public static String getmeasureSemRep(Map<String, List<String>> Sentences) throws IOException
			{
				

//				double avgRecall = 0.0  ; 
//				double avgPrecision = 0.0 ;
//				double avgFmeasure = 0.0 ; 
//				int size_ = titles.size() ;
				int counter = 0 ; 
				measure result = null ;
				measure synResult = null ;
				measure ansResult = null ;
				measure allcResult = new measure() ;  
				measure allsynResult = new measure() ;  
				measure allansResult = new measure() ;  
				measure totResult = new measure() ;
				for(String Sentence : Sentences.keySet())
				{
					counter++  ; 
					// get concepts of the Sentence 
					List<String> GoldSndconcepts = Sentences.get(Sentence); 
					
					try {
						
						
						Map<String, Integer> concepts = new HashMap<String, Integer>();
						Map<String, Integer> synConcepts = new HashMap<String, Integer>();
						Map<String, Integer> ansConcepts = new HashMap<String, Integer>();
						Map<String, Integer> keywords = new HashMap<String, Integer>();
						Sentence  = removestopwords.removestopwordfromsen(Sentence ) ;
						keywords = NGramAnalyzer.entities(1,4, Sentence) ;
						//concepts = ontologyMapping.getAnnotation(mentions)  ;
						for(String keyword: keywords.keySet())
						{
							Map<String, Integer> mentions  = new HashMap<String, Integer>(); 
							mentions.put(keyword, 1);
							Map<String, String> allconcepts = null ;
							allconcepts = bioportal.getConcepts(mentions)  ;
							Map<String, String> ccpts = new HashMap<String, String>();
							
							for ( String cpt : allconcepts.keySet())
							{
								if (!cpt.isEmpty())
								{
									concepts.put(keyword, 1) ;
								} 
							}
							
						}
						result = getPRF(Sentence,concepts,GoldSndconcepts) ; 
						// with no syn and ans
						allcResult.avgRecall =  result.avgRecall +  allcResult.avgRecall ;
						allcResult.avgPrecision =  result.avgPrecision  + allcResult.avgPrecision ;
						allcResult.avgFmeasure =  result.avgFmeasure  + allcResult.avgFmeasure ;
						
	/*					synResult = getPRF(title,synConcepts,GoldSndconcepts) ; 
						
						allsynResult.avgRecall =  synResult .avgRecall +  allsynResult.avgRecall ;
						allsynResult.avgPrecision =  synResult .avgPrecision  + allsynResult.avgPrecision ;
						allsynResult.avgFmeasure =  synResult .avgFmeasure  + allsynResult.avgFmeasure ;
						
						ansResult = getPRF(title,ansConcepts,GoldSndconcepts) ; 
						
						allansResult.avgRecall =  ansResult.avgRecall +  allansResult.avgRecall ;
						allansResult.avgPrecision =  ansResult.avgPrecision  + allansResult.avgPrecision ;
						allansResult.avgFmeasure =  ansResult.avgFmeasure  + allansResult.avgFmeasure ;
						
						totResult.avgRecall =  result.avgRecall +  synResult.avgRecall + ansResult.avgRecall + totResult.avgRecall ;
						totResult.avgPrecision =  result.avgPrecision +  synResult.avgPrecision + ansResult.avgPrecision + totResult.avgPrecision  ;
						totResult.avgFmeasure =  result.avgFmeasure+  synResult.avgFmeasure+ ansResult.avgFmeasure  + totResult.avgFmeasure;*/
						

						
						
						
		                
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				allcResult.avgRecall =  allcResult.avgRecall  / Sentences.size() ;
				allcResult.avgPrecision =  allcResult.avgPrecision / Sentences.size() ;
				allcResult.avgFmeasure =  allcResult.avgFmeasure / Sentences.size() ;
				
	/*			allsynResult.avgRecall =  allsynResult.avgRecall  / titles.size() ;
				allsynResult.avgPrecision =  allsynResult.avgPrecision / titles.size() ;
				allsynResult.avgFmeasure =  allsynResult.avgFmeasure / titles.size() ;
				
				allansResult.avgRecall =  allansResult.avgRecall  / titles.size() ;
				allansResult.avgPrecision =  allansResult.avgPrecision / titles.size() ;
				allansResult.avgFmeasure =  allansResult.avgFmeasure / titles.size() ;
				
				
				totResult.avgRecall = totResult.avgRecall  / titles.size() ;
				totResult.avgPrecision = totResult.avgPrecision  / titles.size() ;
				totResult.avgFmeasure = totResult.avgFmeasure / titles.size() ;*/
				
				String output = Double.toString(allcResult.avgRecall) + " " +  Double.toString(allcResult.avgPrecision) +" " +  Double.toString(allcResult.avgFmeasure) ;
				ReadXMLFile.Serializeddiectionary(goldConceptsfound, "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\data\\GoldnotFound.xml");
				return output;
			}
	/*************************************************************************************************************************************************************************/
	/************************************************************ Evaluation Part 
	 * @throws IOException *******************************************************************************************/
	  public static void getAvaluation() throws IOException
	  {
		  Map<String, List<String>> titles =  ReadCDR_TestSet_BioC()  ; 
		  String result = getmeasureRPF( titles) ;
		  System.out.println(result);
		  
	  }
	  
	  public static String getmeasure(Map<String, List<String>> titles)
		{
			

			double avgRecall = 0.0  ; 
			double avgPrecision = 0.0 ;
			double avgFmeasure = 0.0 ; 
			int size_ = titles.size() ;
			int counter = 0 ; 
			for(String title : titles.keySet())
			{
				counter++  ; 
				// get concepts of the title 
				List<String> GoldSndconcepts = titles.get(title); 
				
				try {
					
					
					Map<String, Integer> concepts = new HashMap<String, Integer>();
					Map<String, Integer> mentions = new HashMap<String, Integer>();
					
					
					title  = removestopwords.removestopwordfromsen(title ) ;
					mentions = NGramAnalyzer.entities(1,3, title ) ;
					// removed to run the gui
					//concepts = ontologyMapping.getAnnotation(mentions)  ;


					String[] arr = new String[concepts.size()] ;
					int i = 0 ; 
					for( String concept : concepts.keySet())
					{
						arr[i] = concept.toLowerCase() ;
						i++ ; 
					}

					// measure the recall precision and  F-measure 
					double relevent = 0 ;
					for( String concept : arr)
					{
	                   if (GoldSndconcepts.contains(concept.toLowerCase()))
	                   {
	                	   relevent++ ; 
	                   }
						
					}
					
					// calculate the Recall 
					//For example for text search on a set of documents recall is the number of correct results divided by the number of results that should have been returned
					double recall = 0.0 ; 
					if (GoldSndconcepts.size() > 0  )
					{
						recall = relevent / GoldSndconcepts.size() ;
					}

					avgRecall = recall  + avgRecall; 
					
					double precision = 0 ; 
					if ( arr.length > 0  )
					   precision = relevent / arr.length ;
					
					 avgPrecision += precision ;	
					
					double Fmeasure  = 0.0 ;
					if (precision + recall > 0 )
					   Fmeasure = 2* ((precision * recall) / (precision + recall)) ;
					   avgFmeasure = Fmeasure + avgFmeasure ;

	                
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			avgRecall = avgRecall / titles.size() ;
			avgPrecision = avgPrecision / titles.size() ;
			avgFmeasure = avgFmeasure / titles.size() ;
			
			String result = Double.toString(avgRecall) + " " +  Double.toString(avgPrecision) +" " +  Double.toString(avgFmeasure) ;
			return result ;
		}
	  
	public static Map<String, List<String>> ReadCDR_TestSet_BioC() {

	    Map<String, List<String>> goldstandard = null ;
	    goldstandard =  null ;
	    if (goldstandard== null )
	    try {

       String filename = "F:\\eclipse64\\eclipse\\CDR_TestSet.BioC.xml" ;


	    goldstandard  = new HashMap<String, List<String>>();
		File fXmlFile = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
        
		//optional, but recommended
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();

		NodeList passageList = doc.getElementsByTagName("passage");
        int count = 0 ; 
		for (int i = 0; i < passageList.getLength()  ; i++)
		{
			List<String> conclist = new ArrayList<String>() ;
			NodeList childList = passageList.item(i).getChildNodes();
			for (int j = 0; j < childList.getLength(); j++)
			{
				if ("infon".equals(childList.item(j).getNodeName()) && "title".equals(childList.item(j).getTextContent()))
				{
					String title = null ; 
					NodeList childList1 = passageList.item(i).getChildNodes();
					for (int kk = 0; kk < childList1.getLength(); kk++)
					{
						 System.out.println(childList1.item(kk).getNodeName());
						if ("text".equals(childList1.item(kk).getNodeName()))
						{
							 System.out.println(childList1.item(kk).getTextContent()
					                    .trim());
							title = childList1.item(kk).getTextContent()
				                    .trim().toLowerCase() ;

							continue ; 
						}
						
						if ("annotation".equals(childList1.item(kk).getNodeName()))
						{
							NodeList childList2 = childList.item(kk).getChildNodes();
							for (int kkk = 0; kkk < childList2.getLength(); kkk++)
							{
								if ("text".equals(childList2.item(kkk).getNodeName()))
								{
									 conclist.add(childList2.item(kkk).getTextContent().trim().toLowerCase()) ;
									 System.out.println(childList2.item(kkk).getTextContent()
							                    .trim());

								}
							}

						}
					}
					
					goldstandard.put(title,conclist) ;
					
				}
			}
           
		}

	    } 
	    catch (Exception e) {
		e.printStackTrace();
	    }
	    return goldstandard;
	  } ;
	  
	  
	  // prefer
	  
	  public static String getmeasureRPF(Map<String, List<String>> titles) throws IOException
		{
			

//			double avgRecall = 0.0  ; 
//			double avgPrecision = 0.0 ;
//			double avgFmeasure = 0.0 ; 
//			int size_ = titles.size() ;
			int counter = 0 ; 
			measure result = null ;
			measure synResult = null ;
			measure ansResult = null ;
			measure allcResult = new measure() ;  
			measure allsynResult = new measure() ;  
			measure allansResult = new measure() ;  
			measure totResult = new measure() ;
			for(String title : titles.keySet())
			{
				counter++  ; 
				// get concepts of the title 
				List<String> GoldSndconcepts = titles.get(title); 
				
				try {
					
					
					Map<String, Integer> concepts = new HashMap<String, Integer>();
					Map<String, Integer> synConcepts = new HashMap<String, Integer>();
					Map<String, Integer> ansConcepts = new HashMap<String, Integer>();
					Map<String, Integer> keywords = new HashMap<String, Integer>();
					title  = removestopwords.removestopwordfromsen(title ) ;
					keywords = NGramAnalyzer.entities(1,6, title ) ;
					//concepts = ontologyMapping.getAnnotation(mentions)  ;
					for(String keyword: keywords.keySet())
					{
						if (ontologyMapping.getKeywordAnnotation(keyword) )
						{
							if (ontologyMapping.getSemanticGroupTypeDISO_CHEM(keyword) )
							{
								concepts.put(keyword, 1) ;
							}
						}
/*						else if (ontologyMapping.getKeywordSynAnnotation(keyword))
						{
							synConcepts.put(keyword, 1) ;
						}
						else if (ontologyMapping.getKeywordAnsAnnotation(keyword))
						{
							ansConcepts.put(keyword, 1) ;
						}*/
						
					}
					result = getPRF(title,concepts,GoldSndconcepts) ; 
					// with no syn and ans
					allcResult.avgRecall =  result.avgRecall +  allcResult.avgRecall ;
					allcResult.avgPrecision =  result.avgPrecision  + allcResult.avgPrecision ;
					allcResult.avgFmeasure =  result.avgFmeasure  + allcResult.avgFmeasure ;
					
/*					synResult = getPRF(title,synConcepts,GoldSndconcepts) ; 
					
					allsynResult.avgRecall =  synResult .avgRecall +  allsynResult.avgRecall ;
					allsynResult.avgPrecision =  synResult .avgPrecision  + allsynResult.avgPrecision ;
					allsynResult.avgFmeasure =  synResult .avgFmeasure  + allsynResult.avgFmeasure ;
					
					ansResult = getPRF(title,ansConcepts,GoldSndconcepts) ; 
					
					allansResult.avgRecall =  ansResult.avgRecall +  allansResult.avgRecall ;
					allansResult.avgPrecision =  ansResult.avgPrecision  + allansResult.avgPrecision ;
					allansResult.avgFmeasure =  ansResult.avgFmeasure  + allansResult.avgFmeasure ;
					
					totResult.avgRecall =  result.avgRecall +  synResult.avgRecall + ansResult.avgRecall + totResult.avgRecall ;
					totResult.avgPrecision =  result.avgPrecision +  synResult.avgPrecision + ansResult.avgPrecision + totResult.avgPrecision  ;
					totResult.avgFmeasure =  result.avgFmeasure+  synResult.avgFmeasure+ ansResult.avgFmeasure  + totResult.avgFmeasure;*/
					

					
					
					
	                
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			allcResult.avgRecall =  allcResult.avgRecall  / titles.size() ;
			allcResult.avgPrecision =  allcResult.avgPrecision / titles.size() ;
			allcResult.avgFmeasure =  allcResult.avgFmeasure / titles.size() ;
			
/*			allsynResult.avgRecall =  allsynResult.avgRecall  / titles.size() ;
			allsynResult.avgPrecision =  allsynResult.avgPrecision / titles.size() ;
			allsynResult.avgFmeasure =  allsynResult.avgFmeasure / titles.size() ;
			
			allansResult.avgRecall =  allansResult.avgRecall  / titles.size() ;
			allansResult.avgPrecision =  allansResult.avgPrecision / titles.size() ;
			allansResult.avgFmeasure =  allansResult.avgFmeasure / titles.size() ;
			
			
			totResult.avgRecall = totResult.avgRecall  / titles.size() ;
			totResult.avgPrecision = totResult.avgPrecision  / titles.size() ;
			totResult.avgFmeasure = totResult.avgFmeasure / titles.size() ;*/
			
			String output = Double.toString(allcResult.avgRecall) + " " +  Double.toString(allcResult.avgPrecision) +" " +  Double.toString(allcResult.avgFmeasure) ;
			
			return output;
		}
	  public static measure getPRF(String  titles, Map<String, Integer> concepts,List<String> GoldSndconcepts)
	  {
			
		measure result = new measure() ; 
		result.Recall = 0.0  ; 
		result.Precision = 0.0 ;
		result.Fmeasure = 0.0 ; 
		try {

			String[] arr = new String[concepts.size()] ;
			int i = 0 ; 
			for( String concept : concepts.keySet())
			{
				arr[i] = concept.toLowerCase() ;
				i++ ; 
			}

			// measure the recall precision and  F-measure 
			double relevent = 0 ;
			for( String concept : arr)
			{
				
			   for( String GoldSndconcept : GoldSndconcepts)
			   {
	               if (GoldSndconcept.equalsIgnoreCase(concept.toLowerCase()))
	               {
	            	   relevent++ ; 
	            	   goldConceptsfound.put(GoldSndconcept, titles) ;
	               }
			   }

			}
			
			// calculate the Recall 
			//For example for text search on a set of documents recall is the number of correct results divided by the number of results that should have been returned
			double recall = 0.0 ; 
			if (GoldSndconcepts.size() > 0  )
			{
				recall = relevent / GoldSndconcepts.size() ;
			}

			result.Recall = recall ; 
			result.avgRecall = recall  + result.avgRecall ; 
			
			double precision = 0 ; 
			if ( arr.length > 0  )
			   precision = relevent / arr.length ;
			
			result.Precision = precision ;	
			result.avgPrecision = precision + result.avgPrecision ;  
			
			double Fmeasure  = 0.0 ;
			if (precision + recall > 0 )
			   Fmeasure = 2* ((precision * recall) / (precision + recall)) ;
			result.Fmeasure = Fmeasure  ;
			result.avgFmeasure = Fmeasure + result.avgFmeasure  ;
            
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result ;
	}

}
