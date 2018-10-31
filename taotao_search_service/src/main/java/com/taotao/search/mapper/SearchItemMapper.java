package com.taotao.search.mapper;

import com.taotao.common.pojo.SearchItem;

import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:
 * @author:cxg
 * @Date:${time}
 */
public interface SearchItemMapper {

  List<SearchItem> getItemList();

  SearchItem getItemById(Long itemId);
}
