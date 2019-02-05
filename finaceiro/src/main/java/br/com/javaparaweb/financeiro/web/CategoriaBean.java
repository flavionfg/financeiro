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
	private Categoria editada = new Categoria(); //o objeto que alimentar� o formulario e receber� a categoria selecionada na arvore.
	private List<SelectItem> categoriasSelect; //a lista que alimentar� a caixa de sele��o de categorias pai.
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
				item = new SelectItem(categoria, prefixo + categoria.getDescricao()); //chamando m�todo construtor do SelectItem
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
 * 1 - Ser� utilizado tanto pelo bot�o Novo quanto pelo Novo Subcategoria. a diferen�a de comportamento para quando acionamos o botao 
 * Novo Subcategoria � que,como ele est� dentro do formulario, a categoria em edi��o tamb�m ser� enviada para a classe Bean.Ela ser�
 * preenchida como categoria pai do novo registro que ser� aberto no formulario.Justamente por isso � que colocamos a condicional 
 * dentro do m�todo novo().
 * 
 * 2 - Ser� configurado no componentee Tree do PrimeFaces para ser chamado no momentoem que algum n� da �rvore for selecionado.
 * Por Padr�o, sempre que algum n� habilitamos o painel de edi��o(this.mostrarEdi��o = true) caso a categoria selecionada tenha um pai
 * definido,ou seja, se n�o for DESPESA ou RECEITA. dessa forma evitamos que as categorias de mais alto nivel sejam editadas.
 * 
 * NodeSelectEvent esse objeto fornece informa��es interresantes sobre o evento ocorrido,principalmente o valor associado ao n�
 * da Tree,por exemplo, event.getTreeNode().getData().
 * 
 * 3 - Funciona como o m�todo salvar da ContaBean,mas temos uma quantidade maior de informa��o que s�o inicializadas ao final da execu��o
 * 
 * this.editada = null - reinicia uma instancia que acabou de ser salva,evite que guarde sujeita para alguma execu��o posterior.
 * 
 * this.mostraEdicao = false - permite decidir literalmente quando o formulario de edi��o deve ser exibido,conforme explicado.
 * 
 * this.categoriasTree = null e this.categoriasSelect = null - permite for�ar o recarregamento dos dados da �rvore de categorias e de 
 * caixas de sele��o de categorias pai,como estamos realizando a altera��o de uma categoria,essas opera��es garantir�o que a classe
 * Bean seja obrigada a buscar novamente as categorias em banco na proxima vez em que forem necessarias.
 * 
 * 4 - idem do item 3 
 * 
 * 5 - � o metodo getter(o setter n�o ser� necessario) dos dados do componente tree.
 * 
 * 6 - Carrega a instrutura hierarquica das categorias do usuario,para isso acontecer ele chama o m�todo CategoriaRN.listar ja construido. 
 * para transformar a estrutura hierarquica de Categoria em uma estrutura hierarquica de Treenode.
 * 
 * 7 - Tem funcionamento Recursivo para realizar essa opera��o,ou seja,o m�todo percorre todas as categorias do usuario e chama a si mesmo
 * cada vez que for necessario descer um nivel de hierarquia.
 * 
 * 8 e 9 - Semelhante ao getCategoriasTree(), por�m este gera uma lista plana de categoria do usario.
 * 
 * 10 - Como o componente de caixa de sele��o(<h:selectOneMenu>) n�o mostra dados em estrutura de �rvore,utilizamos o m�todo montaDadosSelect()
 * para montar uma lista de SelectItem,mas deslocando com espa�os(&nbsp;)cada nome para representar a estrutura hierarquica das categorias.
 * 
 * item.setEscape(false); � importante para que o JSF deixe o navegador interpretar o comando &nbsp; como um espa�o a ser exibido, e n�o
 * mostre literalmente o comando &nbsp em tela.
 * 
 * chamamos o construtor do selectItem
 * 
 * new SelectItem(Object valor,String rotulo) - primeiro � o valor do item,e o segundo � o texto (r�tulo) que deve ser exibido.
 * 
 * para o valor passamos uma instancia inteira de categoria, ou seja,este ser� o valor do item no componente.
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
