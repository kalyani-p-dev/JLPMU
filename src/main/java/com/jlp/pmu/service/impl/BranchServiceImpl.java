package com.jlp.pmu.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jlp.pmu.constant.Constant;
import com.jlp.pmu.dto.BranchRequest;
import com.jlp.pmu.dto.BranchResponse;
import com.jlp.pmu.enums.ObjectType;
import com.jlp.pmu.exception.BranchAlreadyExistException;
import com.jlp.pmu.exception.BranchNotFoundException;
import com.jlp.pmu.exception.MnemonicAlreadyExistException;
import com.jlp.pmu.exception.UserNotFoundException;
import com.jlp.pmu.models.Activity;
import com.jlp.pmu.models.Branch;
import com.jlp.pmu.models.User;
import com.jlp.pmu.repository.ActivityRepository;
import com.jlp.pmu.repository.BranchRepository;
import com.jlp.pmu.repository.UserRepository;
import com.jlp.pmu.service.BranchService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class BranchServiceImpl implements BranchService {

	@Autowired
	BranchRepository branchRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ActivityRepository activityRepository;

	@Autowired
	ModelMapper modelMapper;

	@Override
	public BranchResponse addBranchDetails(BranchRequest requestDto) {

		User user = userRepository.findById(requestDto.getUserId())
				.orElseThrow(() -> new UserNotFoundException(Constant.USER_NOT_FOUND_MESSAGE + requestDto.getUserId(),
						Constant.USER_NOT_FOUND));

		List<Branch> branches = branchRepository.findByBranchCodeOrMnemonic(requestDto.getBranchCode(),
				requestDto.getMnemonic());

		if (branches != null && !branches.isEmpty()) {
			Optional<Branch> optionalBranchCode = branches.stream()
					.filter(branch -> branch.getBranchCode().equals(requestDto.getBranchCode())).findAny();

			if (optionalBranchCode.isPresent()) {
				throw new BranchAlreadyExistException(
						Constant.BRANCH_ALREADY_EXIST_WITH_GIVEN_BRANCHCODE + requestDto.getBranchCode(),
						Constant.BRANCH_ALREADY_EXISTS);
			}

			Optional<Branch> optionalMnemonic = branches.stream()
					.filter(branch -> branch.getMnemonic().equals(requestDto.getMnemonic())).findAny();

			if (optionalMnemonic.isPresent()) {
				throw new MnemonicAlreadyExistException(
						Constant.BRANCH_ALREADY_EXIST_WITH_GIVEN_MNEMONIC + requestDto.getMnemonic(),
						Constant.MNEMONIC_ALREADY_EXISTS);
			}

			return null;
		} else {

			log.info("Adding Branch with the details : {}", requestDto);

			Branch branch = new Branch();
			branch.setBranchCode(requestDto.getBranchCode());
			branch.setBranchName(requestDto.getBranchName());
			branch.setBranchStatus(true);
			branch.setMnemonic(requestDto.getMnemonic());
			branch.setPmuServers(requestDto.getPmuServers());
			branch.setCreatedBy(user.getFirstName() + " " + user.getLastName());
			branch.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
			branch.setLastUpdatedTime(LocalDateTime.now());
			branch.setComment(requestDto.getComment());

			Branch branchResponse = branchRepository.save(branch);

			Activity activity = new Activity();
			activity.setCreatedBy(user.getFirstName() + " " + user.getLastName());
			activity.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
			activity.setLastupdatedtime(LocalDateTime.now());
			activity.setComment(requestDto.getComment());
			activity.setObjectType(ObjectType.BRANCH);
			activity.setObjectID(requestDto.getBranchCode().toString());
			activityRepository.save(activity);
			log.info("Branch Details Added successfully (branchCode: {}) ", requestDto.getBranchCode());
			return modelMapper.map(branchResponse, BranchResponse.class);
		}

	}

	@Override
	public BranchResponse getBranchDetailsByID(Long branchCode) {

		Branch branch = branchRepository.findById(branchCode)
				.orElseThrow(() -> new BranchNotFoundException(
						Constant.THE_BRANCH_WITH_GIVEN_CODE + branchCode + Constant.WAS_NOT_FOUND,
						Constant.BRANCH_NOT_FOUND));
		return modelMapper.map(branch, BranchResponse.class);
	}

	@Override
	public List<BranchResponse> getAllBranchDeatils() {
		List<BranchResponse> branchResponses = new ArrayList<>();
		try {
			List<Branch> branches = branchRepository.findByBranchStatus(true);
			branchResponses = branches.stream()
					.sorted((branch1, branch2) -> branch2.getLastUpdatedTime().compareTo(branch1.getLastUpdatedTime()))
					.map(branch -> modelMapper.map(branch, BranchResponse.class)).toList();
		} catch (Exception e) {
			log.error("Exception getAllBranchDeatils:" + e.getMessage());
		}
		return branchResponses;
	}

	@Override
	public BranchResponse doInactiveBranchByBranchCode(BranchRequest request) {

		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new UserNotFoundException(Constant.USER_NOT_FOUND_MESSAGE + request.getUserId(),
						Constant.USER_NOT_FOUND));

		Branch branch = branchRepository.findById(request.getBranchCode())
				.orElseThrow(() -> new BranchNotFoundException(
						Constant.THE_BRANCH_WITH_GIVEN_CODE + request.getBranchCode() + Constant.WAS_NOT_FOUND,
						Constant.BRANCH_NOT_FOUND));

		log.info("Inactivating Branch Details by BranchCode :{}", request.getBranchCode());

		String comment = Constant.ADDED_BY + branch.getCreatedBy() + Constant.AND_INACTIVATED_BY + user.getFirstName()
				+ " " + user.getLastName();

		branch.setBranchStatus(false);
		branch.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
		branch.setLastUpdatedTime(LocalDateTime.now());
		branch.setComment(comment);
		branchRepository.save(branch);

		Activity activity = new Activity();
		activity.setCreatedBy(branch.getCreatedBy());
		activity.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
		activity.setLastupdatedtime(LocalDateTime.now());
		activity.setComment(comment);
		activity.setObjectType(ObjectType.BRANCH);
		activity.setObjectID(request.getBranchCode().toString());
		activityRepository.save(activity);
		log.info("Branch  Inactivated successfully with BranchCode: {}", request.getBranchCode());
		return modelMapper.map(branch, BranchResponse.class);
	}

	@Override
	public List<String> getPMUServersByBranchID(Long branchCode) {
		Branch branch = branchRepository.findById(branchCode)
				.orElseThrow(() -> new BranchNotFoundException(
						Constant.THE_BRANCH_WITH_GIVEN_CODE + branchCode + Constant.WAS_NOT_FOUND,
						Constant.BRANCH_NOT_FOUND));
		return branch.getPmuServers();
	}

	@Override
	public List<String> getAllPMUServers() {
		return branchRepository.findAllPMUServers();
	}

	@Override
	public List<String> getAllMnemonic() {
		return branchRepository.findAllMnemonic();
	}

}
