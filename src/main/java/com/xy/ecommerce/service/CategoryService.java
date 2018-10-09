package com.xy.ecommerce.service;

import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.entity.Category;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    Response addCategory(String categoryName, Integer parentId);

    Response updateCategoryName(Integer categoryId, String categoryName);

    Response<List<Category>> getParallelChildrenCategory(int parentId);

    Response<Set<Category>> getCategoryWithChildren(int id);
}
