package br.com.javaparaweb.financeiro.web;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import br.com.javaparaweb.financeiro.conta.Conta;
import br.com.javaparaweb.financeiro.conta.ContaRN;

/*
 * A principal responsabilidade de um managed bean é intermediar a comunicação entre as páginas (componentes do JSF) e nosso modelo.
 * Escutar eventos, processa-los e delegar para a camada de negócios são apenas algumas de suas responsabilidades. a página não deveria 
 * conhecer a regra, mas apenas quem a conhece,o componente não conhece a regra, mas sabe exatamente a quem perguntar.
 */



@ManagedBean
@RequestScoped
public class ContaBean {
	
	private Conta selecionada = new Conta();
	private List<Conta> lista = null;
	@ManagedProperty(value = "#{contextoBean}")
	private ContextoBean contextoBean;

	public String salvar() {
		this.selecionada.setUsuario(this.contextoBean.getUsuarioLogado()); //atribuimos o objeto Usuario do usuario logado na Conta de edição.
		ContaRN contaRN = new ContaRN();
		contaRN.salvar(this.selecionada);
		this.selecionada = new Conta(); 
		this.lista = null;
		return null;
		
		/*
		 * Atribuimos new Conta() á propriedade this.selecionada para "limpar" o formulario.dessa forma,quando o painel edicao for 
		 * recarregado,ele aparecerá em branco.
		 * 
		 * atribuimos null na lista de contas para forçar o recarregamento das contas no banco der dados quando o painel listagem for recarregado,
		 * perceba que o método getLista() que as contas no banco só tornam a ser buscadas quando a propriedade this.lista for igual a null.
		 */
	}

	public String excluir() {
		ContaRN contaRN = new ContaRN();
		contaRN.excluir(this.selecionada);
		this.selecionada = new Conta();
		this.lista = null; //atribuimos null aqui para que no proximo carregamento da lista pelo dataTable com valores atualizados
		return null;
	}

	public String tornarFavorita() {
		ContaRN contaRN = new ContaRN();
		contaRN.tornarFavorita(this.selecionada);
		this.selecionada = new Conta();
		return null;
	}

	public Conta getSelecionada() {
		return selecionada;
	}

	public void setSelecionada(Conta selecionada) {
		this.selecionada = selecionada;
	}

	public List<Conta> getLista() {
		if (this.lista == null) {
			ContaRN contaRN = new ContaRN();
			this.lista = contaRN.listar(this.contextoBean.getUsuarioLogado());
		}
		return this.lista;

		/*
		 * Sempre que a DataTable da pagina conta.xhtml for exibida ela vai ela aponta para ContaBean.Lista que vai chamar o getLista(),
		 * Aqui obtemos a instância do usuario logado para fazer consultas referentes a esse usuario.
		 * 
		 * this.lista == null - isso faz com que temos um desenpenho melhor,pois o metodo getLista() e chamado em varios momentos, e cada chamada
		 * seria necessario uma consulta no banco,dessa forma garantimos que a consulta seja feita uma unica vez, e, nas proximas outras vezes em 
		 * que o JSF chama o método getLista(), ele obterá os dados ja consultados.
		 */
	}

	public void setLista(List<Conta> lista) {
		this.lista = lista;
	}

	public ContextoBean getContextoBean() {
		return contextoBean;
	}

	public void setContextoBean(ContextoBean contextoBean) {
		this.contextoBean = contextoBean;
	}

}
