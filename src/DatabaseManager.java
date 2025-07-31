package main;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JOptionPane;

import piece.Piece;
import piece.Queen; 
import piece.Rook;
import piece.Bishop;
import piece.Knight;
import piece.Pawn;
import piece.King;

public class DatabaseManager {
	
	private static String URL;
	private static String USER;
	private static String PASSWORD;

	static {
	    try {
	        Properties props = new Properties();
	        InputStream fis = DatabaseManager.class.getClassLoader().getResourceAsStream("db_config.properties");
	        if (fis == null) {
	            throw new FileNotFoundException("db_config.properties not found in classpath!");
	        }
	        props.load(fis);
	        URL = props.getProperty("db.url");
	        USER = props.getProperty("db.username");
	        PASSWORD = props.getProperty("db.password");
	    } catch (IOException e) {
	        JOptionPane.showMessageDialog(null, "Failed to load DB config file.", "Error", JOptionPane.ERROR_MESSAGE);
	        e.printStackTrace();
	    }
	}

    
    private static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    public static void saveGame(ArrayList<Piece> pieces, int currentColor, int whiteTimeLeft, int blackTimeLeft, boolean useTimer) {
        String gameName = JOptionPane.showInputDialog(null, "Enter game name to save:", "Save Game", JOptionPane.QUESTION_MESSAGE);
        if (gameName == null || gameName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Game name cannot be empty.");
            return;
        }

        try (Connection conn = getConnection()) {
        	
            // Check if the game name already exists or not
            String checkSQL = "SELECT 1 FROM game_state WHERE game_name = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setString(1, gameName);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Game name already exists. Choose a different name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            conn.setAutoCommit(false);

            // Insert data
            String insertSQL = "INSERT INTO game_state (game_name, color_turn, piece_type, piece_color, piece_row, piece_col, white_time, black_time, use_timer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(insertSQL);

            for (Piece p : pieces) {
                ps.setString(1, gameName);
                ps.setString(2, currentColor == 0 ? "WHITE" : "BLACK");
                ps.setString(3, p.type.toString());
                ps.setInt(4, p.color);
                ps.setInt(5, p.row);
                ps.setInt(6, p.col);
                ps.setInt(7, whiteTimeLeft);
                ps.setInt(8, blackTimeLeft);
                ps.setBoolean(9, useTimer);
                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();

            JOptionPane.showMessageDialog(null, "Game saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to save game.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static ArrayList<Piece> loadGame(int[] currentColorRef, boolean useTimer) {
        String gameName = JOptionPane.showInputDialog(null, "Enter game name to load:", "Load Game", JOptionPane.QUESTION_MESSAGE);
        if (gameName == null || gameName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Game name cannot be empty.");
            return new ArrayList<>();
        }

        ArrayList<Piece> loadedPieces = new ArrayList<>();
        String query = "SELECT * FROM game_state WHERE game_name = ?";

        int whiteTime = 0;
        int blackTime = 0;
        boolean firstRow = true;
        boolean dbTimer = false;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, gameName);
            ResultSet rs = ps.executeQuery();

            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "Game not found with name: " + gameName);
                return loadedPieces;
            }

            while (rs.next()) {
                if (firstRow) {
                    String colorTurn = rs.getString("color_turn");
                    dbTimer = rs.getBoolean("use_timer");

                    if (dbTimer != useTimer) {
                        JOptionPane.showMessageDialog(null, "This game was saved with " + (dbTimer ? "TIMER ON" : "TIMER OFF") + ". Please start game with same mode.", "Error", JOptionPane.ERROR_MESSAGE);
                        return new ArrayList<>();
                    }

                    currentColorRef[0] = colorTurn.equals("WHITE") ? 0 : 1;
                    whiteTime = rs.getInt("white_time");
                    blackTime = rs.getInt("black_time");
                    firstRow = false;
                }

                String type = rs.getString("piece_type");
                int color = rs.getInt("piece_color");
                int row = rs.getInt("piece_row");
                int col = rs.getInt("piece_col");

                Piece p = switch (type) {
                    case "PAWN" -> new Pawn(color, row, col);
                    case "ROOK" -> new Rook(color, row, col);
                    case "KNIGHT" -> new Knight(color, row, col);
                    case "BISHOP" -> new Bishop(color, row, col);
                    case "QUEEN" -> new Queen(color, row, col);
                    case "KING" -> new King(color, row, col);
                    default -> null;
                };
                if (p != null) loadedPieces.add(p);
            }

            if (!loadedPieces.isEmpty()) {
                GamePanel.setTimerFromDB(whiteTime, blackTime);
                JOptionPane.showMessageDialog(null, "Game loaded successfully!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load game.");
        }

        return loadedPieces;
    }

}
