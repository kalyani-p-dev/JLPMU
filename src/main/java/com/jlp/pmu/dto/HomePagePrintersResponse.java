package com.jlp.pmu.dto;

import com.jlp.pmu.enums.PrinterType;
import com.jlp.pmu.enums.StatusColor;

import lombok.Data;

import java.util.List;

@Data
public class HomePagePrintersResponse {
	private Long printerID;
	private String printerName;
	private PrinterType printerType;
	private int totalDone;
	private int printToDo;
	private int holdTodo;
	private int totalTodo;
	private StatusColor status;
}
