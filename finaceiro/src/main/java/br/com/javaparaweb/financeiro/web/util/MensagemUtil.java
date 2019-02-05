package br.com.javaparaweb.financeiro.web.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public class MensagemUtil {

	public static String getMensagem(String propriedade) { //1*
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
		return bundle.getString(propriedade);
	}

	public static String getMensagem(String propriedade, Object... parametros) { //2*
		String mensagem = getMensagem(propriedade);
		MessageFormat formatter = new MessageFormat(mensagem);
		return formatter.format(parametros);
	}
}


/* 1 - Criamos um método estático na classe que retorna a mensagem conforme a chave que é passada no 
 * parâmetro do método. Se passarmos a chave cheque_erro_sequencia, será carregada a mensagem que se 
 * encontra no arquivo de tradução do idioma atual, com base no contexto atual da aplicação.
 * 
 * 2 - Vemos o mesmo método anteriror, só que agora podendo passar parâmetros para a mensagem 
 * para eles apareçam dentro do seu texto na exibição em tela. é o caso da mensagem cheque_erro_inicial_final,
 * que foi definida em nossos arquivos de tradução. Isso também justifica o uso de classe MessageFormat nesse
 * método.
 * 
 * O parâmetro Object...parametros é um recurso recente do Java e indica que na sua posição poderão ser
 * passados quantos parâmetros forem necessarios.
 * 
 * MensagemUtil.getMensagem("mensagem","123","456");
 * MensagemUtil.getMensagem("mensagem","123","456","789");
 * MensagemUtil.getMensagem("mensagem","123","456",new Integer(123), new Date());
 * 
 * Observe que mesmo o método apenas dois parâmetros é possivel passar quantos forem necessarios.
 *
 */ 