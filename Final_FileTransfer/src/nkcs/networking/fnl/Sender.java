package nkcs.networking.fnl;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import nkcs.networking.util.CompressUtil;
import nkcs.networking.util.DbManager;
import nkcs.networking.util.SecurityGuard;

public class Sender implements Runnable {
	private String ip, filepath, port;
	Socket socket;
	Boolean run;
	DataOutputStream dataout;
	DataInputStream datain;
	DataInputStream fin;
	File file;
	Thread my;
	String zipCode;
	String myName;
	String oppositeName;

	public Sender(String ip, String port, String filepath, String myName, String oppositeName) {
		this.ip = ip;
		this.port = port;
		this.filepath = filepath;
		this.myName = myName;
		this.oppositeName = oppositeName;
		run = true;
		
		// 文件加密
		try {
			System.out.println(myName + oppositeName);
			this.zipCode = SecurityGuard.getMD5(myName + oppositeName);
			System.out.println(zipCode);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("nonono");
		}
		System.out.println("hhhh "+this.zipCode);
		String filepathString=getFileNameNoEx(filepath);
		String zipAbsoPath = CompressUtil.zip(filepath, filepathString+".zip", false, this.zipCode);
		System.out.println("creating path"+zipAbsoPath);
		this.filepath=zipAbsoPath;
		this.file=new File(filepath);
		
// 		DbManager burrowDm = new DbManager();
// 		if (burrowDm.beforeSend(myName, oppositeName)) {
			this.my = new Thread(this);
			this.my.start();
// 		}
	}

	public static String getFileNameNoEx(String filename) {   
        if ((filename != null) && (filename.length() > 0)) {   
            int dot = filename.lastIndexOf('.');   
            if ((dot >-1) && (dot < (filename.length()))) {   
                return filename.substring(0, dot);   
            }   
        }   
        return filename;   
    } 
	
	
	@Override
	public void run() {
		String result = null;
		try {

			System.out.println("ready to send ip is " + ip);
			System.out.println("ready to send port is " + port);
			socket = new Socket(ip, Integer.parseInt(port));
			System.out.println("connected");
			result = "hello";
		} catch (BindException e) {
			result = "IP地址或端口绑定异常！";
		} catch (UnknownHostException e) {
			result = "未识别主机地址！";
		} catch (SocketTimeoutException e) {
			result = "连接超时！";
		} catch (ConnectException e) {
			result = "拒绝连接！";
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("connection fail");
			run = false;
			if (socket != null)
				try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}
		System.out.println(result);
		if (run) {
			// filein
			try {
				System.out.println("zippath:"+filepath);
				fin = new DataInputStream(new BufferedInputStream(new FileInputStream(filepath)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			// data out to ip (dataout.write)
			try {
				dataout = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			// data in from ip(datain.read)
			try {
				datain = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
//				dataout.writeInt(0);
//				dataout.flush();
				dataout.writeUTF(myName);
				dataout.flush();
				dataout.writeUTF(oppositeName);
				dataout.flush();
				//System.out.println("sending "+file.getName());
				dataout.writeUTF(file.getName());
				dataout.flush();
				dataout.writeLong((long) file.length());
				dataout.flush();
				System.out.println("waiting to be accept");
				String temp = datain.readUTF();
				System.out.println("data " + temp);
				String a = new String("Y");
				String b = new String("N");

				if (temp.equals(a)) {
					System.out.println("sending");
					JOptionPane.showConfirmDialog(null, ip + "确认接收" + file.getName(), "对方应答",
							JOptionPane.CLOSED_OPTION);
				} else if (temp.equals(b)) {
					System.out.println("sending fail");
					JOptionPane.showConfirmDialog(null, ip + "拒绝接收" + file.getName(), "对方应答",
							JOptionPane.CLOSED_OPTION);
					my.stop();
				} else {
					System.out.println("sending fail2");
					my.stop();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int bufferSize = 1024;
			byte[] buf = new byte[bufferSize];
			while (true) {
				try {
					int read = 0;
					if (fin != null) {
						read = fin.read(buf);
					}
					if (read == -1) {
						break;
					}
					dataout.write(buf, 0, read);
					dataout.flush();
				} catch (Exception e) {
					// TODO: handle exception
					break;
				}
			}
				try {
					
					fin.close();
					socket.close();
					System.out.println("文件传输完成");
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			

		}

	}

}
