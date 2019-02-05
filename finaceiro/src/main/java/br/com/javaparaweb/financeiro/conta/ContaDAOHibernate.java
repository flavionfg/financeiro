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
		 * 
		 * para o saveOrUpdate funcionar corretamente, quando for acontecer o save a propriedade do c�digo (propriedade que cont�m 
		 * o @Id) deve estar com o valor NULO (ou zero primitivos)
		 * 
		 * no JSF se um campo numero for nulo ao ser exibido no formulario,continua sendo ao ser enviado, a nao ser que for um int,
		 * o que garante isso � a propriedade javax.faces.INTERPRET_EMPTY_STRING_SUBMITTED_VALUES_AS_NULL com o valor true no web.xml.
		 * 
		 * por fim, se o codigo da conta for um integer e nao int, e nesse caso por algum motivo estiver com o valor 0 (zero)
		 * o Hibernate tentar� fazer update e n�o insert.
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
