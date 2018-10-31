package com.taotao.common.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 功能：分页工具类
 * total:当前页码
 * row: 每页行数
 * @author cxg
 */


public class EasyUIDataGridResult implements Serializable {
    //总记录数
    private Integer total;
    //数据集
    private List rows;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
