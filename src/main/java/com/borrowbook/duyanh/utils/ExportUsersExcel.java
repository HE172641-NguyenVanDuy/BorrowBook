package com.borrowbook.duyanh.utils;

import com.borrowbook.duyanh.entity.InformationOfUser;
import com.borrowbook.duyanh.entity.Role;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.InformationOfUserRepository;
import com.borrowbook.duyanh.repository.RoleRepository;
import com.borrowbook.duyanh.repository.UserRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.borrowbook.duyanh.utils.Constants.*;

@Component
public class ExportUsersExcel {

    private static final Logger log = LoggerFactory.getLogger(ExportUsersExcel.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private InformationOfUserRepository informationOfUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${defaultPassword}")
    String defaultPassword;

    public ExportUsersExcel() {
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
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
        for (User u : usersList) {
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

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional
    public void importExcel(HttpServletResponse response, String filePath) {
        HashMap<String, List<Integer>> emailMap = new HashMap<>();
        HashMap<String, List<Integer>> usernameMap = new HashMap<>();
        HashMap<String, List<Integer>> phoneNumberMap = new HashMap<>();
        List<String> existingPhoneNumber = informationOfUserRepository.findAllPhoneNumber();
        List<String> existingUsername = userRepository.findAllUsername();
        List<String> existingEmails = informationOfUserRepository.findAllEmail();
        boolean hasDuplicates = false;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                Cell usernameCell = row.getCell(0);
                Cell emailCell = row.getCell(3);
                if ((usernameCell == null || usernameCell.getCellType() == CellType.BLANK) &&
                        (emailCell == null || emailCell.getCellType() == CellType.BLANK)) {
                    break;
                }
                String errorMsg = validateRow(row, existingEmails, emailMap,existingUsername
                        ,usernameMap,existingPhoneNumber,phoneNumberMap);

                Cell errorCell = row.createCell(8);
                errorCell.setCellValue(errorMsg.toString());

                CellStyle style = row.getSheet().getWorkbook().createCellStyle();
                style.setWrapText(true);
                errorCell.setCellStyle(style);
                if (!errorMsg.isEmpty()) {
                    hasDuplicates = true;
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


    private String validateRow(Row row,
                               List<String> existingEmails,
                               HashMap<String, List<Integer>> emailMap,
                               List<String> existingUsername,
                               HashMap<String, List<Integer>> usernameMap,
                                List<String> existingPhoneNumber,
                                HashMap<String, List<Integer>> phoneNumberMap )throws Exception {
        StringBuilder errorMsg = new StringBuilder();

        Cell usernameCell = row.getCell(0);
        if (usernameCell != null && usernameCell.getCellType() == CellType.STRING) {
            String username = usernameCell.getStringCellValue();
            if (!checkUsername(username)) {
                errorMsg.append("User name is in the wrong format!\n");
            } else {
                if (existingUsername.contains(username)) {
                    errorMsg.append("User name already exists in the database!\n");
                }
                if (usernameMap.containsKey(username)) {
                    errorMsg.append("User name already exists in the Excel file at rows: ");
                    errorMsg.append(usernameMap.get(username).toString());
                    errorMsg.append("\n");
                } else {
                    usernameMap.put(username, new ArrayList<>());
                }

                usernameMap.get(username).add(row.getRowNum());
            }
        } else {
            errorMsg.append("User name is null!\n");
        }

        Cell passwordCell = row.getCell(1);
        if (passwordCell != null) {
            String password = "";
            if (passwordCell.getCellType() == CellType.STRING) {
                password = passwordCell.getStringCellValue();
                log.info("Password is :" + password);
                if (!checkPassword(password)) {
                    errorMsg.append("Password is in the wrong format!\r\n");
                }
            } else if (passwordCell.getCellType() == CellType.NUMERIC) {
                password = String.valueOf((long) passwordCell.getNumericCellValue());
                log.info("Password is :" + password);
                if (!checkPassword(password)) {
                    errorMsg.append("Password is in the wrong format!\r\n");
                }
            }
        } else log.warn("Password is null !");

        Cell phoneNumberCell = row.getCell(2);
        if (phoneNumberCell != null && phoneNumberCell.getCellType() == CellType.STRING) {
            String phoneNumber = usernameCell.getStringCellValue();
            if (!checkPhoneNumber(phoneNumber)) {
                errorMsg.append("Phone number is in the wrong format!\n");
            } else {
                if (existingPhoneNumber.contains(phoneNumber)) {
                    errorMsg.append("Phone number already exists in the database!\n");
                }
                if (phoneNumberMap.containsKey(phoneNumber)) {
                    errorMsg.append("Phone number already exists in the Excel file at rows: ");
                    errorMsg.append(phoneNumberMap.get(phoneNumber).toString());
                    errorMsg.append("\n");
                } else {
                    phoneNumberMap.put(phoneNumber, new ArrayList<>());
                }

                phoneNumberMap.get(phoneNumber).add(row.getRowNum());
            }
        } else {
            errorMsg.append("Phone number is null!\n");
        }

        Cell emailCell = row.getCell(3);
        if (emailCell != null && emailCell.getCellType() == CellType.STRING) {
            String email = emailCell.getStringCellValue();

            if (!checkEmail(email)) {
                errorMsg.append("Email is in the wrong format!\n");
            } else {
                if (existingEmails.contains(email)) {
                    errorMsg.append("Email already exists in the database!\n");
                }

                if (emailMap.containsKey(email)) {
                    errorMsg.append("Email already exists in the Excel file at rows: ");
                    errorMsg.append(emailMap.get(email).toString());
                    errorMsg.append("\n");
                } else {
                    emailMap.put(email, new ArrayList<>());
                }

                emailMap.get(email).add(row.getRowNum());
            }
        } else {
            errorMsg.append("Email is null!\n");
        }

        Cell dobCell = row.getCell(4);
        if (dobCell != null) {
            if (dobCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dobCell)) {
                Date dobDate = dobCell.getDateCellValue();
                log.info("Dob is: " + dobDate.toString());
                if (!validateDateOfBirth(dobDate)) {
                    errorMsg.append("Date of birth is in the wrong format!\r\n");
                }
            }
            if (dobCell.getCellType() == CellType.STRING) {
                try {
                    String dobString = dobCell.getStringCellValue();
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dobDate = dateFormat.parse(dobString);
                    log.info(dobDate.toString());
                    if (!validateDateOfBirth(dobDate)) {
                        errorMsg.append("Date of birth is in the wrong format!\r\n");
                    }
                } catch (ParseException e) {
                    errorMsg.append("Date of birth is not a valid date!\r\n");
                    log.error("Error parsing date: " + dobCell.getStringCellValue(), e);
                }
            }
        } else {
            //hasDuplicates = true;
            log.warn("Dob is null !");
            errorMsg.append("Dob cell is in the wrong format!\r\n");
        }

        Cell roleCell = row.getCell(5);
        if (roleCell != null) {
            if (roleCell.getCellType() == CellType.STRING) {
                String role = roleCell.getStringCellValue();

                log.info("Role is: " + role.toString());
                if (getRoleByRoleName(role) == null) {
                    errorMsg.append("Role is int the wrong format!\r\n");
                }
            } else
                System.out.println(roleCell.getCellType().name());
        } else {
            log.warn("Role cell is null");
        }
        return errorMsg.toString();
    }


    private void exportExcelWithDuplicates(HttpServletResponse response, Workbook workbook) throws IOException {
        LocalDateTime now = LocalDateTime.now();

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=duplicatedRowExcel.xlsx");

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

        System.out.println("File Excel có các dòng lỗi trùng lặp đã được xuất ra.");
    }

    private void saveExcelData(Sheet sheet) throws Exception {

        for (Row r : sheet) {
            User user = new User();
            InformationOfUser informationOfUser = new InformationOfUser();
            if (r.getRowNum() == 0) {
                continue;
            }

            Cell usernameCell = r.getCell(0);
            Cell emailCell = r.getCell(3);
            if ((usernameCell == null || usernameCell.getCellType() == CellType.BLANK) &&
                    (emailCell == null || emailCell.getCellType() == CellType.BLANK)) {
                break;
            }

            if (usernameCell != null && usernameCell.getCellType() == CellType.STRING) {
                String username = usernameCell.getStringCellValue();
                user.setUsername(username);
            }

            Cell cell = r.getCell(1);
            if (cell == null || cell.getCellType() == CellType.BLANK) {
                log.warn("Password is null");

                user.setPassword(passwordEncoder.encode(defaultPassword));
            } else {
                String password = "";
                log.info("Password is not null");
                if (cell.getCellType() == CellType.STRING) {
                    password = cell.getStringCellValue();
                } else if (cell.getCellType() == CellType.NUMERIC) {
                    password = String.valueOf((long) cell.getNumericCellValue());
                }
                user.setPassword(passwordEncoder.encode(password));

            }

            Cell phoneNumberCell = r.getCell(2);
            if (phoneNumberCell != null) {
                String phoneNumber = phoneNumberCell.getStringCellValue();
                log.info("Phone number: " + phoneNumber);
                informationOfUser.setPhoneNumber(phoneNumber);
            }

            // Email

            if (emailCell != null) {
                String email = emailCell.getStringCellValue();
                log.info("Email: " + email);
                informationOfUser.setEmail(email);
            }

            // Date of Birth
            Cell dobCell = r.getCell(4);
            if (dobCell != null) {
                Date dobDate = dobCell.getDateCellValue();
                log.info(String.valueOf(dobDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
                informationOfUser.setDob(dobDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            user.setStatus("ACTIVE");

            Cell roleCell = r.getCell(5);
            if (roleCell != null) {
                String roleName = roleCell.getStringCellValue();
                user.setRole(getRoleByRoleName(roleName));
            }

            user.setInformationOfUser(informationOfUser);
            informationOfUser.setUser(user);
            userRepository.save(user);
        }
    }

    private boolean checkUsername(String username) throws Exception {
        if (username.length() >= 6 && username.length() <= 30 && username.matches("^[a-zA-Z0-9]+$")) {
            return true;
        }
        return false;
    }

    private boolean checkPassword(String pa) throws Exception {
        if (pa.length() >= 6 && pa.length() <= 20) {
            return true;
        }
        return false;
    }

    private boolean checkPhoneNumber(String pa) throws Exception {
        if (pa.matches("^(?:\\+84|0)(?:[1-9]\\d{8})$")) {
            return true;
        }
        return false;
    }

    private boolean checkEmail(String pa) throws Exception {
        if (pa.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return true;
        }
        return false;
    }

    private boolean validateDateOfBirth(Date dateOfBirth) {
        try {
            LocalDate localDate = dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = localDate.format(formatter);

            LocalDate.parse(formattedDate, formatter);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Role getRoleByRoleName(String roleName) {
        if (ROLE_ADMIN.equalsIgnoreCase(roleName) ||
                ROLE_USER.equalsIgnoreCase(roleName) ||
                ROLE_LIBRARIAN.equalsIgnoreCase(roleName) ||
                ROLE_MANAGER.equalsIgnoreCase(roleName)) {
            //Role role = roleRepository.getRolesByRoleName(roleName);
            Role role = roleRepository.findByRoleName(roleName);
            return role;
        }
        return null;
    }
}
