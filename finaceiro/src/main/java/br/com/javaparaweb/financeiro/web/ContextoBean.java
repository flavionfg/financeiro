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
 * Esta Classe ser� do escopo session,ou seja, haver� uma inst�ncia dela para toda a perman�ncia do usuario no sistema,desde o momento 
 * em que ele fez o login at� o momento em que ele fizer um logout.
 */

/*
 * Local onde passam ser armazenadas informa��es sobre o contexto de execu��o do sistema para o usuario,a Classe ContextoBean ser� responsavel
 * por forncecer um objeto completo do usuario logado (quando necessario) e armazenar o fornecer o objeto da conta ativa do usuario.
 */

@ManagedBean
@SessionScoped
public class ContextoBean implements Serializable {
	
	/*
	 * Como a Classe Bean � do tipo; @SessionScoped ser� armazenada dentro da pagina exibida, para que a mesma inst�ncia seja utilizada 
	 * em uma requisi��o posterior.
	 */

	private static final long serialVersionUID = -2071855184464371947L;
	
	private int codigoContaAtiva = 0;
	private List<Locale> idiomas;//1*

	public Usuario getUsuarioLogado() {
		
		//Obt�m o login do usuario remoto e executa com a carga desse usuario usando o m�todo da calsse UsuarioRn
		
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

	public Conta getContaAtiva() { //fornece a conta ativa no momento, se n�o houver uma ativa,ela obt�m a conta favorita, ou primeira cadastrada.
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

	private Conta getContaAtivaPadrao() { // resolve a logica de determinar qual ser� a conta padrao,tendo uma conta favorita ou n�o.
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
 *  1 - � Definida idiomas que armazenar� a lista de idiomas disponiveis.
 *  
 *  2 - Ao requisitar um usuario logado, as telas que estiverem praparadas v�o respeitar o idioma preferencial do usuario
 *  
 *  3 - M�todo responsavel por listar todos os idiomas suportados na aplica��o, � a base nesse m�todo s�o montadas as 
 *  bandeiras dos paises/idiomas mostradas na tela de cheques.
 *  
 *  4 - Temos o m�todo utilizado para trocar idioma em tela e atualizar os dados do usuario para o novo idioma selecionado
 *  essa troca acontece nas linhas a seguir:
 *  
 *  FacesContext context = FacesContext.getCurrentInstance();
 *	context.getViewRoot().setLocale(locale);
 * 
 * Para traduzir o sistema todo em alem�o PG 425.
 */ 
