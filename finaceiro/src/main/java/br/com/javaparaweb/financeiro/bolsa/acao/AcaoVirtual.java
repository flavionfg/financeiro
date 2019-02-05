package br.com.javaparaweb.financeiro.bolsa.acao;


/*
 * A classe a��o tem o papel de representar os dados que precisamos persistir, como a quantidade de quotas,e para A�aoVirtual
 * o papel de guardar em tempo de execu��o do sistema a informa��o de quanto valem essas cotas.
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
 * 1 - Observe que a classe armazena tamb�m uma inst�ncia da classe a��o,porque na tela,trabalharemos
 * com a�aoVirtual para mostrar dados,mas precisamos persistir outras informa��es com o uso de a�ao.
 * Os outros dois atributos ultimoPreco e total ser�o calculados e mostrados somente em tempo de
 * execu��o do sistema.
 * 
 */
