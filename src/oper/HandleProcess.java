package oper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HandleProcess {
	static Process process;
	ProcessBuilder pb;

	public HandleProcess() {
		try {
			process = Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				throw new Exception("获取Root权限失败，请退出重试");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public static void KillProcess(String packageName) {
		if (process == null)
			try {
				throw new Exception("没有Root权限");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		OutputStream out = process.getOutputStream();
		String cmd = "am force-stop " + packageName + " \n";
		try {
			out.write(cmd.getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String[] getPortInfoBasePackage(String pid) {
		String[] temp = new String[] {};
		String sum = getNetstat(pid);
		temp = sum.split("\n");
		return temp;
	}

	public String getNetstat(String pid) {
		String temp = "ps";
		System.out.println(temp);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Process pc;
		pb = new ProcessBuilder("sh", "-c", temp);
		try {
			pc = pb.start();
			InputStream inIs=pc.getInputStream();
			int read = -1;

			try {
				while ((read = inIs.read()) != -1) {
					baos.write(read);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			byte[] data = baos.toByteArray();
			temp = new String(data);
			inIs.close();
			baos.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
		
		}
		return temp.trim();
	}
}
