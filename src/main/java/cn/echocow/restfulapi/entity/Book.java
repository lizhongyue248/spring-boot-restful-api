package cn.echocow.restfulapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import javax.persistence.*;
import javax.validation.constraints.NotNull;


/**
 * 书籍的实体类
 *
 * @author Echo
 * @version 1.0
 * @date 2019-01-05 21:36
 */
@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, length = 20, nullable = false)
    public Long id;
    @NotNull
    @Column(columnDefinition = "varchar(50) comment '书名'")
    public String name;
    @NotNull
    @Column(columnDefinition = "varchar(25) comment '作者'")
    public String author;
    @Column(columnDefinition = "varchar(255) comment '描述'")
    public String description;
    @NotNull
    @ColumnDefault("1")
    @JsonIgnore
    @Column(columnDefinition = "tinyint(1) comment '是否存在'")
    public Boolean status;
}
