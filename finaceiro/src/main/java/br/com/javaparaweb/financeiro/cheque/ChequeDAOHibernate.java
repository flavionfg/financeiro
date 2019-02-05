package br.com.javaparaweb.financeiro.cheque;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.javaparaweb.financeiro.conta.Conta;

public class ChequeDAOHibernate implements ChequeDAO {

	private Session session;

	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public void salvar(Cheque cheque) {
		this.session.saveOrUpdate(cheque);
	}

	@Override
	public void excluir(Cheque cheque) {
		this.session.delete(cheque);
	}

	@Override
	public Cheque carregar(ChequeId chequeId) { //1*
		return (Cheque) this.session.get(Cheque.class, chequeId);
	}

	@Override
	public List<Cheque> listar(Conta conta) {  //2*
		Criteria criteria = this.session.createCriteria(Cheque.class);
		criteria.add(Restrictions.eq("conta", conta));
		return criteria.list();
	}
}

/*
 * 1 - Em outras classes usadmos os IDs para fazer o carregamento com o session.get(),neste caso vamos passar uma variavel
 * do tipo ChequeId,que representa o @Id da classe
 * 
 * 2 - O método listar tem como parâmetro o objeto Conta, já que a ideia é listar todos os objetos do tipo Cheque vinculados 
 * a determinada conta.
 * 
 */ 
