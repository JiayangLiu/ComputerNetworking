package nkcs.networking.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.security.auth.login.LoginContext;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import nkcs.networking.util.DbManager;
import nkcs.networking.util.UserInfo;

public class UI_LogIn extends JFrame {
	private static JButton btn_LogIn_OK, btn_LogIn_Cancel;
	private static JTextField tf_LogIn_UserName;
	private static JPasswordField pf_LogIn_Password;
	private static JLabel lbl_LogIn_UserName, lbl_LogIn_Password;
	private static JPanel jp1, jp2, jp3;
	public static UI_LogIn logIn;
	
	public UI_LogIn() {
		logIn = this;
		// 创建组件
		btn_LogIn_OK = new JButton("确认");
		btn_LogIn_OK.addActionListener(new ButtonListener());
		btn_LogIn_Cancel = new JButton("取消");
		btn_LogIn_Cancel.addActionListener(new ButtonListener());
		tf_LogIn_UserName = new JTextField(10);
		pf_LogIn_Password = new JPasswordField(10);
		lbl_LogIn_UserName = new JLabel("用户名：");
		lbl_LogIn_Password = new JLabel("密   码：");
		jp1 = new JPanel();
		jp2 = new JPanel();
		jp3 = new JPanel();
		// 设置布局管理器
		this.setLayout(new GridLayout(3, 1, 5, 5));
		// 添加组件
		jp1.add(lbl_LogIn_UserName);
		jp1.add(tf_LogIn_UserName);
		jp2.add(lbl_LogIn_Password);
		jp2.add(pf_LogIn_Password);
		jp3.add(btn_LogIn_OK);
		jp3.add(btn_LogIn_Cancel);
		this.add(jp1);
		this.add(jp2);
		this.add(jp3);

		// 设置窗体属性
		this.setTitle("登录");
		this.setSize(280, 160);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// 设定初始化置顶
		// this.setAlwaysOnTop(true);
		// 设定初始化居中
		Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
		Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
		int screenWidth = screenSize.width / 2; // 获取屏幕的宽
		int screenHeight = screenSize.height / 2; // 获取屏幕的高
		int height = this.getHeight();
		int width = this.getWidth();
		this.setLocation(screenWidth - width / 2, screenHeight - height / 2);
		this.setVisible(true);
	}

	private static class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String currBtnName = e.getActionCommand();
			if (currBtnName.equals("确认")) {
				System.out.println("确认按钮点击");

				// 在此封装好User的对象
				String username = UI_LogIn.tf_LogIn_UserName.getText();
				String password = String.valueOf(UI_LogIn.pf_LogIn_Password.getPassword());
				System.out.println("用户名:\t" + username + "\n密码:\t" + password);
				InetAddress ia = null;
				String ip = null;
				try {
					ia = ia.getLocalHost();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				ip = ia.getHostAddress();
				System.out.println("IP:\t" + ip);

				UserInfo ui = new UserInfo();
				ui.setUsername(username);
				ui.setPassword(password);
				ui.setIp(ip);
				ui.setPort("8989");

				DbManager dm = new DbManager();
				
				if (dm.tryLogin(ui)) {
					System.out.println(ui.getIp());
					new UI_MainPanel(ui); // 改一下构造函数
					logIn.dispose();
				} else {
					JOptionPane.showMessageDialog(null, "用户名与密码不匹配", "失败", JOptionPane.WARNING_MESSAGE);
				}

			} else if (currBtnName.equals("取消")) {
				System.out.println("取消按钮点击");

				System.exit(0);
			}
		}
	}
}