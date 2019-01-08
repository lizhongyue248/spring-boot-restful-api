package cn.echocow.restfulapi.repository;

import cn.echocow.restfulapi.entity.Book;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.transaction.Transactional;

/**
 * Book 资源
 *
 * @author Echo
 * @version 1.0
 * @date 2019-01-05 23:51
 */
@RepositoryRestResource(path = "/books", collectionResourceRel = "books")
public interface BookRestRepository extends PagingAndSortingRepository<Book, Long> {

    /**
     * 通过作者名称查询
     *
     * @param author 作者
     * @return 书单
     */
    @RestResource(path = "authors", rel = "authors")
    Book findBookByAuthor(@Param("author") String author);


    /**
     * 自己写的删除方法
     *
     * @param aLong 删除的id
     */
    @Modifying
    @Query("UPDATE Book SET status = false WHERE id = :id")
    @RestResource(exported = false)
    void delete(@Param("id") Long aLong);



    /**
     * 重写删除方法
     *
     * @param entity 删除的实体
     */
    @Override
    default void delete(Book entity){
        delete(entity.getId());
    }

//    /**
//     * 隐藏删除方法
//     *
//     * @param entity 删除的实体
//     */
//    @Override
//    @RestResource(exported = false)
//    void delete(Book entity);



}
