package com.test.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Data
public class User {
    private final String name;
    private final String sex;
    private final Integer age;
    private Integer ridesOffered;
    private Integer ridesTaken;

    public User(String name, String sex, Integer age) {
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.ridesOffered = 0;
        this.ridesTaken =0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(sex, user.sex) && Objects.equals(age, user.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, sex, age);
    }
}
