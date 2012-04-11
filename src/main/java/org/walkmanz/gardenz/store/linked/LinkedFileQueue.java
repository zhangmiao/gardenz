package org.walkmanz.gardenz.store.linked;

import com.xh.queued.store.FileEOFException;
import com.xh.queued.store.WriteState;
import com.xh.queued.util.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LinkedFileQueue {
	
	private static final Logger LOG = LoggerFactory.getLogger(LinkedFileQueue.class);
	
	public static final String DEFAULT_LOCK_FILE_NAME = "lock.lock";
	
	public static final String INDEX_FILE_DEFAULT_NAME = "index.idx"; 
	
	public static final String fileSeparator = System.getProperty("file.separator");

	private final Lock lock = new ReentrantLock();
	
	private final String directory;
	
	private LinkedIndexFile indexFile;
	
	private LinkedDataFile writerHandler;
	
	private LinkedDataFile readerHandler;
	
	
	public LinkedFileQueue(String path) throws IOException {
		try{
			
			File file = new File(path);
			
			if(file.exists() && file.isDirectory()){
				this.directory = path;
			}else{
				
				throw new FileNotFoundException("文件路径不存在: " + path);
			}
			this.lock();
			this.init();
		}catch(IOException e){
			this.release();
			throw e;
		}
	}
	
	public void init() throws IOException {
		
		String indexPath = this.directory + fileSeparator + INDEX_FILE_DEFAULT_NAME;
		
		File idxFile = new File(indexPath);
		if(!idxFile.exists() || !idxFile.isFile()) {
			LOG.debug(INDEX_FILE_DEFAULT_NAME + "索引文件不存在, 创建中..");
			
			String[] list = idxFile.list();
			
			if(list != null){
				LOG.warn(indexPath + "正在初始化的目录不是一个空目录");
			}
		}
		
		//创建索引文件
		this.indexFile = new LinkedIndexFile(indexPath);
		
		//获取writeHandler
		int writerIndex = this.indexFile.getWriterIndex();
		this.writerHandler = getHandler(writerIndex);
		this.writerHandler.position(this.indexFile.getWriterPosition());
		
		//获取readerHandler
		int readerIndex = this.indexFile.getReaderIndex();
		this.readerHandler = getHandler(readerIndex);
		this.readerHandler.position(this.indexFile.getReaderPosition());
	}
	
	
	public void add(byte[] record) throws IOException {

		int increment = record.length + 4;
		try{
			lock.lock();
			
			//如果数据文件满则创建一个新的
			if(this.writerHandler.isFull(increment)){
				int nextWriterIndex = this.indexFile.getWriterIndex() + 1;
				
				//关闭上一个文件
				this.writerHandler.putEndPosition();
				this.writerHandler.close();
				
				//打开下一个文件
				this.writerHandler = this.getHandler(nextWriterIndex);
				
				//在索引文件中设置新的写指针
				this.indexFile.putWriterIndex(nextWriterIndex);
				this.indexFile.putWriterPosition(LinkedIndexFile.DATA_MESSAGE_START_POSITION);
			}
			
			//写数据
			WriteState state = this.writerHandler.put(record);
			if(!state.equals(WriteState.WRITE_SUCCESS)){
				throw new IOException("文件写入失败");
			}
			
			//写指针移位
			this.indexFile.putWriterPosition(this.indexFile.getWriterPosition() + increment);
			
			//计数累加
			this.indexFile.incrementSize();
			
		}catch(IOException e){
			throw e;
		}finally{
			lock.unlock();
		}
	}
	
	public byte[] remove() throws IOException {
		byte[] result = null;
		
		try{
			lock.lock();
			
			//如果没有数据可读
			if(!hasRead()){
				int writerIndex = this.indexFile.getWriterIndex();
				int readerIndex = this.indexFile.getReaderIndex();
				
				if(writerIndex == readerIndex){
					//队列已空
					return null;
				} else {
					//队列未空, 换文件读
					int nextReaderIndex = readerIndex + 1;
					
					//关闭上一个文件
					this.readerHandler.close();
					
					//打开下一个文件
					this.readerHandler = getHandler(nextReaderIndex);
					this.readerHandler.position(LinkedIndexFile.DATA_MESSAGE_START_POSITION);
					
					//在索引文件中设置新的读指针
					this.indexFile.putReaderIndex(nextReaderIndex);
					this.indexFile.putReaderPosition(LinkedIndexFile.DATA_MESSAGE_START_POSITION);
				}
			}
			//读数据
			result = this.readerHandler.get();
			
			
			if(result != null && result.length != 0){
				//重置读指针
				int position = this.indexFile.getReaderPosition() + result.length + 4;
				this.indexFile.putReaderPosition(position);
				this.indexFile.decrementSize();
			}
			
		} catch (FileEOFException e) {
			return null;
		} catch (IOException e) {
			throw e;
		} finally {
			lock.unlock();
		}
		return result;
	}
	
	
	
	private boolean hasRead() throws IOException {
		int readerIndex = this.indexFile.getReaderIndex();
		int writerIndex = this.indexFile.getWriterIndex();
		
		//如果读写同一个文件
		if(readerIndex == writerIndex){
			int readerPosition = this.indexFile.getReaderPosition();
			int writerPosition = this.indexFile.getWriterPosition();
			
			if(readerPosition + 4 < writerPosition){
				return true;
			}
		} else {
			int readerPosition = this.indexFile.getReaderPosition();
			int endPosition = (int)this.readerHandler.getEndPosition();
			
			if(readerPosition + 4 < endPosition){
				return true;
			}
			
		}
		return false;
	}
	
	public void close() throws IOException {
		try{
			lock.lock();
			
			this.writerHandler.close();
			this.readerHandler.close();
			this.indexFile.close();
			
			this.writerHandler = null;
			this.readerHandler = null;
			//this.indexFile = null;
			
			this.release();
		}catch(IOException e){
			throw e;
		}finally{
			lock.unlock();
		}
	}
	
	private void lock() throws IOException {
		String lockPath = this.directory + fileSeparator + DEFAULT_LOCK_FILE_NAME;
		File file = new File(lockPath);
		
		if(file.exists()){
			throw new RuntimeException("文件锁已存在, 不能初始化文件队列.");
		}else{
			file.createNewFile();
		}
		
	}
	
	private void release() throws IOException {
		String lockPath = this.directory + fileSeparator + DEFAULT_LOCK_FILE_NAME;
		File file = new File(lockPath);
		
		if(file.exists()){
			IoUtils.delete(file);
		}else{
			LOG.warn("{} 文件锁不存在", file.getAbsolutePath());
		}
	}
	
	private LinkedDataFile getHandler(int number) throws IOException {
		String dataPath = this.directory + fileSeparator + "data_" + number + ".db";
		return new LinkedDataFile(dataPath);
	}
	
	public static void main(String args[]) throws Exception {
		LinkedFileQueue queue = new LinkedFileQueue("c:/temp");
		queue.close();
	}
}
