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

/* 1 - Estamos usando o session.merge no lugar do saveOrUpdate, pq quando o formulario � enviado o JSF tem um corportamento de carregar todas as inst�ncias das 
 * categorias da sele��o "pai",inclusive a que esta sendo editada,est�o carregadas na memoria,temos duas instancias da mesma categoria, a carregada para exibir 
 * na tela e a que esta sendo editada, o conceito de persistencia proibe a existencia em memoria de duas instancias para o mesmo objeto,se usar o saveOrUpdate
 * vai acontecer um erro,o session.merge efetuara a fus�o da inst�ncia da categoria 123,sendo que essa mesma categoria j� estava carregada na memoria em uma 
 * inst�ncia que ja esta na memoria.A instancia resultante � retornada pelo m�todo.
 * 
 * 2 - session.flush for�a a sincroniza��o dos objetos em memoria com o banco de dados.
 * 
 * 3 - session.clear - remove da memoria do hibernate todos os objetos carregados, isso � importante para for�ar a recarga dessa inst�ncia do banco pois nessa
 * mesma transa��o,esse objeto ser� novamente carregado para a reexibi��o do formulario.
 * 
 * Caso n�o efetuarmos session.flush() e session.clear() ao final do m�todo salvar,o UPDATE no banco de dados s� seria realmente executado ao final da transa��o.
 * como a transa��o s� � encerrada depois que a tela for exibida, a tela mostraria os dados antigos da categoria.
 * 
 * 4 - A Classe categoria foi configurada para que quando fosse excluida uma categoria seus filhos tmb sejam,por�m no processo delete cascade s� vai funcionar
 * se os filhos e netos estiverem carregados no momento da exclus�o, a utiliza��o do session.merge n�o faz essa carga,mas apenas mescla os dados do objeto fora 
 * do contexto com o objeto dentro do contexto persistente.Para solucionar isso carregamos o objeto utilizando o metodo Carregar() na classe DAO e carrega todos
 * os filhos e netos dessa categoria e o session.delete() em cascata vai excluir todos eles.
 * 
 * 5 - c.pai is null garantir� que somente os primeiros niveis de categoria(DESPESA e RECEITA) sejam carregados, ou seja,haver� apenas dois registros,a carga dos
 * demais niveis ser� feita pelo relacionamento @OneToMany dos filhos.
 * 
 *
 * 
 * 
 * 
 * 
 */
