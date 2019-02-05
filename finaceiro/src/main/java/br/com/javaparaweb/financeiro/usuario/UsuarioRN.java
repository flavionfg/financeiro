package br.com.javaparaweb.financeiro.usuario;

import java.util.List;

import br.com.javaparaweb.financeiro.categoria.CategoriaRN;
import br.com.javaparaweb.financeiro.util.DAOFactory;

/* RN s�o classes de regra de negocio!
 *  
 *  A classe UsuarioRN tem que ser entendida como um card�pio de servi�os disponiveis para a interface(telas).
 * 
 */

public class UsuarioRN {
	private UsuarioDAO usuarioDAO;

	public UsuarioRN() {
		this.usuarioDAO = DAOFactory.criarUsuarioDAO(); //para cada instancia de DAO que for necessaria nesta classe deve se usar o DAOfactory
		
	}

	public Usuario carregar(Integer codigo) { //serve para carregar uma unica inst�ncia de um usuario com base em seu codigo
		return this.usuarioDAO.carregar(codigo);
	}

	public Usuario buscarPorLogin(String login) {
		return this.usuarioDAO.buscarPorLogin(login);
	}

	public void salvar(Usuario usuario) {
		Integer codigo = usuario.getCodigo();
		if (codigo == null || codigo == 0) {
			usuario.getPermissao().add("ROLE_USUARIO"); // Quando for um usuario novo ele tera permissao de usuario adicionado ao Set de permiss�es.
			this.usuarioDAO.salvar(usuario);
			//altera��o para quando o usuario for novo
			CategoriaRN categoriaRN = new CategoriaRN();
			categoriaRN.salvaEstruturaPadrao(usuario);
		} else {
			this.usuarioDAO.atualizar(usuario);
		}
	}

	public void excluir(Usuario usuario) {
		//este m�todo tamb�m precisa excluir as categorias desse usuario.
		CategoriaRN categoriaRN = new CategoriaRN();
		categoriaRN.excluir(usuario);
		this.usuarioDAO.excluir(usuario);
	}

	public List<Usuario> listar() {
		return this.usuarioDAO.listar();
	}
}
