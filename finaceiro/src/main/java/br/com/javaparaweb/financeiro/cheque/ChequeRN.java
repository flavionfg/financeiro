package br.com.javaparaweb.financeiro.cheque;

import java.util.Date;
import java.util.List;

import br.com.javaparaweb.financeiro.conta.Conta;
import br.com.javaparaweb.financeiro.lancamento.Lancamento;
import br.com.javaparaweb.financeiro.util.DAOFactory;
import br.com.javaparaweb.financeiro.util.RNException;

/*
 *  � nessa classe que teremos as regras de neg�cio pertinentes ao cheque.
 *  
 *             Regras de negocio para cadastro de cheque:
 *  
 *  1 - Todo Cheque � vinculado a uma �nica conta, e uma conta pode ter v�rios
 *  cheque vinculados.
 *  
 *  2 - S� existe um cheque por lan�amento e um lan�amento por cheque
 *  
 *  3 - Um cheque ter� tr�s situa��es : N (N�o emitido), B(Baixado) ou C (Cancelado).
 *  
 *  4 - Somente cheques n�o emitidos podem ser usados em lan�amentos.
 *  
 *  5 - Um cheque baixado n�o pode ser cancelado.
 *  
 *  6 - Cheques cancelados ou baixados n�o podem ser excluidos
 *  
 *  7 - Quando um lan�amento for editado e caso exista um cheque vinculado a este, e um
 *  novo n�mero de cheque for informado, o n�mero anteriormente cadastrado dever� ser
 *  desvinculado do lan�amento e ter sua situa��o passada para N novamente.Essa altera��o
 *  s� � possivel com cheques que n�o foram cancelados.
 *  
 *  8 - Nom momento do cadastro, a conta-padr�o � qual um cheque ser� vinculado ser� a conta
 *  que o usu�rio selecionar no menu do sistema.
 * 
 */

public class ChequeRN {

	private ChequeDAO chequeDAO;

	public ChequeRN() {
		this.chequeDAO = DAOFactory.criarChequeDAO();
	}

	public void salvar(Cheque cheque) {
		this.chequeDAO.salvar(cheque);
	}

	public int salvarSequencia(Conta conta, Integer chequeInicial, Integer chequeFinal) { //1*
		Cheque cheque = null;
		ChequeId chequeId = null;
		int contaTotal = 0;
		for (int i = chequeInicial; i <= chequeFinal; i++) {
			chequeId = new ChequeId(conta.getConta(), i);
			cheque = this.carregar(chequeId);
			if (cheque == null) {
				cheque = new Cheque();
				cheque.setChequeId(chequeId);
				cheque.setSituacao(Cheque.SITUACAO_CHEQUE_NAO_EMITIDO);
				cheque.setDataCadastro(new Date());
				this.salvar(cheque);
				contaTotal++;
			}
		}
		return contaTotal;
	}

	public void excluir(Cheque cheque) throws RNException { //2*
		if (cheque.getSituacao() == Cheque.SITUACAO_CHEQUE_NAO_EMITIDO) {
			this.chequeDAO.excluir(cheque);
		} else {
			throw new RNException("N�o � poss�vel excluir cheque, status n�o permitido para opera��o.");
		}
	}

	public Cheque carregar(ChequeId chequeId) { //3*
		return this.chequeDAO.carregar(chequeId);
	}

	public List<Cheque> listar(Conta conta) {
		return this.chequeDAO.listar(conta);
	}

	public void cancelarCheque(Cheque cheque) throws RNException { //4*
		if (cheque.getSituacao() == Cheque.SITUACAO_CHEQUE_NAO_EMITIDO
				|| cheque.getSituacao() == Cheque.SITUACAO_CHEQUE_CANCELADO) {
			cheque.setSituacao(Cheque.SITUACAO_CHEQUE_CANCELADO);
			this.chequeDAO.salvar(cheque);
		} else {
			throw new RNException("N�o � poss�vel cancelar cheque, status n�o permitido para opera��o.");
		}
	}

	public void baixarCheque(ChequeId chequeId, Lancamento lancamento) {
		Cheque cheque = this.carregar(chequeId);
		if (cheque != null) {
			cheque.setSituacao(Cheque.SITUACAO_CHEQUE_BAIXADO);
			cheque.setLancamento(lancamento);
			this.chequeDAO.salvar(cheque);
		}
	}

	public void desvinculaLancamento(Conta conta, Integer numeroCheque) throws RNException { //5*
		ChequeId chequeId = new ChequeId(conta.getConta(), numeroCheque);
		Cheque cheque = this.carregar(chequeId);
		if (cheque == null) {
			return;
		}
		if (cheque.getSituacao() == Cheque.SITUACAO_CHEQUE_CANCELADO) {
			throw new RNException("N�o � poss�vel usar cheque cancelado.");
		} else {
			cheque.setSituacao(Cheque.SITUACAO_CHEQUE_NAO_EMITIDO);
			cheque.setLancamento(null);
			this.salvar(cheque);
		}
	}
}


/*
 * 1 - Em o m�todo Salvar que ser� vinculado posteriormente na tela passar� antes pelo m�todo salvarSequencia,pois ser� informada
 * uma sequ�ncia de cheques.Observe tamb�m que apenas cheques que ainda n�o estejam cadastrados ser�o salvos, para que s� ent�o 
 * o m�todo salvar padr�o seja chamado.
 * 
 * 2 - No m�todo excluir s� ser�o excluidos cheques que n�o foram emitidos.
 * 
 * 3 - No m�todo carregar um cheque � carregado confomre sua chave,no caso, a classe ChequeId.
 * 
 * 4 - S� ser�o cancelados cheques com a situa��o de n�o baixados.
 * 
 * 5 - Esse m�todo desvincula um cheque de um lan�amento caso um lan�amento tenha sido editado e um novo cheque tenha sido informado 
 * para ele.
 * 
 * A vantagem de desenvolver em camadas no qual as regras de neg�cio referentes ao cadadstro do cheque ficam sem um unico ponto.
 * 
 */
