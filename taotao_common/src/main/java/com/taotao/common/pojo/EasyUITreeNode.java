package com.taotao.common.pojo;

import java.io.Serializable;

/**
 * @version V1.0
 * @ClassName: 分类目录
 * @Description:创建一个pojo来描述tree的节点信息，包含三个属性id、text、state。
 * @author:cxg
 * @Date:
 */
public class EasyUITreeNode implements Serializable{
    private long id;
    private String text;
    private  String state;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
