package ProgrammerJava.springdata.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EntityManagerTest {

    @Autowired
    private EntityManagerFactory managerFactory;

    @Test
    void testEntityManagerFactory(){

        Assertions.assertNotNull(managerFactory);

        EntityManager entityManagerFactory = managerFactory.createEntityManager();
        Assertions.assertNotNull(entityManagerFactory);

        managerFactory.close();

    }
}
