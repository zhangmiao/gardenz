package org.walkmanz.gardenz.store;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * 
 *  使用FileChannel实现带有缓冲的文件读写工具类
 *  
 *  无缓冲直接读写文件, 效率很低, 在高速读写需求的场景不推荐使用
 *
 */
public class ChannelDataFile extends DataFile {

	/**
	 * 文件实例
	 */
	private final File file;

	/**
	 * IO通道
	 */
	private FileChannel channel;

	protected boolean isCreateFile = false;

	/**
	 * 构造函数，会打开指定的文件
	 * 
	 * @param file
	 * @throws java.io.IOException
	 */
	public ChannelDataFile(File file, boolean force) throws IOException {
		this(file.getAbsolutePath(),force);
	}

	public ChannelDataFile(String path, boolean force) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			file.createNewFile();
			this.isCreateFile = true;
		}

		this.file = file;
		this.channel = new RandomAccessFile(file, force ? "rws" : "rw")
				.getChannel();
		// 指针移到首字节
		this.channel.position(0);
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
	 * 同步数据
	 */
	@Override
	public void sync() throws IOException {
		
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
	 * 获取指针位置
	 * @return
	 * @throws java.io.IOException
	 */
	@Override
	public long position() throws IOException {
		return this.channel.position();
	}
	
	/**
	 * 设置指针位置
	 * @param position
	 * @throws java.io.IOException
	 */
	@Override
	public void position(long position) throws IOException {
		this.channel.position(position);
	}
	
	/**
	 * 关闭通道
	 * 
	 * @throws java.io.IOException
	 */
	@Override
	public void close() throws IOException {
		this.sync();
		this.channel.close();
		this.channel = null;
	}

	/**
	 * 写一个bytes到文件的当前指针位置, 文件的指针会向后移动bytes的长度
	 * 
	 * @param bytes
	 * @return
	 * @throws java.io.IOException
	 */
	@Override
	public long write(final byte[] bytes) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		this.channel.write(buffer);
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
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		this.channel.write(buffer, position);
	}
	
	/**
	 * 按当前位置文件读取数据到bytes，直到读满或者读到文件结尾。 文件的指针会向后移动bytes的大小
	 * 
	 * @param bytes
	 * @throws java.io.IOException
	 */
	@Override
	public void read(final byte[] bytes) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		this.channel.read(buffer);
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
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		this.channel.read(buffer, position);
	}

	/**
	 * 获取一个MappedByteBuffer
	 * 
	 * @param mode
	 * @param position
	 * @param size
	 * @return
	 * @throws java.io.IOException
	 */
	public MappedByteBuffer map(MapMode mode,
			 long position, long size) throws IOException{
		return this.channel.map(mode, position, size);
	}

}
