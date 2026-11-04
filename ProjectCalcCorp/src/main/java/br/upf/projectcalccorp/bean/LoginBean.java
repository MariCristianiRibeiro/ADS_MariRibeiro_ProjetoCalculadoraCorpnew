package br.upf.projectcalccorp.bean;

import br.upf.projectcalccorp.dao.ColaboradorDAO;
import br.upf.projectcalccorp.model.Colaborador;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("loginBean")
@RequestScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String usuario;
    private String senha;

    @EJB
    private ColaboradorDAO colaboradorDAO;

    public LoginBean() {
    }

    public void autenticar() {
        System.out.println(">>> LOGIN: Tentativa de autenticação com o utilizador: " + usuario);
        
        Colaborador colaborador = colaboradorDAO.autenticar(usuario, senha);

        if (colaborador != null) {
            System.out.println(">>> LOGIN: Sucesso! Utilizador encontrado: " + colaborador.getNome());
            
            try {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                
                // 1. Guarda o colaborador autenticado na sessão HTTP
                facesContext.getExternalContext().getSessionMap().put("colaboradorLogado", colaborador);
                
                // 2. Executa o redirecionamento HTTP explícito para a pasta /admin/agendamento.xhtml
                String contextPath = facesContext.getExternalContext().getRequestContextPath();
                facesContext.getExternalContext().redirect(contextPath + "/admin/agendamento.xhtml");
                
            } catch (Exception e) {
                System.err.println(">>> ERRO ao redirecionar utilizador: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println(">>> LOGIN: Falha na validação para o utilizador: " + usuario);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Usuário ou senha inválidos!"));
        }
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}