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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.borrowbook.duyanh.utils.Constants.*;

@Component
public class ExportUsersExcel {

    private static final Logger log = LoggerFactory.getLogger(ExportUsersExcel.class);
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

    public void importExcel(HttpServletResponse response, String filePath) {
        HashMap<String,List<Integer>> emailMap = new HashMap<>();
        List<String> existingEmails = informationOfUserRepository.findAllEmail();
        boolean hasDuplicates = false;
        String fileName = new File(filePath).getName();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                Cell emailCell = row.getCell(3);
                if (emailCell != null && emailCell.getCellType() == CellType.STRING) {
                    String email = emailCell.getStringCellValue();

                    if (emailMap.containsKey(email)) {
                        hasDuplicates = true;

                        List<Integer> duplicatedRows = emailMap.get(email);

                        Cell duplicateInfoCell = row.createCell(7);
                        duplicateInfoCell.setCellValue("Duplicated in rows: " + duplicatedRows.stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(", ")));

                        duplicatedRows.add(row.getRowNum() + 1);
                    } else {
                        List<Integer> rows = new ArrayList<>();
                        rows.add(row.getRowNum() + 1);
                        emailMap.put(email, rows);
                    }
                    if (existingEmails.contains(email)) {
                        hasDuplicates = true;
                        Cell duplicateInfoCell = row.createCell(7);
                        duplicateInfoCell.setCellValue("Duplicated in database");
                    }
                }
            }
            if (hasDuplicates) {
                exportExcelWithDuplicates(response, workbook);
            } else {
                saveExcelData(sheet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportExcelWithDuplicates(HttpServletResponse response, Workbook workbook) throws IOException {
        LocalDateTime now = LocalDateTime.now();

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=duplicatedExcel.xlsx");

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

        System.out.println("File Excel có email trùng lặp đã được xuất ra.");
    }

    private void saveExcelData(Sheet sheet) throws Exception {
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
            Cell cell = r.getCell(1);
            if(cell == null || cell.getCellType() == CellType.BLANK){
                log.warn("Password is null");
                user.setPassword(defaultPassword);
                //user.setPassword(bCryptPasswordEncoder.encode(defaultPassword));
            } else {
                if(r.getCell(1).getCellType() == CellType.STRING) {
                    log.info("Password is not null");
                    String password = r.getCell(1).getStringCellValue();
                    if (checkPassword(password,r))
                        //user.setPassword(bCryptPasswordEncoder.encode(password));
                        user.setPassword(password);
                }
            }
            Cell phoneNumberCell = r.getCell(2);
            if (phoneNumberCell != null) {
                String phoneNumber;
                if (phoneNumberCell.getCellType() == CellType.STRING) {
                    phoneNumber = phoneNumberCell.getStringCellValue();
                } else if (phoneNumberCell.getCellType() == CellType.NUMERIC) {
                    phoneNumber = String.valueOf((long) phoneNumberCell.getNumericCellValue());
                } else {
                    phoneNumber = "";
                }
                if (phoneNumber.startsWith("'")) {
                    phoneNumber = phoneNumber.substring(1);
                }

                log.info("Phone Number: " + phoneNumber);
                if (checkPhoneNumber(phoneNumber, r)) {
                    informationOfUser.setPhoneNumber(phoneNumber);
                }
            } else {
                log.warn("Phone Number Cell is null.");
            }

            // Email
            Cell emailCell = r.getCell(3);
            if (emailCell != null && emailCell.getCellType() == CellType.STRING) {
                String email = emailCell.getStringCellValue();
                if (checkEmail(email, r)) {
                    informationOfUser.setEmail(email);
                }
            }

            // Date of Birth
            Cell dobCell = r.getCell(4);
            if (dobCell != null) {
                if (dobCell.getCellType() == CellType.STRING) {
                    String dob = dobCell.getStringCellValue();
                    log.info(dob);
                    if (validateDateOfBirth(dob) != null) {
                        informationOfUser.setDob(validateDateOfBirth(dob));
                    }
                } else if (dobCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dobCell)) {

                    Date dobDate = dobCell.getDateCellValue();
                    log.info(dobDate.toString());
                    if (dobDate != null) {
                        informationOfUser.setDob(dobDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    }
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
            informationOfUser.setUser(userSaved);
            informationOfUserRepository.save(informationOfUser);
        }
    }

//    public void importExcel(HttpServletResponse response, String filePath) {
//        HashMap<String,List<Integer>> emailMap = new HashMap<>();
//        Set<String> emailSet = new HashSet<>();
//        List<String> existingEmails = informationOfUserRepository.findAllEmails();
//        //List<Integer> listCount;
//        int count = 1;
//        boolean hasDuplicates = false;
//
//        try (FileInputStream fis = new FileInputStream(filePath)) {
//            Workbook workbook = new XSSFWorkbook(fis);
//            Sheet sheet = workbook.getSheetAt(0);
//
//            for (Row row : sheet) {
//                if (row.getRowNum() == 0) {
//                    continue;
//                }
//
//                Cell emailCell = row.getCell(3);
//                if (emailCell != null && emailCell.getCellType() == CellType.STRING) {
//                    String email = emailCell.getStringCellValue();
//
//                    if (emailMap.containsKey(email)) {
//                        hasDuplicates = true;
//
//                        // Lấy danh sách các dòng mà email đã xuất hiện
//                        List<Integer> duplicatedRows = emailMap.get(email);
//
//                        // Ghi thông tin trùng lặp với các dòng đã xuất hiện
//                        Cell duplicateInfoCell = row.createCell(7);
//                        duplicateInfoCell.setCellValue("Duplicated in rows: " + duplicatedRows.stream()
//                                .map(Object::toString)
//                                .collect(Collectors.joining(", ")));
//
//                        // Thêm dòng hiện tại vào danh sách trùng lặp
//                        duplicatedRows.add(row.getRowNum() + 1); // +1 để hiển thị số dòng cho người dùng
//                    } else {
//                        // Nếu chưa có email này, thêm email vào map và set
//                        List<Integer> rows = new ArrayList<>();
//                        rows.add(row.getRowNum() + 1); // Thêm dòng đầu tiên vào danh sách
//                        emailMap.put(email, rows);
//                    }
//
//                    if (!emailSet.add(email)) {
//                        hasDuplicates = true;
//                        //int duplicatedRow = emailMap.get(email);
//                        Cell duplicateInfoCell = row.createCell(7);
//                        duplicateInfoCell.setCellValue("Duplicated in row: " + duplicatedRow);
//                    }
//
//                    if (existingEmails.contains(email)) {
//                        hasDuplicates = true;
//                        Cell duplicateInfoCell = row.createCell(7);
//                        duplicateInfoCell.setCellValue("Duplicated in database");
//                    }
//                    count++;
//                }
//
//            }
//
//            // Nếu có trùng lặp, xuất lại file Excel với thông tin cột 8
//            if (hasDuplicates) {
//                exportExcelWithDuplicates(response, workbook);
//            } else {
//                saveExcelData(sheet);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



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
    private LocalDate validateDateOfBirth(String dateOfBirth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate localDate = LocalDate.parse(dateOfBirth, formatter);
            log.warn(""+ localDate);
            return localDate; // Chuyển LocalDate thành Date
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
            throw new IllegalArgumentException("Invalid user status: " + roleName);
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
