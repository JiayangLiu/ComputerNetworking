package nkcs.networking.fnl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.zip.ZipException;

import javax.swing.JOptionPane;

import nkcs.networking.util.CompressUtil;
import nkcs.networking.util.SecurityGuard;

/**
 * 处理其他对等方传输过来文件的线程类
 */
public class Receiver implements Runnable {
	private Socket socket;
	private DataInputStream dis;
	private FileOutputStream fos;
	private DataOutputStream connectionAck;
	private String oppositeName;
	private String myName;

	private static DecimalFormat df = null;

	static {
		df = new DecimalFormat("#0.0"); // 设置数字格式，保留一位有效小数
		df.setRoundingMode(RoundingMode.HALF_UP);
		df.setMinimumFractionDigits(1);
		df.setMaximumFractionDigits(1);
	}

	public Receiver(Socket socket) {
		try {
			connectionAck = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.socket = socket;
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			System.out.println("enter receiver");
			dis = new DataInputStream(socket.getInputStream());

			// 文件名和长度
			oppositeName = dis.readUTF();
			myName = dis.readUTF();
			String fileName = dis.readUTF();
			long fileLength = dis.readLong();

			System.out.print("正在准备接收: " + fileName);

			//Scanner ackScanner = new Scanner(System.in);
			//String ackString = ackScanner.next();
			int bufferSize = 8192;
			byte[] bytes1 = new byte[bufferSize];
			boolean t=this.creatACKWindow();
			System.out.println("ready to confirm "+t);
			
			if (!t) {
				connectionAck.writeUTF("N");
				connectionAck.flush();
				System.out.println(bytes1);
			} else {
				connectionAck.writeUTF("Y");
				String str = bytes1.toString();
				System.out.println(str);
				connectionAck.flush();
				System.out.println("output ok");

				// 使用基于项目的相对路径，以提供高移植性
				File directory = new File("FileReceived");
				if (!directory.exists()) {
					directory.mkdir();
				}

				File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
				fos = new FileOutputStream(file);

				// 开始接收文件
				byte[] bytes = new byte[1024];
				int length = 0;
				while ((length = dis.read(bytes, 0, bytes.length)) != -1) {
					fos.write(bytes, 0, length);
					fos.flush();
				}
				System.out.println("======== 文件接收成功 [File Name：" + fileName + "] [Size：" + getFormatFileSize(fileLength)
						+ "] ========");
				
				// 解密文件
				String unzipCode = SecurityGuard.getMD5(oppositeName + myName);
			
				File unzipFile = new File(file.getAbsolutePath());
				System.out.println("hehe"+file.getAbsolutePath());
				File[] unzipFileGroup = CompressUtil.unzip(unzipFile, directory.getAbsolutePath(), unzipCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
				if (dis != null)
					dis.close();
				socket.close();
			} catch (Exception e) {
			}
		}
	}

	private boolean creatACKWindow() {
		boolean flag = false;
		//JOptionPane jop_ACK = new JOptionPane();
		Object[] options = {"接收", "拒绝"};
		//JOptionPane ack = new JOptionPane();
		int response = JOptionPane.showOptionDialog(null, "你怕不怕", "接受请求", JOptionPane.YES_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if (response == 0)
			flag = true;
		else if (response == 1)
			flag = false;
		return flag;
	}

	/**
	 * 格式化文件大小
	 * 
	 * @param length
	 * @return String
	 */
	private String getFormatFileSize(long length) {
		double size = ((double) length) / (1 << 30);
		if (size >= 1) {
			return df.format(size) + "GB";
		}
		size = ((double) length) / (1 << 20);
		if (size >= 1) {
			return df.format(size) + "MB";
		}
		size = ((double) length) / (1 << 10);
		if (size >= 1) {
			return df.format(size) + "KB";
		}
		return length + "B";
	}
}
