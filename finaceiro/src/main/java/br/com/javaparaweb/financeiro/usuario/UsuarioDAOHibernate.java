package br.com.javaparaweb.financeiro.usuario;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

public class UsuarioDAOHibernate implements UsuarioDAO {
	private Session session;

	public void setSession(Session session) { // O Metodo setSession que vai injetar a Session na classe UsuarioDAOHibernate - pacote correto é org.hibernate.Session;
		this.session = session; // aqui usando o this, estamos referenciando a session do atributo, private Session session, logo a baixo do public class UsuarioDAOHibernate.
	}

	public void salvar(Usuario usuario) {
		this.session.save(usuario);
	}

	public void atualizar(Usuario usuario) {
		if (usuario.getPermissao() == null || usuario.getPermissao().size() ==0) { 
			Usuario usuarioPermissao = this.carregar(usuario.getCodigo());
			usuario.setPermissao(usuarioPermissao.getPermissao());
			this.session.evict(usuarioPermissao);
		}
			this.session.update(usuario);
			
			/* Primeiro temos que testar se o usuario tem alguma permissao,se for vazio temos que carrega-lo.
			 * Carrega o usuario pelo codigo e joga esse objeto em uma referencia à parte a usuarioPermissao.
			 * Trasnferimos a permissao original para o objeto usuario a ser salvo.
			 * session.evict - este método retira do contexto de persistente o objeto usuarioPermissao, que só foi utilizado para copiar permissões.
			 */
	}

	public void excluir(Usuario usuario) {
		this.session.delete(usuario);
	}

	public Usuario carregar(Integer codigo) { // faz um SELECT e carrega os dados do usuario recebidos por parametro
		return (Usuario) this.session.get(Usuario.class, codigo);
	}

	public List<Usuario> listar() { // Consulta todos os usuarios existente no banco, sem qualquer condição.
		return this.session.createCriteria(Usuario.class).list();
	}

	public Usuario buscarPorLogin(String login) {
		String hql = "select u from Usuario u where u.login = :login";
		Query consulta = this.session.createQuery(hql);
		consulta.setString("login", login);
		return (Usuario) consulta.uniqueResult(); //uniqueResult() só recebe uma linha da consulta, como login é uma chave natural sabemos
		//que retornara apenas uma linha, se fosse varias deveriamos usar o consulta.list().
		
		//Neste metodo esta sendo usado a linguagem HQL, parecida com SQL só que adataptada para o mundo Objeto-Relacional
	}
}
