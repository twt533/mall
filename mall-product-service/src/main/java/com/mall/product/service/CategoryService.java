package com.mall.product.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mall.common.exception.BusinessException;
import com.mall.product.entity.ProductCategory;
import com.mall.product.mapper.ProductCategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final ProductCategoryMapper categoryMapper;

    public CategoryService(ProductCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public List<ProductCategory> listTree() {
        QueryWrapper<ProductCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).orderByAsc("sort_order");
        return categoryMapper.selectList(wrapper);
    }

    public ProductCategory create(String name, Long parentId, Integer sortOrder) {
        ProductCategory cat = new ProductCategory();
        cat.setName(name);
        cat.setParentId(parentId != null ? parentId : 0L);
        cat.setLevel(parentId != null && parentId > 0 ? 2 : 1);
        cat.setSortOrder(sortOrder != null ? sortOrder : 0);
        cat.setStatus(1);
        categoryMapper.insert(cat);
        return cat;
    }

    public void update(Long id, String name, Integer sortOrder) {
        ProductCategory cat = categoryMapper.selectById(id);
        if (cat == null) {
            throw new BusinessException(404, "分类不存在");
        }
        if (name != null) cat.setName(name);
        if (sortOrder != null) cat.setSortOrder(sortOrder);
        categoryMapper.updateById(cat);
    }

    public void delete(Long id) {
        // Check children
        long childCount = categoryMapper.selectCount(
                new QueryWrapper<ProductCategory>().eq("parent_id", id).eq("status", 1));
        if (childCount > 0) {
            throw new BusinessException("请先删除子分类");
        }
        ProductCategory cat = categoryMapper.selectById(id);
        if (cat != null) {
            cat.setStatus(0);
            categoryMapper.updateById(cat);
        }
    }
}
