package br.com.javaparaweb.financeiro.lancamento;

import java.util.Date;
import java.util.List;

import br.com.javaparaweb.financeiro.conta.Conta;
import br.com.javaparaweb.financeiro.util.DAOFactory;

public class LancamentoRN {
	private LancamentoDAO lancamentoDAO;

	public LancamentoRN() {
		this.lancamentoDAO = DAOFactory.criarLancamentoDAO();
	}

	public void salvar(Lancamento lancamento) {
		this.lancamentoDAO.salvar(lancamento);
	}

	public void excluir(Lancamento lancamento) {
		this.lancamentoDAO.excluir(lancamento);
	}

	public Lancamento carregar(Integer lancamento) {
		return this.lancamentoDAO.carregar(lancamento);
	}

	public float saldo(Conta conta, Date data) { //1*
		float saldoInicial = conta.getSaldoInicial();
		float saldoNaData = this.lancamentoDAO.saldo(conta, data);
		return saldoInicial + saldoNaData;
	}

	public List<Lancamento> listar(Conta conta, Date dataInicio, Date dataFim) { //2*
		return this.lancamentoDAO.listar(conta, dataInicio, dataFim);
	}
}


/*
 *  1 - O m�todo Saldo,ser� utilizado para calcular qual montante de dinheiro uma determinada conta detinha em uma data
 *  J� vimos que o m�todo saldo da classe Lan�amentoDAOhibernate calcula esse montante com base na somat�ria dos lan�amentos
 *  por�m o salvo de uma conta n�o � composto apenas por seus lan�amentos,mas tamb�m pelo saldo inicial que foi especificado
 *  na conta bancaria no momento de seu cadastro no sistema.Unir essas informa��es � uma responsabilidade da camada de regra
 *  de negocio,pois, como j� sabemos, a camada de acesso a dados (DAO) tem como responsabilidade apenas a execu��o de procedimentos
 *  unitarios no banco de dados.
 *  
 *  O m�todo saldo ser� utulizado inicialmente na tela de cadastro de lan�amentos para mostrar o saldo anterior aos lan�amentos
 *  do periodo que estiverem sendo exibidos, tamb�m sera utilizado no relat�rio de extrato da conta.
 *  
 *  2 - O m�todo listar, apesar de ser apenas um m�todo de repasse para a camada DAO,ser� muito bem aproveitado no sistema.
 *  veja alguns exemplos de situa��es nas quais podemos reaproveit�-lo.
 *  
 *  - Listar todos os lan�amentos.
 *  - Lan�amento dos �ltimos 30 dias
 *  - Lan�amento do m�s atual
 *  - Lan�amento futuro deste m�s
 *  - Lan�amento futuros.
 *  
 *  Ou seja como ele recebe uma data de inicio e fim como par�metro e seu funcionamento interno � flexivel, � possivel utiliza-lo
 *  de varias formas no sistema interno.
 * 
 * 
 * 
 */


