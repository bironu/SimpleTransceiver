/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 * Copyright (C) 2005 Luca Veltri - University of Parma - Italy
 * 
 * This file is part of Sipdroid (http://www.sipdroid.org)
 * 
 * Sipdroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.example.bironu.simpletransceiver.rtp;

/**
 * RtpPacket implements a RTP packet.
 */
public class RtpPacket {
	public static final int HEADER_LENGTH = 12;
	public static final int MAX_HEADER_LENGTH = HEADER_LENGTH + 4 * 15;
	
//	/** Creates a new RTP packet */
//	public RtpPacket(byte[] buffer, int ptype, int seqn, long timestamp, long ssrc) {
//		if(buffer == null) {
//			throw new NullPointerException("buffer is null.");
//		}
//		packet = buffer;
//		setVersion(2);
//		setPayloadType(ptype);
//		setSequenceNumber(seqn);
//		setTimestamp(timestamp);
//		setSsrc(ssrc);
//	}
//
	/** Creates a new RTP packet */
	public RtpPacket(byte[] buffer) {
		setBuffer(buffer);
	}
	
	/* RTP packet buffer containing both the RTP header and payload */
	byte[] mPacket;

	/* RTP packet length */
	int mPacketLength;

	/* RTP header length */
	// int header_len;
	/** Gets the RTP packet */
	public byte[] getPacket() {
		return mPacket;
	}

	/** Gets the RTP packet length */
	public int getPacketLength() {
		return mPacketLength;
	}

	/** Gets the RTP header length */
	public int getHeaderLength() {
		return HEADER_LENGTH + 4 * getCscrCount();
	}

	/** Gets the RTP header length */
	public int getPayloadLength() {
		return mPacketLength - getHeaderLength();
	}

	/** Sets the RTP payload length */
	public void setPayloadLength(int len) {
		mPacketLength = getHeaderLength() + len;
	}

	// version (V): 2 bits
	// padding (P): 1 bit
	// extension (X): 1 bit
	// CSRC count (CC): 4 bits
	// marker (M): 1 bit
	// payload type (PT): 7 bits
	// sequence number: 16 bits
	// timestamp: 32 bits
	// SSRC: 32 bits
	// CSRC list: 0 to 15 items, 32 bits each

	/** Gets the version (V) */
	public int getVersion() {
		return (mPacket[0] >> 6 & 0x03);
	}

	/** Sets the version (V) */
	public void setVersion(int v) {
		mPacket[0] = (byte) ((mPacket[0] & 0x3F) | ((v & 0x03) << 6));
	}

	/** Whether has padding (P) */
	public boolean hasPadding() {
		return getBit(mPacket[0], 5);
	}

	/** Set padding (P) */
	public void setPadding(boolean p) {
		mPacket[0] = setBit(p, mPacket[0], 5);
	}

	/** Whether has extension (X) */
	public boolean hasExtension() {
		return getBit(mPacket[0], 4);
	}

	/** Set extension (X) */
	public void setExtension(boolean x) {
		mPacket[0] = setBit(x, mPacket[0], 4);
	}

	/** Gets the CSCR count (CC) */
	public int getCscrCount() {
		return (mPacket[0] & 0x0F);
	}

	/** Whether has marker (M) */
	public boolean hasMarker() {
		return getBit(mPacket[1], 7);
	}

	/** Set marker (M) */
	public void setMarker(boolean m) {
		mPacket[1] = setBit(m, mPacket[1], 7);
	}

	/** Gets the payload type (PT) */
	public int getPayloadType() {
		return (mPacket[1] & 0x7F);
	}

	/** Sets the payload type (PT) */
	public void setPayloadType(int pt) {
		mPacket[1] = (byte) ((mPacket[1] & 0x80) | (pt & 0x7F));
	}

	/** Gets the sequence number */
	public int getSequenceNumber() {
		return getInt(mPacket, 2, 4);
	}

	/** Sets the sequence number */
	public void setSequenceNumber(int sn) {
		setInt(sn, mPacket, 2, 4);
	}

	/** Gets the timestamp */
	public long getTimestamp() {
		return getLong(mPacket, 4, 8);
	}

	/** Sets the timestamp */
	public void setTimestamp(long timestamp) {
		setLong(timestamp, mPacket, 4, 8);
	}

	/** Gets the SSRC */
	public long getSsrc() {
		return getLong(mPacket, 8, 12);
	}

	/** Sets the SSRC */
	public void setSsrc(long ssrc) {
		setLong(ssrc, mPacket, 8, 12);
	}

	/** Gets the CSCR list */
	public long[] getCscrList() {
		int cc = getCscrCount();
		long[] cscr = new long[cc];
		for (int i = 0; i < cc; i++) {
			cscr[i] = getLong(mPacket, 12 + 4 * i, 16 + 4 * i);
		}
		return cscr;
	}

	/** Sets the CSCR list */
	public void setCscrList(long[] cscr) {
		int cc = cscr.length;
		if (cc > 15) {
			cc = 15;
		}
		mPacket[0] = (byte) (((mPacket[0] >> 4) << 4) + cc);
		for (int i = 0; i < cc; i++) {
			setLong(cscr[i], mPacket, 12 + 4 * i, 16 + 4 * i);
		}
		// header_len=12+4*cc;
	}

	/** Sets the payload */
	public void setPayload(byte[] payload, int len) {
		int header_len = getHeaderLength();
		for (int i = 0; i < len; i++) {
			mPacket[header_len + i] = payload[i];
		}
		mPacketLength = header_len + len;
	}

	/** Gets the payload */
	public byte[] getPayload() {
		int header_len = getHeaderLength();
		int len = mPacketLength - header_len;
		byte[] payload = new byte[len];
		for (int i = 0; i < len; i++)
			payload[i] = mPacket[header_len + i];
		return payload;
	}

	// *********************** Private and Static ***********************

//	/** Gets int value */
//	private static int getInt(byte b) {
//		return ((int) b + 256) % 256;
//	}

	/** Gets long value */
	private static long getLong(byte[] data, int begin, int end) {
		long n = 0;
		for (; begin < end; begin++) {
			n <<= 8;
			n += data[begin] & 0xFF;
		}
		return n;
	}

	/** Sets long value */
	private static void setLong(long n, byte[] data, int begin, int end) {
		for (end--; end >= begin; end--) {
			data[end] = (byte) (n % 256);
			n >>= 8;
		}
	}

	/** Gets Int value */
	private static int getInt(byte[] data, int begin, int end) {
		return (int) getLong(data, begin, end);
	}

	/** Sets Int value */
	private static void setInt(int n, byte[] data, int begin, int end) {
		setLong(n, data, begin, end);
	}

	/** Gets bit value */
	private static boolean getBit(byte b, int bit) {
		return (b >> bit) == 1;
	}

	/** Sets bit value */
	private static byte setBit(boolean value, byte b, int bit) {
		if (value)
			return (byte) (b | (1 << bit));
		else
			return (byte) ((b | (1 << bit)) ^ (1 << bit));
	}

	public void setLength(int length) {
		mPacketLength = length;
	}

	public void setBuffer(byte[] buf, int length) {
		mPacket = buf;
		mPacketLength = length;
	}
	
	public void setBuffer(byte[] buf) {
		if(buf != null) {
			setBuffer(buf, buf.length);
		}
		else {
			setBuffer(null, 0);
		}
	}

}
