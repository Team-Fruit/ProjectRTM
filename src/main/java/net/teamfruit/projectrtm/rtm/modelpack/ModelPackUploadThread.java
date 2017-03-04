package net.teamfruit.projectrtm.rtm.modelpack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import net.teamfruit.projectrtm.ngtlib.io.NGTFileLoader;
import net.teamfruit.projectrtm.ngtlib.io.NGTLog;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.network.PacketModelPack;

public class ModelPackUploadThread extends Thread {
	private ByteBuffer buffer = ByteBuffer.allocate(RTMCore.PacketSize);

	public ModelPackUploadThread() {
		super("RTM ModelPack Upload");
	}

	public static void startThread() {
		if (!RTMCore.useServerModelPack) {
			return;
		}
		ModelPackUploadThread thread = new ModelPackUploadThread();
		thread.start();
	}

	@Override
	public void run() {
		NGTLog.debug("[RTM](UploadThread) Start uploading ModelPack");
		List<File> fileList = NGTFileLoader.findFile("ModelPack_", ".zip", "");
		for (File file : fileList) {
			try {
				NGTLog.debug("[RTM](UploadThread) Start uploading "+file.getName());
				RTMCore.NETWORK_WRAPPER.sendToAll(new PacketModelPack("start_file:"+file.getName(), 0, ByteBuffer.allocate(RTMCore.PacketSize)));

				@SuppressWarnings("resource")
				FileChannel channel = new FileInputStream(file).getChannel();
				long size = channel.size();
				while (channel.read(this.buffer)>=0) {
					this.buffer.flip();
					RTMCore.NETWORK_WRAPPER.sendToAll(new PacketModelPack(file.getName(), size, this.buffer));
					this.buffer.clear();
					this.sleep(100);
				}
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		RTMCore.NETWORK_WRAPPER.sendToAll(new PacketModelPack("finish", 0, ByteBuffer.allocate(RTMCore.PacketSize)));
		NGTLog.debug("[RTM](UploadThread) Finish uploading ModelPack");
	}
}