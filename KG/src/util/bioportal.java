package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.annotation.JsonView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.swing.JList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.restcalls;

public class bioportal{
	static final String REST_URL = "http://data.bioontology.org";
    static final String API_KEY = "cc3d58df-1850-49f0-a0c2-795ad7640622";
    static final ObjectMapper OBJ_MAPPER = new ObjectMapper();
    static final ObjectWriter writer = OBJ_MAPPER.writerWithDefaultPrettyPrinter();
    
	public static int Anatomy = 0 ; 
	public static int ChemicalsDrugs = 0 ; 
	public static int Disorders = 0 ; 
	public static int Devices = 0 ; 
	public static int Physiology = 0 ; 
	public static int LivingBeing = 0 ; 
	public static int Objects = 0 ; 
	public static int Organizations = 0 ; 
	public static int ActivitiesBehaviors = 0 ; 
	public static int ConceptsIdeas = 0 ; 
	public static int GenesMolecularSequences = 0 ; 
	public static int GeographicAreas = 0 ; 
	public static int Occupations = 0 ; 
	public static int Phenomena = 0;
	public static int Procedures = 0 ;
	public static int others = 0 ; 
    
    
	public static void main(String[] args) throws JsonProcessingException, IOException {
	System.out.println(getSemanticTypes("melanoma"));

	}
	
	// consider the keyword as concept if the return search has class type 
	public static Map<String, String>  getConcepts(Map<String, Integer> keywords,JList SGT) 
    {
		Map<String, String>  concepts = new HashMap<String, String>();
		
		for(String keyword: keywords.keySet())
		{
			if (isConcept(keyword) )
			{
				Map<String, Integer> semGp = getSemanticGroup(keyword);
				String groups = "" ; 
				for(String gr:semGp.keySet())
				{
					for(Object type:SGT.getSelectedValuesList())
					{
						if (type.toString().equals(gr) )
						{
							groups += gr + " " ; 
						}
								
					}
					 
				}
				
				if( !groups.isEmpty())
					concepts.put(keyword, groups);
			}
		}
		
		return concepts ;

    }
	
	

	public static Map<String, String>  getConcepts(Map<String, Integer> keywords) 
    {
		Map<String, String>  concepts = new HashMap<String, String>();
		
		for(String keyword: keywords.keySet())
		{
			if (keyword.length() > 1 && !StringUtils.isNumeric(keyword) && !keyword.isEmpty() && isConcept(keyword) )
			{
				Map<String, Integer> semGp = getSemanticGroup(keyword);
				String groups = "" ; 
				for(String gr:semGp.keySet())
				{
					groups += gr + " " ; 
					 
				}
				concepts.put(keyword, groups);
			}
		}
		
		return concepts ;

    }
	// consider the keyword as concept if the return search has class type 
	public static boolean  isConcept(String keyword) 
    {
		 keyword  =  keyword.replace(" ", "%20");
		String prefLabel="";
		JsonNode annotations;
		annotations = jsonToNode(get(REST_URL + "/search?q=" +  keyword + "&require_exact_match=true"));
		if (annotations == null )
			return false ;
		List<JsonNode>	 classes= (List<JsonNode>) annotations.get("collection").findValues("@type");
		if( classes.isEmpty() )
		{
			return false ;
		}
		return true;	
    }
	
	
	// consider the keyword as concept if the return search has class type 
		public static boolean  isConceptWithspecficSG(String keyword) 
	    {
			if (keyword.length() <= 1 || StringUtils.isNumeric(keyword) )
				return false ; 
			
			 keyword  =  keyword.replace(" ", "%20");
			String prefLabel="";
			JsonNode annotations;
			annotations = jsonToNode(get(REST_URL + "/search?q=" +  keyword + "&require_exact_match=true"));
			if (annotations == null )
				return false ;
			List<JsonNode>	 classes= (List<JsonNode>) annotations.get("collection").findValues("@type");
			if( classes.isEmpty() )
			{
				return false ;
			}
			
			Map<String, Integer> semGp = getSemanticGroup(keyword);
			String groups = "" ; 

            boolean flg = false ; 
			for(String gr:semGp.keySet())
			{
				
				if (gr.equals("Anatomy"))
				{
					flg = true ; 
					Anatomy++ ;
				}
					 
				else if (gr.equals("Chemicals & Drugs"))
				{
					flg = true ; 
				   ChemicalsDrugs++;
				}
				else if (gr.equals("Disorders"))
				{
					flg = true ; 
					Disorders++;	
				}
				else if (gr.equals("Devices"))
				{
					flg = true ; 
					Devices++;
				}
				else if (gr.equals("Physiology"))
				{
					flg = true ; 
					Physiology++;
				}
				/*else if (gr.equals("Living Beings"))
					LivingBeing++;
				else if (gr.equals("Objects"))
					Objects++;
				else if (gr.equals("Organizations"))
					Organizations++;*/
				else if (gr.equals("Activities & Behaviors"))
				{
					flg = true ; 
					ActivitiesBehaviors++;
				}
			/*	else if (gr.equals("Concepts & Ideas"))
					ConceptsIdeas++;
				else if (gr.equals("Occupations"))
					Occupations++;
				else if (gr.equals("Genes & Molecular Sequences"))
					GenesMolecularSequences++;
				else if (gr.equals("Geographic Areas"))
					GeographicAreas++;*/
				/*else if (gr.equals("Phenomena"))
					Phenomena++;
				else if (gr.equals("Procedures"))
					Procedures++;*/


				 
				 
			}
			
			if ( flg)
			{
				return true ;
			}
			
			return false;	
	    }
		
