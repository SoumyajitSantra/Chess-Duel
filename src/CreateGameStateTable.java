package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateGameStateTable {
    public static void main(String[] args) {
        // Aiven DB
        String url = "jdbc:mysql://chessdb-chessgame.i.aivencloud.com:24934/defaultdb?verifyServerCertificate=false&useSSL=true&requireSSL=true";
        String user = "avnadmin";
        String pass = "AVNS_iPeqVGQ3mzZVSQA-caD";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             Statement st = con.createStatement()) {
        	
        	String sql = """
        		    CREATE TABLE IF NOT EXISTS game_state (
        		        id INT AUTO_INCREMENT PRIMARY KEY,
        		        game_name VARCHAR(100) NOT NULL,
        		        color_turn VARCHAR(10),
        		        piece_type VARCHAR(10),
        		        piece_color INT,
        		        piece_row INT,
        		        piece_col INT,
        		        white_time INT,
        		        black_time INT,
        		        use_timer BOOLEAN NOT NULL,
        		        saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        		    );
        		""";


            st.executeUpdate(sql);
            System.out.println("Table created or already exists in Aiven DB.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
