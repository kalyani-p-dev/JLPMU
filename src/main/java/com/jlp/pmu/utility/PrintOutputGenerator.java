package com.jlp.pmu.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



public class PrintOutputGenerator {
	
	Map messageTypeTemplateIdMapper = new HashMap<String, String>();
	Map templateDataSource= new HashMap<String, String>();
	Map sourceTypes= new HashMap<String, List<String>>();
	Map tmpGlobalProperties = new HashMap<String, String>();
	List sourceTypesList=null;
	public Document mqXmlDocument=null;
	Map <String,Map<String,String>> dynamicHtmlPropertyAndValueMap = new HashMap<String,Map<String,String>>();
	List htmlPortionList = new ArrayList<String>();
	 String templateName;
	
	Utility utilObj = new Utility();

public String readFileContent(String absPathOfFileName)
{
Path fileName
= Path.of(absPathOfFileName);


String htmlFileContent=null;
try {
htmlFileContent = Files.readString(fileName);
} catch (IOException e) {
// TODO Auto-generated catch block
e.printStackTrace();
}
return htmlFileContent;
}

	
	synchronized public void initializeMQXmlDoc(String absPathOfxmlFile)
	{
		

		try
		{
			
			
		String fileContent="";
	        
	        
		//File re = new File(absPathOfxmlFile);
		
	
		
		//System.out.println(inputFile.exists());
		
		
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        
        
        
      //  mqXmlDocument = dBuilder.parse(inputFile);
        
        fileContent = readFileContent(absPathOfxmlFile);
        
int countOfXmlStartTag=0,countOfXmlEndTag=0;
		
		countOfXmlStartTag = StringUtils.countMatches(fileContent, "<");
		countOfXmlEndTag = StringUtils.countMatches(fileContent, ">");
		
		if(!(countOfXmlStartTag>0&&countOfXmlEndTag>0&&countOfXmlEndTag==countOfXmlStartTag))
		{
		
		 JSONObject jsonObject = new JSONObject(fileContent);
		 fileContent = XML.toString(jsonObject);
		}
        
      // JSONObject jsonObject = new JSONObject(fileContent);
       //fileContent = XML.toString(jsonObject);
        
        System.out.println("File content=="+fileContent);
        
        mqXmlDocument = dBuilder.parse(new InputSource(new java.io.StringReader(fileContent)));
        mqXmlDocument.getDocumentElement().normalize();
        
       // mqXmlDocument.
        
        
      
		}
		
		catch(Exception e)
		{
			System.out.println(e);
		}
	
		
	}
	
