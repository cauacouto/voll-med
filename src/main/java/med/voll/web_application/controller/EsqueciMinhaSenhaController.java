package med.voll.web_application.controller;

import med.voll.web_application.domain.RegraDeNegocioException;
import med.voll.web_application.domain.usuario.DadosRecuperacaoConta;
import med.voll.web_application.domain.usuario.UsuarioService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class EsqueciMinhaSenhaController {
    public static final String FORMULARIO_RECUPECAO_SENHA = "autenticacao/formulario-recuperacao-senha";
    public static final String FORMULARIO_RECUPECAO_CONTA = "autenticacao/formulario-recuperacao-conta";
    private final UsuarioService usuarioService;

    public EsqueciMinhaSenhaController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;

    }

    @GetMapping("esqueci-minha-senha")
    public  String carregarPaginaEsqueciMinhaSenha(){
        return  FORMULARIO_RECUPECAO_SENHA;
    }

    @PostMapping("esqueci-minha-senha")
    public String enviarTokenEmail(@ModelAttribute("email") String email, Model model){
        try {
            usuarioService.enviarToken(email);
            return "redirect:esqueci-minha-senha?verificar";
        }catch (RegraDeNegocioException e){
            model.addAttribute("erro", e.getMessage());
            return FORMULARIO_RECUPECAO_SENHA;
        }
    }

    @GetMapping("/recuperar-conta")
    public String carregarPaginaAlterarSenhaEsquecida(@RequestParam(name = "codigo", required = false) String codigo, Model model) {
        if(codigo != null)
            model.addAttribute("codigo", codigo);
        return FORMULARIO_RECUPECAO_CONTA;
    }

    @PostMapping("/recuperar-conta")
    public String carregarPaginaAlterarSenhaEsquecida(@RequestParam(name = "codigo") String codigo, Model model, DadosRecuperacaoConta dados) {
        try {
            usuarioService.recuperarConta(codigo, dados);
            return "redirect:login";
        } catch (RegraDeNegocioException e){
            model.addAttribute("error", e.getMessage());
            return FORMULARIO_RECUPECAO_CONTA;
        }
    }
}
