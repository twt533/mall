package com.mall.product.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mall.common.exception.BusinessException;
import com.mall.product.entity.ProductBrand;
import com.mall.product.mapper.ProductBrandMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {

    private final ProductBrandMapper brandMapper;

    public BrandService(ProductBrandMapper brandMapper) {
        this.brandMapper = brandMapper;
    }

    public List<ProductBrand> list() {
        QueryWrapper<ProductBrand> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).orderByAsc("sort_order");
        return brandMapper.selectList(wrapper);
    }

    public ProductBrand create(String name, String logoUrl, String description) {
        ProductBrand brand = new ProductBrand();
        brand.setName(name);
        brand.setLogoUrl(logoUrl);
        brand.setDescription(description);
        brand.setSortOrder(0);
        brand.setStatus(1);
        brandMapper.insert(brand);
        return brand;
    }

    public void update(Long id, String name, String logoUrl, String description) {
        ProductBrand brand = brandMapper.selectById(id);
        if (brand == null) {
            throw new BusinessException(404, "品牌不存在");
        }
        if (name != null) brand.setName(name);
        if (logoUrl != null) brand.setLogoUrl(logoUrl);
        if (description != null) brand.setDescription(description);
        brandMapper.updateById(brand);
    }

    public void delete(Long id) {
        ProductBrand brand = brandMapper.selectById(id);
        if (brand != null) {
            brand.setStatus(0);
            brandMapper.updateById(brand);
        }
    }
}
