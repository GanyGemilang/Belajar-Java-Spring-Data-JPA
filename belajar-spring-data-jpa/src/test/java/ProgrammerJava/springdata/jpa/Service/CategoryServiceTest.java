package ProgrammerJava.springdata.jpa.Service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    void success(){
        assertThrows(RuntimeException.class, () ->{
            categoryService.create();
        });
    }

    @Test
    void Failed(){
        assertThrows(RuntimeException.class, () ->{
            categoryService.test();
        });
    }

    @Test
    void programetic(){
        assertThrows(RuntimeException.class, () ->{
            categoryService.createCategories();
        });
    }

    @Test
    void manual(){
        assertThrows(RuntimeException.class, () ->{
            categoryService.manual();
        });
    }
}