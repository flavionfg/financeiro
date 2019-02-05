package br.com.javaparaweb.financeiro.cheque;

import java.util.Date;
import java.util.List;

import br.com.javaparaweb.financeiro.conta.Conta;
import br.com.javaparaweb.financeiro.lancamento.Lancamento;
import br.com.javaparaweb.financeiro.util.DAOFactory;
import br.com.javaparaweb.financeiro.util.RNException;

/*
 *  É nessa classe que teremos as regras de negócio pertinentes ao cheque.
 *  
 *             Regras de negocio para cadastro de cheque:
 *  
 *  1 - Todo Cheque é vinculado a uma única conta, e uma conta pode ter vários
 *  cheque vinculados.
 *  
 *  2 - Só existe um cheque por lançamento e um lançamento por cheque
 *  
 *  3 - Um cheque terá três situações : N (Não emitido), B(Baixado) ou C (Cancelado).
 *  
 *  4 - Somente cheques não emitidos podem ser usados em lançamentos.
 *  
 *  5 - Um cheque baixado não pode ser cancelado.
 *  
 *  6 - Cheques cancelados ou baixados não podem ser excluidos
 *  
 *  7 - Quando um lançamento for editado e caso exista um cheque vinculado a este, e um
 *  novo número de cheque for informado, o número anteriormente cadastrado deverá ser
 *  desvinculado do lançamento e ter sua situação passada para N novamente.Essa alteração
 *  só é possivel com cheques que não foram cancelados.
 *  
 *  8 - Nom momento do cadastro, a conta-padrão à qual um cheque será vinculado será a conta
 *  que o usuário selecionar no menu do sistema.
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
			throw new RNException("Não é possível excluir cheque, status não permitido para operação.");
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
			throw new RNException("Não é possível cancelar cheque, status não permitido para operação.");
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
			throw new RNException("Não é possível usar cheque cancelado.");
		} else {
			cheque.setSituacao(Cheque.SITUACAO_CHEQUE_NAO_EMITIDO);
			cheque.setLancamento(null);
			this.salvar(cheque);
		}
	}
}


/*
 * 1 - Em o método Salvar que será vinculado posteriormente na tela passará antes pelo método salvarSequencia,pois será informada
 * uma sequência de cheques.Observe também que apenas cheques que ainda não estejam cadastrados serão salvos, para que só então 
 * o método salvar padrão seja chamado.
 * 
 * 2 - No método excluir só serão excluidos cheques que não foram emitidos.
 * 
 * 3 - No método carregar um cheque é carregado confomre sua chave,no caso, a classe ChequeId.
 * 
 * 4 - Só serão cancelados cheques com a situação de não baixados.
 * 
 * 5 - Esse método desvincula um cheque de um lançamento caso um lançamento tenha sido editado e um novo cheque tenha sido informado 
 * para ele.
 * 
 * A vantagem de desenvolver em camadas no qual as regras de negócio referentes ao cadadstro do cheque ficam sem um unico ponto.
 * 
 */
