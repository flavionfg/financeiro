package br.com.javaparaweb.financeiro.web;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.com.javaparaweb.financeiro.categoria.Categoria;
import br.com.javaparaweb.financeiro.categoria.CategoriaRN;

@ManagedBean(name = "categoriaBean")
@RequestScoped
public class CategoriaBean {

	private TreeNode categoriasTree; //arvore de categorias.
	private Categoria editada = new Categoria(); //o objeto que alimentará o formulario e receberá a categoria selecionada na arvore.
	private List<SelectItem> categoriasSelect; //a lista que alimentará a caixa de seleção de categorias pai.
	private boolean mostraEdicao = false;

	@ManagedProperty(value = "#{contextoBean}")
	private ContextoBean contextoBean;

	public void novo() { //1*
		Categoria pai = null;
		if (this.editada.getCodigo() != null) {
			CategoriaRN categoriaRN = new CategoriaRN();
			pai = categoriaRN.carregar(this.editada.getCodigo());
		}
		this.editada = new Categoria();
		this.editada.setPai(pai);
		this.mostraEdicao = true;
	}

	public void selecionar(NodeSelectEvent event) { //2*
		this.editada = (Categoria) event.getTreeNode().getData();
		Categoria pai = this.editada.getPai(); //verifica se tem um pai definido
		if (this.editada != null && pai != null && pai.getCodigo() != null) {
			this.mostraEdicao = true;
		} else {
			this.mostraEdicao = false;
		}
	}

	public String salvar() { //3*
		CategoriaRN categoriaRN = new CategoriaRN();
		this.editada.setUsuario(this.contextoBean.getUsuarioLogado());
		categoriaRN.salvar(this.editada);

		this.editada = null;
		this.mostraEdicao = false;
		this.categoriasTree = null;
		this.categoriasSelect = null;
		return null;
	}

	public String excluir() { //4*
		CategoriaRN categoriaRN = new CategoriaRN();
		categoriaRN.excluir(this.editada);

		this.editada = null;
		this.mostraEdicao = false;
		this.categoriasTree = null;
		this.categoriasSelect = null;
		return null;
	}

	public TreeNode getCategoriasTree() { //5*
		if (this.categoriasTree == null) {
			CategoriaRN categoriaRN = new CategoriaRN();
			List<Categoria> categorias = categoriaRN.listar(this.contextoBean.getUsuarioLogado()); //6*
			this.categoriasTree = new DefaultTreeNode(null, null);
			this.montaDadosTree(this.categoriasTree, categorias);
		}
		return this.categoriasTree;
	}

	private void montaDadosTree(TreeNode pai, List<Categoria> lista) { //7*
		if (lista != null && lista.size() > 0) {
			TreeNode filho = null;
			for (Categoria categoria : lista) {
				filho = new DefaultTreeNode(categoria, pai);
				this.montaDadosTree(filho, categoria.getFilhos());
			}
		}
	}

	public List<SelectItem> getCategoriasSelect() { //8*
		if (this.categoriasSelect == null) {
			this.categoriasSelect = new ArrayList<SelectItem>();
			CategoriaRN categoriaRN = new CategoriaRN();
			List<Categoria> categorias = categoriaRN.listar(this.contextoBean.getUsuarioLogado()); //9*
			this.montaDadosSelect(this.categoriasSelect, categorias, "");
		}
		return categoriasSelect;
	}

	private void montaDadosSelect(List<SelectItem> select, List<Categoria> categorias, String prefixo) {//10*
		SelectItem item = null;
		if (categorias != null) {
			for (Categoria categoria : categorias) {
				item = new SelectItem(categoria, prefixo + categoria.getDescricao()); //chamando método construtor do SelectItem
				item.setEscape(false);
				select.add(item);
				this.montaDadosSelect(select, categoria.getFilhos(), prefixo + "&nbsp;&nbsp;");
			}
		}
	}

	public Categoria getEditada() {
		return editada;
	}