		// consider the keyword as concept if the return search has class type 
		public static boolean  printSG(String keyword) throws IOException 
	    {
			if (keyword.length() <= 1 || StringUtils.isNumeric(keyword) )
				return false ; 
			
			 keyword  =  keyword.replace(" ", "%20");
			String prefLabel="";
			JsonNode annotations;
			annotations = jsonToNode(get(REST_URL + "/search?q=" +  keyword + "&require_exact_match=true"));
			if (annotations == null )
				return false ;
			List<JsonNode>	 classes= (List<JsonNode>) annotations.get("collection").findValues("@type");
			if( classes.isEmpty() )
			{
				return false ;
			}
			
			Map<String, Integer> semGp = getSemanticGroup(keyword);
			String groups = "" ; 

			String filename = "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\ClinicalNote\\notIdentified2.txt" ;
			boolean flg = false ; 
			for(String gr:semGp.keySet())
			{
				
				if (gr.equals("Anatomy"))
				{
					flg = true ; 
					readfiles.Writestringtofile(keyword + " -> Anatomy", filename);
				}
				if (gr.equals("Chemicals & Drugs"))
				{
					flg = true ; 
					readfiles.Writestringtofile(keyword + " -> Chemicals & Drugs", filename);
				}
				if (gr.equals("Disorders"))
				{
					flg = true ; 
					
					readfiles.Writestringtofile(keyword + " -> Disorders", filename);
				}
				if (gr.equals("Devices"))
				{
					flg = true ; 
					readfiles.Writestringtofile(keyword + " -> Devices", filename);
				}
				if (gr.equals("Physiology"))
				{
					flg = true ; 
					readfiles.Writestringtofile(keyword + " -> Physiology", filename);
				}
				/*else if (gr.equals("Living Beings"))
					readfiles.Writestringtofile(keyword + " ->Living Beings", filename);
				else if (gr.equals("Objects"))
					readfiles.Writestringtofile(keyword + " -> Objects", filename);
				else if (gr.equals("Organizations"))
					readfiles.Writestringtofile(keyword + " -> Organizations", filename);
				else if (gr.equals("Activities & Behaviors"))
					readfiles.Writestringtofile(keyword + " -> Activities & Behaviors", filename);
				else if (gr.equals("Concepts & Ideas"))
					readfiles.Writestringtofile(keyword + " ->Concepts & Ideas", filename);
				else if (gr.equals("Occupations"))
					readfiles.Writestringtofile(keyword + " -> Occupations", filename);
				else if (gr.equals("Genes & Molecular Sequences"))
					readfiles.Writestringtofile(keyword + " -> Genes & Molecular Sequences", filename);
				else if (gr.equals("Geographic Areas"))
					readfiles.Writestringtofile(keyword + " -> Geographic Areas", filename);*/
				if (gr.equals("Phenomena"))
				{
					flg = true ; 
					readfiles.Writestringtofile(keyword + " -> Phenomena", filename);
				}
				if (gr.equals("Procedures"))	
				{
					flg = true ; 
				
					readfiles.Writestringtofile(keyword + " -> Procedures", filename);
				}
				 
			}
			
			if (flg) 
			{
				return true ; 
			}
			
			return false;	
	    }
	
	
	public static String  getConceptID(String keyword) 
    {
		 keyword  =  keyword.replace(" ", "%20");
		String prefLabel="";
		JsonNode annotations;
		annotations = jsonToNode(get(REST_URL + "/search?q=" +  keyword + "&require_exact_match=true"));
		List<JsonNode>	 ids= (List<JsonNode>) annotations.get("collection").findValues("@id");
		if( ids.isEmpty() )
		{
			return null ;
		}
		
		// return first one 
		for(JsonNode id: ids)
		{
			if( !id.asText().contains("#") );
			  return id.asText() ;
			
		}
		return null ; 
    }
	
