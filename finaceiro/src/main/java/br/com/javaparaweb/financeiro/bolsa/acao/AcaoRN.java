package br.com.javaparaweb.financeiro.bolsa.acao;

import java.util.ArrayList;
import java.util.List;

import br.com.javaparaweb.financeiro.usuario.Usuario;
import br.com.javaparaweb.financeiro.util.DAOFactory;
import br.com.javaparaweb.financeiro.util.UtilException;
import br.com.javaparaweb.financeiro.web.util.YahooFinanceUtil;

// Esta classe tem alguns métodos especificos  para tratar dados retornados pelo portal do Yahoo.
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
 * 1 - Método listarAcaoVirtual, no qual consultamos as ações cadastradas no banco, para então construir uma 
 * lista de "Ações virtuais" e, em seguida, também buscar as últimas cotações disponibilizadas para determinada
 * ação com ajuda da classe YahooFinanceUtil. Nesse método também calculamos o total do capital com base no último
 * preço da ação.
 * 
 * 2 - O método getInfoCotacao retorna determinada informação de uma ação. Ele recebe como parametro a ação da qual
 *  queremos saber determinado dado e o indice da informação.Esse indice é determinado pelas constantes INDICE_* 
 *  existentes dentro da classe YahooFinanceUtil.
 *  
 * 3 - A variavel cotacao é uma string nessa linha,convertemos para float, o YahooFinanceUtil fornece varios tipo de
 * informações diferentes.Assim,dependendo do indice que você passou,poderá vir uma informação que não é um numero,
 * mas uma data,por exemplo.
 */
