package br.com.javaparaweb.financeiro.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import br.com.javaparaweb.financeiro.categoria.Categoria;
import br.com.javaparaweb.financeiro.cheque.Cheque;
import br.com.javaparaweb.financeiro.cheque.ChequeId;
import br.com.javaparaweb.financeiro.cheque.ChequeRN;
import br.com.javaparaweb.financeiro.conta.Conta;
import br.com.javaparaweb.financeiro.lancamento.Lancamento;
import br.com.javaparaweb.financeiro.lancamento.LancamentoRN;
import br.com.javaparaweb.financeiro.util.RNException;

/*
 * Um destaque da classe LancamentoBean � a inicializa��o de valores do formulario de edi��o.
 * Nesse formulario o campo data sempre ser� inicializado com a data atual.Isso pode n�o representar muito agora,mas deixa a estrutura
 * organizada e reutilizavel para novas evolu��es.
 * 
 * Na ContaBean a inicializa��o do formulario � incializada com a data atual.
 * 
 * O m�todo novo() � chamada no construtor da classe sempre depois de salvar um registro e quando o bot�o Novo � acionado na tela. Isso
 * garante que o formulario de edi��o sempre apare�a em branco e com a data de hoje preenchida depois de uma opera�ao comcluida.
 *
 */
 
@ManagedBean(name = "lancamentoBean")
@ViewScoped //1*
public class LancamentoBean implements Serializable { //2*
	private static final long serialVersionUID = 3882855062422561543L; 
	private List<Lancamento> lista;
	private Conta conta;
	private List<Double> saldos;
	private float saldoGeral;
	private Lancamento editado = new Lancamento();
	private Integer numeroCheque; //11**

	@ManagedProperty(value = "#{contextoBean}")
	private ContextoBean contextoBean;

	public LancamentoBean() { //3*
		this.novo();
	}

	public String novo() { //4*
		this.editado = new Lancamento();
		this.editado.setData(new Date());
		this.numeroCheque = null;  //12**
		return null;
	}

	public void editar() { //5* e 13**
		Cheque cheque = this.editado.getCheque();
		if (cheque != null) {
			this.numeroCheque = cheque.getChequeId().getCheque();
		}

	}

	public void salvar() {
		this.editado.setUsuario(this.contextoBean.getUsuarioLogado());
		this.editado.setConta(this.contextoBean.getContaAtiva());

		ChequeRN chequeRN = new ChequeRN(); //14**
		ChequeId chequeId = null;
		if (this.numeroCheque != null) {
			chequeId = new ChequeId();
			chequeId.setConta(this.contextoBean.getContaAtiva().getConta());
			chequeId.setCheque(this.numeroCheque);
			Cheque cheque = chequeRN.carregar(chequeId);
			FacesContext context = FacesContext.getCurrentInstance();
			if (cheque == null) {
				context.addMessage(null, new FacesMessage("Cheque n�o cadastrado"));
				return;
			} else if (cheque.getSituacao() == Cheque.SITUACAO_CHEQUE_CANCELADO) {
				context.addMessage(null, new FacesMessage("Cheque j� cancelado"));
				return;
			} else {
				this.editado.setCheque(cheque);
				chequeRN.baixarCheque(chequeId, this.editado);
			}
		}

		LancamentoRN lancamentoRN = new LancamentoRN();
		lancamentoRN.salvar(this.editado);

		this.novo();
		this.lista = null;
	}

	public void mudouCheque(ValueChangeEvent event) { //15**
		Integer chequeAnterior = (Integer) event.getOldValue();
		if (chequeAnterior != null) {
			ChequeRN chequeRN = new ChequeRN();
			try {
				chequeRN.desvinculaLancamento(contextoBean.getContaAtiva(), chequeAnterior);
			} catch (RNException e) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(e.getMessage()));
				return;
			}
		}
	}

	public void excluir() {
		LancamentoRN lancamentoRN = new LancamentoRN();
		lancamentoRN.excluir(this.editado);
		this.lista = null;
	}

	public List<Lancamento> getLista() {  //7*
		if (this.lista == null || this.conta != this.contextoBean.getContaAtiva()) {
			this.conta = this.contextoBean.getContaAtiva();

			Calendar dataSaldo = new GregorianCalendar();  //8*
			dataSaldo.add(Calendar.MONTH, -1);
			dataSaldo.add(Calendar.DAY_OF_MONTH, -1);

			Calendar inicio = new GregorianCalendar(); //9*
			inicio.add(Calendar.MONTH, -1);

			LancamentoRN lancamentoRN = new LancamentoRN();
			this.saldoGeral = lancamentoRN.saldo(this.conta, dataSaldo.getTime()); //10*
			this.lista = lancamentoRN.listar(this.conta, inicio.getTime(), null);

			Categoria categoria = null;
			double saldo = this.saldoGeral;
			this.saldos = new ArrayList<Double>();
			for (Lancamento lancamento : this.lista) {
				categoria = lancamento.getCategoria();
				saldo = saldo + (lancamento.getValor().floatValue() * categoria.getFator());
				this.saldos.add(saldo);
			}
		}
		return this.lista;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public List<Double> getSaldos() {
		return saldos;
	}

	public void setSaldos(List<Double> saldos) {
		this.saldos = saldos;
	}

	public float getSaldoGeral() {
		return saldoGeral;
	}

	public void setSaldoGeral(float saldoGeral) {
		this.saldoGeral = saldoGeral;
	}

	public Lancamento getEditado() {
		return editado;
	}

	public void setEditado(Lancamento editado) {
		this.editado = editado;
	}

	public ContextoBean getContextoBean() {
		return contextoBean;
	}

	public void setContextoBean(ContextoBean contextoBean) {
		this.contextoBean = contextoBean;
	}

	public void setLista(List<Lancamento> lista) {
		this.lista = lista;
	}

	public void setNumeroCheque(Integer numeroCheque) {
		this.numeroCheque = numeroCheque;
	}

	public Integer getNumeroCheque() {
		return numeroCheque;
	}
}

