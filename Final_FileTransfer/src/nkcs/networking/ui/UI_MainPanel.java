package nkcs.networking.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nkcs.networking.burrow.UDPSender;
import nkcs.networking.fnl.Sender;
import nkcs.networking.util.DbManager;
import nkcs.networking.util.SecurityGuard;
import nkcs.networking.util.UserInfo;

public class UI_MainPanel {

	private JFrame frame;
	private JTextField textField;
	private UserInfo ui;
	private String fileName;
	private String fileAbsolutePath;
	JLabel lblUsername;
	JLabel lblIP;
	private UserInfo[] friend_list;
	JList<?> list;
	List<String> selected_list;
	int selectedCount = 0;
	HashMap map_userinfo;
	HashMap map_userport;

	/**
	 * Create the application.
	 */
	public UI_MainPanel(UserInfo ui) {
		initialize();
		this.frame.setVisible(true);
		this.friend_list=null;
		this.ui = ui;
		this.lblUsername.setText("当前用户: " + ui.getUsername());
		this.lblIP.setText("IP地址: " + ui.getIp());
		this.selected_list = new ArrayList<String>();
		this.map_userinfo = new HashMap();
		this.map_userport = new HashMap();
		
		initialize2();
	}

	 private void initialize2()
	 {
		 DbManager dm = new DbManager();
			this.friend_list = dm.getFriend(ui);
			if(friend_list==null)System.out.println("friendlist is null\n");
			String[] name_list = new String[friend_list.length];
			map_userinfo.clear();
			map_userport.clear();
			for (int i=0; i<friend_list.length; i++) {
				
				name_list[i] = new String(friend_list[i].getUsername());
				map_userinfo.put(name_list[i], friend_list[i].getIp());
				map_userport.put(name_list[i], friend_list[i].getPort());
			}
			ListModel jListModel =  new DefaultComboBoxModel(name_list);
			this.list = new JList<String>();
			this.list.setModel(jListModel);
			this.list.setSelectionModel(new DefaultListSelectionModel() {
			    @Override
			    public void setSelectionInterval(int index0, int index1) {
			        if(super.isSelectedIndex(index0)) {
			            super.removeSelectionInterval(index0, index1);
			        }
			        else {
			            super.addSelectionInterval(index0, index1);
			        }
			    }
			});
			this.list.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					Object[] values = list.getSelectedValues();
					selected_list.clear();
					for(int i=0;i<values.length;i++){
						selected_list.add((String) values[i]);
					}   
				}
	        });
			this.list.setBounds(139, 112, 318, 419);
			this.list.setPreferredSize(new java.awt.Dimension(192, 173));
			frame.getContentPane().add(list);
	 }
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		ui = new UserInfo();
				
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 600, 35);
		frame.getContentPane().add(menuBar);
		
		JMenu menu = new JMenu("系统");
		menuBar.add(menu);
		
		JMenuItem menuItem = new JMenuItem("登出");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DbManager dm = new DbManager();
				if (dm.tryLogout(ui)) {
					System.exit(0);
				} else {
					JOptionPane.showMessageDialog(null, "没门", "还想登出？？", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		menu.add(menuItem);
		
		JMenu menu_1 = new JMenu("关于");
		menuBar.add(menu_1);
		
		JButton button = new JButton("加密发送");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnPressed_Send();
			}
		});
		button.setBounds(340, 543, 117, 29);
		frame.getContentPane().add(button);
		
		JButton button_1 = new JButton("文件选取");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectFile();
			}
		});
		button_1.setBounds(340, 71, 117, 29);
		frame.getContentPane().add(button_1);
		
		textField = new JTextField();
		textField.setBounds(135, 71, 193, 26);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		lblUsername = new JLabel("New label");
		lblUsername.setBounds(442, 38, 158, 16);
		frame.getContentPane().add(lblUsername);
		
		lblIP = new JLabel("New label");
		lblIP.setBounds(442, 58, 158, 16);
		frame.getContentPane().add(lblIP);
		
		JButton btn_fresh = new JButton("刷新");
		btn_fresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getFriendList();
			}
		});
		
		btn_fresh.setBounds(135, 543, 117, 29);
		frame.getContentPane().add(btn_fresh);
		
		// 设定初始化置顶
		// frame.setAlwaysOnTop(true);
		// 设定初始化居中
		Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
		Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
		int screenWidth = screenSize.width / 2; // 获取屏幕的宽
		int screenHeight = screenSize.height / 2; // 获取屏幕的高
		int height = frame.getHeight();
		int width = frame.getWidth();
		frame.setLocation(screenWidth - width / 2, screenHeight - height / 2);
		frame.setVisible(true);
		
		fileName = "";
		fileAbsolutePath = "";
		
	}
	
	public void getFriendList() {
		// frame.getContentPane().remove(list);
		
		DbManager dm = new DbManager();
		this.friend_list = dm.getFriend(ui);
		String[] name_list = new String[friend_list.length];
		
		UDPSender udpsender = null;
		for (int i=0; i<friend_list.length; i++) {
			name_list[i] = new String(friend_list[i].getUsername());
			map_userinfo.put(name_list[i], friend_list[i].getIp());
			map_userport.put(name_list[i], friend_list[i].getPort());
			udpsender = new UDPSender(friend_list[i].getIp(), friend_list[i].getPort());
			try {
				udpsender.udpsenderstart();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		ListModel jListModel =  new DefaultComboBoxModel(name_list);
		this.list.setModel(jListModel);
	}
	
	public void selectFile() {
		JFileChooser jfc=new JFileChooser();  
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );  
        jfc.showDialog(new JLabel(), "选择");  
        File file=jfc.getSelectedFile();  
        if(file.isFile()){
        	fileName = file.getName();
        	fileAbsolutePath = file.getAbsolutePath();
            System.out.println("文件路径: " + fileAbsolutePath);
            textField.setText(fileName);
        }else if(file.isDirectory()){
			JOptionPane.showMessageDialog(null, "请选择具体文件!", "提示", JOptionPane.WARNING_MESSAGE);
        }  
        System.out.println(jfc.getSelectedFile().getName());
	}
	
	public void btnPressed_Send() {
		for (int i=0;i<selected_list.size(); i++) {
			System.out.println(selected_list.get(i));
			Sender send = new Sender((String)map_userinfo.get(selected_list.get(i)),(String)map_userport.get(selected_list.get(i)), fileAbsolutePath, ui.getUsername(), selected_list.get(i));
		}
		
	}
}
