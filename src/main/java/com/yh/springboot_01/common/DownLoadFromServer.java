package com.yh.springboot_01.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.servlet.http.HttpServletResponse;

public class DownLoadFromServer {
	 
	/**
	 * 批量从服务器获取文件并压缩
	 * @param fileName文件名字符串
	 * @param url 下载地址
	 * @param port 下载的端口
	 * @param remotePath 远程地址
	 * @param localPath 本地地址
	 * @return
	 */
	public HttpServletResponse download(String path, HttpServletResponse response) {
	    try {
	      // path是指欲下载的文件的路径。
	      File file = new File(path);
	      // 取得文件名。
	      String filename = file.getName();
	      // 取得文件的后缀名。
	      String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
	 
	      // 以流的形式下载文件。
	      InputStream fis = new BufferedInputStream(new FileInputStream(path));
	      byte[] buffer = new byte[fis.available()];
	      fis.read(buffer);
	      fis.close();
	      // 清空response
	      response.reset();
	      // 设置response的Header
	      response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
	      response.addHeader("Content-Length", "" + file.length());
	      OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
	      response.setContentType("application/octet-stream");
	      toClient.write(buffer);
	      toClient.flush();
	      toClient.close();
	    } catch (IOException ex) {
	      ex.printStackTrace();
	    }
	    return response;
	  }
	 
	  public void downloadLocal(HttpServletResponse response) throws FileNotFoundException {
	    // 下载本地文件
	    String fileName = "Operator.doc".toString(); // 文件的默认保存名
	    // 读到流中
	    InputStream inStream = new FileInputStream("c:/Operator.doc");// 文件的存放路径
	    // 设置输出的格式
	    response.reset();
	    response.setContentType("bin");
	    response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
	    // 循环取出流中的数据
	    byte[] b = new byte[100];
	    int len;
	    try {
	      while ((len = inStream.read(b)) > 0)
	        response.getOutputStream().write(b, 0, len);
	      inStream.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	  }
	 
	  public void downloadNet(HttpServletResponse response) throws MalformedURLException {
	    // 下载网络文件
	    int bytesum = 0;
	    int byteread = 0;
	 
	    URL url = new URL("windine.blogdriver.com/logo.gif");
	 
	    try {
	      URLConnection conn = url.openConnection();
	      InputStream inStream = conn.getInputStream();
	      FileOutputStream fs = new FileOutputStream("c:/abc.gif");
	 
	      byte[] buffer = new byte[1204];
	      int length;
	      while ((byteread = inStream.read(buffer)) != -1) {
	        bytesum += byteread;
	        System.out.println(bytesum);
	        fs.write(buffer, 0, byteread);
	      }
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	  } 
	  
	  
 public void downLoad(String filePath, HttpServletResponse response, boolean isOnLine) throws Exception {
	    	File f = new File(filePath);
	      if (!f.exists()) {
	    	response.sendError(404, "File not found!");
	    	return;
	       }
	      BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
	    	byte[] buf = new byte[1024];
	    	int len = 0;
	    
	    response.reset(); // 非常重要
		 if (isOnLine) { // 在线打开方式
			URL u = new URL("file:///" + filePath);
			response.setContentType(u.openConnection().getContentType());
			response.setHeader("Content-Disposition", "inline; filename=" + f.getName());
			 // 文件名应该编码成UTF-8
        } else { // 纯下载方式
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=" + f.getName());
		}
		OutputStream out = response.getOutputStream();
		while ((len = br.read(buf)) > 0){
			 out.write(buf, 0, len);
		}
		br.close();
		out.close();
}
	  public void fileDownLoad(){
		  
	  }
	  
   public static void main(String[] args) {
	   
	   try {
		    File aFile= new File("D://a.txt");
		    aFile.createNewFile();
		    if(!aFile.exists()){
		    	 aFile.createNewFile();
		    }
			FileInputStream fileInputStream = new FileInputStream(new File("C://a.txt"));
		         //打开源文件流管道
		      FileChannel readChannel = fileInputStream.getChannel();
		         //定义缓冲区
		      ByteBuffer buffer = ByteBuffer.allocate(1024);
		         //对流出的地址文件打开流模式
		      FileOutputStream fileOutputStream = new FileOutputStream(aFile);
		         //打开目的地文件流管道
		      FileChannel writeChannel = fileOutputStream.getChannel();

		         //开始读取
		       while (true) {
		             //清除缓存区文件，保证缓冲区的干净
		             buffer.clear();
		             //源文件数据流入管道，到缓冲区
		             int len = readChannel.read(buffer);
		             if (len == -1 ) {
		                 break;
		             }
		             //位置倒置：下一篇文章重点对BufferAPI介绍
		             buffer.flip();
		             //写入文件
		             writeChannel.write(buffer);
		         }
		       //关闭管道
		         readChannel.close();
		         writeChannel.close();
		} catch (Exception e) {
			// TODO: handle exception
		}//将文件打开为流模式
    
	}
	
}

