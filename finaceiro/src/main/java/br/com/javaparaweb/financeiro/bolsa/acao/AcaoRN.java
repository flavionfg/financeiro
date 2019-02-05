package br.com.javaparaweb.financeiro.bolsa.acao;

import java.util.ArrayList;
import java.util.List;

import br.com.javaparaweb.financeiro.usuario.Usuario;
import br.com.javaparaweb.financeiro.util.DAOFactory;
import br.com.javaparaweb.financeiro.util.UtilException;
import br.com.javaparaweb.financeiro.web.util.YahooFinanceUtil;

// Esta classe tem alguns m�todos especificos  para tratar dados retornados pelo portal do Yahoo.
public class AcaoRN {
	private AcaoDAO acaoDAO;

	public AcaoRN() {
		this.acaoDAO = DAOFactory.criarAcaoDAO();
	}

	public void salvar(Acao acao) {
		this.acaoDAO.salvar(acao);
	}

	public void excluir(Acao acao) {
		this.acaoDAO.excluir(acao);
	}

	public List<Acao> listar(Usuario usuario) {
		return this.acaoDAO.listar(usuario);
	}

	public List<AcaoVirtual> listarAcaoVirtual(Usuario usuario) throws UtilException { //1*
		List<AcaoVirtual> listaAcaoVirtual = new ArrayList<AcaoVirtual>();
		AcaoVirtual acaoVirtual = null;
		String cotacao = null;
		float ultimoPreco = 0.0f;
		for (Acao acao : this.listar(usuario)) {
			acaoVirtual = new AcaoVirtual();
			acaoVirtual.setAcao(acao);
			cotacao = YahooFinanceUtil.getInfoCotacao(YahooFinanceUtil.INDICE_ULTIMO_PRECO_DIA_ACAO, acao); //2*
			if (cotacao != null) {
				ultimoPreco = new Float(cotacao).floatValue(); //3*
				acaoVirtual.setUltimoPreco(ultimoPreco);
				acaoVirtual.setTotal(ultimoPreco * acao.getQuantidade());
				listaAcaoVirtual.add(acaoVirtual);
			}
		}
		return listaAcaoVirtual;
	}
}


/*
 * 1 - M�todo listarAcaoVirtual, no qual consultamos as a��es cadastradas no banco, para ent�o construir uma 
 * lista de "A��es virtuais" e, em seguida, tamb�m buscar as �ltimas cota��es disponibilizadas para determinada
 * a��o com ajuda da classe YahooFinanceUtil. Nesse m�todo tamb�m calculamos o total do capital com base no �ltimo
 * pre�o da a��o.
 * 
 * 2 - O m�todo getInfoCotacao retorna determinada informa��o de uma a��o. Ele recebe como parametro a a��o da qual
 *  queremos saber determinado dado e o indice da informa��o.Esse indice � determinado pelas constantes INDICE_* 
 *  existentes dentro da classe YahooFinanceUtil.
 *  
 * 3 - A variavel cotacao � uma string nessa linha,convertemos para float, o YahooFinanceUtil fornece varios tipo de
 * informa��es diferentes.Assim,dependendo do indice que voc� passou,poder� vir uma informa��o que n�o � um numero,
 * mas uma data,por exemplo.
 */
