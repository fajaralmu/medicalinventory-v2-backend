package com.pkm.medicalinventory.service;

public interface ProgressService {
	void init(String pageRequestId);
	void sendComplete();
	void sendProgress(double progress);
	void sendProgress(double progress, String reqId);
	void sendProgress(double progress, double maxProgress, double percent);
	void sendProgress(double i, double j, double k, boolean b);
}
