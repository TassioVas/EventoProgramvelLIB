package enviarEmailNegarPedido;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class Historico {

	Timestamp agora = new Timestamp(System.currentTimeMillis());

	BigDecimal seq;
	String msg;

	public void insereHistorico(String nomeUsu, BigDecimal nuNota) {

		JdbcWrapper JDBC = JapeFactory.getEntityFacade().getJdbcWrapper();
		NativeSql nativeSql = new NativeSql(JDBC);
		SessionHandle hnd = JapeSession.open();
		
		System.out.println("Entrou na inserção de historico");

		try {
			ResultSet rs = nativeSql.executeQuery("SELECT MAX(SEQ)+1 AS SEQ FROM AD_HISTORICOS");

			while (rs.next()) {
				seq = rs.getBigDecimal("SEQ");
				System.out.println(" SEq do historico :" + seq);
			}
			
			String obser = " Liberação de limite negada";
			char[] obs = obser.toCharArray();
			
			try {
				
				JapeWrapper hisDAO = JapeFactory.dao("AD_HISTORICOS");
				DynamicVO savePar = hisDAO.create()
						.set("SEQ", seq)
						.set("NUCHAVE", nuNota)
						.set("TABELA", "NUNOTA")
						.set("USUARIO", nomeUsu)
						.set("DATA", agora)
						.set("ACAO", "LIBERAÇÃO DE LIMITE NEGADA")
						.set("PROCESSO", "LIBERAÇAO DE LIMITES")
						.set("OBS", obs)
						.set("NUNOTA", nuNota).save();
		
			}  catch (Exception e) {
				e.printStackTrace();
				msg = "Erro na inclusao do item " + e.getMessage();
				System.out.println(msg);
			}

		} catch (Exception e) {
			e.printStackTrace();
			msg = "Erro na inclusao do item " + e.getMessage();
			System.out.println(msg);
		}

		JapeSession.close(hnd);
	}
}
