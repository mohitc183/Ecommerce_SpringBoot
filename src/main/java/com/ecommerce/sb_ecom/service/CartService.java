package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.payload.CartDTO;

public interface CartService {

    public CartDTO addProductToCart(Long productId, Integer quantity);
}
