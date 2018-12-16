package HRCHY;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import util.NGramAnalyzer;
import util.readfiles;
import NLP.preProcessing;

public class SyntaticPattern {

	public static void main(String[] args) throws MalformedURLException, IOException {
		// TODO Auto-generated method stub
		getSyntaticPattern(null, null) ; 
	}
	
	public static ArrayList<String> getSyntaticPattern(String orgSentence, Map<String, Integer> allconcepts) throws MalformedURLException, IOException
	{
		File filename  = new File("F:\\Freepal\\medicalFreepaltest.txt") ;
		List<String> patts = readfiles.readLinesbylines(filename.toURL()) ;
		ArrayList<String> RelInstances =  new ArrayList<String>();
		for (String patt : patts)
		{
			String RelationInstance = null ; 
			String Sentence = orgSentence ;
			patt = patt.toLowerCase() ;
			String[] tokens = patt.split("!");
			RelationInstance= "" ;// tokens[1] + "," ;
			patt = tokens[0];
			patt = patt.replace("[x]", "!") ; 
			patt = patt.replace("[y]", "!") ;
			patt = patt.replace("[", "!") ;
			String[] toks = patt.trim().split("!") ;
			String startP =  toks[0].trim() ;
			String middleP = toks[1].trim() ;
			String endP = toks[2].trim() ;
			Sentence = Sentence.trim() ;
			if ( !startP.isEmpty() )
			{
				// if not empty then it should be there otherwise we skip to next pattern
				if (StringUtils.containsIgnoreCase(Sentence, startP + " ")/*|| StringUtils.containsIgnoreCase( NLPEngine.getLemma(Sentence), startP + " ")*/)
				{
					Sentence = Sentence.replace(startP, "!") ;
					String[] tokss = Sentence.split("!") ;
					if (tokss.length > 1) 
						Sentence = tokss[1] ;
					else
						Sentence = "" ;
				}
				else
				{
					continue ; 
				}
			}
			else
			{
				
				Sentence = Sentence ; 
			}
			
			Sentence = Sentence.trim().replaceAll("\\s+", " ") ;		
			String Tokens[] = Sentence.split(middleP) ; 
			Map<String, Integer> mentions = new HashMap<String, Integer>();
			mentions = NGramAnalyzer.entities(1,5, Tokens[0] + middleP ) ;
			Boolean foundconcept = false ; 
			String bestmatch1 = "";
			for ( String tok : mentions.keySet())
			{
				if (allconcepts.containsKey(tok) )
				{
					if ( bestmatch1.length() < tok.length())
					{
						bestmatch1 = tok ;
						foundconcept = true ; 
						//break ;
					}
				}
			}
			
			if ( foundconcept)
			{
				RelationInstance = RelationInstance + bestmatch1 + ",";

			}
			
			// no concept found 
			if (!foundconcept)
				continue ; 
			
			Sentence = Sentence.trim() ;
			if ( !middleP.isEmpty() )
			{
				String temp = preProcessing.getlemma(Sentence) ;
				// if not empty then it should be there otherwise we skip to next pattern
				if (StringUtils.containsIgnoreCase(Sentence," " + middleP + " ") || StringUtils.containsIgnoreCase(Sentence, middleP + " ") /*|| StringUtils.containsIgnoreCase(NLPEngine.getLemma(Sentence), middleP + " ")*/)
				{
					//Sentence = Sentence.replace(middleP, "!") ;
					String[] tokss = Sentence.split(middleP) ;
					if (tokss.length > 1) 
						Sentence = tokss[1] ;
					else
						Sentence = "" ;
					 
				}
				else
				{
					continue ; 
				}
			}
			else
			{
				Sentence = Sentence.trim() ; 
			}
			
			
			Sentence = Sentence.trim() ;
			// find the second encounter concept 
			String[] tokenSs = Sentence.split(" ") ;
			mentions = NGramAnalyzer.entities(1,5, Sentence) ;
			Boolean foundconcept2 = false ; 
			String bestmatch = "";
			for ( String tok : mentions.keySet())
			{
				if (allconcepts.containsKey(tok) )
				{
					if ( bestmatch.length() < tok.length())
					{
						bestmatch = tok ;
        				foundconcept2 = true ; 
						//break ;
					}
				}
			}
			
			
			if ( foundconcept2)
			{
				RelationInstance = RelationInstance + bestmatch + ",";
				Sentence = Sentence.replace(bestmatch , "!") ;
				String[] tokss = Sentence.split("!") ;
				if (tokss.length > 1) 
					Sentence = tokss[1] ;
				else
					Sentence = "" ;
				
				foundconcept2 = true ; 
				//break ;
			}
			
			
			
			// no concept found 
			if (!foundconcept2)
				continue ; 
			
			Sentence = Sentence.trim() ;
			if ( !endP.isEmpty() )
			{
				// if not empty then it should be there otherwise we skip to next pattern
				if (!StringUtils.containsIgnoreCase(Sentence," " + endP))
				{
					continue ;
				}
			}
			else
			{
				Sentence = Sentence ; 
			}
			
			RelationInstance = RelationInstance + tokens[2].trim();
			RelInstances.add(RelationInstance) ;
			System.out.println("found") ;
				
			
			
			
			
	
		}
		return RelInstances; 
	}
	
