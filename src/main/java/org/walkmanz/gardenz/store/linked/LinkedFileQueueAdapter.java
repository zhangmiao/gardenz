package org.walkmanz.gardenz.store.linked;

import java.util.AbstractQueue;
import java.util.Queue;

import java.io.IOException;
import java.util.Iterator;

public class LinkedFileQueueAdapter extends AbstractQueue<byte[]> implements Queue<byte[]> {
	
	private final LinkedFileQueue queue;
	
	
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
		throw new UnsupportedOperationException("peek Unsupported now");
	}

	@Override
	public Iterator<byte[]> iterator() {
		throw new UnsupportedOperationException("iterator Unsupported now");
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException("size Unsupported now");
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
