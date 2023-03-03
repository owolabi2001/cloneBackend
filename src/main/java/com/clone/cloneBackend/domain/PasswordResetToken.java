package com.clone.cloneBackend.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetToken {

    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue
    private Long id;

    private String token;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "app_user_id",referencedColumnName = "id")
    private AppUser appUser;

    private String date;

    public PasswordResetToken(String token, AppUser user,String date) {
    }
}
