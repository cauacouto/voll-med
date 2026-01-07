package med.voll.web_application.domain.usuario;

import med.voll.web_application.domain.RegraDeNegocioException;
import med.voll.web_application.domain.usuario.email.EmailService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encripitador;
    private final EmailService emailService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder encripitador, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.encripitador = encripitador;
        this.emailService = emailService;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("O usuário não foi encontrado!"));
    }

    public Long salvarUsuario(String nome,String email,Perfil perfil){
        String primeiraSenha = UUID.randomUUID().toString().substring(0,8);
        System.out.println("senha gerada" + primeiraSenha);
     String senhaCriptografada = encripitador.encode(primeiraSenha);
       var  usuario = usuarioRepository.save(new Usuario(nome,email,senhaCriptografada,perfil));
       return usuario.getId();
    }

    public void excluir(Long id) {
        usuarioRepository.deleteById(id);
    }

    public void alterarSenha(DadosAlteracaoSenha dados,Usuario logado){
        if (!encripitador.matches(dados.senhaAtual(), logado.getPassword() )) {
            throw new RegraDeNegocioException("senha não confere com a senha atual");
        }
        if (!dados.novaSenha().equals(dados.novaSenhaConfirmacao())){
            throw new RegraDeNegocioException("senha de confirmação no conferem !");
        }
        String senhaCriptografa = encripitador.encode(dados.novaSenha());
        logado.alteraSenha(senhaCriptografa);

        usuarioRepository.save(logado);
    }

    public void enviarToken(String email){
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email).orElseThrow(
                ()-> new RegraDeNegocioException("usuario não encontrado")
        );
        String token = UUID.randomUUID().toString();
        usuario.setToken(token);
        usuario.setExpericaoToken(LocalDateTime.now().plusMinutes(15));
        usuarioRepository.save(usuario);
        emailService.enviarEmailSenha(usuario);
    }

    public void recuperarConta(String codigo, DadosRecuperacaoConta dados) {
        Usuario usuario = usuarioRepository.findByTokenIgnoreCase(codigo)
                .orElseThrow(
                        ()-> new RegraDeNegocioException("link invalido")
                );
        if (usuario.getExpiracaoToken().isBefore(LocalDateTime.now())){
            throw new RegraDeNegocioException("link expirado!");
        }
        if (!dados.novaSenha().equals(dados.novaSenhaConfirmacao())){
            throw new RegraDeNegocioException("senha de confirmação no conferem !");
        }

        String senhaCriptografa = encripitador.encode(dados.novaSenha());
        usuario.alteraSenha(senhaCriptografa);

        usuario.setToken(null);
        usuario.setExpericaoToken(null);
        usuarioRepository.save(usuario);
    }
}

