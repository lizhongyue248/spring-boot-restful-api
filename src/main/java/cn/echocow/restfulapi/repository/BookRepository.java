package cn.echocow.restfulapi.repository;

import cn.echocow.restfulapi.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Book 资源
 *
 * @author Echo
 * @version 1.0
 * @date 2019-01-05 21:56
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}
