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
			throw new UtilException("Não foi possível recuperar cotação. Erro: " + e.getMessage());
		}
	}
}

/* 1 - Detalhamos a origem de uma ação, no caso,com base em dois mercados:Bovespa e Mundial. Essa Informação é importante 
 * por que a partir dela é que indetificamos o local dentro do portal Yahoo que usuaremos para buscar a informação.
 * 
 * 2 - Ja que definimos uma sequência de constantes da classe do tipo numérica de 0 a 8, onde cada uma, pela nome, identifica
 * o tipo de informação que estamos querendo recuperar da ação.
 * 
 * 3 - Dentro do método retornaCotaçao, temos a montagem da URL para buscar as informações de determinada ação.Um exemplo do link
 * que poderia ser montado ali é http://download.finance.yahoo.com/d/quotes.csv?s=PETR4.SA&f=sl1d1t1ohgv&e=.csv. Se o leitor digitar
 * esse endereço diretamente no navegador verá uma planilha com as informações da ação em questão. O importante é saber que parte
 * dele é montado com base na origem da ação e em seu nome.
 * 
 * 4 - Com base na URL montada anteriormente,Abrimos uma conexão com site para buscarmos o conteúdo que queremos,que,nesse caso, é
 * um arquivo com extensao .csv. Ocorrendo tudo bem,armazenamos o conteudo e começamos a prepará-lo para leitura.
 * 
 * A informação que o Yahoo Finance retorna é um valor padrão CSV,ou seja uma sequencia de valores separados por, (virgula) ou
 * ; (ponto-e-virgula). Exemplo.
 * 
 * PETR4.SA,12.82, ""/4/29/2015","5:08pm" + 0.04,12.93,12.53,35564900
 * 
 * 6 - Repare que alguns valores envoltos em ""(aspas), por isso fazemos o replace.
 * 
 * 7 - Realizaremos um .Split() nos caracteres, e ; para contemplar esse doiis formatos possiveis,gerando assim um array com
 * todas as informaçôes obtidas do Yahoo Finance.
 * 
 * 8 - Obtivemos do array a informação que nos interresa agora, que neste caso está posicionada no indice 1,definido pela constante.
 * 
 * 9 - Testamos se o valor obtido é válido (ou seja,diferente de N/A), para então retornar a informação;caso seja invalido.
 * 
 * 10 - Será devolvido 0 (zero).
 * 
 * 
 */ 
