package com.taotao.fastdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.IOException;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description：测试FastDFS，图片服务器
 * @author:cxg
 * @Date:${time}
 */
public class TestFastDFS {
    @Test
    public void  uploadFile() throws IOException, MyException {
        //1、向工程中添加jar包
        //2、创建一个配置文件，配置tracker服务器地址
        //3、加载配置文件
        ClientGlobal.init("G:\\Java-webspace\\taotao_cxg\\taotao_manager_web\\src\\main\\resources\\resource\\client.conf");
        //4、创建一个TrackerClien对象
        TrackerClient trackerClient=new TrackerClient();
        //5、使用TrackerClient对象获得trackerserver对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //6、创建一个StorageServer的引用null
        StorageServer storageServer=null;
        //7、创建一个StorageClient对象。trackerserver、StorageServer两个参数
        StorageClient storageClient=new StorageClient(trackerServer,storageServer);
        //8、使用StorageClient对象上传文件
        String[] strings = storageClient.upload_file("C:\\Users\\Administrator\\Desktop\\12345.jpg", "jpg", null);
        for (String string:strings) {
            System.out.println(string);
        }
    }
}
