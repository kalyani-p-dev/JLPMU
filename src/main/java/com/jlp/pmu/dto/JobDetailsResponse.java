package com.jlp.pmu.dto;

import java.util.List;

import com.jlp.pmu.enums.StatusColor;

import lombok.Data;

@Data
public class JobDetailsResponse {
	private List<TransactionResponse> printerTransaction;
	private int printed;
	private int unprinted;
	
}