	//get the prefLabels for the keyword
	public static String  getPrefLabels (String keyword) 
    {
		//Map<String, Integer> PrefLabels   = new HashMap<String, Integer>();
		 keyword  =  keyword.replace(" ", "%20");
		String prefLabel="";
		JsonNode annotations;
		annotations = jsonToNode(get(REST_URL + "/search?q=" +  keyword + "&require_exact_match=true"));
		List<JsonNode>	 preflable = (List<JsonNode>) annotations.get("collection").findValues("prefLabel");
		for(JsonNode pref: preflable)
		{ 
			if (!pref.toString().contains("http://") )
				return pref.asText() ;
		}
		return null;	
    }
	
	//get the Synonyms for the keyword
	public static Map<String, Integer>  getSynonyms (String keyword) 
    {
		Map<String, Integer> Synonyms   = new HashMap<String, Integer>();
		 keyword  =  keyword.replace(" ", "%20");
		JsonNode annotations;
		annotations = jsonToNode(get(REST_URL + "/search?q=" +  keyword + "&require_exact_match=true"));
		List<JsonNode>	 synonyms = (List<JsonNode>) annotations.get("collection").findValues("synonym");
		for(JsonNode syn: synonyms)
		{ 
	          for (Iterator<JsonNode> iterator = syn.elements(); iterator.hasNext(); ) 
	          {
	                String excludedPropertyName = iterator.next().asText();
	                Synonyms.put(excludedPropertyName, 1) ;
	          }
				
		}
		return Synonyms;	
    }
	
	//get the Synonyms for the keyword
	public static Map<String, Integer>  getDefinitions(String keyword) 
    {
		Map<String, Integer> Definitions   = new HashMap<String, Integer>();
		 keyword  =  keyword.replace(" ", "%20");
		JsonNode annotations;
		annotations = jsonToNode(get(REST_URL + "/search?q=" +  keyword + "&require_exact_match=true"));
		List<JsonNode>	 definitions = (List<JsonNode>) annotations.get("collection").findValues("definition");
		for(JsonNode def: definitions)
		{ 
	          for (Iterator<JsonNode> iterator = def.elements(); iterator.hasNext(); ) 
	          {
	                String excludedPropertyName = iterator.next().asText();
	                Definitions.put(excludedPropertyName, 1) ;
	          }
				
		}
		return Definitions;	
    }
	

