package com.jlp.pmu.pojo;

import java.util.List;
import java.util.Properties;

import com.jlp.pmu.utility.Utility;

public class ReportUtility {
	
	Utility utilObj = new Utility();
	
	Properties propObj = Utility.readPropertiesFile();
	
	public void alignReportBasedOnPageType(ReportMetaData repMetaData)
	{
		String htmlContent = repMetaData.getFinalHtmlContent();
		
		List<String> eachLineInFinalHtml = utilObj.fileContentAsLineList(htmlContent);
		
		System.out.println(eachLineInFinalHtml.size());
	}

}
