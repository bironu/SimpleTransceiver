package com.example.bironu.simpletransceiver.codecs;


public class ulaw
implements Codec
{
	public ulaw() {
	}

	public int open() {
		G711.init();
		return 0;
	}

	public int decode(byte enc[], int offset, short lin[], int frames) {
		G711.ulaw2linear(enc, offset, lin, frames);
		return frames;
	}

	public int encode(short lin[], int offset, byte enc[], int frames) {
		G711.linear2ulaw(lin, offset, enc, frames);
		return frames;
	}

	public void close() {
	}

	@Override
	public int samp_rate() {
		return 8000;
	}

	@Override
	public int frame_size() {
		return 160;
	}

	@Override
	public String name() {
		return "PCMU";
	}

	@Override
	public int number() {
		return 0;
	}
}
