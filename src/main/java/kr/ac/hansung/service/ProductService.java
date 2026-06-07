package kr.ac.hansung.service;

import kr.ac.hansung.dto.ProductDto;
import kr.ac.hansung.entity.Product;
import kr.ac.hansung.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * 전체 상품 목록 페이징 조회.
     * Pageable 을 findAll 에 그대로 넘기면 Spring Data JPA가 페이지 단위로 잘라서 Page<Product> 로 반환한다.
     */
    @Transactional(readOnly = true)
    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /**
     * 키워드 검색 + 페이징 조회. @Query(JPQL LIKE) + Pageable 조합.
     */
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContaining(keyword, pageable);
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다: " + id));
    }

    @Transactional
    public Product save(ProductDto dto) {
        Product product = new Product(
            dto.getName(), dto.getPrice(), dto.getDescription(), dto.getStock()
        );
        return productRepository.save(product);
    }

    /**
     * 상품 수정 — 더티 체킹(Dirty Checking).
     * findById 로 조회한 엔티티는 영속 상태(Managed)이므로,
     * 트랜잭션이 끝나는 시점에 Hibernate가 변경된 필드를 감지해 자동으로 UPDATE 쿼리를 발행한다.
     * 따라서 save() 를 명시적으로 호출하지 않아도 변경 내용이 DB에 반영된다.
     */
    @Transactional
    public Product updateProduct(Long id, ProductDto dto) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + id));

        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        return product;  // save() 불필요 — 더티 체킹으로 트랜잭션 커밋 시 자동 UPDATE
    }

    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
