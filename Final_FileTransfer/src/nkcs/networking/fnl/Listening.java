package nkcs.networking.fnl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import nkcs.networking.burrow.UDPSender;

public class Listening implements Runnable {
	public Listening() {
		new Thread(this).start();
	}

	@SuppressWarnings("resource")
	@Override
	public void run() {
		ServerSocket ss = null;
		try {
			System.out.println("start serversocket");
			ss = new ServerSocket(8989); // 设定接收端口号为9898
			System.out.println("started serversocket");
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (true) {
			Socket get = null;
			try {
				// 尝试接收其他Socket的连接请求，ServerSocket类的accept方法是阻塞式的
				System.out.println("waiting");
				get = ss.accept();
				// ss.close();
				System.out.println("accepted");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("out");
			}
//
//			try {
//				DataInputStream dis = null;
//				dis = new DataInputStream(get.getInputStream());
//				if (dis.readInt() == 1) { // 1为服务器发送
//					String oppositeIp = dis.readUTF();
//					System.out.println("oppositeIp" + oppositeIp);
//					String oppositePort = dis.readUTF();
//					System.out.println("oppositePort" + oppositePort);
//					new UDPSender(oppositeIp, oppositePort);	// 发送UDP给对方
//				} else if (dis.readInt() == 0) {
					// 每接收到一个Socket就建立一个通过Receiver线程类新建一个线程来处理它
					new Receiver(get);
//				} else {
//					System.out.println("接收到外星消息");
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

		}
	}
}

/**
 * 后期: 1.可以在前端的加入设置菜单，并在其中支持接收端口号的修改，以防止冲突 (但很尴尬的是，对方并不知道我们改成了什么。使用MySQL更新时存起来?)
 */
