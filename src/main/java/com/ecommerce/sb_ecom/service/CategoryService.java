package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.payload.CategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {

    CategoryResponse getAllCategories();
    void createCategory(Category category);

    String deleteCategory(Long categoryId);

    Category updateCategory(Category category, Long categoryId);
}
