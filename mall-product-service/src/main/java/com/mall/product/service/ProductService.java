package com.mall.product.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.PageResult;
import com.mall.common.util.SnowflakeIdUtil;
import com.mall.product.document.ProductDocument;
import com.mall.product.dto.ProductPageDTO;
import com.mall.product.dto.ProductSaveDTO;
import com.mall.product.entity.Product;
import com.mall.product.entity.ProductCategory;
import com.mall.product.entity.ProductSku;
import com.mall.product.mapper.ProductCategoryMapper;
import com.mall.product.mapper.ProductMapper;
import com.mall.product.mapper.ProductSkuMapper;
import com.mall.product.repository.ProductEsRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService {

    private final ProductMapper productMapper;
    private final ProductSkuMapper skuMapper;
    private final ProductCategoryMapper categoryMapper;
    private final ProductEsRepository esRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String DETAIL_KEY = "product:detail:";
    private static final String HOT_KEY = "product:hot:";

    public ProductService(ProductMapper productMapper,
                          ProductSkuMapper skuMapper,
                          ProductCategoryMapper categoryMapper,
                          ProductEsRepository esRepository,
                          RedisTemplate<String, Object> redisTemplate,
                          ObjectMapper objectMapper) {
        this.productMapper = productMapper;
        this.skuMapper = skuMapper;
        this.categoryMapper = categoryMapper;
        this.esRepository = esRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Product create(ProductSaveDTO dto) {
        Product product = new Product();
        product.setSpuNo("SPU" + SnowflakeIdUtil.nextIdStr());
        product.setName(dto.getName());
        product.setCategoryId(dto.getCategoryId());
        product.setBrandId(dto.getBrandId());
        product.setDescription(dto.getDescription());
        product.setDetail(dto.getDetail());
        product.setMainImage(dto.getMainImage());
        product.setUnit(dto.getUnit() != null ? dto.getUnit() : "件");

        try {
            if (dto.getImages() != null) {
                product.setImages(objectMapper.writeValueAsString(dto.getImages()));
            }
        } catch (JsonProcessingException e) {
            throw new BusinessException("图片数据格式错误");
        }

        // Compute price range from SKUs
        if (dto.getSkus() != null && !dto.getSkus().isEmpty()) {
            BigDecimal min = dto.getSkus().stream().map(ProductSaveDTO.SkuDTO::getPrice)
                    .min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            BigDecimal max = dto.getSkus().stream().map(ProductSaveDTO.SkuDTO::getPrice)
                    .max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            int totalStock = dto.getSkus().stream().mapToInt(s -> s.getStock() != null ? s.getStock() : 0).sum();
            product.setMinPrice(min);
            product.setMaxPrice(max);
            product.setTotalStock(totalStock);
        }

        product.setTotalSales(0);
        product.setStatus(1);
        product.setCreateTime(LocalDateTime.now());
        productMapper.insert(product);

        // Save SKUs
        if (dto.getSkus() != null) {
            for (ProductSaveDTO.SkuDTO s : dto.getSkus()) {
                ProductSku sku = new ProductSku();
                sku.setSkuNo("SKU" + SnowflakeIdUtil.nextIdStr());
                sku.setProductId(product.getId());
                sku.setSpecValues(s.getSpecValues());
                sku.setPrice(s.getPrice());
                sku.setMarketPrice(s.getMarketPrice());
                sku.setStock(s.getStock() != null ? s.getStock() : 0);
                sku.setImage(s.getImage());
                sku.setStatus(1);
                sku.setCreateTime(LocalDateTime.now());
                skuMapper.insert(sku);
            }
        }

        // Sync to ES
        syncToEs(product);

        // Evict cache
        redisTemplate.delete(HOT_KEY + "*");

        return product;
    }

    public Product getDetail(Long id) {
        // Try cache
        String cacheKey = DETAIL_KEY + id;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached.toString(), Product.class);
            } catch (Exception ignored) {}
        }

        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }

        // Cache for 30 min
        redisTemplate.opsForValue().set(cacheKey, product, 30, TimeUnit.MINUTES);
        return product;
    }

    public List<ProductSku> getSkus(Long productId) {
        QueryWrapper<ProductSku> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id", productId).eq("status", 1);
        return skuMapper.selectList(wrapper);
    }

    public PageResult<Product> search(ProductPageDTO dto) {
        int page = Math.max(dto.getPage(), 1);
        int size = Math.min(Math.max(dto.getSize(), 1), 100);

        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.like("name", dto.getKeyword());
        }
        if (dto.getCategoryId() != null) {
            wrapper.eq("category_id", dto.getCategoryId());
        }
        if (dto.getBrandId() != null) {
            wrapper.eq("brand_id", dto.getBrandId());
        }
        if (dto.getStatus() != null) {
            wrapper.eq("status", dto.getStatus());
        } else {
            wrapper.eq("status", 1); // Only show listed products
        }

        String sortBy = dto.getSortBy();
        if ("price_asc".equals(sortBy)) {
            wrapper.orderByAsc("min_price");
        } else if ("price_desc".equals(sortBy)) {
            wrapper.orderByDesc("min_price");
        } else if ("sales_desc".equals(sortBy)) {
            wrapper.orderByDesc("total_sales");
        } else {
            wrapper.orderByDesc("create_time");
        }

        Page<Product> result = productMapper.selectPage(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    public List<Product> getHot(int limit) {
        return productMapper.selectHot(Math.min(limit, 50));
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        product.setStatus(status);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);

        redisTemplate.delete(DETAIL_KEY + id);
        syncToEs(product);
    }

    public void syncToEs(Product product) {
        try {
            ProductDocument doc = new ProductDocument();
            doc.setId(product.getId());
            doc.setName(product.getName());
            doc.setCategoryId(product.getCategoryId());
            doc.setBrandId(product.getBrandId());
            doc.setMainImage(product.getMainImage());
            doc.setMinPrice(product.getMinPrice());
            doc.setMaxPrice(product.getMaxPrice());
            doc.setTotalSales(product.getTotalSales());
            doc.setTotalStock(product.getTotalStock());
            doc.setStatus(product.getStatus());
            doc.setDescription(product.getDescription());

            ProductCategory cat = categoryMapper.selectById(product.getCategoryId());
            if (cat != null) {
                doc.setCategoryName(cat.getName());
            }

            esRepository.save(doc);
        } catch (Exception e) {
            // ES sync failure should not break main flow
        }
    }
}