/* 1 - @ViewScoped - Esse escopo mat�m a mesma inst�ncia da classe Bean enquanto o usuario estiver na mesma tela,independentemente de quantas
 * requisi��es ele tenha feito para a mesma tela.
 * 
 * 2 - � necessario porque uma classe Bean do tipo @ViewScoped ser� armazenada dentro da pagina exibida, para que a mesma inst�ncia seja utilizada
 * em uma requisi��o posterior. Por isso ela precisa ser serializada.
 * 
 * 5 - Dever� ser criado para atender ao bot�o Editar que existe a cada linha da tabaTable de lan�amentos.
 * 
 * 7 - Construido em outras telas e serve para fornecer os dados que alimentar�o a dataTable dos lan�amentos.
 * A condi��o  this.conta != this.contextoBean.getContaAtiva()), garante que a listagem dos lan�amentos seja recarregada se o usuario
 * trocar a conta ativa no menu.Isso � necess�rio pelo fato da classe Lan�amentoBean ser do tipo @ViewScoped, que sabemos � uma annotation que faz
 * uma inst�ncia da classe a ser mantida entre varias requisi��es.Como a inst�ncia se mant�m,o recarregamento da p�gina pela altera��o da conta ativa
 * n�o faz com que a propriedade lista torne-se null,ent�o precisamos de outra condi��o para reconhecer a necessidade de recarregar a 
 * lista de lan�amentos.
 * 
 * 8 -A classe Calendar � uma vers�o moderna da classe java.util.Date. 
 * 
 * Ainda no m�todo getLista(),Por defini��o determinamos que a tela de registro de lan�amento s� mostraria os lan�amentos dos ultimos 30 dias
 * e os lan�amentos futuros. Por isso, a data inicio realiza um add(Calendar.MONTH, -1), ou seja, um comando para buscar o dia de hoje, um m�s
 * atras.
 * 
 * O objeto do dataSaldo � saber qual era o saldo da conta na data anterior aos lan�amentos sendo exibidos,por,voltamos um dia.
 * 
 * 10 - Buscamos finalmente o saldo na data dataSaldo para realizar o c�lculo de saldo por lan�amento.
 * 
 * 11 - Propriedade numeroCheque, que ser� diretamente ligada ao campo Cheque na tela de lan�amentos.
 * 
 * 12 - No m�todo novo(), a propriedade numeroCheque � reiniciada.
 * 
 * 13 - O m�todo editar(), que antes estava vazio,agora � utilizado para extrair o n�mero do cheque da propriedade lancamento.cheque.
 * 
 * 14 - Agora um cheque � vinculado a uma lan�amento e logo em seguida,� efetuada sua baixa. Tamb�m valida-se determinado cheque
 * pode ser vinculado a um lan�amento de forma a n�o violar as regras de neg�cio do sistema.
 * 
 * 15 - Capturamos o evento mapeado em tela pelo elemento valueChangeListener e com o uso do objeto ValueChangeEvent,capturamos o
 * valor anteriormente informado (event.getOldValue()) e o atual (event.getNewValue()), fazendo as devidas correspond�ncias de 
 * neg�cio em seguida.
 * 
 * valueChangeListener � diferente do Onchange do JS, nele o acionando n�o acontece no momento em que o usuario sai do campo
 * com o valor alterado,mas somente depois do m�todo definido em valueChangeListener.
 * 
 * oldValue somente estar� disponivel caso a classe Bean em quest�o tiver escopo application,session ou view. O escopo
 * normalmente utilizado,request,n�o funciona corretamente para valueChangeListener.
 * 
 * mais informa��o PAG 428.
 * 
 Altera��es da pagina:

	public void salvar() {
		this.editado.setUsuario(this.contextoBean.getUsuarioLogado());
		this.editado.setConta(this.contextoBean.getContaAtiva());

		LancamentoRN lancamentoRN = new LancamentoRN();
		lancamentoRN.salvar(this.editado);

		this.novo();  //6*
		this.lista = null;
	}
 * 
 * 
 */


