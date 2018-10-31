package com.taotao.search.service;

import com.taotao.common.pojo.TaotaoResult;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
public interface SearchItemService {
    /**
     * 导入数据到索引库
     * @return
     */
    TaotaoResult importItemsToIndex();
}
