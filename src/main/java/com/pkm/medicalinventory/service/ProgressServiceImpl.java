package com.pkm.medicalinventory.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.util.HttpRequestUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProgressServiceImpl implements ProgressService {
	@Autowired
	private ServerEventService serverEvtService;

	private final Map<String, Double> progressData = new HashMap<>();

	
	public void init(String requestId) {
		
		log.info(">>>>>>>>Init Progress: {}", requestId);
		progressData.put(requestId, 0d);
		sendProgress(0, requestId);
	}

	/**
	 * 
	 * @param taskProgress             progressPoportion for current task
	 * @param maxProgressOfCurrentTask totalProportion for current task
	 * @param overallProcessProportion task Proportion for whole request
	 * @param newRequest
	 * @param requestId
	 */
	public void sendProgress(
		double taskProgress,
		double maxProgressOfCurrentTask,
		double overallProcessProportion,
		boolean newRequest,
		String requestId
	) {
		if (newRequest) {
			updateProgress(requestId, 0, newRequest);
		}

		double taskProportion = taskProgress / maxProgressOfCurrentTask;
		double overallProportion = taskProportion * overallProcessProportion;
		updateProgress(requestId, overallProportion, newRequest);

	}

	/**
	 * 
	 * @param taskProgress             progressPoportion for current task
	 * @param maxProgressOfCurrentTask totalProportion for current task
	 * @param overallProcessProportion task Proportion for whole request
	 * @param requestId
	 */
	public void sendProgress(
		double taskProgress,
		double maxProgressOfCurrentTask,
		double overallProcessProportion,
		String requestId
	) {
		sendProgress(taskProgress, maxProgressOfCurrentTask, overallProcessProportion, false, requestId);
	}

	private void updateProgress(String requestId, double newProgress, boolean newRequest) {

		checkProgressData(requestId);
		final double currentProgress = newRequest ? 0 : progressData.get(requestId);
		final double overallProgress = currentProgress + newProgress;

		// comment log.info("adding progress: {} for: {}, currentProgress: {} overall:
		// {}", newProgress, requestId, currentProgress, overallProgress);
		if (Math.ceil(currentProgress) == Math.ceil(overallProgress)) {
			progressData.put(requestId, overallProgress);
			return;
		}
		if (overallProgress >= 100) {
			log.info("overallProgress {} >= 100", overallProgress);
			progressData.put(requestId, 99d);
			updateProgress(requestId, 0, newRequest);
		} else {
			progressData.put(requestId, overallProgress);
			sendProgress(overallProgress, requestId);

		}
	}

	private void checkProgressData(String requestId) {
		if (progressData.get(requestId) == null) {
			progressData.put(requestId, 0d);
		}
	}

	public void sendComplete() {
		sendComplete(getRequestId());
	}

	public void sendComplete(String requestId) {
		log.info("________COMPLETE PROGRESS FOR {}________", requestId);
		sendProgress(98, requestId);
		sendProgress(99, requestId);
		sendProgress(100, requestId);
		progressData.remove(requestId);

	}

	private void sendProgress(double progress, String requestId) {
//		log.info("Send Progress: {} to {}", progress, requestId);
//		ThreadUtil.run(() -> {
		serverEvtService.sendProgress(progress, requestId);
//		
	}

	public void sendProgress(
		double progress,
		double maxProgress,
		double percent,
		boolean newProgress
	) {
		String requestId = getRequestId();
		this.sendProgress(progress, maxProgress, percent, newProgress, requestId);
	}

	public void sendProgress(
		double progress,
		double maxProgress,
		double percent
	) {
		sendProgress(progress, maxProgress, percent, false);
	}

	public void sendProgress(double percent) {
		sendProgress(1, 1, percent);
	}

	static String getRequestId() {
		return HttpRequestUtil.getPageRequestId();
	}

	public static void main(String[] ccc) {
		ProgressServiceImpl ps = new ProgressServiceImpl();
		String requestId = "q03i4934i93";
		ps.init(requestId);
		// comment log.info("1");
		ps.sendProgress(1, 2, 30, false, requestId);
		// comment log.info("2");
		ps.sendProgress(1, 2, 30, false, requestId);
		// comment log.info("3");
		ps.sendProgress(1, 3, 40, false, requestId);
		// comment log.info("4");
		ps.sendProgress(1, 3, 40, false, requestId);
		// comment log.info("5");
		ps.sendProgress(1, 3, 40, false, requestId);
		// comment log.info("6");
		ps.sendProgress(1, 2, 30, false, requestId);
		// comment log.info("7");
		ps.sendProgress(1, 2, 30, false, requestId);
	}

}
