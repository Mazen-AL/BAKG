package view;

import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;

import java.awt.EventQueue;
import java.awt.SystemColor;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JButton;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.JScrollPane;

import org.json.simple.parser.ParseException;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import HRCHY.SyntaticPattern;
import NER.ontologyMapping;
import NER.umlsMapping;
import ONTO.BioPontologyfactory;
import util.NGramAnalyzer;
import util.ReadXMLFile;
import util.bioportal;
import util.dataExtractor;
import util.removestopwords;

public class Testgui {
	
	JFrame frmLifeonto;
	private JTextField domainKeyword;
	private JTable titleTable;
	private JTextField maxTitles;
	private JTextField key_API;
	private JButton btnExtract;
	private JComboBox resource;
	private JComboBox retrievField;
	private JButton btnSave;
	private JButton btnOpen;
	private JPanel panel_2;
	private JTextField Sentence;
	private JTable tableConcepts;
	private JList SGT;
	private JCheckBox chckbxUseSataExtraction;
	private JButton btnExtractConcept;
	private JLabel lblNgram;
	private JTextField ngramMax;
	private JLabel lblMin;
	private JTextField ngramMin;
	private JPanel panel_3;
	private JPanel panel_4;
	private JCheckBox chckbxLOD;
	private JCheckBox checkBoxUMLS;	
	private JButton btnHrchyExtraction;
	private JTree tree;
	private JCheckBox chckbxBio;
	private JTree treeSyn;
	private JButton btnSyn;
	private JTable tableAssc;
	private JButton btnAssc ;
	private JCheckBox chckbxontoMode;
	private JButton btnLearn;
	private JButton btnSaveOnto;
	private JTextArea textOnto;
	
