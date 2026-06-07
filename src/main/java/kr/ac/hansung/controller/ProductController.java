package kr.ac.hansung.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.dto.ProductDto;
import kr.ac.hansung.entity.Product;
import kr.ac.hansung.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 목록 — 페이징 & 키워드 검색.
     * page, size 파라미터로 PageRequest 를 만들고 id 오름차순으로 정렬한다.
     * keyword 가 있으면 검색, 없으면 전체 목록을 조회한다.
     */
    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id"));

        // 빈 문자열("")을 null 로 정규화 → Thymeleaf URL 에 keyword 파라미터 미포함
        String normalizedKeyword = (keyword != null && !keyword.isBlank()) ? keyword : null;

        Page<Product> productPage;
        if (normalizedKeyword != null) {
            productPage = productService.searchProducts(normalizedKeyword, pageRequest);
        } else {
            productPage = productService.getProducts(pageRequest);
        }

        model.addAttribute("productPage", productPage);
        model.addAttribute("keyword", normalizedKeyword);
        return "products/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "products/detail";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("product", new ProductDto());
        return "products/add";
    }

    @PostMapping
    public String save(@ModelAttribute ProductDto dto) {
        productService.save(dto);
        return "redirect:/products";
    }

    /**
     * 상품 수정 폼 (ROLE_ADMIN). 기존 데이터를 DTO 에 담아 폼에 pre-fill 한다.
     */
    @GetMapping("/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        ProductDto dto = new ProductDto();
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setDescription(product.getDescription());
        model.addAttribute("productDto", dto);
        model.addAttribute("productId", id);
        return "products/edit";
    }

    /**
     * 상품 수정 처리 (ROLE_ADMIN). @Valid 검증 통과 시 더티 체킹으로 UPDATE 후 목록으로 리다이렉트.
     */
    @PostMapping("/{id}/edit")
    public String editProduct(@PathVariable Long id,
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("productId", id);
            return "products/edit";
        }
        productService.updateProduct(id, productDto);
        ra.addFlashAttribute("successMessage", "상품이 수정되었습니다.");
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/products";
    }
}
