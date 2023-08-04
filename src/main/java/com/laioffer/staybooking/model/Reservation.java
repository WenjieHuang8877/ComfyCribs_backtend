package com.laioffer.staybooking.model;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "reservation")
@JsonDeserialize(builder = Reservation.Builder.class)
//Jackson 库的一个注解，用于指定自定义的反序列化类或方法。
// 它告诉 Jackson 应使用哪个类或方法来将 JSON 数据转换为 Java 对象。
//说明 Jackson 在反序列化 JSON 数据为 Reservation 类的实例时，应该使用 Reservation.Builder 类的实例来作为构建器（Builder）
//这是 Builder 模式的一部分，可以提供更清晰、更灵活的对象构造方式。
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    // 主键ID，自动增长
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // 入住日期
    @JsonProperty("checkin_date")
    private LocalDate checkinDate;

    // 退房日期
    @JsonProperty("checkout_date")
    private LocalDate checkoutDate;

    // 预订者，这是一个多对一的关系，多个预订可以属于一个用户
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User guest;

    // 预订的住宿，这是一个多对一的关系，多个预订可以对应一个住宿
    @ManyToOne
    @JoinColumn(name = "stay_id")
    private Stay stay;

    // 默认构造函数
    public Reservation() {}

    // 建造者模式的构造函数，用于创建新的Reservation对象
    private Reservation(Builder builder) {
        this.id = builder.id;
        this.checkinDate = builder.checkinDate;
        this.checkoutDate = builder.checkoutDate;
        this.guest = builder.guest;
        this.stay = builder.stay;
    }

    // Getter方法用于获取属性值
    public Long getId() {
        return id;
    }

    public LocalDate getCheckinDate() {
        return checkinDate;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public User getGuest() {
        return guest;
    }

    // Setter方法用于设置属性值
    public Reservation setGuest(User guest) {
        this.guest = guest;
        return this;
    }

    public Stay getStay() {
        return stay;
    }

    // Builder类用于构建Reservation对象
    public static class Builder {
        // 定义与Reservation类相同的属性
        @JsonProperty("id")
        private Long id;

        @JsonProperty("checkin_date")
        private LocalDate checkinDate;

        @JsonProperty("checkout_date")
        private LocalDate checkoutDate;

        @JsonProperty("guest")
        private User guest;

        @JsonProperty("stay")
        private Stay stay;

        // Setter方法用于设置Builder类的属性
        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setCheckinDate(LocalDate checkinDate) {
            this.checkinDate = checkinDate;
            return this;
        }

        public Builder setCheckoutDate(LocalDate checkoutDate) {
            this.checkoutDate = checkoutDate;
            return this;
        }

        public Builder setGuest(User guest) {
            this.guest = guest;
            return this;
        }

        public Builder setStay(Stay stay) {
            this.stay = stay;
            return this;
        }

        // 使用当前Builder类的属性创建新的Reservation对象
        public Reservation build() {
            return new Reservation(this);
        }
    }
}

