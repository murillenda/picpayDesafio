package com.picpaydesafio.entity.user;

import com.picpaydesafio.dto.UserDTO;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@Entity(name = "users")
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String document;

    @Column(unique = true)
    private String email;

    private String password;

    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    public User(UserDTO userDTO) {
        this.firstName = userDTO.firstName();
        this.lastName = userDTO.lastName();
        this.document = userDTO.document();
        this.balance = userDTO.balance();
        this.userType = userDTO.userType();
        this.password = userDTO.password();
        this.email = userDTO.email();
    }
}
