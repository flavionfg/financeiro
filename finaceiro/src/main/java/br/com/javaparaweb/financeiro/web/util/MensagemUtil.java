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


/* 1 - Criamos um m�todo est�tico na classe que retorna a mensagem conforme a chave que � passada no 
 * par�metro do m�todo. Se passarmos a chave cheque_erro_sequencia, ser� carregada a mensagem que se 
 * encontra no arquivo de tradu��o do idioma atual, com base no contexto atual da aplica��o.
 * 
 * 2 - Vemos o mesmo m�todo anteriror, s� que agora podendo passar par�metros para a mensagem 
 * para eles apare�am dentro do seu texto na exibi��o em tela. � o caso da mensagem cheque_erro_inicial_final,
 * que foi definida em nossos arquivos de tradu��o. Isso tamb�m justifica o uso de classe MessageFormat nesse
 * m�todo.
 * 
 * O par�metro Object...parametros � um recurso recente do Java e indica que na sua posi��o poder�o ser
 * passados quantos par�metros forem necessarios.
 * 
 * MensagemUtil.getMensagem("mensagem","123","456");
 * MensagemUtil.getMensagem("mensagem","123","456","789");
 * MensagemUtil.getMensagem("mensagem","123","456",new Integer(123), new Date());
 * 
 * Observe que mesmo o m�todo apenas dois par�metros � possivel passar quantos forem necessarios.
 *
 */ 