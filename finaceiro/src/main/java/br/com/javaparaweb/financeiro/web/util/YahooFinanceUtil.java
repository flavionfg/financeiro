package br.com.javaparaweb.financeiro.web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import br.com.javaparaweb.financeiro.bolsa.acao.Acao;
import br.com.javaparaweb.financeiro.util.UtilException;

public class YahooFinanceUtil {
	public static final char ORIGEM_BOVESPA = 'B'; //1*

	public static final String SUFIXO_ACAO_BOVESPA = ".SA";
	public static final String SIGLA_BOVESPA = "^BVSP";

	public static final int INDICE_SIGLA_ACAO = 0; //2*
	public static final int INDICE_ULTIMO_PRECO_DIA_ACAO = 1;
	public static final int INDICE_DATA_NEGOCIACAO_ACAO = 2;
	public static final int INDICE_HORA_NEGOCIACAO_ACAO = 3;
	public static final int INDICE_VARIACAO_DIA_ACAO = 4;
	public static final int INDICE_PRECO_ABERTURA_DIA_ACAO = 5;
	public static final int INDICE_MAIOR_PRECO_DIA_ACAO = 6;
	public static final int INDICE_MENOR_PRECO_DIA_ACAO = 7;
	public static final int INDICE_VOLUME_NEGOCIADO_DIA_ACAO = 8;

	public static String getSiglaLink(Acao acao) {
		if (acao == null || acao.getSigla() == null) {
			return SIGLA_BOVESPA;
		} else if (acao.getOrigem() == ORIGEM_BOVESPA) {
			return acao.getSigla() + SUFIXO_ACAO_BOVESPA;
		}
		return acao.getSigla();
	}

	public static String getInfoCotacao(int indiceInformacao, Acao acao) throws UtilException {
		String sigla = getSiglaLink(acao);
		try {
			String endereco = "http://download.finance.yahoo.com/d/quotes.csv?s=" + sigla + "&f=sl1d1t1c1ohgv&e=.csv";
			URL url = new URL(endereco); //3*
			URLConnection conexao = url.openConnection(); //4*
			InputStreamReader conteudo = new InputStreamReader(conexao.getInputStream());
			BufferedReader arquivo = new BufferedReader(conteudo);

			String linha = null;
			String[] informacoesCotacao = null;
			if ((linha = arquivo.readLine()) != null) {
				arquivo.close(); //5*
				conteudo.close();
				linha = linha.replace("\"", ""); //6*
				informacoesCotacao = linha.split("[;,]"); //7*
				String valor = informacoesCotacao[indiceInformacao]; //8*
				if (!"N/A".equals(valor)) { //9*
					return valor;
				}
			}
			return "0"; //10*
		} catch (IOException e) {
			throw new UtilException("N�o foi poss�vel recuperar cota��o. Erro: " + e.getMessage());
		}
	}
}

/* 1 - Detalhamos a origem de uma a��o, no caso,com base em dois mercados:Bovespa e Mundial. Essa Informa��o � importante 
 * por que a partir dela � que indetificamos o local dentro do portal Yahoo que usuaremos para buscar a informa��o.
 * 
 * 2 - Ja que definimos uma sequ�ncia de constantes da classe do tipo num�rica de 0 a 8, onde cada uma, pela nome, identifica
 * o tipo de informa��o que estamos querendo recuperar da a��o.
 * 
 * 3 - Dentro do m�todo retornaCota�ao, temos a montagem da URL para buscar as informa��es de determinada a��o.Um exemplo do link
 * que poderia ser montado ali � http://download.finance.yahoo.com/d/quotes.csv?s=PETR4.SA&f=sl1d1t1ohgv&e=.csv. Se o leitor digitar
 * esse endere�o diretamente no navegador ver� uma planilha com as informa��es da a��o em quest�o. O importante � saber que parte
 * dele � montado com base na origem da a��o e em seu nome.
 * 
 * 4 - Com base na URL montada anteriormente,Abrimos uma conex�o com site para buscarmos o conte�do que queremos,que,nesse caso, �
 * um arquivo com extensao .csv. Ocorrendo tudo bem,armazenamos o conteudo e come�amos a prepar�-lo para leitura.
 * 
 * A informa��o que o Yahoo Finance retorna � um valor padr�o CSV,ou seja uma sequencia de valores separados por, (virgula) ou
 * ; (ponto-e-virgula). Exemplo.
 * 
 * PETR4.SA,12.82, ""/4/29/2015","5:08pm" + 0.04,12.93,12.53,35564900
 * 
 * 6 - Repare que alguns valores envoltos em ""(aspas), por isso fazemos o replace.
 * 
 * 7 - Realizaremos um .Split() nos caracteres, e ; para contemplar esse doiis formatos possiveis,gerando assim um array com
 * todas as informa��es obtidas do Yahoo Finance.
 * 
 * 8 - Obtivemos do array a informa��o que nos interresa agora, que neste caso est� posicionada no indice 1,definido pela constante.
 * 
 * 9 - Testamos se o valor obtido � v�lido (ou seja,diferente de N/A), para ent�o retornar a informa��o;caso seja invalido.
 * 
 * 10 - Ser� devolvido 0 (zero).
 * 
 * 
 */ 
