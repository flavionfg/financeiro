package br.com.javaparaweb.financeiro.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import br.com.javaparaweb.financeiro.categoria.Categoria;
import br.com.javaparaweb.financeiro.categoria.CategoriaRN;

/*
 * Um conversor no JavaServer Faces é uma classe responsavel por transformar um objeto em textoo e um texto em objeto,a operação de transformar objeto 
 * em texto sempre será realizada quando algum objeto de valor da classe Bean(Double,Integer,Date) tiver que ser representado na pagina HTML resultante.
 * A operação de transformar um texto em objeto sempre sera realizada quando uma informação vinda do navegador ou de um formulario enviado(portanto,
 * em texto) tiver que se transformar em objeto para a classe Bean.
 * 
 * Essa Classe foi criada por que o componente SELECT do HTML (caica de seleção) que exibe a categoria pai trabalhe diretamente para uma propriedade 
 * do tipo categoria(categoriaBean.editada.pai), para isso acontecer vamos criar um novo conversor.
 */

@FacesConverter(forClass = Categoria.class) //1*
public class CategoriaConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) { //2*
		if (value != null && value.trim().length() > 0) {
			Integer codigo = Integer.valueOf(value); //3*
			try {
				CategoriaRN categoriaRN = new CategoriaRN();
				return categoriaRN.carregar(codigo); //4*
			} catch (Exception e) {
				throw new ConverterException(
						"Não foi possível encontrar a categoria de código " + value + ". " + e.getMessage());
			}
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) { //5*
		if (value != null) {
			Categoria categoria = (Categoria) value; //6*
			return categoria.getCodigo().toString(); //7*
		}
		return "";
	}
}


/*
 * 1 - A regra de funcionamento do conversos é determinada pela annotation @FacesConverter, que pode ser efetuada de duas 
 * maneiras: forClass ou value.
 * 
 * A configuração forClass permite definir que o conversor seja usado sempre que uma informação vinda da web tiver que ser
 * atribuida a uma propriedade do tipo definitivo em forClass,para a CategoriaConverter configuramos que sempre que a 
 * propriedade de destino ou origem for do tipo br.com.javaparaweb.financeiro.categoria.Categoria esse conversor deve ser utilizado.
 * 
 * a interface Converter é obrigatória.
 * 
 * 2 - será executada quando alguma informação ( parametro ou campo de formulario) que esta em formato texto por ter vindo do nageador
 * for atribuida para uma propriedade da classe Bean.
 * 
 * 3 - recebemos o parametro String value, que foi a informação digitada ou selecionada do usuario em um objeto Categoria. para isso,
 * primeiro transformamos o value em integer codigo.
 * 
 * 4 - em pose do codigo, usamos a classe CategoriaRN para buscar o objeto completo da categoria.
 * 
 * 5 - esse método é utilizado quando alguma informação da classe Bean for exibida em tela.No caso do converson CategoriaConverter
 * isso acontecerpa quando o JSF for gerar as tags <option> do <select> da categoria pai.
 * 
 * 6 - nesse caso,primeiro fazemos um cast do value recebido para o tipo Categoria.
 * 
 * 7 - retornamos o código da categoria em formato de texto.
 * 
 * 
 */
