package edu.utah.sci.cyclist.core.model;

public class Blob {
	static private char[] digit = "0123456789ABCDEF".toCharArray();
	private String value;
//	private byte[] data;
	
	public Blob(byte[] data) {
		this.value = decode(data);
//		this.data = data;
	}
	
	public Blob(String str) {
		this.value = str;
//		this.data = encode(str);
	}
	
	public byte[] getData() { 
//		return data;
		return encode(value);
	}
	
	public String toString() {
		return value;
	}
	
	private String decode(byte[] data) {
		int n = data.length;
		char[] hex = new char[2*n+3];
		
		int i=0;
		hex[i++] = 'X';
		hex[i++] = '\'';
		
		for (int j=0; j<n; j++) {
			int v = data[j] & 0xff;
			hex[i++] = digit[v>>4];
			hex[i++] = digit[v& 0x0f];
		}
		hex[i++] = '\'';
		
		return new String(hex);
	}
	
	private byte[] encode(String str) {
		int n = str.length()-1;
		
		byte[] bin = new byte[(n-2)/2];
		char[] text = str.toCharArray();
		

		int b = 0;
		for (int i=2; i<n; i+=2) {
			int v1 = text[i] < 'A' ? text[i]-'0' : 10+text[i]-'A';
			int v2 = text[i+1] < 'A' ? text[i+1]-'0' : 10+text[i+1]-'A';
			bin[b++] =(byte)( (v1<<4) + v2);
		}
		
		return bin;
	}
}
