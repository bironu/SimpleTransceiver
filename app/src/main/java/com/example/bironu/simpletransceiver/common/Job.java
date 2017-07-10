package com.example.bironu.simpletransceiver.common;

public interface Job {
	boolean action() throws InterruptedException;
	void close();
}