	public static Map<String, Integer>  getSemanticTypes(String keyword) 
    {
		Map<String, Integer> SemanticTypes   = new HashMap<String, Integer>();
		 keyword  =  keyword.replace(" ", "%20");
		JsonNode annotations;
		annotations = jsonToNode(get(REST_URL + "/search?q=" +  keyword + "&require_exact_match=true"));
		if(annotations != null)
		{
			List<JsonNode>	 semanticTypes = (List<JsonNode>) annotations.get("collection").findValues("semanticType");
			for(JsonNode st: semanticTypes)
			{ 
		          for (Iterator<JsonNode> iterator = st.elements(); iterator.hasNext(); ) 
		          {
		                String excludedPropertyName = iterator.next().asText();
		                SemanticTypes.put(excludedPropertyName, 1) ;
		          }
					
			}
		}
		return SemanticTypes;	
    }
	
	
	public static String  getSemanticTypeNames(String semTypeID) 
    {
		String semTypeName = "" ;
		
		
		{
			Map<String, String> semantictype = ReadXMLFile.Deserializeddiectionar("C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\SemanticTypeDir.dat") ;
			
			semTypeName  = semantictype.get(semTypeID) ;
		}
		
		return semTypeName ;	
    }
	//get the Synonyms for the keyword
	public static Map<String, Integer>  getSemanticGroup(String keyword) 
    {
		Map<String, Integer> SemanticTypes   = new HashMap<String, Integer>();
		Map<String, Integer> SemanticGp   = new HashMap<String, Integer>();
		 keyword  =  keyword.replace(" ", "%20");
		JsonNode annotations;
		annotations = jsonToNode(get(REST_URL + "/search?q=" +  keyword + "&require_exact_match=true"));
		List<JsonNode>	 semanticTypes = (List<JsonNode>) annotations.get("collection").findValues("semanticType");
		for(JsonNode st: semanticTypes)
		{ 
	          for (Iterator<JsonNode> iterator = st.elements(); iterator.hasNext(); ) 
	          {
	                String excludedPropertyName = iterator.next().asText();
	                SemanticTypes.put(excludedPropertyName, 1) ;
	          }
				
		}
		
		Map<String, String> semanticgroup = ReadXMLFile.Deserializeddiectionar("C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\SemanticGroupDir.dat") ;
		for (String type:SemanticTypes.keySet())
		{
			String group = semanticgroup.get(type); 
			
			if (group != null && !group.isEmpty())
			{
				SemanticGp.put(group, 1) ;
			}
		}
		
		return SemanticGp;	
    }
	
	public static Map<String, Integer>  getSameas(String keyword,String URI) 
    {
		Map<String, Integer> sameas   = new HashMap<String, Integer>();
		 keyword  =  keyword.replace(" ", "%20");
		String prefLabel="";
		JsonNode annotations;
		annotations = jsonToNode(get(REST_URL + "/search?q=" +  keyword + "&require_exact_match=true"));
		List<JsonNode>	 ids= (List<JsonNode>) annotations.get("collection").findValues("@id");
		if( ids.isEmpty() )
		{
			return null ;
		}
		
		// return first one 
		for(JsonNode id: ids)
		{

			if( !id.asText().contains("#") && !id.asText().equalsIgnoreCase(URI))
			{
				sameas.put(id.asText(), 1) ;
			}
			
		}
		return sameas ; 
    }
	
