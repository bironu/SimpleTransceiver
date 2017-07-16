package com.example.bironu.simpletransceiver.common;

public interface Job extends AutoCloseable {
	boolean action() throws InterruptedException;
}
