package com.xy.ecommerce.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.List;

public class FTPUtil {

    private static Logger logger=LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp=PropertiesUtil.getProperty("ftp.ip");
    private static int ftpPort=Integer.parseInt(PropertiesUtil.getProperty("ftp.port"));
    private static String ftpUser=PropertiesUtil.getProperty("ftp.user");
    private static String ftpPwd=PropertiesUtil.getProperty("ftp.password");

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public FTPUtil(String ip, int port, String user, String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    public static boolean uploadFile(List<File> fileList) throws  IOException{
        FTPUtil ftpUtil = new FTPUtil(ftpIp,ftpPort,ftpUser, ftpPwd);
        logger.info("connecting ftp server");
        boolean result = ftpUtil.uploadFile("img",fileList);
        logger.info("uploaded");
        return result;
    }

    private boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;

        if(connectServer(this.ip,this.port,this.user,this.pwd)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for(File fileItem : fileList){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
            } catch (IOException e) {
                logger.error("error uploading files",e);
                uploaded = false;
            } finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    private boolean connectServer(String ip, int port, String user, String pwd){
        ftpClient=new FTPClient();
        boolean loggedIn=false;
        try {
            ftpClient.connect(ip, port);
            ftpClient.login(user, pwd);
        } catch (Exception e){
            logger.error("error connecting ftp server",e);
        }
        return loggedIn;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