	public static List<String> getTaxonomic(String keyword, int limit) throws MalformedURLException, IOException, ParseException 
	{
		
		keyword  =  keyword.replace(" ", "%20");
		String prefLabel="";
		JsonNode annotations;
		String Jsonstring  = get(REST_URL + "/search?q=" +  keyword + "&require_exact_match=true") ;
		JSONParser parser = new JSONParser();
		List<String> Hierarchy  = new ArrayList<String>() ;
        try 
        {

            System.out.println("Start");
            {
            
	            // Jackson JSON – Read Specific JSON Key
	            
	          //create ObjectMapper instance
	            ObjectMapper objectMapper = new ObjectMapper();
	            
	            //read JSON like DOM Parser
	            JsonNode rootNode = objectMapper.readTree(Jsonstring);
	            
	            JsonNode idNode = rootNode.path("collection");
	            System.out.println("value= "+ idNode.fieldNames());
	            
	            
	            Iterator<JsonNode> elements = idNode.elements();
	            int counter = 0 ; 
	            while(elements.hasNext())
	            {
	            	JsonNode value = elements.next();
	            	System.out.println("element  = "+ value.toString());
	            	 JsonNode lnkNode = value.path("links");
	            	 System.out.println("element  = "+lnkNode.toString());
	            	 JsonNode ancNode = lnkNode.path("ancestors");
	            	 System.out.println("element  = "+ancNode.toString());
	            	 // get all collection of anc from URL
	            	 String JsonsAnctring = restcalls.httpGetBio(ancNode.textValue()+"?"); 
	            	 if (JsonsAnctring== null )
	            	 {
	            		 continue ; 
	            	 }
	            	 System.out.println("element  = "+ JsonsAnctring);
	            	 
	            	 //loop to get ancs from bottom to top 
	 	            //read JSON like DOM Parser
	 	            JsonNode ancrootNode = objectMapper.readTree(JsonsAnctring);
	 	            Iterator<JsonNode>ancelements = ancrootNode.elements();
	 	            while(ancelements.hasNext())
	 	           {
		            	JsonNode ancvalue = ancelements.next();
		            	JsonNode anclabelNode =ancvalue.path("prefLabel");
		            	Hierarchy.add(anclabelNode.textValue());
		            	System.out.println("element  = "+ anclabelNode.textValue());
	 	           }
	            	 
	 	           System.out.println("************************************************");
	            	counter++ ;
	            	if(counter == limit )
	            	{
	            		// done and leave 
	            		break ; 
	            	}
	            }
	          //  String name = (String) jsonObject.get("entropy").toString();
	          //  String rel = (String) jsonObject.get("toprelation").toString();
            }
            System.out.println("done");
        }
        finally
        {
        	
        }
        
        return Hierarchy ;
	}
	
	public static String annotator2(String Concept) throws JsonProcessingException, IOException
    {
	String prefLabel="";
 	JsonNode annotations;
 	Stack st = new Stack();
	annotations = jsonToNode(get(REST_URL + "/search?q=" + Concept + "&require_exact_match=true"));	
	List<JsonNode>	 link= (List<JsonNode>) annotations.get("collection").findValues("links");
//	System.out.println(link);
	for(JsonNode linkx:link)
	{   st.push(linkx.findValue("parents"));
	//	System.out.println(linkx.findValue("parents"));
	 //   System.out.println(st.size());
	}
	if (st.isEmpty()==false)
	{
	while (!(st.empty()))
	{
	String x= st.pop().toString();
//	System.out.println(x);
	x=x.replaceAll("\"", "");
//	System.out.println(x);
	annotations= jsonToNode(get(x));
	Stack final_Link=new Stack();
	while (annotations.hasNonNull(0))
	{
	  
	//  System.out.println(annotations);
	  JsonNode	 link1=   annotations.get(0).findValue("parents");
	  String y=link1.toString();
	  final_Link.push(annotations);
	  y=y.replaceAll("\"", "");
//	  System.out.println("y="+ y);
	  annotations= jsonToNode(get(y));
//	  System.out.println(annotations);
	  
	}
	if (final_Link.isEmpty()==false)
	{
	annotations=(JsonNode) final_Link.pop();
	JsonNode	 link2=   annotations.get(0).findValue("prefLabel");
    prefLabel= link2.toString();
  //  System.out.println(prefLabel);
	}
	} 
	}
	return prefLabel;
    }
private static JsonNode jsonToNode(String json) {
	        if (json == null) {
	            return null;
	        }
	        JsonNode root = null;
	        try {
	            root = OBJ_MAPPER.readTree(json);
	        } catch (JsonProcessingException e) {
	            return null;
	        } catch (IOException e) {
	            return null;
	        } catch (Exception e) {
	            return null;
	        }
	        return root;
	    }
public static String get(String urlToGet) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(urlToGet);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "apikey token=" + API_KEY);
            conn.setRequestProperty("Accept", "application/json");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            return null;
        }
        return result;
    }

}
