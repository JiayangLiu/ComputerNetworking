package nkcs.networking.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DbManager implements Runnable {
	static UserInfo userInfo;
	static UserInfo[] userfriend;
	int mode;
	static URL url;
	boolean flag, success;

	String c1, c2;

	public DbManager() {

	}
	// public static void main(String arg[]) {
	// DbManager dbManager=new DbManager();
	// UserInfo ui=new UserInfo();
	// ui.setUsername("xiaoxi");
	// ui.setPassword("111");
	// ui.setIp("122.2.2.2");
	// ui.setPort("8989");
	// System.out.println("result is
	// "+dbManager.getFriend(ui)[0].getUsername());
	// }

	public boolean beforeSend(String client1, String client2) // my, opposite
	{
		System.out.println("enter before send");
		c1 = client1;
		c2 = client2;
		flag = false;
		Thread my = new Thread(this);
		mode = 3;
		my.start();
		System.out.println("my start");
		while (flag == false) {
			try {
				System.out.println("begin sleep");
				my.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return success;
	}

	public boolean tryLogin(UserInfo ui) {
		userInfo = ui;
		flag = false;
		Thread my = new Thread(this);
		mode = 0;
		my.start();
		while (flag == false) {
			try {
				my.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return success;
	}

	public boolean tryLogout(UserInfo ui) {
		userInfo = ui;
		flag = false;
		Thread my = new Thread(this);
		mode = 1;
		my.start();
		while (flag == false) {
			try {
				my.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return success;
	}

	public UserInfo[] getFriend(UserInfo ui) {
		userInfo = ui;
		flag = false;
		Thread my = new Thread(this);
		mode = 2;
		my.start();
		while (flag == false) {
			try {
				my.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return userfriend;
	}

	public void run() {

		try {
			switch (mode) {
			case 0:
				url = new URL("http://123.206.90.48/net_site_digging/index.php/Data/login?username="
						+ userInfo.getUsername() + "&&ip=" + userInfo.getIp() + "&&port=" + userInfo.getPort()
						+ "&&password=" + userInfo.getPassword());
				break;
			case 1:
				url = new URL("http://123.206.90.48/net_site_digging/index.php/Data/logout?username="
						+ userInfo.getUsername());
				break;

			case 2:
				url = new URL("http://123.206.90.48/net_site_digging/index.php/Data/getfriend?username="
						+ userInfo.getUsername());
				break;
			case 3:
				url = new URL("http://123.206.90.48/net_site_digging/index.php/Digging/send_message?from=" + c1
						+ "&&to=" + c2);
				break;
			default:
				break;
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("connection fail");
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 设置从connection中读取内容的通道

		// 开始读取内容
		StringBuilder b = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				b.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String str = b.toString();
		JSONArray ja = JSONArray.fromObject(str);
		// System.out.println("user information= "+ja.length());
		// System.out.println("user information= "+ja);
		// System.out.println("user information= "+ja.getJSONObject(0));
		if (mode == 0 || mode == 1) {
			JSONObject temp = ja.optJSONObject(0);
			String suc = temp.get("success").toString();
			if (suc.equals("yes")) {
				success = true;
			} else {
				success = false;
			}
			flag = true;
		}
		if (mode == 2) {
			int n = ja.length();
			userfriend = new UserInfo[n - 1];
			for (int i = 1; i < n; i++) {
				UserInfo temp = new UserInfo();
				JSONObject jo = ja.getJSONObject(i);
				temp.setIp(jo.getString("ip"));
				temp.setUsername(jo.getString("username"));
				temp.setPort(jo.getString("port"));
				userfriend[i - 1] = temp;
			}
			flag = true;
		}
		if (mode == 3) {
			JSONObject temp = ja.optJSONObject(0);
			String suc = temp.get("success").toString();
			if (suc.equals("yes")) {
				success = true;
			} else {
				success = false;
			}
			flag = true;
		}
	}
}
