package br.com.javaparaweb.financeiro.categoria;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import br.com.javaparaweb.financeiro.usuario.Usuario;

public class CategoriaDAOHibernate implements CategoriaDAO {

	private Session session;

	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public Categoria salvar(Categoria categoria) { //1*
		Categoria merged = (Categoria) this.session.merge(categoria);
		this.session.flush(); //2*
		this.session.clear(); //3*
		return merged;
	}

	@Override
	public void excluir(Categoria categoria) { //4*
		categoria = (Categoria) this.carregar(categoria.getCodigo());
		this.session.delete(categoria);
		this.session.flush();
		this.session.clear();
	}

	@Override
	public Categoria carregar(Integer categoria) {
		return (Categoria) this.session.get(Categoria.class, categoria);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Categoria> listar(Usuario usuario) {

		String hql = "select c from Categoria c where c.pai is null and c.usuario = :usuario"; //5*
		Query query = this.session.createQuery(hql);
		query.setInteger("usuario", usuario.getCodigo());

		List<Categoria> lista = query.list();

		return lista;
	}
}

/* 1 - Estamos usando o session.merge no lugar do saveOrUpdate, pq quando o formulario é enviado o JSF tem um corportamento de carregar todas as instâncias das 
 * categorias da seleção "pai",inclusive a que esta sendo editada,estão carregadas na memoria,temos duas instancias da mesma categoria, a carregada para exibir 
 * na tela e a que esta sendo editada, o conceito de persistencia proibe a existencia em memoria de duas instancias para o mesmo objeto,se usar o saveOrUpdate
 * vai acontecer um erro,o session.merge efetuara a fusão da instância da categoria 123,sendo que essa mesma categoria já estava carregada na memoria em uma 
 * instância que ja esta na memoria.A instancia resultante é retornada pelo método.
 * 
 * 2 - session.flush força a sincronização dos objetos em memoria com o banco de dados.
 * 
 * 3 - session.clear - remove da memoria do hibernate todos os objetos carregados, isso é importante para forçar a recarga dessa instância do banco pois nessa
 * mesma transação,esse objeto será novamente carregado para a reexibição do formulario.
 * 
 * Caso não efetuarmos session.flush() e session.clear() ao final do método salvar,o UPDATE no banco de dados só seria realmente executado ao final da transação.
 * como a transação só é encerrada depois que a tela for exibida, a tela mostraria os dados antigos da categoria.
 * 
 * 4 - A Classe categoria foi configurada para que quando fosse excluida uma categoria seus filhos tmb sejam,porém no processo delete cascade só vai funcionar
 * se os filhos e netos estiverem carregados no momento da exclusão, a utilização do session.merge não faz essa carga,mas apenas mescla os dados do objeto fora 
 * do contexto com o objeto dentro do contexto persistente.Para solucionar isso carregamos o objeto utilizando o metodo Carregar() na classe DAO e carrega todos
 * os filhos e netos dessa categoria e o session.delete() em cascata vai excluir todos eles.
 * 
 * 5 - c.pai is null garantirá que somente os primeiros niveis de categoria(DESPESA e RECEITA) sejam carregados, ou seja,haverá apenas dois registros,a carga dos
 * demais niveis será feita pelo relacionamento @OneToMany dos filhos.
 * 
 *
 * 
 * 
 * 
 * 
 */
