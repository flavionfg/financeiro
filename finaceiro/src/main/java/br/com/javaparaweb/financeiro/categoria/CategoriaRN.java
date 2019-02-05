package br.com.javaparaweb.financeiro.categoria;

import java.util.List;

import br.com.javaparaweb.financeiro.usuario.Usuario;
import br.com.javaparaweb.financeiro.util.DAOFactory;

public class CategoriaRN {

	private CategoriaDAO categoriaDAO;

	public CategoriaRN() {
		this.categoriaDAO = DAOFactory.criarCategoriaDAO();
	}

	public List<Categoria> listar(Usuario usuario) {
		return this.categoriaDAO.listar(usuario);
	}

	public Categoria salvar(Categoria categoria) {
		Categoria pai = categoria.getPai();

		if (pai == null) { //1*
			String msg = "A Categoria " + categoria.getDescricao() + " deve ter um pai definido";
			throw new IllegalArgumentException(msg);
		}

		boolean mudouFator = pai.getFator() != categoria.getFator(); //2*

		categoria.setFator(pai.getFator());
		categoria = this.categoriaDAO.salvar(categoria); //3*

		if (mudouFator) { //4*
			categoria = this.carregar(categoria.getCodigo());
			this.replicarFator(categoria, categoria.getFator());
		}
		return categoria;
	}

	private void replicarFator(Categoria categoria, int fator) { //5*
		if (categoria.getFilhos() != null) {
			for (Categoria filho : categoria.getFilhos()) {
				filho.setFator(fator);
				this.categoriaDAO.salvar(filho);
				this.replicarFator(filho, fator);
			}
		}
	}

	public void excluir(Categoria categoria) { 
		this.categoriaDAO.excluir(categoria);
	}

	public void excluir(Usuario usuario) { //7*
		List<Categoria> lista = this.listar(usuario);
		for (Categoria categoria : lista) {
			this.categoriaDAO.excluir(categoria);
		}
	}

	public Categoria carregar(Integer categoria) {
		return this.categoriaDAO.carregar(categoria);
	}

	public void salvaEstruturaPadrao(Usuario usuario) { //8*

		Categoria despesas = new Categoria(null, usuario, "DESPESAS", -1);
		despesas = this.categoriaDAO.salvar(despesas);
		this.categoriaDAO.salvar(new Categoria(despesas, usuario, "Moradia", -1));
		this.categoriaDAO.salvar(new Categoria(despesas, usuario, "Alimenta��o", -1));
		this.categoriaDAO.salvar(new Categoria(despesas, usuario, "Vestu�rio", -1));
		this.categoriaDAO.salvar(new Categoria(despesas, usuario, "Deslocamento", -1));
		this.categoriaDAO.salvar(new Categoria(despesas, usuario, "Cuidados Pessoais", -1));
		this.categoriaDAO.salvar(new Categoria(despesas, usuario, "Educa��o", -1));
		this.categoriaDAO.salvar(new Categoria(despesas, usuario, "Sa�de", -1));
		this.categoriaDAO.salvar(new Categoria(despesas, usuario, "Lazer", -1));
		this.categoriaDAO.salvar(new Categoria(despesas, usuario, "Despesas Financeiras", -1));

		Categoria receitas = new Categoria(null, usuario, "RECEITAS", 1);
		receitas = this.categoriaDAO.salvar(receitas);
		this.categoriaDAO.salvar(new Categoria(receitas, usuario, "Sal�rio", 1));
		this.categoriaDAO.salvar(new Categoria(receitas, usuario, "Restitui��es", 1));
		this.categoriaDAO.salvar(new Categoria(receitas, usuario, "Rendimento", 1));
	}
}


/* 1 - Verifica se a categoria que esta sendo salva n�o tem um pai,sabemos que categorias de alto nivel (Despesas e Receitas) n�o tem 
 * pai,pois est�o no topo da hierarquia,uma regra de negocio nossa � que as defini�oes de categoria de nivel mais alto n�o poderam
 * ser alteradas, que no nosso caso � Despesas e Receitas.
 *  
 * 4 - Esse Fator de multiplica��o sempre ser� obtido  da categoria pai na qual a categoria em quest�o foi criada, ou seja, se a 
 * categoria for criada sob DESPESAS, seu fator ser� -1; se for criada sob RECEITAS,ser� 1.
 * 
 * A transferencia do fator do pai e filho � feita sempre antes de salvar a categoria.pode ocorrer tamb�m,contudo altera��o de um
 * ramo de categorias de RECEITAS para DESPESAS ou vice versa,nesse caso � preciso mudar o fator da categoria atual e de seus filhos
 * mas como isso � pesado para o sistema s� mudamos se mudouFator for true,como objeto veio por formulario n�o temos os filhos 
 * carregados,por isso chamamos o m�todo carregar().
 * 
 * 5 - metodo recursivo que reapassa essa mudan�a de fator para todas as categorias  da hierarquia abaixo da categoria alterada.
 * 
 * 7 - realiza uma exclus�o por usuario,faz uma consulta por usuario e deleta uma a uma,isso foi necessario pq temos que excluir a 
 * categoria manualmente  quando um usuario for excluido... - Pagina 346.
 * 
 * 8 - Salva um novo usuario e s� sera chamado a partir da classe UsuarioRN,no m�todo criamos uma estutura basica de categoria
 * para que o usuario n�o tenha que cadastrar todas manualmente. uma vez que os dados ja estao perfeitamente organizados usamos
 * o metodo categoriaDAO.salvar.
 * 
 * 
 * 
 */
