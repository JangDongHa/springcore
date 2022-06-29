package com.sparta.springcore.service;

import com.sparta.springcore.dto.ProductMypriceRequestDto;
import com.sparta.springcore.dto.ProductRequestDto;
import com.sparta.springcore.model.Product;
import com.sparta.springcore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // Bean 등록
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

//    @Autowired
//    public ProductService(ApplicationContext context) {
//        // 1.'빈' 이름으로 가져오기
//        ProductRepository productRepository = (ProductRepository) context.getBean("productRepository");
//        // 2.'빈' 클래스 형식으로 가져오기
//        // ProductRepository productRepository = context.getBean(ProductRepository.class);
//        this.productRepository = productRepository;
//    }

    // 신규 상품 등록
    public Product createService (ProductRequestDto requestDto) {
        Product product = new Product(requestDto);
        return productRepository.save(product);
    }

    // 상품 수정
    public Product updateService (Long id, ProductMypriceRequestDto requestDto) {
        Product product = productRepository.findById(id).orElseThrow(()-> new NullPointerException("해당 아이디가 존재하지 않습니다."));

        // 수정사항 설정
        product.setMyprice(requestDto.getMyprice());

        if (product.getMyprice() <= 0)
            throw new RuntimeException("myPrice must be higher than 0");

        // 수정사항 적용
        productRepository.save(product);

        return product;
    }

    // 상품 리스트 조회
    public List<Product> listAllService () {
        return productRepository.findAll();
    }


}
