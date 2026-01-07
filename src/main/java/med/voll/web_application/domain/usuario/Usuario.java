package med.voll.web_application.domain.usuario;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Table(name="usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String nome;

    private String email;

    private String senha;

    private String token;
@Column(name = "expiracao_token")
    private LocalDateTime expiracaoToken;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



    @Enumerated(EnumType.STRING)
    private Perfil perfil;

    public Usuario(){}

    public Usuario(String nome, String email, String senha,Perfil perfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getExpiracaoToken() {
        return expiracaoToken;
    }

    public void setExpericaoToken(LocalDateTime expericaoToken) {
        this.expiracaoToken = expiracaoToken;
    }

    public void alteraSenha(String senhaCriptografa) {
        this.alteraSenha(senhaCriptografa);
    }
}
