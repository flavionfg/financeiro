package br.com.javaparaweb.financeiro.conta;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.javaparaweb.financeiro.usuario.Usuario;

public class ContaDAOHibernate implements ContaDAO {
	private Session session;

	public void setSession(Session session) {
		this.session = session;
	}

	public void excluir(Conta conta) {
		this.session.delete(conta);
	}

	public void salvar(Conta conta) {
		this.session.saveOrUpdate(conta);
		
		/*O m�todo sabeOrUpdate do Hibernate insere ou atualiza a conta, se ja existir ele faz um update, se n�o faz um insert
		 * � uma abordagem diferente da classe usuario.
		 */
	}

	public Conta carregar(Integer conta) {
		return (Conta) this.session.get(Conta.class, conta);
	}

	public List<Conta> listar(Usuario usuario) {
		Criteria criteria = this.session.createCriteria(Conta.class);
		criteria.add(Restrictions.eq("usuario", usuario));
		return criteria.list();
		
		/*
		 * A classe Criteria permite adicionar Filtros � consulta,
		 * neste caso devemos obter somente registros em que o usuario seja igual ao recebido por par�metro.
		 * 
		 * Para retornar objeto que possuem um valor de propriedade que � igual � nossa restri��o, usamos o m�todo eq() em Restrictions
		 */
	}

	public Conta buscarFavorita(Usuario usuario) {
		Criteria criteria = this.session.createCriteria(Conta.class);
		criteria.add(Restrictions.eq("usuario", usuario));
		criteria.add(Restrictions.eq("favorita", true));
		return (Conta) criteria.uniqueResult();
		
		/*
		 * estamos usando o criteria.uniqueResult() pois sabemos que a consulta s� trar� apenas uma resultado,
		 * se fosse varios seria criteria.list().
		 */
		
	}
}