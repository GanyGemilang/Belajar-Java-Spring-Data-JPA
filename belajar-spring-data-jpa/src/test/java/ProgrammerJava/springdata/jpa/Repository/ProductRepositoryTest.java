package ProgrammerJava.springdata.jpa.Repository;

import ProgrammerJava.springdata.jpa.Entity.Category;
import ProgrammerJava.springdata.jpa.Entity.Product;
import ProgrammerJava.springdata.jpa.Model.ProductPrice;
import ProgrammerJava.springdata.jpa.Model.SimpleProduct;
import com.jayway.jsonpath.Criteria;
import jakarta.persistence.criteria.CriteriaQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.transaction.support.TransactionOperations;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionOperations transactionOperations;

    @Test
    void createProduct(){
        Category category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);

        {
            Product product = new Product();
            product.setName("Apple iPhone 14 Pro Max");
            product.setPrice(25_000_000L);
            product.setCategory(category);
            productRepository.save(product);
        }
        {
            Product product = new Product();
            product.setName("Apple iPhone 13 Pro Max");
            product.setPrice(18_000_000L);
            product.setCategory(category);
            productRepository.save(product);
        }
    }

    @Test
    void findByCategoryName(){
        List<Product> products = productRepository.findAllByCategory_Name("Gadget Murah");

        assertEquals(2, products.size());
        assertEquals("Apple iPhone 14 Pro Max", products.get(0).getName());
        assertEquals("Apple iPhone 13 Pro Max", products.get(1).getName());

    }

    @Test
    void findProductsSort(){
        Sort sort = Sort.by(Sort.Order.desc("id"));
        List<Product> products = productRepository.findAllByCategory_Name("Gadget Murah", sort);

        assertEquals(2, products.size());
        assertEquals("Apple iPhone 13 Pro Max", products.get(0).getName());
        assertEquals("Apple iPhone 14 Pro Max", products.get(1).getName());
    }

    @Test
    void testFindProductsWithPageable(){

        Pageable pageable = PageRequest.of(0,1,Sort.by(Sort.Order.desc("id")));
        Page<Product> products = productRepository.findAllByCategory_Name("Gadget Murah", pageable);

        assertEquals(1, products.getContent().size());
        assertEquals(0, products.getNumber());
        assertEquals(2, products.getTotalElements());
        assertEquals(2, products.getTotalPages());
        assertEquals("Apple iPhone 13 Pro Max", products.getContent().get(0).getName());

        pageable = PageRequest.of(1,1,Sort.by(Sort.Order.desc("id")));
        products = productRepository.findAllByCategory_Name("Gadget Murah", pageable);

        assertEquals(1, products.getContent().size());
        assertEquals(1, products.getNumber());
        assertEquals(2, products.getTotalElements());
        assertEquals(2, products.getTotalPages());
        assertEquals("Apple iPhone 14 Pro Max", products.getContent().get(0).getName());
    }

    @Test
    void testCount(){
        Long count = productRepository.count();
        assertEquals(2L, count);

        count = productRepository.countByCategory_Name("GADGET MURAH");
        assertEquals(2L, count);
    }

    @Test
    void testExsist(){
        boolean exists = productRepository.existsByName(("Apple iPhone 14 Pro Max"));
        assertTrue(exists);

        exists = productRepository.existsByName(("Apple iPhone 14 Pro Max 2"));
        assertFalse(exists);
    }

    @Test
    void testDeleteOld(){
        transactionOperations.executeWithoutResult(transactionStatus -> {
            Category category = categoryRepository.findById(1L).orElse(null);
            assertNotNull(category);

            Product product = new Product();
            product.setName("Samsung Galaxy S9");
            product.setPrice(10_000_000L);
            product.setCategory(category);
            productRepository.save(product);

            int delete = productRepository.deleteByname("Samsung Galaxy S9");
            assertEquals(1, delete);

            delete = productRepository.deleteByname("Samsung Galaxy S9");
            assertEquals(0, delete);

        });
    }
    @Test
    void testDeleteNew(){
        Category category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);

        Product product = new Product();
        product.setName("Samsung Galaxy S9");
        product.setPrice(10_000_000L);
        product.setCategory(category);
        productRepository.save(product);

        int delete = productRepository.deleteByname("Samsung Galaxy S9");
        assertEquals(1, delete);

        delete = productRepository.deleteByname("Samsung Galaxy S9");
        assertEquals(0, delete);
    }

    @Test
    void testnameQuery(){
        Pageable pageable = PageRequest.of(0,1);
        List<Product> products = productRepository.searchProductUsingName("Apple iPhone 14 Pro Max", pageable);
        assertEquals(1, products.size());
        assertEquals("Apple iPhone 14 Pro Max", products.get(0).getName());
    }

    @Test
    void searchProductLike(){
        Pageable pageable = PageRequest.of(0,1, Sort.by(Sort.Order.desc("id")));
        Page<Product> products = productRepository.searchProduct("%iPhone%", pageable);
        assertEquals(1, products.getContent().size());
        assertEquals(0, products.getNumber());
        assertEquals(2, products.getTotalPages());
        assertEquals(2, products.getTotalElements());

        products = productRepository.searchProduct("%GADGET%", pageable);
        assertEquals(1, products.getContent().size());
        assertEquals(0, products.getNumber());
        assertEquals(2, products.getTotalPages());
        assertEquals(2, products.getTotalElements());
    }
    
    @Test
    void deleteProductUsingNameTest(){
        transactionOperations.executeWithoutResult(transactionStatus -> {
            int total = productRepository.deleteProductUsingName("Wrong");
            assertEquals(0,total);

            total = productRepository.updateProductPriceToZero(1L);
            assertEquals(1,total);

            Product product = productRepository.findById(1L).orElse(null);
            assertNotNull(product);
            assertEquals(0L, product.getPrice());
        });
    }

    @Test
    void stream(){
        transactionOperations.executeWithoutResult(transactionStatus -> {
            Category category = categoryRepository.findById(1L).orElse(null);
            assertNotNull(category);

            Stream<Product> stream = productRepository.streamAllByCategory(category);
            stream.forEach(product -> System.out.println(product.getId() + " : " + product.getName()));
        });
    }

    @Test
    void slice(){
        Pageable firstPageable = PageRequest.of(0,1);

        Category category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);

        Slice<Product> slice = productRepository.findAllByCategory(category, firstPageable);
        // Tampilkan konten products

        while (slice.hasNext()){
            slice = productRepository.findAllByCategory(category, slice.nextPageable());
        }
    }

    @Test
    void lock1(){
        transactionOperations.executeWithoutResult(transactionStatus -> {
            try{
                Product product = productRepository.findFirstByIdEquals(1L).orElse(null);
                assertNotNull(product);
                product.setPrice(30_000_000L);

                Thread.sleep(20_000L);
                productRepository.save(product);
            } catch (InterruptedException exception){
                throw new RuntimeException(exception);
            }
        });
    }

    @Test
    void lock2(){
        transactionOperations.executeWithoutResult(transactionStatus -> {
            Product product = productRepository.findFirstByIdEquals(1L).orElse(null);
            assertNotNull(product);
            product.setPrice(10_000_000L);
            productRepository.save(product);
        });
    }

    @Test
    void audit(){
        Category category = new Category();
        category.setName("Sample Audit");
        categoryRepository.save(category);

        assertNotNull(category.getId());
        assertNotNull(category.getCreatedDate());
        assertNotNull(category.getLastModifiedDate());
    }

    @Test
    void spesification(){

        Specification<Product> specification = (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaQuery.where(
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("name"), "Apple iPhone 14 Pro Max"),
                            criteriaBuilder.equal(root.get("name"), "Apple iPhone 13 Pro Max")
                    )
            ).getRestriction();
        };

        List<Product> products = productRepository.findAll(specification);
        assertEquals(2, products.size());
    }

    @Test
    void projection(){
        //List<SimpleProduct> simpleProducts = productRepository.findAllByNameLike("%Apple%");
        List<SimpleProduct> simpleProducts = productRepository.findAllByNameLike("%Apple%", SimpleProduct.class);
        assertEquals(2, simpleProducts.size());

        List<ProductPrice> simpleProductPrice = productRepository.findAllByNameLike("%Apple%", ProductPrice.class);
        assertEquals(2, simpleProductPrice.size());
    }
}