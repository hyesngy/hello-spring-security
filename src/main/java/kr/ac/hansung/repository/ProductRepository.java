package kr.ac.hansung.repository;

import kr.ac.hansung.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    long countByStockEquals(int stock);

    /**
     * JPQL LIKE 검색 + 페이징.
     * Pageable 파라미터를 받으면 Spring Data JPA가 자동으로 LIMIT / OFFSET 을 적용하고,
     * 반환 타입이 Page<T> 이면 전체 건수(count) 쿼리도 함께 실행해
     * totalElements / totalPages 를 채워준다.
     * %:keyword% 형태로 부분 일치(Containing) 검색을 수행한다.
     */
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword%")
    Page<Product> findByNameContaining(@Param("keyword") String keyword, Pageable pageable);
}
