package br.com.javaparaweb.financeiro.bolsa.acao;


/*
 * A classe ação tem o papel de representar os dados que precisamos persistir, como a quantidade de quotas,e para AçaoVirtual
 * o papel de guardar em tempo de execução do sistema a informação de quanto valem essas cotas.
 *
 */
public class AcaoVirtual {
	private Acao acao = new Acao(); //*1
	private float ultimoPreco;
	private float total;

	public Acao getAcao() {
		return acao;
	}

	public void setAcao(Acao acao) {
		this.acao = acao;
	}

	public float getUltimoPreco() {
		return ultimoPreco;
	}

	public void setUltimoPreco(float ultimoPreco) {
		this.ultimoPreco = ultimoPreco;
	}

	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}
	
}

/*
 * 1 - Observe que a classe armazena também uma instância da classe ação,porque na tela,trabalharemos
 * com açaoVirtual para mostrar dados,mas precisamos persistir outras informações com o uso de açao.
 * Os outros dois atributos ultimoPreco e total serão calculados e mostrados somente em tempo de
 * execução do sistema.
 * 
 */
