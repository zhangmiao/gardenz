package org.walkmanz.gardenz.store.linked;

import java.io.IOException;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Queue;

public class LinkedFileQueueAdapter extends AbstractQueue<byte[]> implements Queue<byte[]> {
	
	private final LinkedFileQueue queue;
	
	
	public LinkedFileQueueAdapter(String path, int bufferSize) {
		try {
			queue = new LinkedFileQueue(path, bufferSize);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public LinkedFileQueueAdapter(String path) {
		try {
			queue = new LinkedFileQueue(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	

	@Override
	public boolean offer(byte[] e) {
		try {
			queue.add(e);
		} catch (IOException ex) {
			return false;
		}
		return true;
	}

	@Override
	public byte[] poll() {
		byte[] bytes = null;
		try {
			bytes = queue.remove();
		} catch (IOException ex) {
			return null;
		}
		return bytes;
	}

	@Override
	public byte[] peek() {
		byte[] bytes = null;
		try {
			bytes = queue.peek();
		} catch (IOException ex) {
			return null;
		}
		return bytes;
	}

	@Override
	public Iterator<byte[]> iterator() {
		throw new UnsupportedOperationException("暂不支持iterator");
	}

	@Override
	public int size() {
		return this.queue.size();
	}

	//@Override
	public void close() {
		try{
			if (queue != null) {
				queue.close();
			}
		}catch(IOException ex){
			
		}
	}

}
