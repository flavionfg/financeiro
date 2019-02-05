package br.com.javaparaweb.financeiro.usuario;

import java.util.List;


/* Esta classe tem apenas com as assinaturas dos métodos,segue as diretrizes do Design Pattern DAO.
 * 
 * Desta forma a classe de regra de negocio trabalhará sem nem ao menos saber que a camada de Banco de Dados (DAO) esta funcionando
 * em Hibernate, sendo assim possivel até trocar a tecnologia da camada de acesso a dados sem precisar mexer na camada de regra 
 * de negocio. 
 * 
 */

public interface UsuarioDAO {
	
	public void salvar(Usuario usuario);

	public void atualizar(Usuario usuario);

	public void excluir(Usuario usuario);

	public Usuario carregar(Integer codigo);

	public Usuario buscarPorLogin(String login);

	public List<Usuario> listar();
}