	String generatePrintableOutput(String templateName)
	{
		Path fileName
        = Path.of(utilObj.prop.getProperty("SourceDirectoryOfTemplate")+templateName+".html");
		
		
		this.templateName=templateName;

    // Now calling Files.readString() method to
    // read the file
    String htmlFileContent=null;
	try {
		htmlFileContent = Files.readString(fileName);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

    // Printing the string
    //System.out.println(htmlFileContent);
    
    return htmlFileContent;
	}
	
	synchronized public void getTheKeyValuesForTemplateFromMessageQueue(String templateNameInMQ) throws IOException
	{
		System.out.println("Template name in MQ  =="+templateNameInMQ);
		this.templateName=templateNameInMQ;
		
		
		
		
		String templateName=(String) tmpGlobalProperties.get(templateNameInMQ);
		
		String htmlFileStringContent = generatePrintableOutput(templateName);
		
		String sourceParameter,propertyFileEntry,attributeName;
		
		
		System.out.println("templateName=="+templateName);
		
		Iterator propertyIterator = tmpGlobalProperties.entrySet().iterator();
	    while(propertyIterator.hasNext())
	    {
	    	
	    	Map.Entry keyValueIterator             = (Map.Entry)propertyIterator.next();
	    	
	    	propertyFileEntry =keyValueIterator.getKey().toString();
	    	
	    	if(StringUtils.containsIgnoreCase(propertyFileEntry, templateName+"."))
	    		
	    	{
	    		
	    		
	    		sourceParameter="{$"+propertyFileEntry+"}";
	    		
	    		sourceParameter=StringUtils.replace(sourceParameter, templateName+".", "");
	    		
	    		attributeName = (String)tmpGlobalProperties.get(propertyFileEntry);
	    		
	    		populateDynamicHtmlElementMap((String)tmpGlobalProperties.get(templateNameInMQ+"_RootTag"),attributeName);
	    		
	    	//	htmlFileStringContent=StringUtils.replace(htmlFileStringContent, sourceParameter, getTheMQValue(keyValueIterator.getValue().toString()));
	    		   
	    	System.out.println("htmlFileStringContent=="+htmlFileStringContent);
	    	}
	    	
	    	
	    }
	    
	    
		//System.out.println("Trinity=="+htmlFileStringContent);
	    
	    
	  //  htmlFileStringContent=StringUtils.replace(htmlFileStringContent, sourceParameter, getTheMQValue(keyValueIterator.getValue().toString()));
    	
		htmlFileStringContent = generateHtmlContentAfterReplacingDynamicElement(htmlFileStringContent);
		
		 FileWriter fWriter = new FileWriter(utilObj.prop.getProperty("htmlOutputDirectory")+templateName+".html");
		 
         // Writing into file
         // Note: The content taken above inside the
         // string
         fWriter.write(htmlFileStringContent);
         fWriter.close();
		
		return ;
	}
	
	
	
	
	private String generateHtmlContentAfterReplacingDynamicElement(String htmlFileStringContent)
	{
		String finalHtmlContent="",htmlPortion,eachHtmlTag,htmlPortionFromList="",elementNameInXml,elementValue="";
		String [] arrVariablesInHtml;
		List arrVariablesInHtmlList = null;
		Map<String,String> eachHtmlTagValueMap = null;// = dynamicHtmlPropertyAndValueMap
		Iterator<String> htmlVariablesIter;
		
		htmlPortion=StringUtils.substringBetween(htmlFileStringContent, "<div id=\"start_of_dynamic_html\">", "<div id=\"end_of_dynamic_html\"> ");
		
		//Iterator dynamicElementEntry = dynamicHtmlPropertyAnd
		
		
		
		
		
		if(dynamicHtmlPropertyAndValueMap!=null&&dynamicHtmlPropertyAndValueMap.size()>0)
		{
			
			
				arrVariablesInHtml= StringUtils.substringsBetween(htmlPortion, "{$", "}");
				if(arrVariablesInHtml!=null&&arrVariablesInHtml.length>0)
					arrVariablesInHtmlList= Arrays.asList(arrVariablesInHtml);
				
				if(arrVariablesInHtmlList!=null&&arrVariablesInHtmlList.size()>0)
				{
					htmlVariablesIter = arrVariablesInHtmlList.iterator();
					
					while(htmlVariablesIter.hasNext())
					{
						eachHtmlTag = htmlVariablesIter.next();
						elementNameInXml = (String)tmpGlobalProperties.get(templateName+"."+eachHtmlTag);
						int numberOfUniqueRecord=0;
						eachHtmlTagValueMap = dynamicHtmlPropertyAndValueMap.get(elementNameInXml);
						
						if(eachHtmlTagValueMap!=null&&eachHtmlTagValueMap.size()>0)
						{
						
							for (Map.Entry<String,String> entry : eachHtmlTagValueMap.entrySet())  
							{
								if(htmlPortionList.size()>numberOfUniqueRecord)
								{
									htmlPortionFromList = (String) htmlPortionList.get(numberOfUniqueRecord);
									
								}
								else
									htmlPortionList.add(htmlPortion);
								
								htmlPortionFromList = (String) htmlPortionList.get(numberOfUniqueRecord);
								
								
								elementNameInXml = (String)tmpGlobalProperties.get(templateName+"."+eachHtmlTag);
								//Need to write code to get property.get property passing the 
								numberOfUniqueRecord++;
								if(eachHtmlTagValueMap!=null&&eachHtmlTagValueMap.get(elementNameInXml+"_"+String.valueOf(numberOfUniqueRecord))!=null)
								{
									elementValue= eachHtmlTagValueMap.get(elementNameInXml+"_"+String.valueOf(numberOfUniqueRecord));
									htmlPortionFromList = StringUtils.replace(htmlPortionFromList,"{$"+eachHtmlTag+"}",  elementValue);
									htmlPortionList.remove(numberOfUniqueRecord-1);
									htmlPortionList.add(numberOfUniqueRecord-1, htmlPortionFromList);
								}
								
							}
						}
						
						
					}
				}
				
				
			
	           
		}
		
		
		//String htmlPortion = StringUtils.substringsBetween(htmlFileStringContent, "<div id=\"start_of_dynamic_html\">", "<div id=\"end_of_dynamic_html\"> ");
		finalHtmlContent=generateHtmlFromList(htmlFileStringContent);
		return finalHtmlContent;
	}
	
	private String generateHtmlFromList(String sourceHtmlWithParameters)
	{
		String finalHtml="",eachHtmlBlock="";
		//htmlPortion=StringUtils.substringBetween(htmlFileStringContent, "<div id=\"start_of_dynamic_html\">", "<div id=\"end_of_dynamic_html\"> ");
		
		finalHtml=StringUtils.substringBefore(sourceHtmlWithParameters,  "<div id=\"start_of_dynamic_html\">");
		
		if(htmlPortionList!=null&&htmlPortionList.size()>0)
		{
			Iterator<String> htmlIter = htmlPortionList.iterator();
			while(htmlIter.hasNext())
			{
				eachHtmlBlock = htmlIter.next();
				finalHtml = finalHtml+eachHtmlBlock;
				finalHtml = finalHtml+"<div class=\"page-break-jlp\"></div>";
			}
		}
		finalHtml = finalHtml+StringUtils.substringAfter(sourceHtmlWithParameters,  "<div id=\"end_of_dynamic_html\">");
		
		return finalHtml;
	}
	
	private void populateDynamicHtmlElementMap(String mainRepeatingElementName,String htmlElementName)
	{
		String repeatingElementPropertyName = StringUtils.substringBeforeLast(templateName, "_Template")+"_RootTag";
		String repeatingElementName = (String)tmpGlobalProperties.get(repeatingElementPropertyName);
		NodeList productNodeList = mqXmlDocument.getElementsByTagName(repeatingElementName),propertyNodeList;
		
		String attributeValue;
		Element propertyElement,productElement;
		Node productNode,propertyNode;
		Map<String,String> eachElementValuesMap;
		XPathFactory xpathfactory = XPathFactory.newInstance();
	    XPath xpath = xpathfactory.newXPath();
		
		 XPathExpression expr=null;
		 String expression;
		// XPathExpression expr=null;
			
		
		for (int i = 0; i < productNodeList.getLength(); i++) {
			
			 productNode = productNodeList.item(i);
			 
			 //System.out.println("productNode each=="+productNode.getTextContent());

             if (productNode.getNodeType() == Node.ELEMENT_NODE) {
            	
            	productElement = (Element) productNode;
            	
            	
            	eachElementValuesMap = new HashMap<String,String>();
            	
            	if(dynamicHtmlPropertyAndValueMap!=null&&dynamicHtmlPropertyAndValueMap.containsKey(htmlElementName))
            	eachElementValuesMap=dynamicHtmlPropertyAndValueMap.get(htmlElementName);
            	
            	
            		
            		
            	dynamicHtmlPropertyAndValueMap.put(htmlElementName, eachElementValuesMap);
            	
           	 expression = "property[descendant::key[text()=" + "'" + htmlElementName + "'" + "]]/value";
           	 
           //	xpath.compile(expression);
           	 
           	 try {
           	expr  =xpath.compile(expression);
           	 
           	 Object result = expr.evaluate(productElement, XPathConstants.NODESET);
           	 
         //  	 Object result = expr.evaluate(mqXmlDocument, XPathConstants.NODESET);
     	    
     	    
     	    
     	    System.out.println(result);
     	   propertyNodeList = (NodeList) result;
     	    
     
            
            	
            	for(int j=0;j<propertyNodeList.getLength();j++)
            	{
            		propertyNode = propertyNodeList.item(j);
            		
            		if (propertyNode.getNodeType() == Node.ELEMENT_NODE) 
            		{
            			propertyElement=	(Element) propertyNode;
            			
            		          attributeValue= propertyElement.getTextContent();
                       		eachElementValuesMap.put(htmlElementName+"_"+Integer.toString(i+1), propertyElement.getTextContent());
                       		 
                    	
            		}

               	
            	
            	
            	
            	}
           	 }
           	 
            	
            
             catch (Exception e)
             {
            	 System.out.println(e);
             }
             }
		}
		
		  
	}

	
	public String getTheMQValue(Document mqXmlDocument,String keyName)
	{
		
		
	    
	    XPathFactory xpathfactory = XPathFactory.newInstance();
	    XPath xpath = xpathfactory.newXPath();
	    NodeList nodeResultList=null;
	    XPathExpression expr=null;
	    
	    String valueFromESBXml;
	    
	    try
	    {
	    
	    
	  //  XPathExpression expr = xpath.compile("//question[@name='consent_obtained']");
	    	
	    	
	    		//expr = xpath.compile("//property/name="+keyName+"");
	    		
	    		String expression = "//property[descendant::key[text()=" + "'" + keyName + "'" + "]]/value";
	    		String expression1 = "//Tutorial[descendant::title[text()=" + "'" + "trinity" + "'" + "]]";
	    		
	    	
	    	expr  =xpath.compile(expression);
	    	
	    Object result = expr.evaluate(mqXmlDocument, XPathConstants.NODESET);
	    
	    
	    
	    System.out.println(result);
	     nodeResultList = (NodeList) result;
	     
	     System.out.println(nodeResultList.getLength());
	    }
	    catch(Exception e)
	    {
	    	System.out.println(e);
	    }
	    
	  
		if(nodeResultList==null||nodeResultList.getLength()<=0)
			return "NOVALUE";
	    
	    Node esbValueObj = nodeResultList.item(0);
	    
	    if(StringUtils.isNotEmpty(keyName))
		
	    valueFromESBXml= esbValueObj.getTextContent();
	    else
	    {
	    	
	    	
	    	
	    	Element el = (Element)esbValueObj;
	    	
	    	valueFromESBXml= el.getAttribute("id");
	    	
	    }
		
		

	 return valueFromESBXml;

	}
	
	
	public void initializeDataSource() throws IOException
	{
		
		FileReader reader=new FileReader("/home/J84542310UA/PMU/properties/jlp-dynamic.properties");  
	      
	    Properties p=new Properties();  
	    p.load(reader); 
	    Set set=p.entrySet();
	    
	    Iterator globalPrpertyIter=set.iterator(); 
	    
	    //globalPrpertyIter.g
	    
	  
	    
	   
	    
	    Map.Entry propertyFileEntryMap=null;
	    while(globalPrpertyIter.hasNext()){  
	     propertyFileEntryMap=(Map.Entry)globalPrpertyIter.next(); 
	     
	     if(StringUtils.equalsIgnoreCase(propertyFileEntryMap.getKey().toString(), "sourceTypes"))
	     {
	    	 sourceTypesList = new ArrayList<String>();
	    	 
	    	 System.out.println("Key =="+propertyFileEntryMap.getValue());
	    	 
	    	
	    	 List<String> templateList =  Arrays.asList(propertyFileEntryMap.getValue().toString().split("\\s*,\\s*"));
	    	
	    	 
	    	 
	    	 sourceTypesList.addAll(new ArrayList<String>(templateList));
	    	 
	    	 
	     }
	     else
	    	 tmpGlobalProperties.put(propertyFileEntryMap.getKey(), propertyFileEntryMap.getValue());
	     
	     
	     
	     
	    }
	    
	    
	    
	    
	    
	    
	}
	
	

}
