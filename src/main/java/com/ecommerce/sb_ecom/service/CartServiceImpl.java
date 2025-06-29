package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.exceptions.APIException;
import com.ecommerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecommerce.sb_ecom.model.Cart;
import com.ecommerce.sb_ecom.model.CartItem;
import com.ecommerce.sb_ecom.model.Product;
import com.ecommerce.sb_ecom.payload.CartDTO;
import com.ecommerce.sb_ecom.payload.ProductDTO;
import com.ecommerce.sb_ecom.repository.CartItemRepository;
import com.ecommerce.sb_ecom.repository.CartRepository;
import com.ecommerce.sb_ecom.repository.ProductRepository;
import com.ecommerce.sb_ecom.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    ProductRepository productRepository;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        //Find existing cart or create one
        Cart cart = createCart();


        //Retrieve Product Details
        Product product = productRepository.findById(productId)
                .orElseThrow( () -> new ResourceNotFoundException("Product","productId", productId));


        //Perform Validations
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(),productId);

        if(cartItem != null){
            throw new APIException("Product " + product.getProductName() + " already exists in the cart!!");
        }

        if(product.getQuantity() == 0){
            throw new APIException(product.getProductName() + " is not available!!");
        }

        if(product.getQuantity() < quantity){
            throw new APIException("Please make an order for " + product.getProductName() + " less than or equal to " +
                    "quantity " + product.getQuantity() + "." );
        }


        //Create cart item
        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());


        //Save cart item
        cartItemRepository.save(newCartItem);

//        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));


        //Save cart
        cartRepository.save(cart);


        //Return updated cart information
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();
        System.out.println("Cart items :-\n" + cart.getCartItems());

        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            System.out.println("item :- " +  item);
            return map;
        });

        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }

    private Cart createCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart != null){
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart = cartRepository.save(cart);

        return newCart;

    }

    @Override
    public List<CartDTO> getAllCarts() {

        List<Cart> carts = cartRepository.findAll();

        if(carts.size() == 0 ){
            throw new APIException("No carts exists!!");
        }

        List<CartDTO> cartDTOS = carts.stream()
                .map(cart ->{
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                    List<ProductDTO> products = cart.getCartItems().stream()
                            .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                            .collect(Collectors.toList());
                    cartDTO.setProducts(products);

                    return cartDTO;
                }).collect(Collectors.toList());

        return cartDTOS;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {

        Cart cart = cartRepository.findCartByEmailAndCartID(emailId, cartId);

        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        //setting product's quantity present in cart!
        cart.getCartItems().forEach(c -> c.getProduct().setQuantity(c.getQuantity()));

        List<ProductDTO> products = cart.getCartItems().stream()
                .map( p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                .collect(Collectors.toList());

        cartDTO.setProducts(products);

        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow( () -> new ResourceNotFoundException("Cart", "cartId", cartId));

        //Retrieve Product Details
        Product product = productRepository.findById(productId)
                .orElseThrow( () -> new ResourceNotFoundException("Product","productId", productId));


        if(product.getQuantity() == 0){
            throw new APIException(product.getProductName() + " is not available!!");
        }

        if(product.getQuantity() < quantity){
            throw new APIException("Please make an order for " + product.getProductName() + " less than or equal to " + "quantity " + product.getQuantity() + "." );
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if(cartItem ==null){
            throw new APIException("Product " + product.getProductName() + " not availible in the cart!");
        }

        //updating cart item
        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItem.setDiscount(product.getDiscount());
        cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));

        cartRepository.save(cart);

        CartItem updatedItem  = cartItemRepository.save(cartItem);

        if(updatedItem.getQuantity() == 0){
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {
            ProductDTO prd = modelMapper.map(item.getProduct(), ProductDTO.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        });
        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }

    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {

        //basic validations
        Cart cart=cartRepository.findById(cartId)
                .orElseThrow( () -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null){
            throw new ResourceNotFoundException("Product","productId", productId);
        }

        //update cart total price
        cart.setTotalPrice(cart.getTotalPrice() -
                (cartItem.getProductPrice() * cartItem.getQuantity()));

        //delete item from cart
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart!";
    }
}