package NewTime.fun.fibonacci;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Fibonacci {

	public static void main(String[] args) {
		Fibonacci f = new Fibonacci(8*128);
		for(int i = 0; i < f.getCount(); i++) {
			long number = f.getNumber(i);
			if(number < 1) {
				break;
			}
			System.out.println(number);
		}
		System.out.println("Count: " + f.getCount());
	}
	
	private int count = 0;
	
	private int p1, p2, pc;
	private int s = Integer.MAX_VALUE;
	private int i = Integer.MAX_VALUE;
	private int l = Integer.MAX_VALUE;
	
	private ByteBuffer buffer; 
		
	public Fibonacci(int initial) {
		buffer = ByteBuffer.allocate(initial);
		buffer.order(ByteOrder.BIG_ENDIAN);
		
		byte[] base = {1,1};
		buffer.put(base);
		base = null;		
		
		try {
			while(initial-- > 0) {
				calculate();
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.err.println(buffer.position() + " / " + buffer.capacity());
		}
	}
	
	public byte[] getArray() {
		return buffer.array();
	}
	
	public int getCount() {
		return count;
	}
	
	public long getNumber(int index) {
		int byteCount = s;
		int shortCount = (i - s) / 2;
		int intCount = (l - i) / 4;
		int longCount = (buffer.limit() - l) / 4;
		
		if(index < byteCount) {
			buffer.position(index);
			return buffer.get();
		}
		
		int count = byteCount + shortCount;
		int offset = byteCount;
		
		if(index < count) {
			int local = (index - byteCount) * 2;
			buffer.position(offset + local);
			return buffer.getShort();
		}
		
		count += intCount;
		offset += shortCount*2;
		
		if(index < count) {
			int local = (index - byteCount - shortCount) * 4;
			buffer.position(offset + local);
			return buffer.getInt();
		}
		
		count += longCount;
		offset += intCount*4;
		
		if(index < count) {
			int local = (index - byteCount - shortCount - intCount) * 8;
			buffer.position(offset + local);
			return buffer.getLong();
		}
		
		return -1;
	}
	
	private void calculate() throws Exception {
		long one = -1;
		long two = -1;
		
		buffer.position(p1);
				
		if(buffer.position() >= l) {
			one = buffer.getLong();
		}else if(buffer.position() >= i){
			one = buffer.getInt();
		}else if(buffer.position() >= s) {
			one = buffer.getShort();
		}else {
			one = buffer.get();
		}
		
		int offset = 0;
		
		if(buffer.position() >= l) {
			two = buffer.getLong();
			offset = 8;
		}else if(buffer.position() >= i){
			two = buffer.getInt();
			offset = 4;
		}else if(buffer.position() >= s) {
			two = buffer.getShort();
			offset = 2;
		}else {
			two = buffer.get();
			offset = 1;
		}
		
		
		p1 = buffer.position()-offset;
		
		long nNo = one + two;
		
		if(nNo < Byte.MAX_VALUE) {
			buffer.put((byte)nNo);
		}else if(nNo < Short.MAX_VALUE) {
			if(s == Integer.MAX_VALUE) {s = buffer.position();}
			buffer.putShort((short)nNo);
		}else if(nNo < Integer.MAX_VALUE) {
			if(i == Integer.MAX_VALUE) {i = buffer.position();}
			buffer.putInt((int)nNo);
		}else if(nNo < Long.MAX_VALUE) {
			if(l == Integer.MAX_VALUE) {l = buffer.position();}
			buffer.putLong((long)nNo);
		}else {
			System.out.println("Max variable length reached.");
			return;
		}
		
		count++;
		
	}
		
}
