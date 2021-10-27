package com.cognizant.servelt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

public class CopyLogFiles implements Runnable {

	private String Host = null;
	private String User = null;
	private String Password = null;
	private String LogPath = null;
	private String SearchString = null;

	public CopyLogFiles(String sString, JSONObject jObj) {
		this.SearchString = sString;
		try {
			this.Host = jObj.get("Host").toString();
			this.User = jObj.get("User").toString();
			this.Password = jObj.get("Password").toString();
			this.LogPath = jObj.get("LogPath").toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		JSch sJSch = null;
		Session sSession = null;
		String fname = null;
		JSch dJSch = null;
		ChannelSftp dSftpChannel = null;
		Channel dChannel = null;
		Session dSession = null;
		String fpath = null ;
		ChannelSftp sSftpChannel = null;
		Channel sChannel = null;


		try {
			sJSch = new JSch();
			sSession = sJSch.getSession(this.User, this.Host, 22);
			sSession.setConfig("StrictHostKeyChecking", "no");
			sSession.setPassword(this.Password);
			sSession.connect();

			sChannel = sSession.openChannel("sftp");
			sChannel.connect();

			sSftpChannel = (ChannelSftp) sChannel;
			Vector<ChannelSftp.LsEntry> directoryEntries = sSftpChannel.ls(this.LogPath);


			for (LsEntry file : directoryEntries) {

				fpath = this.LogPath;

				Date fileDate = new Date(file.getAttrs().getMTime() * 1000L);
				Date currentDate = new Date();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(currentDate);
				calendar.add(Calendar.HOUR_OF_DAY, -1);


				if ((file.getAttrs().isDir() == Boolean.FALSE) && (fileDate.compareTo(calendar.getTime()) > 0)) {
					if (!file.getFilename().endsWith("snapshot.log")) {
						fname = file.getFilename();
						fpath = fpath + "/" + fname;
						InputStream stream = sSftpChannel.get(fpath);

						try {
							BufferedReader br = new BufferedReader(new InputStreamReader(stream));
							try {
								String line = null;
								int count = 0;
								while ((line = br.readLine()) != null) {

									if (line.contains(this.SearchString)) {
										count++;
										stream.close();
										break;
									}
								}
								if (count == 1) {
									if((null==dSession) || (!dSession.isConnected())) {
										dJSch = new JSch();
										dSession = dJSch.getSession(FTPConfig.User, FTPConfig.Host, 22);
										dSession.setConfig("StrictHostKeyChecking", "no");
										dSession.setPassword(FTPConfig.Password);

										dSession.connect();

									}
									stream = sSftpChannel.get(fpath);
									System.out.println(String.format("File - %s", fpath));

									dChannel = dSession.openChannel("sftp");
									dChannel.connect();

									String tmpPath = FTPConfig.Path + this.SearchString + "/";
									//System.out.println("The Tmp Path : " + tmpPath);
									dSftpChannel = (ChannelSftp) dChannel;

									SftpATTRS attrs = null;
									try {
										attrs = dSftpChannel.stat(tmpPath);
									} catch (Exception e) {
										System.out.println(tmpPath + " is not found");
									}

									if (attrs == null) {
										System.out.println("Creating dir ");
										dSftpChannel.mkdir(tmpPath);
									}

									File dFile = new File(fpath);

									//	System.out.println("File Name : " + dFile.getName());
									dSftpChannel.put(stream, tmpPath + dFile.getName(), ChannelSftp.OVERWRITE);

								}

								stream.close();
							} catch (IOException e) {
								try {
									stream.close();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								e.printStackTrace();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}

			}

		} catch (JSchException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		} finally {
			//System.out.println("dSftpChannel closed");
			if (dSftpChannel != null) {
				dSftpChannel.exit();
				dSession.disconnect();
			}
			if (sSftpChannel != null) {
				//System.out.println("sSftpChannel closed");
				sSftpChannel.exit();
				sChannel.disconnect();
				sSession.disconnect();
			}
		}

	}

}
