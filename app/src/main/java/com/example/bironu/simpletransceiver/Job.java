package com.example.bironu.simpletransceiver;

public interface Job {
	boolean action() throws InterruptedException;
	void close();
}
