package com.borrowbook.duyanh.utils;

import com.borrowbook.duyanh.entity.Category;
import com.borrowbook.duyanh.entity.InformationOfUser;
import com.borrowbook.duyanh.entity.Role;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.InformationOfUserRepository;
import com.borrowbook.duyanh.repository.RoleRepository;
import com.borrowbook.duyanh.repository.UserRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.borrowbook.duyanh.utils.Constants.*;

@Component
public class ExportUsersExcel {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InformationOfUserRepository informationOfUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${defaultPassword}")
    String defaultPassword;

    public ExportUsersExcel() {
    }

    public void generateExcel(HttpServletResponse response) throws IOException {
        List<User> usersList = userRepository.findAll();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("User list");
        XSSFRow row = sheet.createRow(0);

        row.createCell(0).setCellValue("User name");
        row.createCell(1).setCellValue("Role name");
        row.createCell(2).setCellValue("Email");
        row.createCell(3).setCellValue("Phone number");
        row.createCell(4).setCellValue("Dob");
        row.createCell(5).setCellValue("Status");

        int dataRowIndex = 1;
        for(User u: usersList) {
            XSSFRow dataRow = sheet.createRow(dataRowIndex);
            dataRow.createCell(0).setCellValue(u.getUsername());
            dataRow.createCell(1).setCellValue(u.getRole().getRoleName());
            InformationOfUser informationOfUser = informationOfUserRepository.findById(u.getId()).orElseThrow(
                    () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage())
            );
            dataRow.createCell(2).setCellValue(informationOfUser.getEmail());
            dataRow.createCell(3).setCellValue(informationOfUser.getPhoneNumber());
            dataRow.createCell(4).setCellValue(informationOfUser.getDob());
            dataRow.createCell(5).setCellValue(u.getStatus());
            dataRowIndex++;
        }

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void saveExcelData(String filePath) {
        //List<Category> list = new ArrayList<>();

       // BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
        try {
            FileInputStream fis = new FileInputStream(filePath);

            Workbook workbook;
            if(filePath.endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else if(filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else {
                throw new IllegalArgumentException("Unsupported file format");
            }
            Sheet sheet = workbook.getSheetAt(0);
            for(Row r: sheet) {
                if(r.getRowNum() == 0) {
                    continue;
                }
                User user = new User();
                InformationOfUser informationOfUser = new InformationOfUser();
                if (r.getCell(0).getCellType() == CellType.STRING) {
                    String username = r.getCell(0).getStringCellValue();
                    if(checkUsername(username,r) )
                    user.setUsername(username);
                }

                if(r.getCell(1).getCellType() == CellType.STRING) {
                    String password = r.getCell(1).getStringCellValue();
                    if (checkPassword(password,r))
                        //user.setPassword(bCryptPasswordEncoder.encode(password));
                        user.setPassword(password);
                } else {
                    user.setPassword(defaultPassword);
                    //user.setPassword(bCryptPasswordEncoder.encode(defaultPassword));
                }
                if (r.getCell(2).getCellType() == CellType.STRING) {
                    String phoneNumber = r.getCell(2).getStringCellValue();
                    if(checkPhoneNumber(phoneNumber,r)) {
                        informationOfUser.setPhoneNumber(phoneNumber);
                    }
                }
                if (r.getCell(3).getCellType() == CellType.STRING) {
                    String email = r.getCell(3).getStringCellValue();
                    if(checkEmail(email,r)) {
                        informationOfUser.setEmail(email);
                    }
                }
                if (r.getCell(4).getCellType() == CellType.STRING) {
                    String dob = r.getCell(4).getStringCellValue();
                    if(validateDateOfBirth(dob)!= null) {
                        informationOfUser.setDob( validateDateOfBirth(dob));
                    }
                }
                if (r.getCell(5).getCellType() == CellType.STRING) {
                    String status = r.getCell(5).getStringCellValue();
                    if(!getStatusUser(status).isEmpty()) user.setStatus(getStatusUser(status));
                }
                if (r.getCell(6).getCellType() == CellType.STRING) {
                    String roleName = r.getCell(6).getStringCellValue();
                    if(getRoleByRoleName(roleName)!= null) {
                        user.setRole(getRoleByRoleName(roleName));
                    }
                }
                User userSaved = userRepository.save(user);
                informationOfUser.setUserId(userSaved.getId());
                informationOfUserRepository.save(informationOfUser);

                //list.add(category);
            }
            //categoryRepository.saveAll(list);

        } catch (Exception e) {
             e.printStackTrace();
        }
    }

    private boolean checkUsername(String username, Row r) throws Exception {
        if(username.length() >= 6 && username.length() <= 30 && username.matches("^[a-zA-Z0-9]+$")) {
            return true;
        } else {
            throw new Exception("Dòng " + (r.getRowNum() + 1) + " Cột 1: Username không hợp lệ.");
        }
    }

    private boolean checkPassword(String pa, Row r)throws Exception {
        if(pa.length() >= 6 && pa.length() <= 20) {
            return true;
        } else throw new Exception("Dòng " + (r.getRowNum() + 1) + " Cột 2: Password không hợp lệ, phải từ 6-20 ký tự.");
    }

    private  boolean checkPhoneNumber(String pa, Row r)throws Exception {
        if(pa.matches("^(?:\\+84|0)(?:[1-9]\\d{8})$")) {
            return true;
        } else throw new Exception("Dòng " + (r.getRowNum() + 1) + ", Cột 3: Số điện thoại không hợp lệ.");
    }

    private boolean checkEmail(String pa, Row r)throws Exception {
        if(pa.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return  true;
        } else throw new Exception("Dòng " + (r.getRowNum() + 1) + ", Cột 4: Email không hợp lệ.");
    }
    private Date validateDateOfBirth(String dateOfBirth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        try {
            LocalDate localDate = LocalDate.parse(dateOfBirth, formatter);
            return (Date) Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()); // Chuyển LocalDate thành Date
        } catch (DateTimeParseException e) {
            return null; // Ngày sinh không hợp lệ
        }
    }

    private String getStatusUser(String status) {
        if (STATUS_ACTIVE.equalsIgnoreCase(status) ||
                STATUS_BANNED.equalsIgnoreCase(status) ||
                STATUS_DELETED.equalsIgnoreCase(status)) {
            return status.toUpperCase();
        } else {
            throw new IllegalArgumentException("Invalid user status: " + status); // Trạng thái không hợp lệ
        }
    }

    private Role getRoleByRoleName(String roleName) {
        if(ROLE_ADMIN.equalsIgnoreCase(roleName) ||
                ROLE_USER.equalsIgnoreCase(roleName) ||
                ROLE_LIBRARIAN.equalsIgnoreCase(roleName) ||
                ROLE_MANAGER.equalsIgnoreCase(roleName)) {
            //Role role = roleRepository.getRolesByRoleName(roleName);
            Role role = roleRepository.findByRoleName(roleName);
            return role;
        } else {
            throw new IllegalArgumentException("Invalid user status: " + roleName); // Trạng thái không hợp lệ
        }
    }


//    public static LocalDate validateDateOfBirth(String dateOfBirth) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
//        try {
//            LocalDate date = LocalDate.parse(dateOfBirth, formatter);
//            return date; // Trả về ngày sinh hợp lệ
//        } catch (DateTimeParseException e) {
//            return null; // Ngày sinh không hợp lệ
//        }
//    }
}
