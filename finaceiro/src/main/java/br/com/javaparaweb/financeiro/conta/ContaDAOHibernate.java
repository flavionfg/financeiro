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
		
		/*O método sabeOrUpdate do Hibernate insere ou atualiza a conta, se ja existir ele faz um update, se não faz um insert
		 * é uma abordagem diferente da classe usuario.
		 * 
		 * para o saveOrUpdate funcionar corretamente, quando for acontecer o save a propriedade do código (propriedade que contém 
		 * o @Id) deve estar com o valor NULO (ou zero primitivos)
		 * 
		 * no JSF se um campo numero for nulo ao ser exibido no formulario,continua sendo ao ser enviado, a nao ser que for um int,
		 * o que garante isso é a propriedade javax.faces.INTERPRET_EMPTY_STRING_SUBMITTED_VALUES_AS_NULL com o valor true no web.xml.
		 * 
		 * por fim, se o codigo da conta for um integer e nao int, e nesse caso por algum motivo estiver com o valor 0 (zero)
		 * o Hibernate tentará fazer update e não insert.
		 * 
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
		 * A classe Criteria permite adicionar Filtros à consulta,
		 * neste caso devemos obter somente registros em que o usuario seja igual ao recebido por parâmetro.
		 * 
		 * Para retornar objeto que possuem um valor de propriedade que é igual à nossa restrição, usamos o método eq() em Restrictions
		 */
	}

	public Conta buscarFavorita(Usuario usuario) {
		Criteria criteria = this.session.createCriteria(Conta.class);
		criteria.add(Restrictions.eq("usuario", usuario));
		criteria.add(Restrictions.eq("favorita", true));
		return (Conta) criteria.uniqueResult();
		
		/*
		 * estamos usando o criteria.uniqueResult() pois sabemos que a consulta só trará apenas uma resultado,
		 * se fosse varios seria criteria.list().
		 */
		
	}
}
