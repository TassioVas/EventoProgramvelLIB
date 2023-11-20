package enviarEmailNegarPedido;

import java.math.BigDecimal;
import java.sql.Timestamp;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class EnviaEmail {
	
	Timestamp agora = new Timestamp(System.currentTimeMillis());

	String msg;
	String corpoEmail;

	BigDecimal codFila = null;
	BigDecimal seq;
	
	public void enviarEmail (String email, String nomeUsu, BigDecimal nuNota, String obsLib) {


			JdbcWrapper jdbc = JapeFactory.getEntityFacade().getJdbcWrapper();
			NativeSql nativeSql = new NativeSql(jdbc);

			EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
			jdbc = dwfEntityFacade.getJdbcWrapper();
			JapeSession.SessionHandle hnd = JapeSession.open();
			
			System.out.println("Nome do usuario que enviou :" + nomeUsu);
		
			corpoEmail = "<h2>"+nomeUsu+"</h2>\r\n"
					+ "		<p><span style=\"font-size:14px\"><b>A NOTA DO NÚMERO ÚNICO DE ORIGEM :</b>\r\n"
					+ "		 <span style=\"font-size:16px\"><span style=\"color:#FF0000\"><b>"+nuNota+"</span></span></span></b><span style=\"font-size:14px\">\r\n"
					+ "		 <b> Foi negado o pedido de liberação de Limites, favor analisar se possivel lançar novamente o pedido, ou fazer outra solicitação de liberação.  </b></span><span style=\"font-size:16px\">\r\n"
					+ "			\r\n"
					+ " <span style=\"font-size:16px\"><b>Observação: "+obsLib+"</b></span>";

			char[] corpoEmailchar = corpoEmail.toCharArray();

			try {

				DynamicVO filaMensagemVO = (DynamicVO) dwfEntityFacade.getDefaultValueObjectInstance("MSDFilaMensagem");
				filaMensagemVO.setProperty("CODMSG", null);
				filaMensagemVO.setProperty("DTENTRADA", agora);
				filaMensagemVO.setProperty("STATUS", "Pendente");
				filaMensagemVO.setProperty("CODCON", new BigDecimal(0));
				filaMensagemVO.setProperty("TENTENVIO", new BigDecimal(0));
				filaMensagemVO.setProperty("MENSAGEM", corpoEmailchar);
				filaMensagemVO.setProperty("TIPOENVIO", "E");
				filaMensagemVO.setProperty("MAXTENTENVIO", new BigDecimal(3));
				filaMensagemVO.setProperty("ASSUNTO", "Liberação De Limites");

				filaMensagemVO.setProperty("EMAIL", email);
				//filaMensagemVO.setProperty("EMAIL", "tassio@faepu.org.br");

				// filaMensagemVO.setProperty("CODSMTP", new BigDecimal(2));
				//filaMensagemVO.setProperty("CODUSUREMET", nomeUsu);

				PersistentLocalEntity createFilaMensagem = dwfEntityFacade.createEntity("MSDFilaMensagem",
						(EntityVO) filaMensagemVO);
				filaMensagemVO = (DynamicVO) createFilaMensagem.getValueObject();
				codFila = filaMensagemVO.asBigDecimal("CODFILA");

			} catch (Exception e) {
				e.printStackTrace();
				msg = "Erro na inclusao do item " + e.getMessage();
				System.out.println(msg);
			}
			
			//ResultSet rsSeq = nativeSql.executeQuery("");
			
			try {

				System.out.println("CODFILA na inserção do item" + codFila);
				System.out.println("alteração do cod");

				EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
				DynamicVO dynamicVO1 = (DynamicVO) dwfFacade.getDefaultValueObjectInstance("MSDDestFilaMensagem");

				dynamicVO1.setProperty("CODFILA", codFila);
				//dynamicVO1.setProperty("EMAIL", "wanderson@faepu.org.br");
				dynamicVO1.setProperty("EMAIL", "t.santos.vasconcelos@gmail.com");
				//dynamicVO1.setProperty("SEQUENCIA", seq);
				PersistentLocalEntity createEntity = dwfFacade.createEntity("MSDDestFilaMensagem",
						(EntityVO) dynamicVO1);
				DynamicVO save = (DynamicVO) createEntity.getValueObject();

				System.out.println("CODFILA " + codFila);

			} catch (Exception e) {
				e.printStackTrace();
				msg = "Erro na inclusao do item " + e.getMessage();
				System.out.println(msg);
			}
			JapeSession.close(hnd);
	}
}
