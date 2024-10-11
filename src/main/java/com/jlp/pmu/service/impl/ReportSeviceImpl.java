package com.jlp.pmu.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jlp.pmu.constant.Constant;
import com.jlp.pmu.dto.PrinterResponse;
import com.jlp.pmu.dto.ReportRequest;
import com.jlp.pmu.dto.ReportResponse;
import com.jlp.pmu.enums.ObjectType;
import com.jlp.pmu.enums.PrinterType;
import com.jlp.pmu.exception.PrinterNotFoundException;
import com.jlp.pmu.exception.ReportAlreadyExistsException;
import com.jlp.pmu.exception.ReportNotFoundException;
import com.jlp.pmu.exception.UserNotFoundException;
import com.jlp.pmu.models.Activity;
import com.jlp.pmu.models.Printer;
import com.jlp.pmu.models.Report;
import com.jlp.pmu.models.User;
import com.jlp.pmu.repository.ActivityRepository;
import com.jlp.pmu.repository.FormStatRepository;
import com.jlp.pmu.repository.PrinterRepository;
import com.jlp.pmu.repository.ReportRepository;
import com.jlp.pmu.repository.UserRepository;
import com.jlp.pmu.service.ReportService;

@Service
public class ReportSeviceImpl implements ReportService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	ActivityRepository activityRepository;

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	PrinterRepository printerRepository;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	FormStatRepository formStatRepository;

	@Override
	public ReportResponse addReport(ReportRequest request) {

		User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserNotFoundException(
				Constant.USER_NOT_FOUND_MESSAGE + request.getUserId(),
				Constant.USER_NOT_FOUND));

		Optional<Report> optional = reportRepository.findReportByCombinations(request.getReportName(),
				request.getBranchCode(), request.getDept(), request.getSubDept());

		if (optional.isPresent()) {
			throw new ReportAlreadyExistsException(Constant.REPORT_GIVEN_COMBINATION_ALREADY_EXISTS,
					Constant.REPORT_ALREADY_EXISTS);
		}

		Printer printer = printerRepository.findById(request.getPrinterId())
				.orElseThrow(() -> new PrinterNotFoundException(
						Constant.THE_PRINTER_WAS_NOT_FOUND_WITH_GIVEN_ID + request.getPrinterId(),
						Constant.PRINTER_NOT_FOUND));

		Report report = new Report();
		report.setReportName(request.getReportName());
		report.setDept(request.getDept());
		report.setSubDept(request.getSubDept());
		report.setPrinterId(request.getPrinterId());
		report.setPrintjoboptions(request.getPrintjoboptions());
		report.setComments(request.getComments());
		report.setBranchCode(request.getBranchCode());
		report.setCopies(request.getCopies());

		report.setCreatedBy(user.getFirstName() + " " + user.getLastName());
		report.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
		report.setLastUpdatedTime(LocalDateTime.now());
		report.setStatus(true);

		Report response = reportRepository.save(report);

		Activity activity = new Activity();
		activity.setCreatedBy(user.getFirstName() + " " + user.getLastName());
		activity.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
		activity.setLastupdatedtime(LocalDateTime.now());
		activity.setComment(Constant.ADDED_BY + user.getFirstName() + " " + user.getLastName());
		activity.setObjectType(ObjectType.REPORT);
		activity.setObjectID(response.getReportId().toString());

		activityRepository.save(activity);

		ReportResponse reportResponse = modelMapper.map(response, ReportResponse.class);

		if (request.getPrinterId() > 0) {

			PrinterResponse printerResponse = modelMapper.map(printer, PrinterResponse.class);

			reportResponse.setPrinter(printerResponse);
			reportResponse.setPrinterName(printerResponse.getPmuPrinterName());
			reportResponse.setPrinterType(printerResponse.getPrinterType());
		}
			
		return reportResponse;
	}

	@Override
	public ReportResponse updateReport(ReportRequest request) {

		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new UserNotFoundException(Constant.USER_NOT_FOUND_MESSAGE + request.getUserId(),
						Constant.USER_NOT_FOUND));

		Report report = reportRepository.findById(request.getReportId()).orElseThrow(() -> new ReportNotFoundException(
				Constant.THE_REPORT_WITH_GIVEN_ID + request.getReportId(), Constant.REPORT_NOT_FOUND));

		Printer printer = printerRepository.findById(request.getPrinterId())
				.orElseThrow(() -> new PrinterNotFoundException(
						Constant.THE_PRINTER_WAS_NOT_FOUND_WITH_GIVEN_ID + request.getPrinterId(),
						Constant.PRINTER_NOT_FOUND));

		report.setReportName(request.getReportName());
		report.setDept(request.getDept());
		report.setSubDept(request.getSubDept());
		report.setPrinterId(request.getPrinterId());
		report.setPrintjoboptions(request.getPrintjoboptions());
		report.setComments(request.getComments());
		report.setBranchCode(request.getBranchCode());
		report.setCopies(request.getCopies());

		report.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
		report.setLastUpdatedTime(LocalDateTime.now());
		report.setStatus(true);

		Report response = reportRepository.save(report);

		Activity activity = new Activity();
		activity.setCreatedBy(response.getCreatedBy());
		activity.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
		activity.setLastupdatedtime(LocalDateTime.now());
		activity.setComment(request.getComments());
		activity.setObjectType(ObjectType.REPORT);
		activity.setObjectID(response.getReportId().toString());

		activityRepository.save(activity);

		ReportResponse reportResponse = modelMapper.map(response, ReportResponse.class);

		if (request.getPrinterId() > 0) {

			PrinterResponse printerResponse = modelMapper.map(printer, PrinterResponse.class);

			reportResponse.setPrinter(printerResponse);
			reportResponse.setPrinterName(printerResponse.getPmuPrinterName());
			reportResponse.setPrinterType(printerResponse.getPrinterType());
		}

		return reportResponse;
	}

	@Override
	public ReportResponse inactiveReport(ReportRequest request) {
		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new UserNotFoundException(Constant.USER_NOT_FOUND_MESSAGE + request.getUserId(),
						Constant.USER_NOT_FOUND));

		Report report = reportRepository.findById(request.getReportId()).orElseThrow(() -> new ReportNotFoundException(
				Constant.THE_REPORT_WITH_GIVEN_ID + request.getReportId(), Constant.REPORT_NOT_FOUND));

		String comment = Constant.ADDED_BY + report.getCreatedBy() + Constant.AND_INACTIVATED_BY + user.getFirstName()
				+ " " + user.getLastName();

		report.setStatus(false);
		report.setComments(comment);
		report.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
		report.setLastUpdatedTime(LocalDateTime.now());

		Report response = reportRepository.save(report);

		Activity activity = new Activity();
		activity.setCreatedBy(response.getCreatedBy());
		activity.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
		activity.setLastupdatedtime(LocalDateTime.now());
		activity.setComment(comment);
		activity.setObjectType(ObjectType.REPORT);
		activity.setObjectID(response.getReportId().toString());

		activityRepository.save(activity);

		return modelMapper.map(report, ReportResponse.class);
	}

	@Override
	public List<ReportResponse> getAllReports(Long branchCode) {
		List<Report> reports = reportRepository.findByStatusAndBranchCodeOrderbyDate(true, branchCode);
		List<ReportResponse> response;
		if (!reports.isEmpty()) {

			Set<Long> printerIds = reports.stream().map(report -> report.getPrinterId()).collect(Collectors.toSet());
			List<Printer> printers = printerRepository.findByPrinterIDIn(printerIds);

			if (!printers.isEmpty()) {

				response = reports.stream().map(report -> {
					PrinterResponse printerResponse = modelMapper.map(printers.stream()
							.filter(printer -> printer.getPrinterID().equals(report.getPrinterId())).findFirst().get(),
							PrinterResponse.class);
					String printerName = modelMapper.map(
							printers.stream().filter(printer -> printer.getPrinterID().equals(report.getPrinterId()))
									.findFirst().get().getPmuPrinterName(),
							String.class);
					PrinterType printerType = modelMapper.map(
							printers.stream().filter(printer -> printer.getPrinterID().equals(report.getPrinterId()))
									.findFirst().get().getPrinterType(),
									PrinterType.class);

					ReportResponse reportResponse = modelMapper.map(report, ReportResponse.class);
					reportResponse.setPrinterName(printerName);
					reportResponse.setPrinterType(printerType);
					reportResponse.setPrinter(printerResponse);
					return reportResponse;
				}).toList();
				return response;
			}

			return reports.stream().map(report -> modelMapper.map(report, ReportResponse.class)).toList();
		}

		return new ArrayList<>();
	}

	public Optional<Report> findReportByReportNameAndBranchCode(boolean b, String reportName, long branchCode,String dept, String subDept) {
		Optional<Report> optionalReport = reportRepository.findReportByReportNameAndBranchCode(true,reportName,branchCode,dept,subDept);
		return optionalReport;
	}

}
