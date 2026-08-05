package br.com.financeiro.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "carteira_membro", uniqueConstraints = {
        @UniqueConstraint(name = "uk_carteira_usuario", columnNames = { "carteira_id", "usuario_id" })
})
public class CarteiraMembro extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carteira_id", nullable = false)
    private Carteira carteira;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PapelCarteira papel;

    @Column(name = "entrado_em", nullable = false, updatable = false)
    private LocalDateTime entradoEm;

    @PrePersist
    protected void onEntrar() {
        entradoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Carteira getCarteira() {
        return carteira;
    }

    public void setCarteira(Carteira carteira) {
        this.carteira = carteira;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public PapelCarteira getPapel() {
        return papel;
    }

    public void setPapel(PapelCarteira papel) {
        this.papel = papel;
    }

    public LocalDateTime getEntradoEm() {
        return entradoEm;
    }
}
