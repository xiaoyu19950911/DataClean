package process;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import properties.Property;

public class GenerateSeoContent {

	private static Connection connection;
	private static String s;
	private static Vector<String> vecExercisesId = new Vector<String>();

	public static void generateExercises() throws SQLException, DocumentException, ClassNotFoundException {
		connect();
		Statement sql = connection.createStatement();// ʵ����Statement����
		ResultSet rs = sql.executeQuery("select Id from smallqjddataservicedbfull.exercises");// ִ��sql���
		while (rs.next()) {
			vecExercisesId.add(rs.getString("Id"));
		}
		
		for (int i = 0; i < vecExercisesId.size(); i++) {
			String t=vecExercisesId.get(i);
			System.out.println(t);
			generateSibgleExercise(t);
		}
		clear();
		disconnect();
	}

	private static void generateSibgleExercise(String exercises_ID) throws SQLException, DocumentException {
		// ��ȡxml
		//connect();
		Statement sql1 = connection.createStatement();// ʵ����Statement����
		ResultSet rs1 = sql1.executeQuery("select Xmldata from smallqjddataservicedbfull.exercises where Id='"+exercises_ID+"'");
		String Xmldata="";
		while(rs1.next()){
			Xmldata=rs1.getString("Xmldata");
		}
		Document document=DocumentHelper.parseText(Xmldata);
		Element rootElement = document.getRootElement();
		s="";
		getSeoContent(rootElement);
		PreparedStatement pre = connection.prepareStatement("update smallqjddataservicedbfull.exercises set SeoContent=? where Id=?");
		pre.setString(1, s);
		pre.setString(2, exercises_ID);
		pre.executeUpdate();
		rs1.close();
		//disconnect();

	}

	private static void getSeoContent(Element node){
		String s1 = node.getName();
		String s2 = node.getTextTrim();
		if (s1.equals("t")) {
			s = s + s2;
		}

		List<Element> listElement = node.elements();
		for (Element e : listElement) {
			getSeoContent(e);
		}
		
	}

	private static void connect() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(
				"jdbc:mysql://" + Property.dbString + ":" + Property.port + "/smallqjddataservicedbfull",
				Property.username, Property.password);
		System.out.println("数据库连接成功");
	}

	private static void disconnect() throws SQLException {
		connection.close();
		System.out.println("数据库已经关闭");
	}

	private static void clear() {
		vecExercisesId.clear();
	}

}
