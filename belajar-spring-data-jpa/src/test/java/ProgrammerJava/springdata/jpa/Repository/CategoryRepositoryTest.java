package ProgrammerJava.springdata.jpa.Repository;

import ProgrammerJava.springdata.jpa.Entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testInsert(){
        Category category = new Category();
        category.setName("GADGET");

        categoryRepository.save(category);

        assertNotNull(category.getId());
    }

    @Test
    void testUpdate(){
        Category category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);

        category.setName("GADGET MURAH");
        categoryRepository.save(category);

        //categoryRepository.delete(category);

        category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);
        assertEquals("GADGET MURAH", category.getName());
    }

    @Test
    void testQueryMethod(){
        Category category = categoryRepository.findFirstByNameEquals("GADGET MURAH").orElse(null);
        assertNotNull(category);
        assertEquals("GADGET MURAH", category.getName());

        List<Category> categories = categoryRepository.findAllByNameLike("%GADGET%");
        assertNotNull(category);
        assertEquals(1, categories.size());
        assertEquals("GADGET MURAH", category.getName());
    }

    @Test
    void example1(){
        Category category = new Category();
        category.setName("GADGET MURAH");

        Example<Category> example = Example.of(category);

        List<Category> categories = categoryRepository.findAll(example);
        assertEquals(1, categories.size());
    }

    @Test
    void example2(){
        Category category = new Category();
        category.setName("GADGET MURAH");
        category.setId(1L);

        Example<Category> example = Example.of(category);

        List<Category> categories = categoryRepository.findAll(example);
        assertEquals(1, categories.size());
    }

    @Test
    void exampleMatcher(){
        Category category1 = new Category();
        category1.setName("gadget MURAH");

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues()
                .withIgnoreCase();

        Example<Category> example1 = Example.of(category1, matcher);

        List<Category> categories = categoryRepository.findAll(example1);
        assertEquals(1, categories.size());
    }
}