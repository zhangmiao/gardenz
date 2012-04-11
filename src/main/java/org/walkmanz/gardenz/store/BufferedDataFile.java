package org.walkmanz.gardenz.store;

import org.walkmanz.gardenz.util.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * 
 * 使用MappedByteBuffer实现带有缓冲的文件读写工具类
 *  
 * 默认缓冲大小是1M, 通过构造参数可调整大小
 * 
 * 带有position参数的绝对读写方法会反复调用IO操作，不推荐频繁使用
 * 
 */
public class BufferedDataFile extends DataFile {

	public static final int BUFFER_LIMIT_LENGTH = 1024 * 1024;

	/**
	 * 文件实例
	 */
	private final File file;

	private final FileChannel channel;

	private final int bufferLimitLength;
	
	private final MapMode mapMode;
	
	
	
	private long mappedStartPosition;
	
	private MappedByteBuffer mappedByteBuffer;
	
	protected boolean isCreateFile = false;

	
	/**
	 * 构造函数，会打开指定的文件
	 * 
	 * @param file
	 * @throws java.io.IOException
	 */
	public BufferedDataFile(File file, boolean force) throws IOException {
		this(file.getAbsolutePath(), BufferedDataFile.BUFFER_LIMIT_LENGTH, force);
	}
	
	public BufferedDataFile(String path, boolean force) throws IOException {
		this(path, BufferedDataFile.BUFFER_LIMIT_LENGTH, force);
	}

	public BufferedDataFile(String path, int bufferLimitLength , boolean force) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			file.createNewFile();
			this.isCreateFile = true;
		}

		this.file = file;
		this.bufferLimitLength = bufferLimitLength;
		this.mapMode = MapMode.READ_WRITE;

		this.channel = new RandomAccessFile(file, force ? "rws" : "rw")
				.getChannel();

		this.position(channel.size());

	}
	
	/**
	 * 获得文件名
	 * 
	 * @return
	 * @throws java.io.IOException
	 */
	@Override
	public String getFileName() throws IOException {
		return this.file.getName();
	}

	/**
	 * 删除文件
	 * 
	 * @return
	 * @throws java.io.IOException
	 */
	@Override
	public boolean delete() throws IOException {
		this.close();
		return this.file.delete();
	}

	/**
	 * 同步数据到磁盘
	 *
	 * @throws IOException
	 */
	@Override
	public void sync() throws IOException {
		this.mappedByteBuffer.force();
	}
	
	/**
	 * 获取大小
	 * 
	 * @return
	 * @throws java.io.IOException
	 */
	@Override
	public long size() throws IOException {
		return this.channel.size();
	}
	
	/**
	 * 设置文件指针位置, 可能会改变缓冲区在通道中的取值位置
	 * @param position
	 * @throws java.io.IOException
	 */
	@Override
	public void position(long position) throws IOException {
		long start = this.mappedStartPosition;
		long end = start + this.bufferLimitLength - 1;
		
		if(this.mappedByteBuffer != null && start <= position && position <= end){
			int pos = (int)(position - this.mappedStartPosition);
			this.mappedByteBuffer.position(pos);
		}else{
			this.map(position);
		}
	}

	/**
	 * 获取文件指针位置
	 * 
	 * @return
	 * @throws java.io.IOException
	 */
	@Override
	public long position() throws IOException {
		return this.mappedStartPosition + mappedByteBuffer.position();
	}
	
	/**
	 * 查看缓冲区是否还有装下length长度的剩余空间
	 * 
	 * @param length
	 * @return
	 * @throws java.io.IOException
	 */
	private boolean hasRemain(int length) throws IOException {
		int size = this.mappedByteBuffer.capacity();
		int position = this.mappedByteBuffer.position();
		
		if(size - length < position) {
			return false;
		}
		return true;
	}
	
	/**
	 * 从指定位置重新映射一个缓冲区
	 * @param position
	 * @throws java.io.IOException
	 */
	private void map(long position) throws IOException {
		if (mappedByteBuffer != null) {
			this.sync();
		}
		
		//map重新映射
		this.mappedByteBuffer = this.channel.map(this.mapMode, position,
				this.bufferLimitLength);
		this.mappedStartPosition = position;
	}
	
	/**
	 * 关闭通道
	 * 
	 * @throws java.io.IOException
	 */
	@Override
	public void close() throws IOException {
		
		this.sync();
		IoUtils.clean(this.mappedByteBuffer);
		this.mappedByteBuffer = null;
		this.channel.close();
	}


	/**
	 * 写一个bytes到文件的当前指针位置, 文件的指针会向后移动bytes的长度
	 * 
	 * @param bytes
	 * @throws java.io.IOException
	 */
	@Override
	public long write(final byte[] bytes) throws IOException {
		int length = bytes.length;
		
		if(!hasRemain(length)){
			this.map(this.position());
		}
		this.mappedByteBuffer.put(bytes);
		return this.position();
	}

	/**
	 * 写一个bytes到文件的指定位置, 文件指针不会移动
	 * 
	 * @param bytes
	 * @param position
	 * @throws java.io.IOException
	 */
	@Override
	public void write(final byte[] bytes, final long position)
			throws IOException {
		
		long pos = this.position();
		this.position(position);
		this.write(bytes);
		this.position(pos);
	}
	
	/**
	 * 按当前位置文件读取数据到bytes，直到读满或者读到文件结尾。 文件的指针会向后移动bytes的大小
	 * 
	 * @param bytes
	 * @throws java.io.IOException
	 */
	@Override
	public void read(final byte[] bytes) throws IOException {
		int length = bytes.length;
		
		if(!hasRemain(length)){
			this.map(this.position());
		}
		this.mappedByteBuffer.get(bytes);
	}
	
	/**
	 * 从文件的指定位置读取数据到bytes, 直到读满或者读到文件结尾。 文件指针不会移动
	 * 
	 * @param bytes
	 * @param position
	 * @throws java.io.IOException
	 */
	@Override
	public void read(final byte[] bytes, final long position)
			throws IOException {
		long pos = this.position();
		this.position(position);
		this.read(bytes);
		this.position(pos);
	}
	
}
