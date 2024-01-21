package com.pkm.medicalinventory.service;

public interface ProgressNotifier {
	void notify(int progress, int maxProgress, double percent);

	static ProgressNotifier empty() {
		return new ProgressNotifier() {

			@Override
			public void notify(int progress, int maxProgress, double percent) {
				// TODO Auto-generated method stub

			}
		};
	}
}
