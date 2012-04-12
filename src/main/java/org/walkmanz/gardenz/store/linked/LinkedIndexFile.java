package org.walkmanz.gardenz.store.linked;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.walkmanz.gardenz.store.ChannelDataFile;
import org.walkmanz.gardenz.store.FileFormatException;
import org.walkmanz.gardenz.util.IoUtils;

/**
 * 
 * 简单索引文件
 *
 */
public class LinkedIndexFile extends ChannelDataFile {

    private static final Logger LOG = LoggerFactory.getLogger(LinkedIndexFile.class);
    
    /**
     * 索引文件格式常量定义
     */
    public static final String INDEX_FILE_HEAD_LOGO = "qlinkidx"; //16字节字符串
    
    public static final String DATA_FILE_HEAD_LOGO = "qlinkidb";
    
    private static final int INDEX_FILE_LIMIT_LENGTH = 32;
    
    public static final int DATA_FILE_LIMIT_LENGTH = 1024 * 1024 * 256;
    
    public static final int DATA_MESSAGE_START_POSITION = 32;
    
	/**
	 * 数据文件实例以及IO对象
	 */
	private MappedByteBuffer mappedByteBuffer;
	
	/**
	 * 数据文件读写指针
	 */
	private int readerPosition = -1;
	private int writerPosition = -1;
	
	/**
	 * 数据文件读写编号
	 */
	private int readerIndex = -1;
	private int writerIndex = -1;
	
	private AtomicInteger size = new AtomicInteger();

	/**
	 * 文件操作位置信息
	 */
	private String logoString = null;
	private int version = -1;
	
	public LinkedIndexFile(String path) throws IOException, FileFormatException {
		super(path,true);

		// 文件不存在, 创建文件
		if (this.size() == 0) {
			ByteBuffer buffer = ByteBuffer.allocate(LinkedIndexFile.INDEX_FILE_LIMIT_LENGTH);
			
			buffer.put(LinkedIndexFile.INDEX_FILE_HEAD_LOGO.getBytes());// logo 0-7字节
			buffer.putInt(1);// 8-11 version int4 字节
			buffer.putInt(LinkedIndexFile.DATA_MESSAGE_START_POSITION);// 12-15 reader
			buffer.putInt(LinkedIndexFile.DATA_MESSAGE_START_POSITION); //16-29 write
			buffer.putInt(0);// 20-23 readerindex
			buffer.putInt(0);// 24-27 writerindex
			buffer.putInt(0);// 28-31 size
			
			buffer.rewind();
			
			super.write(buffer.array());
			
			//先写文件, 之后更新状态
			logoString = INDEX_FILE_HEAD_LOGO;
			version = 1;
			readerPosition = DATA_MESSAGE_START_POSITION;
			writerPosition = DATA_MESSAGE_START_POSITION;
			readerIndex = 0;
			writerIndex = 0;
			
			mappedByteBuffer = super.map(MapMode.READ_WRITE, 0, LinkedIndexFile.INDEX_FILE_LIMIT_LENGTH);
			
			LOG.info("索引文件 {} 创建完毕", super.getFileName());
		} else {
			
			if (this.size() < LinkedIndexFile.INDEX_FILE_LIMIT_LENGTH) {
				throw new FileFormatException("索引文件格式错误");
			}
			
			logoString = super.readUTF(8,0);
			version = super.readInt(8);
			readerPosition = super.readInt(12);
			writerPosition = super.readInt(16);
			readerIndex = super.readInt(20);
			writerIndex = super.readInt(24);
			
			size.set(super.readInt(28));

			mappedByteBuffer = super.map(MapMode.READ_WRITE, 0, LinkedIndexFile.INDEX_FILE_LIMIT_LENGTH);
			
			
			LOG.info("索引文件 {} 已打开", super.getFileName());
			
			System.out.println(this.headerInfo());
		}
	}
	
	public LinkedIndexFile(File file) throws IOException, FileFormatException {
		this(file.getAbsolutePath());
	}
	

	/**
	 * 记录写位置
	 * 
	 * @param pos
	 */
	public void putWriterPosition(int pos) {
		mappedByteBuffer.position(16);
		mappedByteBuffer.putInt(pos);
		this.writerPosition = pos;
	}

	/**
	 * 记录读取的位置
	 * 
	 * @param pos
	 */
	public void putReaderPosition(int pos) {
		mappedByteBuffer.position(12);
		mappedByteBuffer.putInt(pos);
		this.readerPosition = pos;
	}

	/**
	 * 设置写数据文件编号
	 * 
	 * @param index
	 */
	public void putWriterIndex(int index) {
		mappedByteBuffer.position(24);
		mappedByteBuffer.putInt(index);
		this.writerIndex = index;
	}

	/**
	 * 设置读取文件编号
	 * 
	 * @param index
	 */
	public void putReaderIndex(int index) {
		mappedByteBuffer.position(20);
		mappedByteBuffer.putInt(index);
		this.readerIndex = index;
	}

	public void incrementSize() {
		int num = size.incrementAndGet();
		mappedByteBuffer.position(28);
		mappedByteBuffer.putInt(num);
	}

	public void decrementSize() {
		int num = size.decrementAndGet();
		mappedByteBuffer.position(28);
		mappedByteBuffer.putInt(num);
	}

	public String getLogoString() {
		return logoString;
	}

	public int getVersion() {
		return version;
	}

	public int getReaderPosition() {
		return readerPosition;
	}

	public int getWriterPosition() {
		return writerPosition;
	}

	public int getReaderIndex() {
		return readerIndex;
	}

	public int getWriterIndex() {
		return writerIndex;
	}

	public int getSize() {
		return size.get();
	}

	/**
	 * 关闭索引文件
	 */
	public void close() {
		try {
			mappedByteBuffer.force();
			IoUtils.clean(mappedByteBuffer);
			super.close();
			mappedByteBuffer = null;
		} catch (IOException e) {
			LOG.error("关闭索引文件异常", e);
		}
	}

	public String headerInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append(" logoString:");
		sb.append(logoString);
		sb.append(" version:");
		sb.append(version);
		sb.append(" readerPosition:");
		sb.append(readerPosition);
		sb.append(" writerPosition:");
		sb.append(writerPosition);
		sb.append(" size:");
		sb.append(size);
		sb.append(" readerIndex:");
		sb.append(readerIndex);
		sb.append(" writerIndex:");
		sb.append(writerIndex);
		return sb.toString();
	}

	
}

