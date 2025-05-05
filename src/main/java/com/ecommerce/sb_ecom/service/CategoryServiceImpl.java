package com.ecommerce.sb_ecom.service;

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

//    private List<Category> categories = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
//        category.setId(nextId++);
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
//                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found!") );
                .orElseThrow( () -> new ResourceNotFoundException("Category", "categoryId", String.valueOf(categoryId)) );



        categoryRepository.delete(category);

        return "Category with ID " + categoryId + " deleted!!";
//        List<Category> categories = categoryRepository.findAll();
//        Category category = categories.stream()
//                .filter(c -> c.getId().equals(categoryId))
//                .findFirst()
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found!"));


//        categories.remove(category);

//        if(category == null)
//            return "Category not found";

    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {

//        Throwing custom exceptions and handling them using our custoom exception handler
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow( () -> new ResourceNotFoundException("Category", "categoryId", String.valueOf(categoryId)) );


//        Category savedCategory = categoryRepository.findById(categoryId)
//                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found!") );


        category.setId(categoryId);
        savedCategory = categoryRepository.save(category);

        return savedCategory;



//        Optional<Category> savedCategoryOtional = categoryRepository.findById(categoryId);
//
//        Category savedCategory = savedCategoryOtional
//                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found!") );

//        Earlier we were fetching all categories from DB and updating only one required category
//                now we are fetching only one category from the DB that needs to be updated
//        Optional<Category> optionalCategory = categories.stream()
//                .filter(c -> c.getId().equals(categoryId))
//                .findFirst();
//
//        if (optionalCategory.isPresent()){
//            Category existingCategory = optionalCategory.get();
//            existingCategory.setCategoryName(category.getCategoryName());
//
//            Category savedCategory = categoryRepository.save(existingCategory);
//
//            return savedCategory;
//        }else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found!");
//        }

    }
}