	List<String> listofTitles_Abstract = null ; 
	MetaMapApi api = null ; 

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Testgui window = new Testgui();
					window.frmLifeonto.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	private void createSetting()
	{
		        
		        checkBoxUMLS.addActionListener(new ActionListener(){
	        	@Override
	            public void actionPerformed(ActionEvent e) {

	        		
	        		api = new MetaMapApiImpl();
	        		try {
		        		List<String> theOptions = new ArrayList<String>();
		        	    theOptions.add("-y");  // turn on Word Sense Disambiguation
		        	    theOptions.add("-u");  //  unique abrevation 
		        	    //theOptions.add("--negex");  
		        	    theOptions.add("-v");
		        	    theOptions.add("-l");
		        	    theOptions.add("-c");   // use relaxed model that  containing internal syntactic structure, such as conjunction.
		        	    if (theOptions.size() > 0) {
		        	      api.setOptions(theOptions);
		        	      api.processCitationsFromString("mazin") ;
		        	    }
	        		}
	        		catch(Exception e1)
	        		{
	        			 JOptionPane.showMessageDialog(null, "Start MetaMap Server before enabling this method", "Setting" , JOptionPane.OK_CANCEL_OPTION);
	        			 checkBoxUMLS.setSelected(false) ;
	        		}
	        		
	         }
	        });
		
	}
	private void createDataExtraction()
	{
        btnExtract.addActionListener(new ActionListener(){
        	@Override
            public void actionPerformed(ActionEvent e) {

              if ( key_API.getText().isEmpty())
              {
            	  JOptionPane.showMessageDialog(null, "Bioportal KEY_API is required", "Bioportal KEY_API is required" , JOptionPane.OK_CANCEL_OPTION);
              }
              else
              {
            	  // do data extraction
            	  if(domainKeyword.getText().isEmpty())
            	  {
            		  JOptionPane.showMessageDialog(null, "Domain of interst Keyword Required", "Domain of interst Keyword Required" , JOptionPane.OK_CANCEL_OPTION);
            	  }
            	  else
            	  {
            		  try {
            			  listofTitles_Abstract = dataExtractor.dataExtraction(domainKeyword.getText(), maxTitles.getText());
            			  int count = 0 ;
            			  DefaultTableModel defaultModel = (DefaultTableModel) titleTable.getModel();
            			  defaultModel.getDataVector().removeAllElements();
            			  for (String tit : listofTitles_Abstract)
            			  {
            				    count++ ; 
            				    Object [] row = new Object [2];
            				    row[0] = Integer.toString(count);
            				    row[1] = tit ;
            				   
            				    defaultModel.addRow(row);
            			  }
            			  
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
            	  }
            	  
              }
            }
        });

        
        btnSave.addActionListener(new ActionListener(){
        	@Override
            public void actionPerformed(ActionEvent e) {
        		
        		
                if ((listofTitles_Abstract == null || listofTitles_Abstract.isEmpty()))
                {
              	  JOptionPane.showMessageDialog(null, "No Data was extracted ", "Data Extraction" , JOptionPane.OK_CANCEL_OPTION);
                }
                else
                {
	        	       //Create a file chooser
	        		JFileChooser fc = new JFileChooser();
	        		
	        		
	        		
	        		int returnVal = fc.showSaveDialog(null);
	                if (returnVal == JFileChooser.APPROVE_OPTION) 
	                {
	                    File file = fc.getSelectedFile();
	                    //This is where a real application would save the file.
	                    try {
							ReadXMLFile.Serializeddir(listofTitles_Abstract, file.getAbsolutePath());
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	                    
	                } 
                }
        		

            }
        });
        
        
        
        btnOpen.addActionListener(new ActionListener(){
        	@Override
            public void actionPerformed(ActionEvent e) {
	        	       //Create a file chooser
	        		JFileChooser fc = new JFileChooser();
	
	        		int returnVal = fc.showSaveDialog(null);
	                if (returnVal == JFileChooser.APPROVE_OPTION) 
	                {
	                    File file = fc.getSelectedFile();
	                    listofTitles_Abstract = ReadXMLFile.Deserializedirlis(file.getAbsolutePath());int count = 0 ;
						  DefaultTableModel defaultModel = (DefaultTableModel) titleTable.getModel();
						  defaultModel.getDataVector().removeAllElements();
						  for (String tit : listofTitles_Abstract)
						  {
							    count++ ; 
							    Object [] row = new Object [2];
							    row[0] = Integer.toString(count);
							    row[1] = tit ;
							   
							    defaultModel.addRow(row);
						  }
	                    
	                } 
        		

            }
        });
	}
	
	private void createConceptExtraction()
	{
		btnExtractConcept.addActionListener(new ActionListener(){
        	@Override
            public void actionPerformed(ActionEvent e) {
        		
        		DefaultTableModel defaultModel = (DefaultTableModel) tableConcepts.getModel();
        		defaultModel.getDataVector().removeAllElements();  
        		defaultModel.getDataVector().clear();
			    Object [] row1 = new Object [3];
			    defaultModel.addRow(row1);


              if ( key_API.getText().isEmpty())
              {
            	  JOptionPane.showMessageDialog(null, "Bioportal KEY_API is required", "Bioportal KEY_API is required" , JOptionPane.OK_CANCEL_OPTION);
              }
              else
              {
            	  // do data extraction
            	  if( SGT.isSelectionEmpty())
            	  {
            		  JOptionPane.showMessageDialog(null, "Semantic Group is Required", "Concept Extraction" , JOptionPane.OK_CANCEL_OPTION);
            	  }
            	  else
            	  {
            		  
            		  // extract from sentence
            		  if(!chckbxUseSataExtraction.isSelected())
            		  {
            			  
                    	  // do data extraction
                    	  if( Sentence.getText().isEmpty())
                    	  {
                    		  JOptionPane.showMessageDialog(null, "Please Enter the Sentence", "Concept Extraction" , JOptionPane.OK_CANCEL_OPTION);
                    	  }
                    	  else
                    	  {
		                    	  if( !checkBoxUMLS.isSelected() && !chckbxLOD.isSelected() && !chckbxBio.isSelected())
		                    	  {
		                    		  JOptionPane.showMessageDialog(null, "please select the extraction method from the setting tab", "Concept Extraction" , JOptionPane.OK_CANCEL_OPTION);
		                    	  }
		                    	  else
		                    	  {
					            		try 
			            		        {
					            			  DefaultTableModel defaultModel1 = (DefaultTableModel) tableConcepts.getModel();
					            			  defaultModel1.getDataVector().removeAllElements();    
					            			  		            			  
					            			  Map<String, Integer> mentions = null ; 
				            				  Map<String, String> concepts = new HashMap<String, String>(); ;
				            				  if (chckbxLOD.isSelected())
				            				  {
						            			  String text  = removestopwords.removestopwordfromsen(Sentence.getText()) ;
						            			  mentions = NGramAnalyzer.entities(Integer.parseInt(ngramMin.getText()),Integer.parseInt(ngramMax.getText()), text) ;
						            			  concepts.putAll(ontologyMapping.getAnnotation(mentions,SGT))  ;
				            				  }
				            				  if (checkBoxUMLS.isSelected())
				            				  {
				            					  concepts.putAll(umlsMapping.getconcepts_SemanticGroup(Sentence.getText(), api,SGT)) ;
				            				  }
				            				  if (chckbxBio.isSelected())
				            				  {
				            					  String text  = removestopwords.removestopwordfromsen(Sentence.getText()) ;
						            			  mentions = NGramAnalyzer.entities(Integer.parseInt(ngramMin.getText()),Integer.parseInt(ngramMax.getText()), text) ;
						            			  concepts.putAll(bioportal.getConcepts(mentions,SGT)) ;
				            				  }
					            			  
				            				 
					            			    
					            			  int count = 0 ;
					            			  for (String tit : concepts.keySet())
					            			  {
					            				    count++ ; 
					            				    Object [] row = new Object [3];
					            				    row[0] = Integer.toString(count);
					            				    row[1] = tit ;
					            				    row[2] = concepts.get(tit) ;
					            				   
					            				    defaultModel.addRow(row);
					            			  }
					            			  
										} 
			            		  catch (IOException e1)
										{
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} 
					            	catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
		                    	  }
                    	  }
            		  }
            		  else
            		  {
                    	  // do data extraction
	                    	  if( listofTitles_Abstract == null || listofTitles_Abstract.isEmpty())
	                    	  {
	                    		  JOptionPane.showMessageDialog(null, "No Data, please use the data extraction tab to get data", "Concept Extraction" , JOptionPane.OK_CANCEL_OPTION);
	                    	  }
	                    	  else
	                    	  {
		                    	  if( !checkBoxUMLS.isSelected() && !chckbxLOD.isSelected())
		                    	  {
		                    		  JOptionPane.showMessageDialog(null, "please select a method", "Concept Extraction" , JOptionPane.OK_CANCEL_OPTION);
		                    	  }
		                    	  else
		                    	  {
		                    	 
					            		try 
			            		        {
					            			  DefaultTableModel defaultModel1 = (DefaultTableModel) tableConcepts.getModel();
					            			  defaultModel1.getDataVector().removeAllElements();    
					            			  int count = 0 ;
					            			  for (String title:listofTitles_Abstract )
					            			  {
					            				  Map<String, Integer> mentions = null ; 
					            				  Map<String, String> concepts = null ;
					            				  if (chckbxLOD.isSelected())
					            				  {
							            			  String text  = removestopwords.removestopwordfromsen(title) ;
							            			  mentions = NGramAnalyzer.entities(Integer.parseInt(ngramMin.getText()),Integer.parseInt(ngramMax.getText()), text) ;
							            			  concepts = ontologyMapping.getAnnotation(mentions,SGT)  ;
					            				  }
					            				  else if (checkBoxUMLS.isSelected())
					            				  {
					            					  //concepts =  umlsMapping.getconcepts(title, api) ;
					            				  }
					                              if (concepts != null )
					                              {
							            			  for (String tit : concepts.keySet())
							            			  {
							            				    count++ ; 
							            				    Object [] row = new Object [3];
							            				    row[0] = Integer.toString(count);
							            				    row[1] = tit ;
							            				    row[2] = concepts.get(tit) ;
							            				   
							            				    defaultModel.addRow(row);
							            			  }
					                              }
					            			  }
					            			  
											} 
				            		  catch (IOException e1)
											{
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
					            	 catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
		                    	  }
	                    	  }
            			  
            		  }
		            		
	  			}
            	  
              }
            }
        });

	}
	
	
	private void createHierarchyExtraction()
	{
		btnHrchyExtraction.addActionListener(new ActionListener(){
        	@Override
            public void actionPerformed(ActionEvent e) {

              if ( tableConcepts.getRowCount() == 0)
              {
            	  JOptionPane.showMessageDialog(null, "No Concepts", "No Concepts" , JOptionPane.OK_CANCEL_OPTION);
              }
              else
              {
            	 
            	  int rowNumber = tableConcepts.getRowCount() ;
            	  DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            	  DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
            	  root.removeAllChildren();
            	     for (int i = 0; i < rowNumber; i++) {
            	    	 
            	    	 String concept = tableConcepts.getModel().getValueAt(i, 1).toString();
            	    	 DefaultMutableTreeNode conceptNode = new DefaultMutableTreeNode(concept);
            	    	  //add the child nodes to the root node
            	         root.add(conceptNode);
            	         DefaultMutableTreeNode parentNode = conceptNode ; 
            	 		try {
							List<String>   listTaxon = bioportal.getTaxonomic(concept,1) ;
							if( listTaxon != null)
							{
								for(String child : listTaxon)
								{
									
									DefaultMutableTreeNode kid = new DefaultMutableTreeNode(child);
									parentNode.add(kid);
									parentNode = kid ; 
								}
							}
							
						} catch (IOException
								| ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 
            	    	 
            	     }
            	     
            	     model.reload(root);
              }
            }
        });
	}

	private void createSynonym()
	{
		btnSyn.addActionListener(new ActionListener(){
        	@Override
            public void actionPerformed(ActionEvent e) {

              if ( tableConcepts.getRowCount() == 0)
              {
            	  JOptionPane.showMessageDialog(null, "No Concepts were extracted" ,"Synonyms", JOptionPane.OK_CANCEL_OPTION);
              }
              else
              {
            	 
            	  int rowNumber = tableConcepts.getRowCount() ;
            	  DefaultTreeModel model = (DefaultTreeModel) treeSyn.getModel();
            	  DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
            	  root.removeAllChildren();
            	  
            	     for (int i = 0; i < rowNumber; i++) {
            	    	 
            	    	 String concept = tableConcepts.getModel().getValueAt(i, 1).toString();
            	    	 DefaultMutableTreeNode conceptNode = new DefaultMutableTreeNode(concept);
            	    	  //add the child nodes to the root node
            	         root.add(conceptNode);
            	         DefaultMutableTreeNode parentNode = conceptNode ; 
            	 		Map<String, Integer>   listsyn = bioportal.getSynonyms(concept) ;
						if( listsyn != null)
						{
							for(String syn : listsyn.keySet())
							{
								
								DefaultMutableTreeNode synn = new DefaultMutableTreeNode(syn);
								parentNode.add(synn);
							}
						} 
            	    	 
            	     }
            	     
            	     model.reload(root);
              }
            }
        });
	}	
	
	private void createAssc()
	{
		btnAssc.addActionListener(new ActionListener(){
        	@Override
            public void actionPerformed(ActionEvent e) {

              if ( tableConcepts.getRowCount() == 0)
              {
            	  JOptionPane.showMessageDialog(null, "No Concepts were extracted" ,"Association", JOptionPane.OK_CANCEL_OPTION);
              }
              else
              {
          		DefaultTableModel defaultModel = (DefaultTableModel) tableAssc.getModel();
          		defaultModel.getDataVector().removeAllElements(); 
			//    Object [] row = new Object [3];        				   
			//    defaultModel.addRow(row);
			    
  			    int rowNumber = tableConcepts.getRowCount();
  			    Map<String, Integer> allconcepts = new HashMap<String, Integer>();
  			    for (int i = 0; i < rowNumber; i++) 
  			    {
		    	    for (int j = i+1; j < rowNumber; j++) 
		    	    {
		    	       allconcepts.put(tableConcepts.getValueAt(i, 1).toString(),1); 
		    	    	 
		    	    
		    	    
				    	    try {
				    	    	ArrayList<String> RelInstances =  SyntaticPattern.getSyntaticRel(Sentence.getText(), tableConcepts.getValueAt(i, 1).toString(),tableConcepts.getValueAt(j, 1).toString());
				    	    	if (RelInstances != null && !RelInstances.isEmpty())
				    	    	{
				              	  DefaultTableModel defaultModel1 = (DefaultTableModel) tableAssc.getModel();
			                      int count = 0 ; 
			           			  for (String rel : RelInstances)
			           			  {
			           				    count++ ; 
			           				    Object [] row1 = new Object [1];
			           				    row1[0] = rel ;           				   
			           				    defaultModel1.addRow(row1);
			           			  }
				    	    	}
								
							} catch (MalformedURLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
		    	    }
	            	     
	              }
              }
            }
        });
	}
	
	private void LearningOnto()
	{
		btnLearn.addActionListener(new ActionListener(){
        	@Override
            public void actionPerformed(ActionEvent e) {

              if ( tableConcepts.getRowCount() == 0)
              {
            	  JOptionPane.showMessageDialog(null, "No Concepts", "No Concepts" , JOptionPane.OK_CANCEL_OPTION);
              }
              else
              {
          	      OntModel OntoGraph = ModelFactory.createOntologyModel();
        		  OntoGraph.setNsPrefix( "skos", BioPontologyfactory.skos) ;
            	  int rowNumber = tableConcepts.getRowCount() ;
            	  DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            	  DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
            	  
            	     for (int i = 0; i < rowNumber; i++) 
            	     {
            	    	 
            	    	 String concept = tableConcepts.getModel().getValueAt(i, 1).toString();
            	    	 try {
							BioPontologyfactory.createOntoBioP(concept ,OntoGraph);
							
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
            	    	 
            	     }
            	     ByteArrayOutputStream test = new ByteArrayOutputStream();
            	     PrintStream PS = new PrintStream(test);
            	     PrintStream console= System.out;
            	     System.setOut(PS);
            	     OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
            	     System.setOut(console);
            	     textOnto.setText(test.toString());
            	     

              }
            }
        });
		
		
		
		btnSaveOnto.addActionListener(new ActionListener(){
        	@Override
            public void actionPerformed(ActionEvent e) {
    
    	       //Create a file chooser
    		JFileChooser fc = new JFileChooser();
    		
    		
    		
    		int returnVal = fc.showSaveDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) 
            {
                File file = fc.getSelectedFile();
                //This is where a real application would save the file.
   
       	     PrintStream PS;
			try {
				 PS = new PrintStream(file);
	    	     PrintStream console= System.out;
	       	     System.setOut(PS);
	       	     System.out.println(textOnto.getText()) ;
	       	     System.setOut(console);
	                
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
   
            } 

        		

            }
        });
	}
	
	/**
	 * Create the application.
	 */
	public Testgui() {
		initialize();
		createSetting() ;
		createDataExtraction(); 
		createConceptExtraction();
		createHierarchyExtraction();
		createSynonym(); 
		createAssc();
		LearningOnto(); 
        
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLifeonto = new JFrame();
		frmLifeonto.setTitle("LifeOnto");
		frmLifeonto.setBounds(100, 100, 670, 469);
		frmLifeonto.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLifeonto.getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 644, 420);
		frmLifeonto.getContentPane().add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Setting", null, panel, null);
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Bioportal Key_API:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setBounds(10, 11, 132, 14);
		panel.add(lblNewLabel);
		
		key_API = new JTextField();
		key_API.setBounds(170, 8, 285, 20);
		panel.add(key_API);
		key_API.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Max Tiles/Abstracts ");
		lblNewLabel_2.setBounds(10, 36, 132, 31);
		panel.add(lblNewLabel_2);
		
		maxTitles = new JTextField();
		maxTitles.setText("10");
		maxTitles.setBounds(170, 39, 86, 20);
		panel.add(maxTitles);
		maxTitles.setColumns(10);
		
		panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "N-Gram", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(10, 87, 178, 82);
		panel.add(panel_3);
		panel_3.setLayout(null);
		
		lblNgram = new JLabel(" Max");
		lblNgram.setBounds(10, 22, 33, 14);
		panel_3.add(lblNgram);
		
		ngramMax = new JTextField();
		ngramMax.setBounds(43, 19, 86, 20);
		panel_3.add(ngramMax);
		ngramMax.setText("3");
		ngramMax.setColumns(10);
		
		lblMin = new JLabel("Min");
		lblMin.setBounds(10, 53, 23, 14);
		panel_3.add(lblMin);
		
		ngramMin = new JTextField();
		ngramMin.setBounds(43, 50, 86, 20);
		panel_3.add(ngramMin);
		ngramMin.setText("1");
		ngramMin.setColumns(10);
		
		panel_4 = new JPanel();
		panel_4.setLayout(null);
		panel_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Concepts Extraction", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_4.setBounds(225, 87, 178, 114);
		panel.add(panel_4);
		
		chckbxLOD = new JCheckBox("Linked Life Data");
		chckbxLOD.setBounds(6, 52, 149, 23);
		panel_4.add(chckbxLOD);
		
		checkBoxUMLS = new JCheckBox("UMLS Mapping");
		checkBoxUMLS.setBounds(6, 18, 115, 23);
		panel_4.add(checkBoxUMLS);
		
		chckbxBio = new JCheckBox("BioPortal");
		chckbxBio.setBounds(6, 84, 149, 23);
		panel_4.add(chckbxBio);
		
		chckbxontoMode = new JCheckBox("Restricted");
		chckbxontoMode.setBounds(28, 214, 97, 23);
		panel.add(chckbxontoMode);
		
		JLabel lblOntologyMode = new JLabel("Ontology Mode");
		lblOntologyMode.setBounds(28, 196, 97, 14);
		panel.add(lblOntologyMode);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Data Extraction", null, panel_1, null);
		panel_1.setLayout(null);
		
		JLabel lblDomainOfInterst = new JLabel("Domain of Interst Keyword");
		lblDomainOfInterst.setBounds(10, 11, 170, 20);
		panel_1.add(lblDomainOfInterst);
		
		domainKeyword = new JTextField();
		domainKeyword.setText("Venous Thromboembolism");
		domainKeyword.setBounds(190, 8, 280, 20);
		panel_1.add(domainKeyword);
		domainKeyword.setColumns(10);
		
		JLabel lblResource = new JLabel("Resource");
		lblResource.setBounds(10, 36, 124, 14);
		panel_1.add(lblResource);
		
		JLabel lblNewLabel_1 = new JLabel("Retrieved Fields");
		lblNewLabel_1.setBounds(10, 71, 117, 14);
		panel_1.add(lblNewLabel_1);
		
		btnExtract = new JButton("Extract ");
		btnExtract.setBounds(20, 102, 565, 23);
		panel_1.add(btnExtract);
		
		resource = new JComboBox();
		resource.setModel(new DefaultComboBoxModel(new String[] {"MEDLINE/PubMed"}));
		resource.setBounds(190, 33, 205, 20);
		panel_1.add(resource);
		
		retrievField = new JComboBox();
		retrievField.setModel(new DefaultComboBoxModel(new String[] {"Titles", "Abstracts"}));
		retrievField.setBounds(190, 68, 205, 20);
		panel_1.add(retrievField);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(14, 136, 571, 185);
		panel_1.add(scrollPane);
		
		titleTable = new JTable();
		titleTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"#", "Titles"
			}
		) {
			Class[] columnTypes = new Class[] {
				Integer.class, Object.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		titleTable.getColumnModel().getColumn(0).setPreferredWidth(72);
		titleTable.getColumnModel().getColumn(1).setPreferredWidth(558);
		scrollPane.setViewportView(titleTable);
		
		btnSave = new JButton("Save");
		btnSave.setBounds(20, 332, 89, 23);
		panel_1.add(btnSave);
		
		btnOpen = new JButton("Open");
		btnOpen.setBounds(481, 332, 89, 23);
		panel_1.add(btnOpen);
		
		panel_2 = new JPanel();
		tabbedPane.addTab("Concept Extraction", null, panel_2, null);
		panel_2.setLayout(null);
		
		JLabel lblConceptType = new JLabel("Semantic Group");
		lblConceptType.setBounds(6, 11, 105, 19);
		panel_2.add(lblConceptType);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBounds(110, 11, 145, 94);
		panel_2.add(scrollPane_1);
		
		SGT = new JList();
		scrollPane_1.setViewportView(SGT);
		SGT.setVisibleRowCount(4);
		SGT.setModel(new AbstractListModel() {
			String[] values = new String[] {"Activities & Behaviors", "Anatomy", "Chemicals & Drugs", "Devices", "Disorders", "Genes & Molecular Sequences", "Living Beings", "Phenomena", "Physiology", "Procedures"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		
		btnExtractConcept = new JButton("Extract Concept");
		btnExtractConcept.setBounds(432, 104, 152, 23);
		panel_2.add(btnExtractConcept);
		
		Sentence = new JTextField();
		Sentence.setText("Aneurysms often occur in the aorta, brain, back of the knee, intestine, or spleen.");
		Sentence.setBounds(265, 24, 342, 20);
		panel_2.add(Sentence);
		Sentence.setColumns(10);
		
		JLabel lblEnterText = new JLabel("Enter Sentence");
		lblEnterText.setBounds(265, 5, 94, 19);
		panel_2.add(lblEnterText);
		
		chckbxUseSataExtraction = new JCheckBox("Use Data Extraction");
		chckbxUseSataExtraction.setBounds(262, 56, 189, 23);
		panel_2.add(chckbxUseSataExtraction);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_2.setBounds(30, 175, 479, 177);
		panel_2.add(scrollPane_2);
		
		tableConcepts = new JTable();
		tableConcepts.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"#", "Concept Name", "Semantic Group"
			}
		));
		scrollPane_2.setViewportView(tableConcepts);
		
		JPanel panel_5 = new JPanel();
		tabbedPane.addTab("Hierarchy Relation Extraction", null, panel_5, null);
		panel_5.setLayout(null);
		
		btnHrchyExtraction = new JButton("Extraction");
		btnHrchyExtraction.setBounds(259, 287, 117, 23);
		panel_5.add(btnHrchyExtraction);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_3.setViewportBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		scrollPane_3.setBounds(20, 21, 586, 255);
		panel_5.add(scrollPane_3);
		
		tree = new JTree();
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Thing") {
				{
					add(new DefaultMutableTreeNode(""));
				}
			}
		));
		scrollPane_3.setViewportView(tree);
		
		JPanel panel_6 = new JPanel();
		tabbedPane.addTab("Synonym ", null, panel_6, null);
		panel_6.setLayout(null);
		
		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_4.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_4.setBounds(33, 22, 550, 259);
		panel_6.add(scrollPane_4);
		
		treeSyn = new JTree();
		treeSyn.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Concepts") {
				{
				}
			}
		));
		scrollPane_4.setViewportView(treeSyn);
		
