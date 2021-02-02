package com.fajar.medicalinventory.report;

public interface ProgressNotifier {
  void nofity(int progress, int maxProgress, double percent);
}
