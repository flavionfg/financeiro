package br.com.javaparaweb.financeiro.lancamento;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.javaparaweb.financeiro.conta.Conta;

public class LancamentoDAOHibernate implements LancamentoDAO {
	private Session session;

	public void setSession(Session session) {
		this.session = session;
	}

	public void salvar(Lancamento lancamento) {
		this.session.saveOrUpdate(lancamento);
	}

	public void excluir(Lancamento lancamento) {
		this.session.delete(lancamento);
	}

	public Lancamento carregar(Integer lancamento) {
		return (Lancamento) this.session.get(Lancamento.class, lancamento);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Lancamento> listar(Conta conta, Date dataInicio, Date dataFim) { //1*
		Criteria criteria = this.session.createCriteria(Lancamento.class);

		if (dataInicio != null && dataFim != null) {
			criteria.add(Restrictions.between("data", dataInicio, dataFim));
		} else if (dataInicio != null) {
			criteria.add(Restrictions.ge("data", dataInicio));
		} else if (dataFim != null) {
			criteria.add(Restrictions.le("data", dataFim));
		}

		criteria.add(Restrictions.eq("conta", conta));
		criteria.addOrder(Order.asc("data")); //2*
		return criteria.list();
	}

	public float saldo(Conta conta, Date data) { //3*
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(l.valor * c.fator)");
		sql.append(" from LANCAMENTO l,");
		sql.append("		CATEGORIA c");
		sql.append(" where l.categoria = c.codigo");
		sql.append("  and l.conta = :conta");
		sql.append("  and l.data <= :data");
		SQLQuery query = this.session.createSQLQuery(sql.toString()); //4*
		query.setParameter("conta", conta.getConta());
		query.setParameter("data", data);
		BigDecimal saldo = (BigDecimal) query.uniqueResult();
		if (saldo != null) {
			return saldo.floatValue();
		}
		return 0f;
	}
}

/* 1 - Este método permite buscar os lançamentos entre as datas de inicio e fim, a partir da data de inicio ou até a data
 * ou até a data fim. Para isso,testamos quais parâmetros foram recebidos e montamos o filtro da consulta realizando 
 * critéria.add() para cada condição.
 * 
 * Essa técnica é interessante quando se necessita criar filtros flexiveis conforme as informações preenchidas em tela,por exemplo.
 * 
 * 2 - Definição da cláusula order by, que trará todos os lançamentos da data mais antiga até a mais recente,ainda podemos definir
 * uma ordenação decrescente(descendente) usando order.desc("campo") e ordenar quantos campos forem necessarios,repetindo
 * a chamada criteria.addOrder().
 * 
 * 3 - Permite calcular o saldo dos lançamentos até a data recebida por parametro.Contudo,ele faz esse cálculo utilziando  
 * uma SQL pura, e não a HQL(Hibernate Query Language), como ja haviamos utilizado.isso é possivel utilizando o método 
 * sesion.createSQLquery(String).
 * 
 * 4 - Esse método fornece um objeto dotipo SQLQuery, mas que funciona igual ao Query já utilizado anteriormente.Nessa SQL
 * fazemos o somatório (sum) dos valores de todos os lançamentos até a data especificada,multiplicando cada lançamento até
 * a data especificada,multiplicando cada lançamento pelo fator(1 - ou -1) da respectiva categoria.
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