	public void setEditada(Categoria editada) {
		this.editada = editada;
	}

	public boolean isMostraEdicao() {
		return mostraEdicao;
	}

	public void setMostraEdicao(boolean mostraEdicao) {
		this.mostraEdicao = mostraEdicao;
	}

	public ContextoBean getContextoBean() {
		return contextoBean;
	}

	public void setContextoBean(ContextoBean contextoBean) {
		this.contextoBean = contextoBean;
	}

}


/*
 * 1 - Será utilizado tanto pelo botão Novo quanto pelo Novo Subcategoria. a diferença de comportamento para quando acionamos o botao 
 * Novo Subcategoria é que,como ele está dentro do formulario, a categoria em edição também será enviada para a classe Bean.Ela será
 * preenchida como categoria pai do novo registro que será aberto no formulario.Justamente por isso é que colocamos a condicional 
 * dentro do método novo().
 * 
 * 2 - Será configurado no componentee Tree do PrimeFaces para ser chamado no momentoem que algum né da árvore for selecionado.
 * Por Padrão, sempre que algum né habilitamos o painel de edição(this.mostrarEdição = true) caso a categoria selecionada tenha um pai
 * definido,ou seja, se não for DESPESA ou RECEITA. dessa forma evitamos que as categorias de mais alto nivel sejam editadas.
 * 
 * NodeSelectEvent esse objeto fornece informações interresantes sobre o evento ocorrido,principalmente o valor associado ao nó
 * da Tree,por exemplo, event.getTreeNode().getData().
 * 
 * 3 - Funciona como o método salvar da ContaBean,mas temos uma quantidade maior de informação que são inicializadas ao final da execução
 * 
 * this.editada = null - reinicia uma instancia que acabou de ser salva,evite que guarde sujeita para alguma execução posterior.
 * 
 * this.mostraEdicao = false - permite decidir literalmente quando o formulario de edição deve ser exibido,conforme explicado.
 * 
 * this.categoriasTree = null e this.categoriasSelect = null - permite forçar o recarregamento dos dados da árvore de categorias e de 
 * caixas de seleção de categorias pai,como estamos realizando a alteração de uma categoria,essas operações garantirão que a classe
 * Bean seja obrigada a buscar novamente as categorias em banco na proxima vez em que forem necessarias.
 * 
 * 4 - idem do item 3 
 * 
 * 5 - é o metodo getter(o setter não será necessario) dos dados do componente tree.
 * 
 * 6 - Carrega a instrutura hierarquica das categorias do usuario,para isso acontecer ele chama o método CategoriaRN.listar ja construido. 
 * para transformar a estrutura hierarquica de Categoria em uma estrutura hierarquica de Treenode.
 * 
 * 7 - Tem funcionamento Recursivo para realizar essa operação,ou seja,o método percorre todas as categorias do usuario e chama a si mesmo
 * cada vez que for necessario descer um nivel de hierarquia.
 * 
 * 8 e 9 - Semelhante ao getCategoriasTree(), porém este gera uma lista plana de categoria do usario.
 * 
 * 10 - Como o componente de caixa de seleção(<h:selectOneMenu>) não mostra dados em estrutura de árvore,utilizamos o método montaDadosSelect()
 * para montar uma lista de SelectItem,mas deslocando com espaços(&nbsp;)cada nome para representar a estrutura hierarquica das categorias.
 * 
 * item.setEscape(false); é importante para que o JSF deixe o navegador interpretar o comando &nbsp; como um espaço a ser exibido, e não
 * mostre literalmente o comando &nbsp em tela.
 * 
 * chamamos o construtor do selectItem
 * 
 * new SelectItem(Object valor,String rotulo) - primeiro é o valor do item,e o segundo é o texto (rótulo) que deve ser exibido.
 * 
 * para o valor passamos uma instancia inteira de categoria, ou seja,este será o valor do item no componente.
 * 
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
