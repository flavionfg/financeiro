package br.com.javaparaweb.financeiro.util;

import br.com.javaparaweb.financeiro.conta.ContaDAO;
import br.com.javaparaweb.financeiro.conta.ContaDAOHibernate;
import br.com.javaparaweb.financeiro.usuario.UsuarioDAO;
import br.com.javaparaweb.financeiro.usuario.UsuarioDAOHibernate;


public class DAOFactory {

	/*Esta Classe � um construtor de DAOs,
	* Deve ser o unico ponto do sistema onde as DAOs s�o instanciadas
	*/
	public static UsuarioDAO criarUsuarioDAO() {
		UsuarioDAOHibernate usuarioDAO = new UsuarioDAOHibernate();
		usuarioDAO.setSession(HibernateUtil.getSessionFactory().getCurrentSession());
		return usuarioDAO;
	}
	
	public static ContaDAO criarContaDAO() {
		ContaDAOHibernate contaDAO = new ContaDAOHibernate();
		contaDAO.setSession(HibernateUtil.getSessionFactory().getCurrentSession());
		return contaDAO;
	}
}


//Ctrl + Shift + o  = para reorganizar os importes de classe.