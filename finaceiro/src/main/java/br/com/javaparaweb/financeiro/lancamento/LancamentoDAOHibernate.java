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

/* 1 - Este m�todo permite buscar os lan�amentos entre as datas de inicio e fim, a partir da data de inicio ou at� a data
 * ou at� a data fim. Para isso,testamos quais par�metros foram recebidos e montamos o filtro da consulta realizando 
 * crit�ria.add() para cada condi��o.
 * 
 * Essa t�cnica � interessante quando se necessita criar filtros flexiveis conforme as informa��es preenchidas em tela,por exemplo.
 * 
 * 2 - Defini��o da cl�usula order by, que trar� todos os lan�amentos da data mais antiga at� a mais recente,ainda podemos definir
 * uma ordena��o decrescente(descendente) usando order.desc("campo") e ordenar quantos campos forem necessarios,repetindo
 * a chamada criteria.addOrder().
 * 
 * 3 - Permite calcular o saldo dos lan�amentos at� a data recebida por parametro.Contudo,ele faz esse c�lculo utilziando  
 * uma SQL pura, e n�o a HQL(Hibernate Query Language), como ja haviamos utilizado.isso � possivel utilizando o m�todo 
 * sesion.createSQLquery(String).
 * 
 * 4 - Esse m�todo fornece um objeto dotipo SQLQuery, mas que funciona igual ao Query j� utilizado anteriormente.Nessa SQL
 * fazemos o somat�rio (sum) dos valores de todos os lan�amentos at� a data especificada,multiplicando cada lan�amento at�
 * a data especificada,multiplicando cada lan�amento pelo fator(1 - ou -1) da respectiva categoria.
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
