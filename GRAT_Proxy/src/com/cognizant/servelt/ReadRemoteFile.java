package com.cognizant.servelt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
/**
 * Contains some methods to list files and folders from a directory
 *
 * @author Loiane Groner
 * http://loiane.com (Portuguese)
 * http://loianegroner.com (English)
 */
public class ReadRemoteFile {
    /**
     * List all the files and folders from a directory
     * @param directoryName to be listed
     */
    public void listFilesAndFolders(String directoryName){
        File directory = new File(directoryName);
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            System.out.println(file.getName());
        }
    }
    /**
     * List all the files under a directory
     * @param directoryName to be listed
     */
    public void listFiles(String directoryName){
        File directory = new File(directoryName);
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isFile()){
                System.out.println(file.getName());
            }
        }
    }
    /**
     * List all the folder under a directory
     * @param directoryName to be listed
     */
    public void listFolders(String directoryName){
        File directory = new File(directoryName);
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isDirectory()){
                System.out.println(file.getName());
            }
        }
    }
    /**
     * List all files from a directory and its subdirectories
     * @param directoryName to be listed
     */
    public void listFilesAndFilesSubDirectories(String directoryName){
        File directory = new File(directoryName);
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isFile()){
                System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()){
                listFilesAndFilesSubDirectories(file.getAbsolutePath());
            }
        }
    }
    
    public  void getRemoteFileList() throws JSchException, 
	SftpException, Exception {
    	 JSch jsch = new JSch();
         Session session = null;
    	 session = jsch.getSession("skarunai", "apsrd8408.uhc.com", 22);
    	  session.setConfig("StrictHostKeyChecking", "no");
          session.setPassword("Genesys$123");
          session.connect();
	Vector<String> lsVec=null;
	Channel channel = session.openChannel("sftp");
	channel.connect();
	ChannelSftp sftpChannel = (ChannelSftp) channel;
	  sftpChannel.cd("/logs/gcti/chat-ELR_1");
	try {
		//lsVec=(Vector<LsEntry>)sftpChannel.ls(cwd); //sftpChannel.lpwd()
		System.out.println(sftpChannel.lpwd());
	} catch (Exception e) {
		e.printStackTrace();
		throw e;
	} finally {
		sftpChannel.exit();
		channel.disconnect();
		session.disconnect();			
	}		
	
}
  
    public void listRemoteFiles() {
    	 JSch jsch = new JSch();
         Session session = null;
         Vector<LsEntry> lsVec=null;
         String destinationPath = "/tmp/";
       //  String regex = ".*\\.log (:|$).*";
         try {
        
             session = jsch.getSession("skarunai", "apsrd8408.uhc.com", 22);
             session.setConfig("StrictHostKeyChecking", "no");
             session.setPassword("Genesys$123");
             session.connect();

    
        String fname=null;
String fpath = "/logs/gcti/chat-ELR_1";
       //      String fpath = "/logs/gcti/chat-CTC_1";
             Channel channel = session.openChannel("sftp");
             channel.connect();
           
             ChannelSftp sftpChannel = (ChannelSftp) channel;
        //     sftpChannel.cd("/logs/gcti/chat-ELR_1");
          //   lsVec = sftpChannel.ls(regex);
            Vector<LsEntry> directoryEntries = sftpChannel.ls(fpath);
             for (LsEntry file : directoryEntries) {
            	 if(file.getAttrs().isDir()==Boolean.FALSE){
            		 if(!file.getFilename().endsWith("snapshot.log")){
            			 fname =  file.getFilename();
            			 fpath =fpath+"/"+ fname;
            			 System.out.println(String.format("File - %s", fpath));
            			 
            			  InputStream stream = sftpChannel.get(fpath);
            			  
            			 
            			    try {
            			      BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            			      int index = -1;
            			        try {
            			        	String line;
            			        	int count = 0;
            			        	while ((line = br.readLine()) != null)  {  
            			        	  
            			        	    if(line.contains("00059aDNW7BJ0618")){
            			        	        count++;
            			        	        break;
            			        	    }
            			        	}
            			        	if(count==1){
            			        		// sftpChannel.put(fpath,".");
            			        	
            			        		   JSch jsch2 = new JSch();
            			       	        Session session2 = null;
            			       	        session2 = jsch2.getSession("skarunai", "apsrd8409.uhc.com", 22);
            			                   session2.setConfig("StrictHostKeyChecking", "no");
            			                   session2.setPassword("Genesys$123");
            			                   session2.connect();
            			       	        Channel channel2 = session2.openChannel("sftp");
            			       	        channel2.connect();
            			       	        ChannelSftp channelSftp2 = (ChannelSftp) channel2;
            		                       File f = new File(fpath);
            		                       channelSftp2.put(stream, destinationPath+f.getName());
            		                       channelSftp2.exit();
                 			              session2.disconnect();
            			        	}
            			        	System.out.println(count);
								/*	List<String> alist = Files.lines(Paths.get(file.getFilename()))
										    .filter(line -> line.contains("00059aDNW7BJ06MT"))
										    .collect(Collectors.toList());
									System.out.println(alist);*/
            			        	 
								} catch (IOException e) {
									// TODO Auto-generated catch block
									try {
										stream.close();
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									e.printStackTrace();
								}
            			    } finally {
            			       
            			    }
            			 
            		 }
            	 }
         		
         	}
           
             sftpChannel.exit();
             session.disconnect();
         } catch (JSchException e) {
             e.printStackTrace();  
         } catch (SftpException e) {
             e.printStackTrace();
         }
     }
    
    
    public void listRemoteFiles_working() {
   	 JSch jsch = new JSch();
        Session session = null;
        Vector<LsEntry> lsVec=null;
      //  String regex = ".*\\.log (:|$).*";
        try {
       
            session = jsch.getSession("skarunai", "apsrd8408.uhc.com", 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword("Genesys$123");
            session.connect();

   
       
String fpath = "/logs/gcti/chat-ELR_1";
      //      String fpath = "/logs/gcti/chat-CTC_1";
            Channel channel = session.openChannel("sftp");
            channel.connect();
          
            ChannelSftp sftpChannel = (ChannelSftp) channel;
       //     sftpChannel.cd("/logs/gcti/chat-ELR_1");
         //   lsVec = sftpChannel.ls(regex);
           Vector<LsEntry> directoryEntries = sftpChannel.ls(fpath);
            for (LsEntry file : directoryEntries) {
           	 if(file.getAttrs().isDir()==Boolean.FALSE){
           		 if(!file.getFilename().endsWith("snapshot.log")){
           			 fpath =fpath+"/"+ file.getFilename();
           			 System.out.println(String.format("File - %s", fpath));
           			 
           			  InputStream stream = sftpChannel.get(fpath);
           			 
           			    try {
           			      BufferedReader br = new BufferedReader(new InputStreamReader(stream));
           			      int index = -1;
           			        try {
           			        	String line;
           			        	int count = 0;
           			        	while ((line = br.readLine()) != null)  {  
           			        	  
           			        	    if(line.contains("00059aDNW7BJ0618")){
           			        	        count++;
           			        	        break;
           			        	    }
           			        	}
           			        	System.out.println(count);
								/*	List<String> alist = Files.lines(Paths.get(file.getFilename()))
										    .filter(line -> line.contains("00059aDNW7BJ06MT"))
										    .collect(Collectors.toList());
									System.out.println(alist);*/
								} catch (IOException e) {
									// TODO Auto-generated catch block
									try {
										stream.close();
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									e.printStackTrace();
								}
           			    } finally {
           			       
           			    }
           			 
           		 }
           	 }
        		
        	}
            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();  
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }
    
    public void listRemoteFiles2() {
   	 JSch jsch = new JSch();
        Session session = null;
        Vector<LsEntry> lsVec=null;
      //  String regex = ".*\\.log (:|$).*";
        try {
       
            session = jsch.getSession("skarunai", "apsrd8408.uhc.com", 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword("Genesys$123");
            session.connect();

   
       

            Channel channel = session.openChannel("sftp");
            channel.connect();
          
            ChannelSftp sftpChannel = (ChannelSftp) channel;
       //     sftpChannel.cd("/logs/gcti/chat-ELR_1");
         //   lsVec = sftpChannel.ls(regex);
           Vector<LsEntry> directoryEntries = sftpChannel.ls("/logs/gcti/chat-ELR_1");
            for (LsEntry file : directoryEntries) {
           	 if(file.getAttrs().isDir()==Boolean.FALSE){
           		 if(!file.getFilename().endsWith("snapshot.log")){
           			 System.out.println(String.format("File - %s", file.getFilename()));
           		 }
           	 }
        		
        	}
            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();  
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }
    
	
    public static void main (String[] args){
   ReadRemoteFile obj = new ReadRemoteFile();
   try {
	obj.listRemoteFiles();
} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
    }
}
