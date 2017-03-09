package obj;

import android.graphics.drawable.Drawable;

public class Apps {

	int uid;
	String appname;
	long upload;
	Drawable icon;
	Drawable lvl_warning;
	public Drawable getLvl_warning() {
		return lvl_warning;
	}

	public void setLvl_warning(Drawable lvl_warning) {
		this.lvl_warning = lvl_warning;
	}
	String packageName;
	long download;
	String upUnit;
	String pid;
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}
	String downUnit;
	public double perUp = 0.00;
	public double perDown = 0.00;
	/**
	 * 应用类构造方法
	 * @param uid
	 * @param appname
	 * @param upload
	 * @param download
	 * @param icon
	 * @param packetname
	 */
	public Apps(int uid, String appname, long upload, long download, Drawable icon, String packetname) {
		this.uid = uid;
		this.appname = appname;
		this.upload = upload;
		this.icon = icon;
		this.packageName = packetname;
		this.download = download;
		this.upUnit = "KB/s";
		this.downUnit = "KB/s";
	}

	public Apps() {

	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getAppname() {
		if(appname.length()>20)
		{
			appname=appname.substring(0,20);
		}
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public long getUpload() {
		int flag = 1;
		this.upUnit = GetUnit(flag);
		return upload;
	}

	public void setUpload(long upload) {
		this.upload = upload;
	}

	public Drawable getIcon() {
		return this.icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}


	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}


	public long getDownload() {
		int flag = 1;
		this.downUnit = GetUnit(flag);
		return download;
	}

	public void setDownload(long download) {
		this.download = download;
	}


	public String getUpUnit() {
		return upUnit;
	}

	public String getDownUnit() {
		return downUnit;
	}
	private String GetUnit(int flag) {
		String rtnVal = "B/s";
		switch (flag) {
		case 0:
			break;
		case 1:
			rtnVal = "KB/s";
			break;
		case 2:
			rtnVal = "MB/s";
			break;
		case 3:
			rtnVal = "GB/s";
			break;
		default:
			break;
		}
		return rtnVal;
	}
}