		btnSyn = new JButton("Extraction");
		btnSyn.setBounds(260, 305, 106, 23);
		panel_6.add(btnSyn);
		
		JPanel panel_7 = new JPanel();
		tabbedPane.addTab("Assciaction Relation", null, panel_7, null);
		panel_7.setLayout(null);
		
		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_5.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_5.setBounds(10, 11, 597, 305);
		panel_7.add(scrollPane_5);
		
		tableAssc = new JTable();
		tableAssc.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Relation"
			}
		));
		scrollPane_5.setViewportView(tableAssc);
		btnAssc = new JButton("Extraction");
		btnAssc.setBounds(253, 331, 133, 23);
		panel_7.add(btnAssc);
		
		JPanel panel_8 = new JPanel();
		tabbedPane.addTab("Ontology Learning", null, panel_8, null);
		panel_8.setLayout(null);
		
		btnLearn = new JButton("Learn");
		btnLearn.setBounds(275, 11, 89, 23);
		panel_8.add(btnLearn);
		
		btnSaveOnto = new JButton("Save");
		btnSaveOnto.setBounds(263, 331, 89, 23);
		panel_8.add(btnSaveOnto);
		
		JScrollPane scrollPane_6 = new JScrollPane();
		scrollPane_6.setBounds(10, 49, 619, 266);
		scrollPane_6.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_6.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		panel_8.add(scrollPane_6);
		
		textOnto = new JTextArea();
		scrollPane_6.setViewportView(textOnto);
	}
}
