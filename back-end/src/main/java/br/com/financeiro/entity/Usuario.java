package br.com.financeiro.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "usuario")
public class Usuario extends Base implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "senha_criptografada", nullable = false)
    private String senhaCriptografada;

    @ToString.Exclude
    @OneToMany(mappedBy = "dono")
    private List<Carteira> carteiras = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "usuario")
    private List<CarteiraMembro> carteiraMembros = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "usuario")
    private List<Categoria> categorias = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return senhaCriptografada;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