	public static ArrayList<String> getSyntaticRel(String orgSentence, String cpt1, String cpt2) throws MalformedURLException, IOException
	{
		File filename  = new File("F:\\Freepal\\medicalFreepaltest.txt") ;
		List<String> patts = readfiles.readLinesbylines(filename.toURL()) ;
		ArrayList<String> RelInstances =  new ArrayList<String>();
		for (String patt : patts)
		{		
			System.out.println(patt);
			patt = patt.toLowerCase() ;
			String[] tokens = patt.split("!");
			patt = tokens[0];
			patt = patt.replace("[x]", "!") ; 
			patt = patt.replace("[y]", "!") ;
			patt = patt.replace("[", "!") ;
			String[] toks = patt.trim().split("!") ;
			String startP =  toks[0].trim() ;
			String middleP = toks[1].trim() ;
			String endP = toks[2].trim() ;
			
			if( !startP.isEmpty() && middleP.isEmpty() && endP.isEmpty())
			{

				Pattern pattern = Pattern.compile(startP);
				Matcher matcher = pattern.matcher(orgSentence);
				
				if (matcher.find())
				{
					String sections[] = orgSentence.split(startP); 
					
					if (( sections[0].contains(cpt1) && sections[0].contains(cpt2)))
					{
						RelInstances.add("<" + cpt1 +","+ tokens[2] + "," + cpt2 + ">"); 
					}
				}
			}
			else if( startP.isEmpty() && !middleP.isEmpty() && endP.isEmpty())
				
				{
					Pattern pattern = Pattern.compile(middleP);
					Matcher matcher = pattern.matcher(orgSentence);
					
					if (matcher.find())
					{
						String sections[] = orgSentence.split(middleP); 
						
						if (sections.length >= 2 && sections[0].contains(cpt1) && sections[1].contains(cpt2))
						{
							RelInstances.add("<" + cpt1 +","+ tokens[2] + "," + cpt2 + ">"); 
						}
						else if (sections.length >= 2 && sections[0].contains(cpt2) && sections[1].contains(cpt1))
						{
							RelInstances.add("<" + cpt2 +","+ tokens[2] + "," + cpt1 + ">");
						}
					}
				}
			else if( startP.isEmpty() && middleP.isEmpty() && !endP.isEmpty())
			{
				Pattern pattern = Pattern.compile(endP);
				Matcher matcher = pattern.matcher(orgSentence);
				
				if (matcher.find())
				{
					String sections[] = orgSentence.split(endP); 
					if ((sections[0].contains(cpt1) && sections[0].contains(cpt2)))
					{
						RelInstances.add("<" + cpt1 +","+ tokens[2] + "," + cpt2 + ">"); 
					}
				}
			}
			else if( !startP.isEmpty() && !middleP.isEmpty() && endP.isEmpty())
			{
				Pattern pattern = Pattern.compile(startP +".*"+ middleP);
				Matcher matcher = pattern.matcher(orgSentence);
				
				if (matcher.find())
				{
					String sections[] = orgSentence.split(startP +".*"+ middleP);
					String firstSection = orgSentence.replaceAll(sections[1], ""); 
					
					if ((firstSection.contains(cpt1) && sections[1].contains(cpt2)))
					{
						RelInstances.add("<" + cpt1 +","+ tokens[2] + "," + cpt2 + ">"); 
					}
					else if ((firstSection.contains(cpt2) && sections[1].contains(cpt1)))
					{
						RelInstances.add("<" + cpt2 +","+ tokens[2] + "," + cpt1 + ">"); 
					}
						
				}
			}
			else if( startP.isEmpty() && !middleP.isEmpty() && !endP.isEmpty())
			{
				Pattern pattern = Pattern.compile(middleP +".*"+ endP);
				Matcher matcher = pattern.matcher(orgSentence);
				
				if (matcher.find())
				{
					String sections[] = orgSentence.split(middleP +".*"+ endP);
					String secondSection = matcher.group() ; //orgSentence.replaceAll(sections[0], ""); matcher. 
					
					if ((sections[0].contains(cpt1) && secondSection.contains(cpt2) ))
					{
						RelInstances.add("<" + cpt1 +","+ tokens[2] + "," + cpt2 + ">"); 
					}
					else if ((sections[0].contains(cpt2) && secondSection.contains(cpt1) ))
					{
						RelInstances.add("<" + cpt2 +","+ tokens[2] + "," + cpt1 + ">"); 
					}
						
				}
			}
	
		}
		return RelInstances; 
	}

}
