package br.com.javaparaweb.financeiro.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import br.com.javaparaweb.financeiro.conta.Conta;
import br.com.javaparaweb.financeiro.conta.ContaRN;
import br.com.javaparaweb.financeiro.usuario.Usuario;
import br.com.javaparaweb.financeiro.usuario.UsuarioRN;

/*
 * Esta Classe será do escopo session,ou seja, haverá uma instância dela para toda a permanência do usuario no sistema,desde o momento 
 * em que ele fez o login até o momento em que ele fizer um logout.
 */

/*
 * Local onde passam ser armazenadas informações sobre o contexto de execução do sistema para o usuario,a Classe ContextoBean será responsavel
 * por forncecer um objeto completo do usuario logado (quando necessario) e armazenar o fornecer o objeto da conta ativa do usuario.
 */

@ManagedBean
@SessionScoped
public class ContextoBean implements Serializable {
	
	/*
	 * Como a Classe Bean é do tipo; @SessionScoped será armazenada dentro da pagina exibida, para que a mesma instância seja utilizada 
	 * em uma requisição posterior.
	 */

	private static final long serialVersionUID = -2071855184464371947L;
	
	private int codigoContaAtiva = 0;
	private List<Locale> idiomas;//1*

	public Usuario getUsuarioLogado() {
		
		//Obtém o login do usuario remoto e executa com a carga desse usuario usando o método da calsse UsuarioRn
		
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext external = context.getExternalContext();
		String login = external.getRemoteUser();
		if (login != null) {
			UsuarioRN usuarioRN = new UsuarioRN();
			Usuario usuario = usuarioRN.buscarPorLogin(login);
			String[] info = usuario.getIdioma().split("_");
			Locale locale = new Locale(info[0], info[1]);
			context.getViewRoot().setLocale(locale); //2*
			return usuario;
		}
		return null;
	}
	
	public List<Locale> getIdiomas() { //3*
		FacesContext context = FacesContext.getCurrentInstance();
		Iterator<Locale> locales = context.getApplication().getSupportedLocales();
		this.idiomas = new ArrayList<Locale>();
		while (locales.hasNext()) {
			this.idiomas.add(locales.next());
		}
		return this.idiomas;
	}
	
	public void setIdiomaUsuario(String idioma) { //4*
		Usuario usuario = this.getUsuarioLogado();
		usuario.setIdioma(idioma);
		UsuarioRN usuarioRN = new UsuarioRN();
		usuarioRN.salvar(usuario);

		String[] info = idioma.split("_");
		Locale locale = new Locale(info[0], info[1]);

		FacesContext context = FacesContext.getCurrentInstance();
		context.getViewRoot().setLocale(locale);
	}

	public Conta getContaAtiva() { //fornece a conta ativa no momento, se não houver uma ativa,ela obtém a conta favorita, ou primeira cadastrada.
		Conta contaAtiva = null;
		if (this.codigoContaAtiva == 0) {
			contaAtiva = this.getContaAtivaPadrao(); 
		} else {
			ContaRN contaRN = new ContaRN();
			contaAtiva = contaRN.carregar(this.codigoContaAtiva);
		}
		if (contaAtiva != null) {
			this.codigoContaAtiva = contaAtiva.getConta();
			return contaAtiva;
		}
		return null;
	}

	private Conta getContaAtivaPadrao() { // resolve a logica de determinar qual será a conta padrao,tendo uma conta favorita ou não.
		ContaRN contaRN = new ContaRN();
		Conta contaAtiva = null;
		Usuario usuario = this.getUsuarioLogado();
		contaAtiva = contaRN.buscarFavorita(usuario);
		if (contaAtiva == null) {
			List<Conta> contas = contaRN.listar(usuario);
			if (contas != null && contas.size() > 0) {
				contaAtiva = contas.get(0);
			}
		}
		return contaAtiva;
	}

	public void changeContaAtiva(ValueChangeEvent event) {
		this.codigoContaAtiva = (Integer) event.getNewValue();
	}
}

/*
 *  1 - É Definida idiomas que armazenará a lista de idiomas disponiveis.
 *  
 *  2 - Ao requisitar um usuario logado, as telas que estiverem praparadas vão respeitar o idioma preferencial do usuario
 *  
 *  3 - Método responsavel por listar todos os idiomas suportados na aplicação, é a base nesse método são montadas as 
 *  bandeiras dos paises/idiomas mostradas na tela de cheques.
 *  
 *  4 - Temos o método utilizado para trocar idioma em tela e atualizar os dados do usuario para o novo idioma selecionado
 *  essa troca acontece nas linhas a seguir:
 *  
 *  FacesContext context = FacesContext.getCurrentInstance();
 *	context.getViewRoot().setLocale(locale);
 * 
 * Para traduzir o sistema todo em alemão PG 425.
 */ 
