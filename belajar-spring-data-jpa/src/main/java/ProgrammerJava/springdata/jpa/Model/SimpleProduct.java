package ProgrammerJava.springdata.jpa.Model;

//Menggunakan interface
//public interface SimpleProduct {
//
//    Long getId();
//
//    String getName();
//}

//Menggunakan Java Record
public record SimpleProduct(Long id, String name) {
}