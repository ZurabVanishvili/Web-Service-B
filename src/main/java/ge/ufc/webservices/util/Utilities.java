package ge.ufc.webservices.util;

import ge.ufc.webservices.DuplicateFault;
import ge.ufc.webservices.DuplicateFault_Exception;
import ge.ufc.webservices.dao.DatabaseException;
import ge.ufc.webservices.dao.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Utilities {
//    private static Connection connection;
//    public static boolean checkPayment(String pay_id,int user_id,double amount) throws DatabaseException {
//        connection = DatabaseManager.getDatabaseConnection();
//
//        String sql = "select user_id,amount,status from payments where payment_id = ? ";
//        try(PreparedStatement ps = connection.prepareStatement(sql)){
//            ps.setString(1,pay_id);
//            try(ResultSet rs = ps.executeQuery()){
//                int id = rs.getInt("user_id");
//                double payment_amount = rs.getDouble("amount");
//                int status = rs.getInt("status");
//                if (!(user_id==id&&payment_amount==amount)){
//                        throw new DuplicateFault_Exception("Duplicate fault",new DuplicateFault());
//                }else {
//                    if (status==1){
//
//                    }
//                }
//            }
//
//        } catch (SQLException | DuplicateFault_Exception throwables) {
//            throwables.printStackTrace();
//        }
//    }
}
