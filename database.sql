CREATE DATABASE belajar_spring_data_jpa;

use belajar_spring_data_jpa;


Create TABLE categories(
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)

)engine = InnoDB

select * from categories;

delete from categories where name = 'Sample Audit';

create table products (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    price BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    primary key(id),
    foreign key fk_products_categories (category_id) REFERENCES categories (id)
);

select * from products;

alter table categories add column created_date Timestamp;

alter table categories add column last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
