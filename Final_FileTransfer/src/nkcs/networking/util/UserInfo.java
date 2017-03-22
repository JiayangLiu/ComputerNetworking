package nkcs.networking.util;

public class UserInfo {
	private String username;
	private String password;
	private String ip;
	private String port;
	
	public UserInfo() {
		this.username = "default";
		this.password= "";
		this.ip = "x.x.x.x";
		this.port = "";		
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
}
