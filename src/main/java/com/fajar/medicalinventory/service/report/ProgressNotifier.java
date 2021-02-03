package com.fajar.medicalinventory.service.report;

public interface ProgressNotifier {
  void nofity(int progress, int maxProgress, double percent);
}
