package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.exceptions.APIException;
import com.ecommerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {

        List<Category> categories = categoryRepository.findAll();
        if(categories.isEmpty())
            throw new APIException("There are currently no categories present in the Database!!");

        return categories;
    }



    @Override
    public void createCategory(Category category) {
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if(savedCategory != null){
            throw new APIException("Category with " + category.getCategoryName() + " already exists!!!");
        }
        categoryRepository.save(category);
    }



    @Override
    public String deleteCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow( () -> new ResourceNotFoundException("Category", "categoryId", String.valueOf(categoryId)) );

        categoryRepository.delete(category);
        return "Category with ID " + categoryId + " deleted!!";
    }



    @Override
    public Category updateCategory(Category category, Long categoryId) {

//      Throwing custom exceptions and handling them using our custom exception handler
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow( () -> new ResourceNotFoundException("Category", "categoryId", String.valueOf(categoryId)) );

        category.setId(categoryId);
        savedCategory = categoryRepository.save(category);

        return savedCategory;
    }


//    @Override
//    public Category updateCategory(Category category, Long categoryId) {
//        Category savedCategory = categoryRepository.findById(categoryId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found!"));
//
//
//        Optional<Category> savedCategoryOtional = categoryRepository.findById(categoryId);
//
//        Category savedCategory = savedCategoryOtional
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found!"));
//
////        Earlier we were fetching all categories from DB and updating only one required category
////        now we are fetching only one category from the DB that needs to be updated
//        Optional<Category> optionalCategory = categories.stream()
//                .filter(c -> c.getId().equals(categoryId))
//                .findFirst();
//
//        if (optionalCategory.isPresent()) {
//            Category existingCategory = optionalCategory.get();
//            existingCategory.setCategoryName(category.getCategoryName());
//
//            Category savedCategory = categoryRepository.save(existingCategory);
//
//            return savedCategory;
//        } else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found!");
//        }
//    }
}
