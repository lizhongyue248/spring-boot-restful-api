package cn.echocow.restfulapi.controller;

import cn.echocow.restfulapi.entity.Book;
import cn.echocow.restfulapi.exception.InvalidRequestException;
import cn.echocow.restfulapi.exception.ResourceNoFoundException;
import cn.echocow.restfulapi.repository.BookRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * rest 风格 api
 * <p>
 * GET     /api/v1/books        所有书单
 * GET     /api/v1/books/{id}   获取一条书单
 * POST    /api/v1/books        新建一条书单
 * PUT     /api/v1/books/{id}   更新一条书单，提供全部信息
 * PATCH   /api/v1/books/{id}   更新一条书单，提供部分信息
 * DELETE  /api/v1/books/{id}   删除一条书单
 * DELETE  /API/v1/books        删除所有书单
 *
 * @author Echo
 * @version 1.0
 * @date 2019-01-05 21:59
 */
@RestController
@RequestMapping("/v1")
public class BookController {
    private final BookRepository bookRepository;

    @Autowired
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * 获取所有书单
     * GET     /api/v1/books        所有书单
     *
     * @return http 响应
     */
    @GetMapping("/books")
    public HttpEntity<?> books() {
        List<Book> books = bookRepository.findAll();
        if (books == null || books.isEmpty()) {
            throw new ResourceNoFoundException("Not Found books!");
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }


    /**
     * 获取一个书单
     * GET     /api/v1/books/{id}   获取一条书单
     *
     * @param id id
     * @return http 响应
     */
    @GetMapping("/books/{id}")
    public HttpEntity<?> booksOne(@PathVariable Long id) {
        return new ResponseEntity<>(bookRepository.findById(id).orElseThrow(() ->
                new ResourceNoFoundException(String.format("Book by id %s not found!", id))),
                HttpStatus.OK);
    }

    /**
     * 添加一个书单
     * POST    /api/v1/books        新建一条书单
     *
     * @param book 书单
     * @return http 响应
     */
    @PostMapping("/books")
    public HttpEntity<?> booksAdd(@Valid @RequestBody Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException("Invalid parameter", bindingResult);
        }
        book.setId(null);
        return new ResponseEntity<>(bookRepository.save(book), HttpStatus.CREATED);
    }

    /**
     * 更新一个书单,提供一个书单的全部信息
     * PUT     /api/v1/books/{id}   更新一条书单，提供全部信息
     *
     * @param id   更新的id
     * @param book 更新后的书单
     * @return http 响应
     */
    @PutMapping("/books/{id}")
    public HttpEntity<?> booksPut(@PathVariable Long id, @Valid @RequestBody Book book, BindingResult bindingResult) {
        Book exist = bookRepository.findById(id).orElseThrow(() ->
                new ResourceNoFoundException(String.format("Book by id %s not found!", id)));
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException("Invalid parameter", bindingResult);
        }
        book.setId(exist.getId());
        return new ResponseEntity<>(bookRepository.save(book), HttpStatus.OK);
    }

    /**
     * 更新一个书单,提供一个书单的部分信息
     * PATCH   /api/v1/books/{id}   更新一条书单，提供部分信息
     *
     * @param id   更新的id
     * @param book 更新后的书单
     * @return http 响应
     */
    @PatchMapping("/books/{id}")
    public HttpEntity<?> booksPatch(@PathVariable Long id, @RequestBody Book book) {
        Book exist = bookRepository.findById(id).orElseThrow(() ->
                new ResourceNoFoundException(String.format("Book by id %s not found!", id)));
        BeanWrapper beanWrapper = new BeanWrapperImpl(book);
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
        List<String> nullPropertyNames = new ArrayList<>();
        for (PropertyDescriptor pd :
                propertyDescriptors) {
            if (beanWrapper.getPropertyValue(pd.getName()) == null) {
                nullPropertyNames.add(pd.getName());
            }
        }
        BeanUtils.copyProperties(book, exist, nullPropertyNames.toArray(new String[nullPropertyNames.size()]));
        return new ResponseEntity<>(bookRepository.save(exist), HttpStatus.OK);
    }

    /**
     * 删除一个书单
     * DELETE  /api/v1/books/{id}   删除一条书单
     *
     * @param id id
     * @return http 响应
     */
    @DeleteMapping("/books/{id}")
    public HttpEntity<?> booksDeleteOne(@PathVariable Long id) {
        Book exist = bookRepository.findById(id).orElseThrow(() ->
                new ResourceNoFoundException(String.format("Book by id %s not found!", id)));
        bookRepository.deleteById(exist.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 删除所有书单
     * DELETE  /API/v1/books        删除所有书单
     *
     * @return http 响应
     */
    @DeleteMapping("/books")
    public HttpEntity<?> booksDeleteAll() {
        List<Book> books = bookRepository.findAll();
        if (books == null || books.isEmpty()) {
            throw new ResourceNoFoundException("Not found books!");
        }
        bookRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
