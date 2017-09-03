package com.example.bironu.simpletransceiver.io;

public interface Job extends AutoCloseable {
	boolean action() throws InterruptedException;
}
