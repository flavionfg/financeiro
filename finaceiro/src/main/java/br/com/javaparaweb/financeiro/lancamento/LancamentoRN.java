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
 *  1 - O método Saldo,será utilizado para calcular qual montante de dinheiro uma determinada conta detinha em uma data
 *  Já vimos que o método saldo da classe LançamentoDAOhibernate calcula esse montante com base na somatória dos lançamentos
 *  porém o salvo de uma conta não é composto apenas por seus lançamentos,mas também pelo saldo inicial que foi especificado
 *  na conta bancaria no momento de seu cadastro no sistema.Unir essas informações é uma responsabilidade da camada de regra
 *  de negocio,pois, como já sabemos, a camada de acesso a dados (DAO) tem como responsabilidade apenas a execução de procedimentos
 *  unitarios no banco de dados.
 *  
 *  O método saldo será utulizado inicialmente na tela de cadastro de lançamentos para mostrar o saldo anterior aos lançamentos
 *  do periodo que estiverem sendo exibidos, também sera utilizado no relatório de extrato da conta.
 *  
 *  2 - O método listar, apesar de ser apenas um método de repasse para a camada DAO,será muito bem aproveitado no sistema.
 *  veja alguns exemplos de situações nas quais podemos reaproveitá-lo.
 *  
 *  - Listar todos os lançamentos.
 *  - Lançamento dos últimos 30 dias
 *  - Lançamento do mês atual
 *  - Lançamento futuro deste mês
 *  - Lançamento futuros.
 *  
 *  Ou seja como ele recebe uma data de inicio e fim como parâmetro e seu funcionamento interno é flexivel, é possivel utiliza-lo
 *  de varias formas no sistema interno.
 * 
 * 
 * 
 */


