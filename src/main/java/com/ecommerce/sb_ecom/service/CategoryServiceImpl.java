package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.exceptions.APIException;
import com.ecommerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.payload.CategoryDTO;
import com.ecommerce.sb_ecom.payload.CategoryResponse;
import com.ecommerce.sb_ecom.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize,String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();


//        Pageable pageDetails = PageRequest.of(pageNumber, pageSize);
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> categories = categoryPage.getContent();
//        List<Category> categories = categoryRepository.findAll();

        if(categories.isEmpty())
            throw new APIException("There are currently no categories present in the Database!!");

        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

//        CategoryResponse categoryResponse = new CategoryResponse(categoryDTOS);
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());


        return categoryResponse;
    }



    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {

        Category categoryFromDB = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        Category category = modelMapper.map(categoryDTO, Category.class);

        if(categoryFromDB != null){
            throw new APIException("Category with " + categoryDTO.getCategoryName() + " already exists!!!");
        }

        Category savedCategory = categoryRepository.save(category);

        return modelMapper.map(savedCategory, CategoryDTO.class);

    }



    @Override
    public CategoryDTO deleteCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow( () -> new ResourceNotFoundException("Category", "categoryId", String.valueOf(categoryId)) );

        CategoryDTO deletedCategory = modelMapper.map(category, CategoryDTO.class);
        categoryRepository.delete(category);

        return deletedCategory;
    }



    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {

//      Throwing custom exceptions and handling them using our custom exception handler
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow( () -> new ResourceNotFoundException("Category", "categoryId", String.valueOf(categoryId)) );

        Category category = modelMapper.map(categoryDTO, Category.class);

        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);

        return modelMapper.map(savedCategory, CategoryDTO.class);
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
