package br.com.javaparaweb.financeiro.web;

import java.util.List;

/*
 * O usuarioBean pertence ao escopo Request,ou seja,haverá uma nova instância para cada requisição realizada para o usuário.
 */

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import br.com.javaparaweb.financeiro.conta.Conta;
import br.com.javaparaweb.financeiro.conta.ContaRN;
import br.com.javaparaweb.financeiro.usuario.Usuario;
import br.com.javaparaweb.financeiro.usuario.UsuarioRN;

/*
 * A principal responsabilidade de um managed bean é intermediar a comunicação entre as páginas (componentes do JSF) e nosso modelo.
 * Escutar eventos, processa-los e delegar para a camada de negócios são apenas algumas de suas responsabilidades. a página não deveria 
 * conhecer a regra, mas apenas quem a conhece,o componente não conhece a regra, mas sabe exatamente a quem perguntar.
 * 
 * UsuarioBean é do escopo resquest,ou seja haverá uma nova instância para cada requisição realizada pelo usuario.
 */

@ManagedBean(name = "usuarioBean")
@RequestScoped
public class UsuarioBean {
	private Usuario usuario = new Usuario();
	private String confirmarSenha;
	private List<Usuario> lista;
	private String destinoSalvar;
	private Conta conta = new Conta();
	
	/*Com a implementação da area adiministrativa temos que alterar o metodo de salvar, pois na area admistrativa o usuario podera
	 * editar o usuario,desta forma o metodo salvar deverá resultar em páginas diferentes,dependendo de onde for acionada.
	 * A propriedade destinoSalvar,armazena a informação do destino do metodo.
	 */

	public String novo() {
		this.destinoSalvar = "usuariosucesso";
		this.usuario = new Usuario();
		this.usuario.setAtivo(true);
		return "/publico/usuario"; //retorna para a pagina usuario.xhtml da pasta publico
	}

	public String editar() { //apenas abre a pagina usuario.xhtml com os dados do usuario a ser editado,pois o objeto em usuario em si ja esta carregado.
		this.confirmarSenha = this.usuario.getSenha();
		return "/publico/usuario";
	}

	public String salvar() {
		FacesContext context = FacesContext.getCurrentInstance();  
		
		//A instancia da FacesContext será utilizada para adicionar mensagens de erro que possam vir a ser criados no condicional a baixo.
		 
		String senha = this.usuario.getSenha();
		if (!senha.equals(this.confirmarSenha)) {
			FacesMessage facesMessage = new FacesMessage("A senha não foi confirmada corretamente");
			context.addMessage(null, facesMessage);
			return null;
			
			//O retorno null indica que a ação executada nao caira em uma nova pagina,vai reexibir esta,com mensagem de erro
			//e os campos vão ser preenchidos com os valores enviados e o usuario realiza as correções solicitada na mensagem.
		}

		UsuarioRN usuarioRN = new UsuarioRN();
		usuarioRN.salvar(this.usuario);
		
		if (this.conta.getDescricao() != null) {
			this.conta.setUsuario(this.usuario); //atribuindo o usuario que acabou de ser cadastrado a uma conta
			this.conta.setFavorita(true); //deixando a conta como favorita
			ContaRN contaRN = new ContaRN();
			contaRN.salvar(this.conta);
		}

		return this.destinoSalvar; //desta forma se for uma edição da area adimistrativa ele vai apenas listar, se nao vai para pagina de boas vindas.
	}

	public String excluir() {
		UsuarioRN usuarioRN = new UsuarioRN();
		usuarioRN.excluir(this.usuario);
		this.lista = null;
		
		//Quando o usuario chama o metodo excluir, o metodo chama o metodo getlist para obter o usuario daquela linha,como vamos precisar
		//reexibir a tela e ocorre duas chamadas do getlist durante a mesmo tempo de vida da instancia do Usuariobean, teremos que forçar
		//a exibição da lista, passando null para lista em this.lista = null; desta forma a lista tras os usuarios atualizados 
		//e nao mostra o que acabamos de excluir.
	
		return null; // o retorno é null para que a pagina  de listagem seja reexibida.
	}

	public String ativar() { // uso a tag f:setPropertyActionListener para obeter o objeto usuario da linha clicada.
		if (this.usuario.isAtivo()) //se ja tiver ativado, ao chamar o metodo ele desativa passando false para o setAtivo.
			this.usuario.setAtivo(false);
		else
			this.usuario.setAtivo(true);

		UsuarioRN usuarioRN = new UsuarioRN(); //salvando a alteração utilizando a classe de negocio usuarioRN.
		usuarioRN.salvar(this.usuario);
		return null;
	}

	public List<Usuario> getLista() {
		if (this.lista == null) {
			UsuarioRN usuarioRN = new UsuarioRN();
			this.lista = usuarioRN.listar();
		}
		return this.lista;
	}
	
	public String atribuiPermissao(Usuario usuario, String permissao) { //em pose do objeto usuario,obetemos o set de permissoes e adicionamos ou removemos a permissao,dependendo se ela ja for existe ou não.
		this.usuario = usuario;
		java.util.Set<String> permissoes = this.usuario.getPermissao();
		if (permissoes.contains(permissao)) {
			permissoes.remove(permissao);
		} else {
			permissoes.add(permissao);
		}
		return null;
		
		/* Não temos codigo para salvar aqui, mas o Hibernate atraves do metodo HashCode e Equals, percebe a mudança do objeto
		 * ao adicionar as permissoes,ele fara automaticamento a sincronização com o banco de dados realizando insert  ou delete
		 * das permissoes.
		 * 
		 *  Como obtivemos um objeto carregado do usuario no momento do clique do botao?
		 * sempre que vc clicar em um botao em alguma linha de tabela o JSF carrega os dados no lado do servidor,
		 * e em posse desse dados o JSF passa por parametro um metodo declado em action ou por meio da tag <f:setPropertyListener/>.
		 */
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getConfirmarSenha() {
		return confirmarSenha;
	}

	public void setConfirmarSenha(String confirmarSenha) {
		this.confirmarSenha = confirmarSenha;
	}
	
	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public String getDestinoSalvar() {
		return destinoSalvar;
	}

	public void setDestinoSalvar(String destinoSalvar) {
		this.destinoSalvar = destinoSalvar;
	}


}
