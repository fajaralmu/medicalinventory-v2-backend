package com.fajar.medicalinventory.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadUtil {

	public static Thread run(Runnable runnable) {
		
		Thread thread  = new Thread(runnable);
		log.trace("running thread: {}", thread.getId());
		log.trace("active thread: {}", Thread.activeCount());
		thread.start(); 
		return thread;
	}
}
